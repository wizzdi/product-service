package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractNoSqlRepositoryPlugin;
import com.flexicore.product.containers.request.AlertFiltering;
import com.flexicore.product.containers.request.CreateAggregatedReport;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.containers.response.AggregationReportEntry;
import com.flexicore.product.interfaces.IEventNoSqlRepository;
import com.flexicore.product.interfaces.IEventService;
import com.flexicore.product.model.Alert;
import com.flexicore.product.model.Event;
import com.flexicore.product.request.AckEventsRequest;
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
import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import static com.mongodb.client.model.Updates.set;

import static com.flexicore.service.MongoConnectionService.MONGO_DB;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


@PluginInfo(version = 1)
public class EventNoSQLRepository extends AbstractNoSqlRepositoryPlugin implements IEventNoSqlRepository {

    @Inject
    private Logger logger;
    private static AtomicBoolean init = new AtomicBoolean(false);
    private static long pojoTime;
    private static CodecRegistry pojoCodecRegistry;

    static {
        IEventService.addClassForMongoCodec(Event.class);
        IEventService.addClassForMongoCodec(Alert.class);
    }

    public static CodecRegistry getPojoCodecRegistry() {
        return pojoCodecRegistry;
    }

    @Inject
    private MongoClient mongoClient;

    @Inject
    @Named(MONGO_DB)
    private String mongoDBName;

