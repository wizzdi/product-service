package com.flexicore.product.iot.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.flexicore.data.jsoncontainers.CrossLoaderResolver;
import com.flexicore.security.SecurityContext;

import javax.websocket.Session;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,property = "type")
@JsonTypeIdResolver(CrossLoaderResolver.class)
public class FlexiCoreIOTRequest {

    private String id;
    private String authKey;
    @JsonIgnore
    private Session sessionReceivedFrom;
    @JsonIgnore
    private SecurityContext securityContext;

    public String getId() {
        return id;
    }

    public FlexiCoreIOTRequest setId(String id) {
        this.id = id;
        return this;
    }

    public String getAuthKey() {
        return authKey;
    }

    public FlexiCoreIOTRequest setAuthKey(String authKey) {
        this.authKey = authKey;
        return this;
    }
    @JsonIgnore

    public Session getSessionReceivedFrom() {
        return sessionReceivedFrom;
    }

    public FlexiCoreIOTRequest setSessionReceivedFrom(Session sessionReceivedFrom) {
        this.sessionReceivedFrom = sessionReceivedFrom;
        return this;
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    public FlexiCoreIOTRequest setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
        return this;
    }
}
