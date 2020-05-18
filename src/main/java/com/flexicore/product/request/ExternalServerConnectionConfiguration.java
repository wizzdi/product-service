package com.flexicore.product.request;

import com.flexicore.model.Baseclass;
import com.flexicore.product.response.GenericInspectResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExternalServerConnectionConfiguration<S extends com.flexicore.iot.ExternalServer, C extends com.flexicore.product.request.ConnectionHolder<S>> {

    private String id;
    private Supplier<List<S>> onRefresh;
    private Function<S, C> onConnect;
    private Function<C,GenericInspectResponse> onInspect;
    private List<S> cache;
    private long lastCacheRefresh;
    private Map<String,C> connectionHolders =new HashMap<>();
    private LinkedBlockingQueue<SingleInspectJob<S,C>> singleInspectJobs=new LinkedBlockingQueue<>();
    private Function<SingleInspectJob<S,C>,GenericInspectResponse> onSingleInspect;




    public ExternalServerConnectionConfiguration(Supplier<List<S>> onRefresh, Function<S, C> onConnect, Function<C, GenericInspectResponse> onInspect) {
        this();
        this.onRefresh = onRefresh;
        this.onConnect = onConnect;
        this.onInspect = onInspect;
    }

    public ExternalServerConnectionConfiguration() {
        this.id=Baseclass.getBase64ID();
    }

    public Supplier<List<S>> getOnRefresh() {
        return onRefresh;
    }

    public LinkedBlockingQueue<SingleInspectJob<S, C>> getSingleInspectJobs() {
        return singleInspectJobs;
    }

    public <T extends ExternalServerConnectionConfiguration<S, C>> T setSingleInspectJobs(LinkedBlockingQueue<SingleInspectJob<S, C>> singleInspectJobs) {
        this.singleInspectJobs = singleInspectJobs;
        return (T) this;
    }

    public <T extends ExternalServerConnectionConfiguration<S, C>> T setOnRefresh(Supplier<List<S>> onRefresh) {
        this.onRefresh = onRefresh;
        return (T) this;
    }

    public Function<S, C> getOnConnect() {
        return onConnect;
    }

    public <T extends ExternalServerConnectionConfiguration<S, C>> T setOnConnect(Function<S, C> onConnect) {
        this.onConnect = onConnect;
        return (T) this;
    }

    public Function<C, GenericInspectResponse> getOnInspect() {
        return onInspect;
    }

    public <T extends ExternalServerConnectionConfiguration<S, C>> T setOnInspect(Function<C, GenericInspectResponse> onInspect) {
        this.onInspect = onInspect;
        return (T) this;
    }

    public List<S> getCache() {
        return cache;
    }

    public <T extends ExternalServerConnectionConfiguration<S, C>> T setCache(List<S> cache) {
        this.cache = cache;
        return (T) this;
    }

    public long getLastCacheRefresh() {
        return lastCacheRefresh;
    }

    public <T extends ExternalServerConnectionConfiguration<S, C>> T setLastCacheRefresh(long lastCacheRefresh) {
        this.lastCacheRefresh = lastCacheRefresh;
        return (T) this;
    }

    public Map<String, C> getConnectionHolders() {
        return connectionHolders;
    }

    public <T extends ExternalServerConnectionConfiguration<S, C>> T setConnectionHolders(Map<String, C> connectionHolders) {
        this.connectionHolders = connectionHolders;
        return (T) this;
    }

    public void addSingleInspectJob(SingleInspectJob<S,C> singleInspectJob){
        if(onSingleInspect!=null){
            this.singleInspectJobs.add(singleInspectJob);
        }
    }

    public Function<SingleInspectJob<S, C>, GenericInspectResponse> getOnSingleInspect() {
        return onSingleInspect;
    }

    public <T extends ExternalServerConnectionConfiguration<S, C>> T setOnSingleInspect(Function<SingleInspectJob<S, C>, GenericInspectResponse> onSingleInspect) {
        this.onSingleInspect = onSingleInspect;
        return (T) this;
    }

    public String getId() {
        return id;
    }

    public <T extends ExternalServerConnectionConfiguration<S, C>> T setId(String id) {
        this.id = id;
        return (T) this;
    }
}
