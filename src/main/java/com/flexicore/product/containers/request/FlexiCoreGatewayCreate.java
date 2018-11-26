package com.flexicore.product.containers.request;

import com.flexicore.interfaces.dynamic.FieldInfo;

public class FlexiCoreGatewayCreate extends GatewayCreate {

    @FieldInfo(mandatory = true,description = "web socket url used to connect to this FlexiCore gateway")
    private String webSocketUrl;

    public String getWebSocketUrl() {
        return webSocketUrl;
    }

    public FlexiCoreGatewayCreate setWebSocketUrl(String webSocketUrl) {
        this.webSocketUrl = webSocketUrl;
        return this;
    }
}
