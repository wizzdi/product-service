package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractNoSqlRepositoryPlugin;
import com.flexicore.product.containers.request.AlertFiltering;
import com.flexicore.product.interfaces.IAlertNoSqlRepository;
import com.flexicore.product.interfaces.IAlertService;
import com.flexicore.product.model.Alert;
import com.flexicore.service.MongoConnectionService;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;



@PluginInfo(version = 1)
public class AlertNoSQLRepository extends AbstractNoSqlRepositoryPlugin implements IAlertNoSqlRepository {

    @Inject
    private Logger logger;
    private static AtomicBoolean init = new AtomicBoolean(false);
    private static long pojoTime;
    private static CodecRegistry pojoCodecRegistry;

    static{
        IAlertService.addClassForMongoCodec(Alert.class);
    }

    public static CodecRegistry getPojoCodecRegistry() {
        return pojoCodecRegistry;
    }

    @PostConstruct
    private void postConstruct() {
        MongoConnectionService.init(logger, em);
        if (pojoTime!=IAlertService.lastListUpdateTime.get()||init.compareAndSet(false, true)) {
            PojoCodecProvider.Builder builder = PojoCodecProvider.builder();
            Pair<Long, Set<Class<? extends Alert>>> alertClazzToRegister = IAlertService.getAlertClazzToRegister();
            for (Class<? extends Alert> aClass : alertClazzToRegister.getValue()) {
                builder=builder.register(aClass);
            }
            pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    fromProviders(builder.build()));
            pojoTime=alertClazzToRegister.getKey();
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

@Override
    public void massMergeAlerts(List<? extends Alert> o) {
        MongoDatabase db = MongoConnectionService.getMongoClient().getDatabase(MongoConnectionService.getDbName());
        MongoCollection<Alert> collection = db.getCollection(COLLECTION_NAME, Alert.class).withCodecRegistry(pojoCodecRegistry);
        collection.insertMany(o);


    }
@Override
    public long countAllAlerts(AlertFiltering alertFiltering) {
        MongoDatabase db = MongoConnectionService.getMongoClient().getDatabase(MongoConnectionService.getDbName()).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Alert> collection = db.getCollection(COLLECTION_NAME, Alert.class).withCodecRegistry(pojoCodecRegistry);

        Bson pred = IAlertNoSqlRepository.getAlertsPredicate(alertFiltering);

        return pred==null?collection.count():collection.count(pred);

    }

    @Override
    public<T extends Alert> List<T> getAllAlerts(AlertFiltering alertFiltering, Class<T> c) {
        MongoDatabase db = MongoConnectionService.getMongoClient().getDatabase(MongoConnectionService.getDbName()).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<T> collection = db.getCollection(COLLECTION_NAME, c).withCodecRegistry(pojoCodecRegistry);

        Bson pred = IAlertNoSqlRepository.getAlertsPredicate(alertFiltering);

        FindIterable<T> base = pred==null?collection.find(c):collection.find(pred, c);
        FindIterable<T> iter= base.sort(orderBy(descending(ALERT_DATE)));
        if(alertFiltering.getCurrentPage()!=null && alertFiltering.getPageSize()!=null&& alertFiltering.getCurrentPage() > -1 && alertFiltering.getPageSize()> 0){
            iter.limit(alertFiltering.getPageSize()).skip(alertFiltering.getPageSize()*alertFiltering.getCurrentPage());
        }
        List<T> alerts=new ArrayList<>();
        for (T alert : iter) {
            alerts.add(alert);
        }
        return alerts;
    }

}
