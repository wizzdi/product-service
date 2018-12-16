package com.flexicore.product.containers.request;

import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.model.dynamic.ExecutionParametersHolder;

public class FlexiCoreGatewayUpdateParameters  extends ExecutionParametersHolder {
    @FieldInfo(mandatory = true,displayName = "Update Container")
    private FlexiCoreGatewayUpdate flexiCoreGatewayUpdate;

    public FlexiCoreGatewayUpdate getFlexiCoreGatewayUpdate() {
        return flexiCoreGatewayUpdate;
    }

    public FlexiCoreGatewayUpdateParameters setFlexiCoreGatewayUpdate(FlexiCoreGatewayUpdate flexiCoreGatewayUpdate) {
        this.flexiCoreGatewayUpdate = flexiCoreGatewayUpdate;
        return this;
    }
}
