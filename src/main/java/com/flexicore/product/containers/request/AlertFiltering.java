package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Clazz;
import com.flexicore.model.Tenant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlertFiltering extends EventFiltering {


    private Integer severityStart;
    private Integer severityEnd;
    private String alertType;



 
    public Integer getSeverityStart() {
        return severityStart;
    }

    public AlertFiltering setSeverityStart(Integer severityStart) {
        this.severityStart = severityStart;
        return this;
    }

    public Integer getSeverityEnd() {
        return severityEnd;
    }

    public AlertFiltering setSeverityEnd(Integer severityEnd) {
        this.severityEnd = severityEnd;
        return this;
    }

    @Override
    public AlertFiltering setHumanReadableTextLike(String humanReadableTextLike) {
        return  (AlertFiltering)super.setHumanReadableTextLike(humanReadableTextLike);
    }

    @Override
    public AlertFiltering setBaseclassIds(Set<String> baseclassIds) {
        return (AlertFiltering) super.setBaseclassIds(baseclassIds);
    }

    @Override
    public AlertFiltering setBaseclass(List<Baseclass> baseclass) {
        return  (AlertFiltering)super.setBaseclass(baseclass);
    }

    @Override
    public AlertFiltering setClazzName(String clazzName) {
        return  (AlertFiltering)super.setClazzName(clazzName);
    }

    @Override
    public AlertFiltering setTenantIds(Set<String> tenantIds) {
        return  (AlertFiltering)super.setTenantIds(tenantIds);
    }

    @Override
    public AlertFiltering setTenants(List<Tenant> tenants) {
        return (AlertFiltering) super.setTenants(tenants);
    }

    @Override
    public AlertFiltering setBaseclassNameLike(String baseclassNameLike) {
        return  (AlertFiltering)super.setBaseclassNameLike(baseclassNameLike);
    }

    public String getAlertType() {
        return alertType;
    }

    public AlertFiltering setAlertType(String alertType) {
        this.alertType = alertType;
        return this;
    }
}
