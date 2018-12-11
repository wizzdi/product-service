package com.flexicore.product.iot.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.flexicore.data.jsoncontainers.CrossLoaderResolver;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,property = "type")
@JsonTypeIdResolver(CrossLoaderResolver.class)
public class FlexiCoreIOTRequest {

    private String id;
    private String authKey;

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
}
