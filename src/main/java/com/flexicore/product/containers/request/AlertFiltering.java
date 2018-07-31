package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Clazz;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlertFiltering extends FilteringInformationHolder {


    private LocalDateTime alertDateStart;
    private LocalDateTime alertDateEnd;

    private Integer severityStart;
    private Integer severityEnd;
    private String humanReadableTextLike;

    private String alertType;
    private Set<String> baseclassIds=new HashSet<>();
    @JsonIgnore
    private List<Baseclass> baseclass;
    private String clazzName;
    @JsonIgnore
    private Clazz clazz;


    public LocalDateTime getAlertDateStart() {
        return alertDateStart;
    }

    public AlertFiltering setAlertDateStart(LocalDateTime alertDateStart) {
        this.alertDateStart = alertDateStart;
        return this;
    }

    public LocalDateTime getAlertDateEnd() {
        return alertDateEnd;
    }

    public AlertFiltering setAlertDateEnd(LocalDateTime alertDateEnd) {
        this.alertDateEnd = alertDateEnd;
        return this;
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

    public String getHumanReadableTextLike() {
        return humanReadableTextLike;
    }

    public AlertFiltering setHumanReadableTextLike(String humanReadableTextLike) {
        this.humanReadableTextLike = humanReadableTextLike;
        return this;
    }

    public String getAlertType() {
        return alertType;
    }

    public AlertFiltering setAlertType(String alertType) {
        this.alertType = alertType;
        return this;
    }


    public Set<String> getBaseclassIds() {
        return baseclassIds;
    }

    public AlertFiltering setBaseclassIds(Set<String> baseclassIds) {
        this.baseclassIds = baseclassIds;
        return this;
    }

    @JsonIgnore
    public List<Baseclass> getBaseclass() {
        return baseclass;
    }

    public AlertFiltering setBaseclass(List<Baseclass> baseclass) {
        this.baseclass = baseclass;
        return this;
    }

    public String getClazzName() {
        return clazzName;
    }

    public AlertFiltering setClazzName(String clazzName) {
        this.clazzName = clazzName;
        return this;
    }
    @JsonIgnore
    public Clazz getClazz() {
        return clazz;
    }

    public AlertFiltering setClazz(Clazz clazz) {
        this.clazz = clazz;
        return this;
    }
}
