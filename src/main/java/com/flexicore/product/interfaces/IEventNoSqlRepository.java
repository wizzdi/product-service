package com.flexicore.product.interfaces;

import com.flexicore.interfaces.PluginRepository;
import com.flexicore.product.containers.request.AlertFiltering;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.model.*;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;

public interface IEventNoSqlRepository extends PluginRepository {


    String BASECLASS_ID = "baseclassId";
    String EVENTS_COLLECTION_NAME = "Events";
    String ALERT_DATE = "eventDate";
    String CLAZZ_NAME = "clazzName";
    String ALERT_TYPE = "alertType";
    String SEVERITY = "severity";
    String TENANT_ID = "baseclassTenantId";
    String BASECLASS_NAME = "baseclassName";
    String HUMAN_READABLE_TEXT = "humanReadableText";

    static Bson getAlertsPredicate(AlertFiltering eventFiltering) {
        Bson pred=getEventsPredicate(eventFiltering);
        if(eventFiltering.getSeverityStart()!=null){
            Bson gte = gte(SEVERITY, eventFiltering.getSeverityStart());
            pred=pred==null?gte:and(pred, gte);
        }

        if(eventFiltering.getSeverityEnd()!=null){
            Bson lte = lte(SEVERITY, eventFiltering.getSeverityEnd());
            pred=pred==null?lte:and(pred, lte);
        }
        return pred;
    }


        static Bson getEventsPredicate(EventFiltering eventFiltering) {
        Bson pred=null;
        if(eventFiltering.getFromDate()!=null){

            Date start=Date.from(eventFiltering.getFromDate().toInstant(ZoneOffset.UTC));
            Bson gte = gte(ALERT_DATE, start);
            pred=pred==null?gte:and(pred, gte);
        }

        if(eventFiltering.getToDate()!=null){
            Date end=Date.from(eventFiltering.getToDate().toInstant(ZoneOffset.UTC));
            Bson lte = lte(ALERT_DATE, end);
            pred=pred==null?lte:and(pred, lte);
        }
        if(!eventFiltering.getBaseclass().isEmpty()){
            Set<String> baseclasIds = eventFiltering.getBaseclass().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            Bson in = in(BASECLASS_ID, baseclasIds);
            pred=pred==null?in:and(pred, in);
        }

        if(!eventFiltering.getTenants().isEmpty()){
            Set<String> tenantsIds = eventFiltering.getTenants().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            Bson in = in(TENANT_ID, tenantsIds);
            pred=pred==null?in:and(pred, in);
        }

        if(eventFiltering.getClazz()!=null){
            Bson eq = eq(CLAZZ_NAME, eventFiltering.getClazz().getName());
            pred=pred==null?eq:and(pred, eq);
        }

        if(eventFiltering.getEventType()!=null){
            Bson eq = eq(ALERT_TYPE, eventFiltering.getEventType());
            pred=pred==null?eq:and(pred, eq);
        }



        if(eventFiltering.getBaseclassNameLike()!=null){

            Bson eq = Filters.eq(BASECLASS_NAME, eventFiltering.getBaseclassNameLike());
            pred=pred==null?eq:and(pred, eq);
        }

        if(eventFiltering.getHumanReadableTextLike()!=null){

            Bson eq = Filters.eq(HUMAN_READABLE_TEXT, eventFiltering.getHumanReadableTextLike());
            pred=pred==null?eq:and(pred, eq);
        }
        return pred;
    }

    void massMergeEvents(List<? extends Event> o);

    long countAllEvents(EventFiltering eventFiltering);

    long countAllAlerts(AlertFiltering eventFiltering);

    <T extends Event> List<T> getAllEvents(EventFiltering eventFiltering, Class<T> c);

    <T extends Alert> List<T> getAllAlerts(AlertFiltering eventFiltering, Class<T> c);
}
