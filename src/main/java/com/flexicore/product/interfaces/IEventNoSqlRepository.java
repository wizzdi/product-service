package com.flexicore.product.interfaces;

import com.flexicore.interfaces.PluginRepository;
import com.flexicore.product.containers.request.AlertFiltering;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.model.*;
import com.flexicore.request.GetClassInfo;
import com.flexicore.service.BaseclassService;
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
    String EVENT_DATE = "eventDate";
    String CLAZZ_NAME = "clazzName";
    String EVENT_TYPE = "eventType";
    String EVENT_SUB_TYPE = "eventSubType";

    String SEVERITY = "severity";
    String TENANT_ID = "baseclassTenantId";
    String BASECLASS_NAME = "baseclassName";
    String HUMAN_READABLE_TEXT = "humanReadableText";
    String BASECLASS_LAT = "baseclassLat";
    String BASECLASS_LON = "baseclassLon";
    String ID = "id";
    String USER_ACKED = "userAcked";


    static Bson getAlertsPredicate(AlertFiltering eventFiltering) {
        Bson pred = getEventsPredicate(eventFiltering);
        if (eventFiltering.getSeverityStart() != null) {
            Bson gte = gte(SEVERITY, eventFiltering.getSeverityStart());
            pred = pred == null ? gte : and(pred, gte);
        }

        if (eventFiltering.getSeverityEnd() != null) {
            Bson lte = lte(SEVERITY, eventFiltering.getSeverityEnd());
            pred = pred == null ? lte : and(pred, lte);
        }
        return pred;
    }


    static Bson getEventsPredicate(EventFiltering eventFiltering) {
        Bson pred = null;
        if (eventFiltering.getFromDate() != null) {

            Date start = Date.from(eventFiltering.getFromDate().toInstant(ZoneOffset.UTC));
            Bson gte = gte(EVENT_DATE, start);
            pred = pred == null ? gte : and(pred, gte);
        }

        if (eventFiltering.getToDate() != null) {
            Date end = Date.from(eventFiltering.getToDate().toInstant(ZoneOffset.UTC));
            Bson lte = lte(EVENT_DATE, end);
            pred = pred == null ? lte : and(pred, lte);
        }
        if (eventFiltering.getBaseclass()!=null&&!eventFiltering.getBaseclass().isEmpty()) {
            Set<String> baseclasIds = eventFiltering.getBaseclass().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            Bson in = in(BASECLASS_ID, baseclasIds);
            pred = pred == null ? in : and(pred, in);
        }

        if (eventFiltering.getTenantIds()!=null&&!eventFiltering.getTenantIds().isEmpty()) {
            Set<String> tenantsIds = eventFiltering.getTenantIds().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            Bson in = in(TENANT_ID, tenantsIds);
            pred = pred == null ? in : and(pred, in);
        }

        if (eventFiltering.getClazz() != null) {
            Bson eq = eq(CLAZZ_NAME, eventFiltering.getClazz().getName());
            pred = pred == null ? eq : and(pred, eq);
        }

        String eventType = eventFiltering.getEventType();
        if (eventType != null) {
            Set<String> names=BaseclassService.listInheritingClassesWithFilter(new GetClassInfo().setClassName(eventType)).getList().parallelStream().map(f->f.getClazz().getCanonicalName()).collect(Collectors.toSet());
            names.add(eventType);
            Bson eq = in(EVENT_TYPE, names);
            pred = pred == null ? eq : and(pred, eq);
        }


        if (eventFiltering.getLocationArea() != null) {
            Bson eq = and(
                    gte(BASECLASS_LAT, eventFiltering.getLocationArea().getLatStart()),
                    gte(BASECLASS_LON, eventFiltering.getLocationArea().getLonStart()),
                    lte(BASECLASS_LAT, eventFiltering.getLocationArea().getLatEnd()),
                    lte(BASECLASS_LON, eventFiltering.getLocationArea().getLonEnd())
            );
            pred = pred == null ? eq : and(pred, eq);
        }

        if (eventFiltering.getEventSubType() != null) {
            Bson eq = eq(EVENT_SUB_TYPE, eventFiltering.getEventSubType());
            pred = pred == null ? eq : and(pred, eq);
        }

        if (eventFiltering.getBaseclassNameLike() != null) {

            Bson eq = Filters.eq(BASECLASS_NAME, eventFiltering.getBaseclassNameLike());
            pred = pred == null ? eq : and(pred, eq);
        }

        if (eventFiltering.getHumanReadableTextLike() != null) {

            Bson eq = Filters.eq(HUMAN_READABLE_TEXT, eventFiltering.getHumanReadableTextLike());
            pred = pred == null ? eq : and(pred, eq);
        }
        if(eventFiltering.getAcked()!=null){
            Bson ack=Filters.ne(USER_ACKED,null);
            pred = pred == null ? ack : and(pred, ack);

        }
        return pred;
    }

    void massMergeEvents(List<? extends Event> o);

    long countAllEvents(EventFiltering eventFiltering);

    long countAllAlerts(AlertFiltering eventFiltering);

    <T extends Event> List<T> getAllEvents(EventFiltering eventFiltering, Class<T> c);

    <T extends Alert> List<T> getAllAlerts(AlertFiltering eventFiltering, Class<T> c);
}
