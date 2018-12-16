package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.interfaces.dynamic.IdRefFieldInfo;
import com.flexicore.model.FlexiCoreServer;

public class FlexiCoreGatewayCreate extends GatewayCreate {


    @IdRefFieldInfo(displayName = "flexicore server", refType = FlexiCoreServer.class,list = false)
    private String flexicoreServerId;

    @JsonIgnore
    private FlexiCoreServer flexiCoreServer;



    public String getFlexicoreServerId() {
        return flexicoreServerId;
    }

    public FlexiCoreGatewayCreate setFlexicoreServerId(String flexicoreServerId) {
        this.flexicoreServerId = flexicoreServerId;
        return this;
    }

    @JsonIgnore
    public FlexiCoreServer getFlexiCoreServer() {
        return flexiCoreServer;
    }

    public FlexiCoreGatewayCreate setFlexiCoreServer(FlexiCoreServer flexiCoreServer) {
        this.flexiCoreServer = flexiCoreServer;
        return this;
    }
}
