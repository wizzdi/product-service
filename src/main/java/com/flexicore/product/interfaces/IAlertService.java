package com.flexicore.product.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.containers.request.AlertFiltering;
import com.flexicore.product.model.Alert;
import com.flexicore.security.SecurityContext;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public interface IAlertService extends ServicePlugin {

    static Queue<Class<? extends Alert>> clazzToRegister=new ConcurrentLinkedQueue<>();
    AtomicReference<Long> lastListUpdateTime=new AtomicReference<>(0L);

    void merge(Alert alert);

    void massMergeAlerts(List<Alert> o);

    <T extends Alert> PaginationResponse<T>  getAllAlerts(AlertFiltering alertFiltering,Class<T> c);


    static void addClassForMongoCodec(Class<? extends Alert> c){
        clazzToRegister.add(c);
        lastListUpdateTime.set(System.currentTimeMillis());
    }

    static Pair<Long,Set<Class<? extends Alert>>> getAlertClazzToRegister(){
        return Pair.of(lastListUpdateTime.get(),new HashSet<>(clazzToRegister));
    }

    void validateFiltering(AlertFiltering alertFiltering, SecurityContext securityContext);
}