    @PostConstruct
    private void postConstruct() {
        if (pojoTime != IEventService.lastListUpdateTime.get() || init.compareAndSet(false, true)) {
            PojoCodecProvider.Builder builder = PojoCodecProvider.builder();
            Pair<Long, Set<Class<? extends Event>>> alertClazzToRegister = IEventService.getAlertClazzToRegister();
            for (Class<? extends Event> aClass : alertClazzToRegister.getValue()) {
                builder = builder.register(aClass);
            }
            pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    fromProviders(builder.build()));
            pojoTime = alertClazzToRegister.getKey();
        }

    }

    public List<AggregationReportEntry> generateReport(CreateAggregatedReport createAggregatedReport, LocalDateTime endTime) {
        createAggregatedReport = new CreateAggregatedReport(createAggregatedReport);
        createAggregatedReport.setToDate(endTime);
        MongoDatabase db = mongoClient.getDatabase(mongoDBName).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Document> collection = db.getCollection(EVENTS_COLLECTION_NAME);
        Bson pred = IEventNoSqlRepository.getEventsPredicate(createAggregatedReport);
        List<Bson> aggregatePipeline = new ArrayList<>(Arrays.asList(
                Aggregates.match(
                        pred
                ),
                Aggregates.sort(Sorts.descending("baseclassId", "eventDate")), // order by baseclassId and than event date desc so $first has a meaning
                Aggregates.group("$baseclassId",
                        Accumulators.first("statusIds", "$statusIds"),
                        Accumulators.first("productTypeId", "$productTypeId"),
                        Accumulators.first("baseclassTenantId", "$baseclassTenantId")), //group by baseclassId(related equipment) , keep the value of the first status Ids array per group
                Aggregates.unwind("$statusIds") // unwind status ids array so we have multiple entries one per status

        ));
        if (createAggregatedReport.getProductStatus() != null && !createAggregatedReport.getProductStatus().isEmpty()) {
            aggregatePipeline.add(
                    Aggregates.match(
                            Filters.in("statusIds", createAggregatedReport.getProductStatus()) // remove groups which are not one of the desired status ids
                    )
            );

        }
        aggregatePipeline.add(Aggregates.group(
                new BasicDBObject()
                        .append("statusId", "$statusIds")
                        .append("productTypeId", "$productTypeId")
                        .append("baseclassTenantId", "$baseclassTenantId")
                , Accumulators.sum("count", 1))); // finally group by status Id - count the group size

        AggregateIterable<Document> documents = collection.aggregate(aggregatePipeline);
        List<AggregationReportEntry> toRet = new ArrayList<>();
        for (Document document : documents) {
            Document id = document.get("_id", Document.class);
            toRet.add(new AggregationReportEntry(id.getString("statusId"), id.getString("productTypeId"), id.getString("baseclassTenantId"), document.getInteger("count")));
            System.out.println(document);
        }
        return toRet;


    }

    @Override
    public void merge(Object o) {

        if (o instanceof Event) {
            MongoDatabase db = mongoClient.getDatabase(mongoDBName);
            MongoCollection<Event> collection = db.getCollection(EVENTS_COLLECTION_NAME, Event.class).withCodecRegistry(pojoCodecRegistry);
            collection.insertOne((Event) o);
        }

    }


    @Override
    public void massMergeEvents(List<? extends Event> o) {
        MongoDatabase db = mongoClient.getDatabase(mongoDBName);
        MongoCollection<Event> collection = db.getCollection(EVENTS_COLLECTION_NAME, Event.class).withCodecRegistry(pojoCodecRegistry);
        collection.insertMany(o);


    }


    @Override
    public long countAllEvents(EventFiltering eventFiltering) {
        MongoDatabase db = mongoClient.getDatabase(mongoDBName).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Event> collection = db.getCollection(EVENTS_COLLECTION_NAME, Event.class).withCodecRegistry(pojoCodecRegistry);

        Bson pred = IEventNoSqlRepository.getEventsPredicate(eventFiltering);

        return pred == null ? collection.count() : collection.count(pred);

    }

    @Override
    public long countAllAlerts(AlertFiltering eventFiltering) {
        MongoDatabase db = mongoClient.getDatabase(mongoDBName).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Event> collection = db.getCollection(EVENTS_COLLECTION_NAME, Event.class).withCodecRegistry(pojoCodecRegistry);

        Bson pred = IEventNoSqlRepository.getAlertsPredicate(eventFiltering);

        return pred == null ? collection.count() : collection.count(pred);

    }

    @Override
    public <T extends Event> List<T> getAllEvents(EventFiltering eventFiltering, Class<T> c) {
        MongoDatabase db = mongoClient.getDatabase(mongoDBName).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<T> collection = db.getCollection(EVENTS_COLLECTION_NAME, c).withCodecRegistry(pojoCodecRegistry);

        Bson pred = IEventNoSqlRepository.getEventsPredicate(eventFiltering);

        FindIterable<T> base = pred == null ? collection.find(c) : collection.find(pred, c);
        FindIterable<T> iter = base.sort(orderBy(descending(EVENT_DATE)));
        if (eventFiltering.getCurrentPage() != null && eventFiltering.getPageSize() != null && eventFiltering.getCurrentPage() > -1 && eventFiltering.getPageSize() > 0) {
            iter.limit(eventFiltering.getPageSize()).skip(eventFiltering.getPageSize() * eventFiltering.getCurrentPage());
        }
        List<T> alerts = new ArrayList<>();
        for (T alert : iter) {
            alerts.add(alert);
        }
        return alerts;
    }


    @Override
    public <T extends Alert> List<T> getAllAlerts(AlertFiltering eventFiltering, Class<T> c) {
        MongoDatabase db = mongoClient.getDatabase(mongoDBName).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<T> collection = db.getCollection(EVENTS_COLLECTION_NAME, c).withCodecRegistry(pojoCodecRegistry);

        Bson pred = IEventNoSqlRepository.getAlertsPredicate(eventFiltering);

        FindIterable<T> base = pred == null ? collection.find(c) : collection.find(pred, c);
        FindIterable<T> iter = base.sort(orderBy(descending(EVENT_DATE)));
        if (eventFiltering.getCurrentPage() != null && eventFiltering.getPageSize() != null && eventFiltering.getCurrentPage() > -1 && eventFiltering.getPageSize() > 0) {
            iter.limit(eventFiltering.getPageSize()).skip(eventFiltering.getPageSize() * eventFiltering.getCurrentPage());
        }
        List<T> alerts = new ArrayList<>();
        for (T alert : iter) {
            alerts.add(alert);
        }
        return alerts;
    }

    public long ackEvents(AckEventsRequest ackEventsRequest, SecurityContext securityContext) {
        MongoDatabase db = mongoClient.getDatabase(mongoDBName).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Event> collection = db.getCollection(EVENTS_COLLECTION_NAME, Event.class).withCodecRegistry(pojoCodecRegistry);
        Bson filter=in(ID,ackEventsRequest.getEventIds());

        Bson query=set(USER_ACKED,securityContext.getUser().getId());
        UpdateResult updateResult=collection.updateMany(filter,query);
        return updateResult.getModifiedCount();
    }
}
