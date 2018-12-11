package com.flexicore.product.iot.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.FlexiCoreGateway;

public class OpenFlexiCoreGateway {
    private String id;
    @JsonIgnore
    private FlexiCoreGateway flexiCoreGateway;

    public String getId() {
        return id;
    }

    public OpenFlexiCoreGateway setId(String id) {
        this.id = id;
        return this;
    }

    @JsonIgnore
    public FlexiCoreGateway getFlexiCoreGateway() {
        return flexiCoreGateway;
    }

    public OpenFlexiCoreGateway setFlexiCoreGateway(FlexiCoreGateway flexiCoreGateway) {
        this.flexiCoreGateway = flexiCoreGateway;
        return this;
    }
}
