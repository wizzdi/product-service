package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.*;
import com.flexicore.product.model.Alert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlertFiltering extends EventFiltering {


    private Integer severityStart;
    private Integer severityEnd;

    public AlertFiltering() {
        super();
        setEventType(Alert.class.getCanonicalName());
    }

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
    public FilteringInformationHolder setTenantIds(List<TenantIdFiltering> tenantIds) {
        return super.setTenantIds(tenantIds);
    }

    @Override
    public AlertFiltering setBaseclassNameLike(String baseclassNameLike) {
        return  (AlertFiltering)super.setBaseclassNameLike(baseclassNameLike);
    }

}
