package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractNoSqlRepositoryPlugin;
import com.flexicore.product.containers.request.AlertFiltering;
import com.flexicore.product.model.Alert;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.MongoConnectionService;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


@PluginInfo(version = 1)
public class AlertNoSQLRepository extends AbstractNoSqlRepositoryPlugin {
    public static final String COLLECTION_NAME = "Alerts";
    public static final String ALERT_DATE = "alertDate";
    public static final String BASECLASS_ID = "baseclassId";
    public static final String CLAZZ_NAME = "clazzName";
    public static final String ALERT_TYPE = "alertType";
    public static final String SEVERITY = "severity";
    @Inject
    private Logger logger;
    private static CodecRegistry pojoCodecRegistry;
    private static AtomicBoolean init = new AtomicBoolean(false);


    @PostConstruct
    private void postConstruct() {
        MongoConnectionService.init(logger, em);
        if (init.compareAndSet(false, true)) {
            pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().register(Alert.class).build()));
        }

    }

    @Override
    public void merge(Object o) {
        if (o instanceof Alert) {
            MongoDatabase db = MongoConnectionService.getMongoClient().getDatabase(MongoConnectionService.getDbName());
            MongoCollection<Alert> collection = db.getCollection(COLLECTION_NAME, Alert.class).withCodecRegistry(pojoCodecRegistry);
            collection.insertOne((Alert) o);
        }

    }


    public void massMergeAlerts(List<Alert> o) {
        MongoDatabase db = MongoConnectionService.getMongoClient().getDatabase(MongoConnectionService.getDbName());
        MongoCollection<Alert> collection = db.getCollection(COLLECTION_NAME, Alert.class).withCodecRegistry(pojoCodecRegistry);
        collection.insertMany(o);


    }


    public List<Alert> getAllAlerts(AlertFiltering alertFiltering) {
        MongoDatabase db = MongoConnectionService.getMongoClient().getDatabase(MongoConnectionService.getDbName()).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Alert> collection = db.getCollection(COLLECTION_NAME, Alert.class).withCodecRegistry(pojoCodecRegistry);

        Bson pred=and();
        if(alertFiltering.getAlertDateStart()!=null){
            Date start=Date.from(alertFiltering.getAlertDateStart().toInstant(ZoneOffset.UTC));
            pred=and(pred,gte(ALERT_DATE,start));
        }

        if(alertFiltering.getAlertDateEnd()!=null){
            Date end=Date.from(alertFiltering.getAlertDateEnd().toInstant(ZoneOffset.UTC));
            pred=and(pred,lte(ALERT_DATE,end));
        }
        if(!alertFiltering.getBaseclass().isEmpty()){
            Set<String> baseclasIds = alertFiltering.getBaseclass().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            pred=and(pred,in(BASECLASS_ID, baseclasIds));
        }

        if(alertFiltering.getClazz()!=null){
            pred=and(pred,eq(CLAZZ_NAME, alertFiltering.getClazz().getName()));
        }

        if(alertFiltering.getAlertType()!=null){
            pred=and(pred,eq(ALERT_TYPE, alertFiltering.getAlertType()));
        }

        if(alertFiltering.getSeverityStart()!=null){
            pred=and(pred,gte(SEVERITY, alertFiltering.getSeverityStart()));
        }

        if(alertFiltering.getSeverityEnd()!=null){
            pred=and(pred,lte(SEVERITY, alertFiltering.getSeverityEnd()));
        }

        FindIterable<Alert> iter=collection.find(pred,Alert.class).sort(orderBy(descending(ALERT_DATE)));
        if(alertFiltering.getCurrentPage()!=null && alertFiltering.getPageSize()!=null&& alertFiltering.getCurrentPage() > -1 && alertFiltering.getPageSize()> 0){
            iter.limit(alertFiltering.getPageSize()).skip(alertFiltering.getPageSize()*alertFiltering.getCurrentPage());
        }
        List<Alert> alerts=new ArrayList<>();
        for (Alert alert : iter) {
            alerts.add(alert);
        }
        return alerts;
    }
}
