package com.flexicore.product.iot.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.interfaces.dynamic.IdRefFieldInfo;
import com.flexicore.iot.ExternalServer;

public class ExternalServerUserCreate {
    @FieldInfo
    private String name;
    @FieldInfo
    private String description;
    @FieldInfo
    private String username;
    @FieldInfo
    private String password;
    @IdRefFieldInfo(refType = ExternalServer.class,list = false,mandatory = true)
    private String externalServerId;
    @JsonIgnore
    private ExternalServer externalServer;

    public String getName() {
        return name;
    }

    public <T extends ExternalServerUserCreate> T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public String getDescription() {
        return description;
    }

    public <T extends ExternalServerUserCreate> T setDescription(String description) {
        this.description = description;
        return (T) this;
    }

    public String getUsername() {
        return username;
    }

    public <T extends ExternalServerUserCreate> T setUsername(String username) {
        this.username = username;
        return (T) this;
    }

    public String getPassword() {
        return password;
    }

    public <T extends ExternalServerUserCreate> T setPassword(String password) {
        this.password = password;
        return (T) this;
    }

    public String getExternalServerId() {
        return externalServerId;
    }

    public <T extends ExternalServerUserCreate> T setExternalServerId(String externalServerId) {
        this.externalServerId = externalServerId;
        return (T) this;
    }

    @JsonIgnore
    public ExternalServer getExternalServer() {
        return externalServer;
    }

    public <T extends ExternalServerUserCreate> T setExternalServer(ExternalServer externalServer) {
        this.externalServer = externalServer;
        return (T) this;
    }
}
