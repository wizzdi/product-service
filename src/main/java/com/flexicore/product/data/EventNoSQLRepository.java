package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractNoSqlRepositoryPlugin;
import com.flexicore.product.containers.request.AlertFiltering;
import com.flexicore.product.containers.request.CreateAggregatedReport;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.containers.response.AggregationReportEntry;
import com.flexicore.product.interfaces.IEventNoSqlRepository;
import com.flexicore.product.interfaces.IEventService;
import com.flexicore.product.model.*;
import com.flexicore.product.request.AckEventsRequest;
import com.flexicore.product.request.DetailedEquipmentFilter;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.MongoConnectionService;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.set;

import static com.flexicore.service.MongoConnectionService.MONGO_DB;
import static com.mongodb.client.model.Filters.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@Extension
@Component
public class EventNoSQLRepository extends AbstractNoSqlRepositoryPlugin
		implements
			IEventNoSqlRepository {

	private static final String DETAILED_EQUIPMENT_STATUS_COLLECTION = "DETAILED_EQUIPMENT_STATUS";
	public static final String EQUIPMENT_BY_STATUS_ENTRY = "equipmentByStatusEntry";
	private static final String EQUIPMENT_BY_STATUS_COLLECTION = "EQUIPMENT_BY_STATUS_COLLECTION";
	public static final String EQUIPMENT_BY_STATUS_EVENT_ID = "equipmentByStatusEventId";
	@Autowired
	private Logger logger;
	private static AtomicBoolean init = new AtomicBoolean(false);
	private static long pojoTime;
	private static CodecRegistry pojoCodecRegistry;

	static {
		IEventService.addClassForMongoCodec(Event.class);
		IEventService.addClassForMongoCodec(Alert.class);
		IEventService.addClassForMongoCodec(DetailedEquipmentStatus.class);
		IEventService.addClassForMongoCodec(EquipmentByStatusEntry.class);

	}

	public static CodecRegistry getPojoCodecRegistry() {
		return pojoCodecRegistry;
	}

	@Autowired
	private MongoClient mongoClient;

	@Named(MONGO_DB)
	@Autowired
	private String mongoDBName;

	@PostConstruct
	private void postConstruct() {
		if (pojoTime != IEventService.lastListUpdateTime.get()
				|| init.compareAndSet(false, true)) {
			PojoCodecProvider.Builder builder = PojoCodecProvider.builder();
			Pair<Long, Set<Class<?>>> alertClazzToRegister = IEventService
					.getAlertClazzToRegister();
			for (Class<?> aClass : alertClazzToRegister.getValue()) {
				builder = builder.register(aClass);
			}
			pojoCodecRegistry = fromRegistries(
					MongoClient.getDefaultCodecRegistry(),
					fromProviders(builder.build()));
			pojoTime = alertClazzToRegister.getKey();
		}

	}

	public List<AggregationReportEntry> generateReport(
			CreateAggregatedReport createAggregatedReport, OffsetDateTime endTime) {
		createAggregatedReport = new CreateAggregatedReport(
				createAggregatedReport);
		createAggregatedReport.setToDate(endTime);
		MongoDatabase db = mongoClient.getDatabase(mongoDBName)
				.withCodecRegistry(pojoCodecRegistry);
		MongoCollection<Document> collection = db
				.getCollection(EVENTS_COLLECTION_NAME);
		Bson pred = IEventNoSqlRepository
				.getEventsPredicate(createAggregatedReport);
		List<Bson> aggregatePipeline = new ArrayList<>(Arrays.asList(Aggregates
				.match(pred), Aggregates.sort(Sorts.descending("baseclassId",
				"eventDate")), // order by baseclassId and than event date desc
								// so $first has a meaning
				Aggregates.group("$baseclassId", Accumulators.first(
						"statusIds", "$statusIds"), Accumulators.first(
						"productTypeId", "$productTypeId"), Accumulators.first(
						"baseclassTenantId", "$baseclassTenantId")), // group by
																		// baseclassId(related
																		// equipment)
																		// ,
																		// keep
																		// the
																		// value
																		// of
																		// the
																		// first
																		// status
																		// Ids
																		// array
																		// per
																		// group
				Aggregates.unwind("$statusIds") // unwind status ids array so we
												// have multiple entries one per
												// status

				));
		if (createAggregatedReport.getProductStatus() != null
				&& !createAggregatedReport.getProductStatus().isEmpty()) {
			aggregatePipeline.add(Aggregates.match(Filters.in("statusIds",
					createAggregatedReport.getProductStatus()) // remove groups
																// which are not
																// one of the
																// desired
																// status ids
					));

		}
		aggregatePipeline.add(Aggregates.group(
				new BasicDBObject().append("statusId", "$statusIds")
						.append("productTypeId", "$productTypeId")
						.append("baseclassTenantId", "$baseclassTenantId"),
				Accumulators.sum("count", 1))); // finally group by status Id -
												// count the group size

		AggregateIterable<Document> documents = collection
				.aggregate(aggregatePipeline);
		List<AggregationReportEntry> toRet = new ArrayList<>();
		for (Document document : documents) {
			Document id = document.get("_id", Document.class);
			toRet.add(new AggregationReportEntry(id.getString("statusId"), id
					.getString("productTypeId"), id
					.getString("baseclassTenantId"), document
					.getInteger("count")));
			System.out.println(document);
		}
		return toRet;

	}

	@Override
	public void merge(Object o) {

		if (o instanceof Event) {
			MongoDatabase db = mongoClient.getDatabase(mongoDBName);
			MongoCollection<Event> collection = db.getCollection(
					EVENTS_COLLECTION_NAME, Event.class).withCodecRegistry(
					pojoCodecRegistry);
			collection.insertOne((Event) o);
		}

	}

	@Override
	public void massMergeEvents(List<? extends Event> o) {
		MongoDatabase db = mongoClient.getDatabase(mongoDBName);
		MongoCollection<Event> collection = db.getCollection(
				EVENTS_COLLECTION_NAME, Event.class).withCodecRegistry(
				pojoCodecRegistry);
		collection.insertMany(o);

	}

	public void massMergeDetailedStatus(List<DetailedEquipmentStatus> o) {
		MongoDatabase db = mongoClient.getDatabase(mongoDBName);
		MongoCollection<DetailedEquipmentStatus> collection = db.getCollection(
				DETAILED_EQUIPMENT_STATUS_COLLECTION,
				DetailedEquipmentStatus.class).withCodecRegistry(
				pojoCodecRegistry);
		collection.insertMany(o);

	}

	@Override
	public long countAllEvents(EventFiltering eventFiltering) {
		MongoDatabase db = mongoClient.getDatabase(mongoDBName)
				.withCodecRegistry(pojoCodecRegistry);
		MongoCollection<Event> collection = db.getCollection(
				EVENTS_COLLECTION_NAME, Event.class).withCodecRegistry(
				pojoCodecRegistry);

		Bson pred = IEventNoSqlRepository.getEventsPredicate(eventFiltering);

		return pred == null ? collection.count() : collection.count(pred);

	}

	@Override
	public long countAllAlerts(AlertFiltering eventFiltering) {
		MongoDatabase db = mongoClient.getDatabase(mongoDBName)
				.withCodecRegistry(pojoCodecRegistry);
		MongoCollection<Event> collection = db.getCollection(
				EVENTS_COLLECTION_NAME, Event.class).withCodecRegistry(
				pojoCodecRegistry);

		Bson pred = IEventNoSqlRepository.getAlertsPredicate(eventFiltering);

		return pred == null ? collection.count() : collection.count(pred);

	}

	@Override
	public <T extends Event> List<T> getAllEvents(
			EventFiltering eventFiltering, Class<T> c) {
		MongoDatabase db = mongoClient.getDatabase(mongoDBName)
				.withCodecRegistry(pojoCodecRegistry);
		MongoCollection<T> collection = db.getCollection(
				EVENTS_COLLECTION_NAME, c).withCodecRegistry(pojoCodecRegistry);

		Bson pred = IEventNoSqlRepository.getEventsPredicate(eventFiltering);

		FindIterable<T> base = pred == null ? collection.find(c) : collection
				.find(pred, c);
		FindIterable<T> iter = base.sort(orderBy(descending(EVENT_DATE)));
		if (eventFiltering.getCurrentPage() != null
				&& eventFiltering.getPageSize() != null
				&& eventFiltering.getCurrentPage() > -1
				&& eventFiltering.getPageSize() > 0) {
			iter.limit(eventFiltering.getPageSize()).skip(
					eventFiltering.getPageSize()
							* eventFiltering.getCurrentPage());
		}
		List<T> alerts = new ArrayList<>();
		for (T alert : iter) {
			alerts.add(alert);
		}
		return alerts;
	}

	@Override
	public <T extends Alert> List<T> getAllAlerts(
			AlertFiltering eventFiltering, Class<T> c) {
		MongoDatabase db = mongoClient.getDatabase(mongoDBName)
				.withCodecRegistry(pojoCodecRegistry);
		MongoCollection<T> collection = db.getCollection(
				EVENTS_COLLECTION_NAME, c).withCodecRegistry(pojoCodecRegistry);

		Bson pred = IEventNoSqlRepository.getAlertsPredicate(eventFiltering);

		FindIterable<T> base = pred == null ? collection.find(c) : collection
				.find(pred, c);
		FindIterable<T> iter = base.sort(orderBy(descending(EVENT_DATE)));
		if (eventFiltering.getCurrentPage() != null
				&& eventFiltering.getPageSize() != null
				&& eventFiltering.getCurrentPage() > -1
				&& eventFiltering.getPageSize() > 0) {
			iter.limit(eventFiltering.getPageSize()).skip(
					eventFiltering.getPageSize()
							* eventFiltering.getCurrentPage());
		}
		List<T> alerts = new ArrayList<>();
		for (T alert : iter) {
			alerts.add(alert);
		}
		return alerts;
	}

	public long ackEvents(AckEventsRequest ackEventsRequest,
			SecurityContext securityContext) {
		MongoDatabase db = mongoClient.getDatabase(mongoDBName)
				.withCodecRegistry(pojoCodecRegistry);
		MongoCollection<Event> collection = db.getCollection(
				EVENTS_COLLECTION_NAME, Event.class).withCodecRegistry(
				pojoCodecRegistry);
		Bson filter = in(ID, ackEventsRequest.getEventIds());

		Bson query = Updates.combine(
				set(USER_ACKED, securityContext.getUser().getId()),
				set(USER_ACKED_NAME, securityContext.getUser().getName()),
				set(ACK_NOTES, ackEventsRequest.getAckNotes()),
				set(FALSE_ALARM, ackEventsRequest.isFalseAlarm())

		);
		UpdateResult updateResult = collection.updateMany(filter, query);
		return updateResult.getModifiedCount();
	}

	public List<DetailedEquipmentStatus> listAllDetailedEquipmentStatus(
			DetailedEquipmentFilter processErrorsFiltering) {

		MongoDatabase db = mongoClient.getDatabase(mongoDBName)
				.withCodecRegistry(pojoCodecRegistry);
		MongoCollection<DetailedEquipmentStatus> collection = db.getCollection(
				DETAILED_EQUIPMENT_STATUS_COLLECTION,
				DetailedEquipmentStatus.class).withCodecRegistry(
				pojoCodecRegistry);

		Bson pred = getProcessErrorsPredicate(processErrorsFiltering);

		FindIterable<DetailedEquipmentStatus> base = pred == null ? collection
				.find(DetailedEquipmentStatus.class) : collection.find(pred,
				DetailedEquipmentStatus.class);
		FindIterable<DetailedEquipmentStatus> iter = base;
		if (processErrorsFiltering.getCurrentPage() != null
				&& processErrorsFiltering.getPageSize() != null
				&& processErrorsFiltering.getCurrentPage() > -1
				&& processErrorsFiltering.getPageSize() > 0) {
			iter.limit(processErrorsFiltering.getPageSize()).skip(
					processErrorsFiltering.getPageSize()
							* processErrorsFiltering.getCurrentPage());
		}
		List<DetailedEquipmentStatus> alerts = new ArrayList<>();
		for (DetailedEquipmentStatus alert : iter) {
			alerts.add(alert);
		}
		return alerts;
	}

	private Bson getProcessErrorsPredicate(
			DetailedEquipmentFilter processErrorsFiltering) {

		Bson bson = null;
		if (processErrorsFiltering.getEquipmentByStatusEntryIds() != null
				&& !processErrorsFiltering.getEquipmentByStatusEntryIds()
						.isEmpty()) {
			Bson pred = in(EQUIPMENT_BY_STATUS_ENTRY,
					processErrorsFiltering.getEquipmentByStatusEntryIds());
			bson = bson == null ? pred : and(bson, pred);
		}

		return bson;
	}

	public long countAllDetailedEquipmentStatus(
			DetailedEquipmentFilter processErrorsFiltering) {
		MongoDatabase db = mongoClient.getDatabase(mongoDBName)
				.withCodecRegistry(pojoCodecRegistry);
		MongoCollection<DetailedEquipmentStatus> collection = db.getCollection(
				DETAILED_EQUIPMENT_STATUS_COLLECTION,
				DetailedEquipmentStatus.class).withCodecRegistry(
				pojoCodecRegistry);

		Bson pred = getProcessErrorsPredicate(processErrorsFiltering);

		return pred == null ? collection.count() : collection.count(pred);
	}

	public void massMergeEntries(List<EquipmentByStatusEntry> entries) {

		MongoDatabase db = mongoClient.getDatabase(mongoDBName);
		MongoCollection<EquipmentByStatusEntry> collection = db.getCollection(
				EQUIPMENT_BY_STATUS_COLLECTION, EquipmentByStatusEntry.class)
				.withCodecRegistry(pojoCodecRegistry);
		collection.insertMany(entries);

	}

	public List<EquipmentByStatusEntry> listAllEquipmentByStatusEntry(
			EquipmentByStatusEntryFiltering processErrorsFiltering) {

		MongoDatabase db = mongoClient.getDatabase(mongoDBName)
				.withCodecRegistry(pojoCodecRegistry);
		MongoCollection<EquipmentByStatusEntry> collection = db.getCollection(
				EQUIPMENT_BY_STATUS_COLLECTION, EquipmentByStatusEntry.class)
				.withCodecRegistry(pojoCodecRegistry);

		Bson pred = getEquipmentByStatusPredicate(processErrorsFiltering);

		FindIterable<EquipmentByStatusEntry> base = pred == null ? collection
				.find(EquipmentByStatusEntry.class) : collection.find(pred,
				EquipmentByStatusEntry.class);
		FindIterable<EquipmentByStatusEntry> iter = base;
		if (processErrorsFiltering.getCurrentPage() != null
				&& processErrorsFiltering.getPageSize() != null
				&& processErrorsFiltering.getCurrentPage() > -1
				&& processErrorsFiltering.getPageSize() > 0) {
			iter.limit(processErrorsFiltering.getPageSize()).skip(
					processErrorsFiltering.getPageSize()
							* processErrorsFiltering.getCurrentPage());
		}
		List<EquipmentByStatusEntry> alerts = new ArrayList<>();
		for (EquipmentByStatusEntry alert : iter) {
			alerts.add(alert);
		}
		return alerts;
	}

	private Bson getEquipmentByStatusPredicate(
			EquipmentByStatusEntryFiltering processErrorsFiltering) {

		Bson bson = null;
		if (processErrorsFiltering.getEquipmentByStatusEventIdFilterings() != null
				&& !processErrorsFiltering
						.getEquipmentByStatusEventIdFilterings().isEmpty()) {
			Set<String> ids = processErrorsFiltering
					.getEquipmentByStatusEventIdFilterings().parallelStream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			Bson pred = in(EQUIPMENT_BY_STATUS_EVENT_ID, ids);
			bson = bson == null ? pred : and(bson, pred);
		}

		if (processErrorsFiltering.getNameLike() != null) {

			Bson eq = Filters.eq(BASECLASS_NAME,
					processErrorsFiltering.getNameLike());
			bson = bson == null ? eq : and(bson, eq);
		}

		return bson;
	}

	public long countAllEquipmentByStatusEntry(
			EquipmentByStatusEntryFiltering processErrorsFiltering) {
		MongoDatabase db = mongoClient.getDatabase(mongoDBName)
				.withCodecRegistry(pojoCodecRegistry);
		MongoCollection<EquipmentByStatusEntry> collection = db.getCollection(
				EQUIPMENT_BY_STATUS_COLLECTION, EquipmentByStatusEntry.class)
				.withCodecRegistry(pojoCodecRegistry);

		Bson pred = getEquipmentByStatusPredicate(processErrorsFiltering);

		return pred == null ? collection.count() : collection.count(pred);
	}
}
