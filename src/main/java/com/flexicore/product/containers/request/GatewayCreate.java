package com.flexicore.product.containers.request;

import com.flexicore.interfaces.dynamic.FieldInfo;

public class GatewayCreate extends EquipmentCreate {

    @FieldInfo(mandatory = true,description = "ip used to connect to this gateway")

    private String ip;
    @FieldInfo(mandatory = true,description = "port used to connect to this gateway",defaultValue = "0")

    private Integer port;

    @FieldInfo(description = "username")
    private String username;
    @FieldInfo(description = "password")
    private String password;

    public String getIp() {
        return ip;
    }

    public GatewayCreate setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public GatewayCreate setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public GatewayCreate setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public GatewayCreate setPassword(String password) {
        this.password = password;
        return this;
    }
}
