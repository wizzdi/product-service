package com.flexicore.product.containers.request;

import com.flexicore.interfaces.dynamic.FieldInfo;

public class GatewayCreate extends EquipmentCreate {

    @FieldInfo(mandatory = true,description = "ip used to connect to this gateway")

    private String ip;
    @FieldInfo(mandatory = true,description = "port used to connect to this gateway")

    private int port;

    public String getIp() {
        return ip;
    }

    public GatewayCreate setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public int getPort() {
        return port;
    }

    public GatewayCreate setPort(int port) {
        this.port = port;
        return this;
    }
}
