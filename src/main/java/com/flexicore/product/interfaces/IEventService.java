package com.flexicore.product.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.containers.request.AlertFiltering;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.model.Alert;
import com.flexicore.product.model.Event;
import com.flexicore.product.request.AckEventsRequest;
import com.flexicore.product.response.AckEventsResponse;
import com.flexicore.security.SecurityContext;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public interface IEventService extends ServicePlugin {

    static Queue<Class<? extends Event>> clazzToRegister=new ConcurrentLinkedQueue<>();
    AtomicReference<Long> lastListUpdateTime=new AtomicReference<>(0L);

    void merge(Event event);

    void massMergeEvents(List<? extends Event> o);

    <T extends Event> PaginationResponse<T> getAllEvents(EventFiltering eventFiltering, Class<T> c);


    static void addClassForMongoCodec(Class<? extends Event> c){
        clazzToRegister.add(c);
        lastListUpdateTime.set(System.currentTimeMillis());
    }

    static Pair<Long,Set<Class<? extends Event>>> getAlertClazzToRegister(){
        return Pair.of(lastListUpdateTime.get(),new HashSet<>(clazzToRegister));
    }

    <T extends Alert> PaginationResponse<T> getAllAlerts(AlertFiltering eventFiltering, Class<T> c);

    void validateFiltering(EventFiltering eventFiltering, SecurityContext securityContext);

    AckEventsResponse ackEvents(AckEventsRequest ackEventsRequest, SecurityContext securityContext);
}
