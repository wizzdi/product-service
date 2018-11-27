package com.flexicore.product.iot.request;

import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.model.dynamic.ExecutionParametersHolder;
import com.flexicore.product.containers.request.FlexiCoreGatewayCreate;

public class FlexiCoreGatewayCreateParameters extends ExecutionParametersHolder {

    @FieldInfo(mandatory = true,displayName = "Creation Container")
    private FlexiCoreGatewayCreate flexiCoreGatewayCreate;

    public FlexiCoreGatewayCreate getFlexiCoreGatewayCreate() {
        return flexiCoreGatewayCreate;
    }

    public FlexiCoreGatewayCreateParameters setFlexiCoreGatewayCreate(FlexiCoreGatewayCreate flexiCoreGatewayCreate) {
        this.flexiCoreGatewayCreate = flexiCoreGatewayCreate;
        return this;
    }
}
