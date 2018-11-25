package com.flexicore.product.iot.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.flexicore.data.jsoncontainers.CrossLoaderResolver;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,property = "type")
@JsonTypeIdResolver(CrossLoaderResolver.class)
public class FlexiCoreIOTResponse {
    private String requestId;
    private FlexiCoreIOTStatus flexiCoreIOTStatus;

    public FlexiCoreIOTStatus getFlexiCoreIOTStatus() {
        return flexiCoreIOTStatus;
    }

    public FlexiCoreIOTResponse setFlexiCoreIOTStatus(FlexiCoreIOTStatus flexiCoreIOTStatus) {
        this.flexiCoreIOTStatus = flexiCoreIOTStatus;
        return this;
    }

    public String getRequestId() {
        return requestId;
    }

    public FlexiCoreIOTResponse setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
}
