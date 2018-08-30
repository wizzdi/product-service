package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Clazz;
import com.flexicore.model.Tenant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventFiltering extends FilteringInformationHolder {


    private String humanReadableTextLike;

    private String eventType;
    private Set<String> baseclassIds=new HashSet<>();
    @JsonIgnore
    private List<Baseclass> baseclass;
    private String clazzName;
    @JsonIgnore
    private Clazz clazz;
    private Set<String> tenantIds=new HashSet<>();

    private String baseclassNameLike;

    @JsonIgnore
    private List<Tenant> tenants;



    public String getHumanReadableTextLike() {
        return humanReadableTextLike;
    }

    public EventFiltering setHumanReadableTextLike(String humanReadableTextLike) {
        this.humanReadableTextLike = humanReadableTextLike;
        return this;
    }

    public String getEventType() {
        return eventType;
    }

    public EventFiltering setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }


    public Set<String> getBaseclassIds() {
        return baseclassIds;
    }

    public EventFiltering setBaseclassIds(Set<String> baseclassIds) {
        this.baseclassIds = baseclassIds;
        return this;
    }

    @JsonIgnore
    public List<Baseclass> getBaseclass() {
        return baseclass;
    }

    public EventFiltering setBaseclass(List<Baseclass> baseclass) {
        this.baseclass = baseclass;
        return this;
    }

    public String getClazzName() {
        return clazzName;
    }

    public EventFiltering setClazzName(String clazzName) {
        this.clazzName = clazzName;
        return this;
    }
    @JsonIgnore
    public Clazz getClazz() {
        return clazz;
    }

    public EventFiltering setClazz(Clazz clazz) {
        this.clazz = clazz;
        return this;
    }

    public Set<String> getTenantIds() {
        return tenantIds;
    }

    public EventFiltering setTenantIds(Set<String> tenantIds) {
        this.tenantIds = tenantIds;
        return this;
    }

    @JsonIgnore
    public List<Tenant> getTenants() {
        return tenants;
    }

    public EventFiltering setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
        return this;
    }

    public String getBaseclassNameLike() {
        return baseclassNameLike;
    }

    public EventFiltering setBaseclassNameLike(String baseclassNameLike) {
        this.baseclassNameLike = baseclassNameLike;
        return this;
    }
}
