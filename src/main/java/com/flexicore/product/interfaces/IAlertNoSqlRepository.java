package com.flexicore.product.interfaces;

import com.flexicore.interfaces.PluginRepository;
import com.flexicore.product.containers.request.AlertFiltering;
import com.flexicore.product.data.AlertNoSQLRepository;
import com.flexicore.product.model.*;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;

public interface IAlertNoSqlRepository extends PluginRepository {


    String BASECLASS_ID = "baseclassId";
    String COLLECTION_NAME = "Alerts";
    String ALERT_DATE = "alertDate";
    String CLAZZ_NAME = "clazzName";
    String ALERT_TYPE = "alertType";
    String SEVERITY = "severity";
    String TENANT_ID = "baseclassTenantId";
    String BASECLASS_NAME = "baseclassName";
    String HUMAN_READABLE_TEXT = "humanReadableText";

    static Bson getAlertsPredicate(AlertFiltering alertFiltering) {
        Bson pred=null;
        if(alertFiltering.getAlertDateStart()!=null){

            Date start=Date.from(alertFiltering.getAlertDateStart().toInstant(ZoneOffset.UTC));
            Bson gte = gte(ALERT_DATE, start);
            pred=pred==null?gte:and(pred, gte);
        }

        if(alertFiltering.getAlertDateEnd()!=null){
            Date end=Date.from(alertFiltering.getAlertDateEnd().toInstant(ZoneOffset.UTC));
            Bson lte = lte(ALERT_DATE, end);
            pred=pred==null?lte:and(pred, lte);
        }
        if(!alertFiltering.getBaseclass().isEmpty()){
            Set<String> baseclasIds = alertFiltering.getBaseclass().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            Bson in = in(BASECLASS_ID, baseclasIds);
            pred=pred==null?in:and(pred, in);
        }

        if(!alertFiltering.getTenants().isEmpty()){
            Set<String> tenantsIds = alertFiltering.getTenants().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            Bson in = in(TENANT_ID, tenantsIds);
            pred=pred==null?in:and(pred, in);
        }

        if(alertFiltering.getClazz()!=null){
            Bson eq = eq(CLAZZ_NAME, alertFiltering.getClazz().getName());
            pred=pred==null?eq:and(pred, eq);
        }

        if(alertFiltering.getAlertType()!=null){
            Bson eq = eq(ALERT_TYPE, alertFiltering.getAlertType());
            pred=pred==null?eq:and(pred, eq);
        }

        if(alertFiltering.getSeverityStart()!=null){
            Bson gte = gte(SEVERITY, alertFiltering.getSeverityStart());
            pred=pred==null?gte:and(pred, gte);
        }

        if(alertFiltering.getSeverityEnd()!=null){
            Bson lte = lte(SEVERITY, alertFiltering.getSeverityEnd());
            pred=pred==null?lte:and(pred, lte);
        }

        if(alertFiltering.getBaseclassNameLike()!=null){

            Bson eq = Filters.eq(BASECLASS_NAME,alertFiltering.getBaseclassNameLike());
            pred=pred==null?eq:and(pred, eq);
        }

        if(alertFiltering.getHumanReadableTextLike()!=null){

            Bson eq = Filters.eq(HUMAN_READABLE_TEXT,alertFiltering.getHumanReadableTextLike());
            pred=pred==null?eq:and(pred, eq);
        }
        return pred;
    }

    void massMergeAlerts(List<? extends Alert> o);

    long countAllAlerts(AlertFiltering alertFiltering);

    <T extends Alert> List<T> getAllAlerts(AlertFiltering alertFiltering, Class<T> c);
}
