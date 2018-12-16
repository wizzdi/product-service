package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.interfaces.dynamic.IdRefFieldInfo;
import com.flexicore.model.FlexiCoreServer;
import com.flexicore.product.model.FlexiCoreGateway;

public class FlexiCoreGatewayUpdate extends FlexiCoreGatewayCreate {



    @IdRefFieldInfo(refType = FlexiCoreGateway.class,mandatory = true,displayName = "flxicore gateway",list = false)
    private String id;
    @JsonIgnore
    private FlexiCoreGateway flexiCoreGateway;

    public String getId() {
        return id;
    }

    public FlexiCoreGatewayUpdate setId(String id) {
        this.id = id;
        return this;
    }

    @JsonIgnore
    public FlexiCoreGateway getFlexiCoreGateway() {
        return flexiCoreGateway;
    }

    public FlexiCoreGatewayUpdate setFlexiCoreGateway(FlexiCoreGateway flexiCoreGateway) {
        this.flexiCoreGateway = flexiCoreGateway;
        return this;
    }
}
