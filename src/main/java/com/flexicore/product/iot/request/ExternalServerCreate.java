package com.flexicore.product.iot.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.product.containers.request.EquipmentCreate;

import java.time.LocalDateTime;

public class ExternalServerCreate extends EquipmentCreate {
    @FieldInfo
    private String url;

    @FieldInfo
    private Long inspectIntervalMs;
    @JsonIgnore
    private LocalDateTime lastInspectAttempt;
    @JsonIgnore
    private LocalDateTime lastSuccessfulInspect;

    public String getUrl() {
        return url;
    }

    public <T extends ExternalServerCreate> T setUrl(String url) {
        this.url = url;
        return (T) this;
    }

    public Long getInspectIntervalMs() {
        return inspectIntervalMs;
    }

    public <T extends ExternalServerCreate> T setInspectIntervalMs(Long inspectIntervalMs) {
        this.inspectIntervalMs = inspectIntervalMs;
        return (T) this;
    }

    @JsonIgnore
    public LocalDateTime getLastInspectAttempt() {
        return lastInspectAttempt;
    }

    public <T extends ExternalServerCreate> T setLastInspectAttempt(LocalDateTime lastInspectAttempt) {
        this.lastInspectAttempt = lastInspectAttempt;
        return (T) this;
    }

    @JsonIgnore
    public LocalDateTime getLastSuccessfulInspect() {
        return lastSuccessfulInspect;
    }

    public <T extends ExternalServerCreate> T setLastSuccessfulInspect(LocalDateTime lastSuccessfulInspect) {
        this.lastSuccessfulInspect = lastSuccessfulInspect;
        return (T) this;
    }
}
