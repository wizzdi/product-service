package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.BaseclassNoSQLRepository;
import com.flexicore.events.BaseclassNoSQLCreated;
import com.flexicore.events.PluginsLoadedEvent;
import com.flexicore.interfaces.AbstractNoSqlRepositoryPlugin;
import com.flexicore.model.nosql.BaseclassNoSQL;
import com.flexicore.product.model.EquipmentLocation;
import com.flexicore.product.request.EquipmentLocationFiltering;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.flexicore.service.MongoConnectionService.MONGO_DB;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@PluginInfo(version = 1)
@Extension
@Component
public class EquipmentLocationNoSQLRepository extends AbstractNoSqlRepositoryPlugin {


	private static final String COLLECTION_NAME = "EquipmentLocation";
	private static final String DATE_AT_LOCATION="dateAtLocation";
	@Autowired
	private Logger logger;
	private static AtomicBoolean init = new AtomicBoolean(false);
	private static CodecRegistry pojoCodecRegistry;


	public static CodecRegistry getPojoCodecRegistry() {
		return pojoCodecRegistry;
	}

	@Autowired
	private MongoClient mongoClient;

	@Qualifier(MONGO_DB)
	@Autowired
	private String mongoDBName;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;


	@PostConstruct
	private void postConstruct() {
		refreshCodec();

	}

	@EventListener
	@Order(11)
	public void init(PluginsLoadedEvent o){
		refreshCodec();
	}

	private void refreshCodec() {

			PojoCodecProvider.Builder builder = PojoCodecProvider.builder()
					.register(BaseclassNoSQL.class,EquipmentLocation.class);

			pojoCodecRegistry = fromRegistries(
					MongoClientSettings.getDefaultCodecRegistry(),
					fromProviders(builder.build()));


	}



	@Override
	public void merge(Object o) {

		if (o instanceof EquipmentLocation) {
			EquipmentLocation equipmentLocation = (EquipmentLocation) o;

			MongoDatabase db = mongoClient.getDatabase(mongoDBName);
			MongoCollection<EquipmentLocation> collection = db.getCollection(
					COLLECTION_NAME, EquipmentLocation.class).withCodecRegistry(
					pojoCodecRegistry);
			collection.insertOne(equipmentLocation);
			applicationEventPublisher.publishEvent(new BaseclassNoSQLCreated<>(equipmentLocation));
		}

	}



	public void massMerge(List<? extends EquipmentLocation> o) {
		MongoDatabase db = mongoClient.getDatabase(mongoDBName);
		MongoCollection<EquipmentLocation> collection = db.getCollection(
				COLLECTION_NAME, EquipmentLocation.class).withCodecRegistry(
				pojoCodecRegistry);
		collection.insertMany(o);

	}


	public long countAllEquipmentLocation(EquipmentLocationFiltering equipmentLocationFiltering) {
		MongoDatabase db = mongoClient.getDatabase(mongoDBName)
				.withCodecRegistry(pojoCodecRegistry);
		MongoCollection<EquipmentLocation> collection = db.getCollection(
				COLLECTION_NAME, EquipmentLocation.class).withCodecRegistry(
				pojoCodecRegistry);

		Bson pred = getEquipmentLocationPredicates(equipmentLocationFiltering);

		return pred == null ? collection.countDocuments() : collection.countDocuments(pred);

	}
	

	public List<EquipmentLocation> getAllEquipmentLocation(
			EquipmentLocationFiltering equipmentLocationFiltering) {
		MongoDatabase db = mongoClient.getDatabase(mongoDBName)
				.withCodecRegistry(pojoCodecRegistry);
		MongoCollection<EquipmentLocation> collection = db.getCollection(
				COLLECTION_NAME, EquipmentLocation.class).withCodecRegistry(pojoCodecRegistry);

		Bson pred = getEquipmentLocationPredicates(equipmentLocationFiltering);

		FindIterable<EquipmentLocation> base = pred == null ? collection.find(EquipmentLocation.class) : collection
				.find(pred, EquipmentLocation.class);
		FindIterable<EquipmentLocation> iter = base.sort(orderBy(descending(DATE_AT_LOCATION)));
		if (equipmentLocationFiltering.getCurrentPage() != null
				&& equipmentLocationFiltering.getPageSize() != null
				&& equipmentLocationFiltering.getCurrentPage() > -1
				&& equipmentLocationFiltering.getPageSize() > 0) {
			iter.limit(equipmentLocationFiltering.getPageSize()).skip(
					equipmentLocationFiltering.getPageSize()
							* equipmentLocationFiltering.getCurrentPage());
		}
		List<EquipmentLocation> alerts = new ArrayList<>();
		for (EquipmentLocation alert : iter) {
			alerts.add(alert);
		}
		return alerts;
	}

	

	private Bson getEquipmentLocationPredicates(
			EquipmentLocationFiltering equipmentLocationFiltering) {

		Bson bson = BaseclassNoSQLRepository.getBaseclassNoSQLPredicates(equipmentLocationFiltering);


		return bson;
	}

	
}
