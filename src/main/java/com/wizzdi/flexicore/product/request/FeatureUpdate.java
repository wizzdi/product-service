package com.wizzdi.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.product.model.Feature;


public class FeatureUpdate extends FeatureCreate{

    private String id;
    @JsonIgnore
    private Feature feature;


    public String getId() {
        return id;
    }

    public <T extends FeatureUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public Feature getFeature() {
        return feature;
    }

    public <T extends FeatureUpdate> T setFeature(Feature feature) {
        this.feature = feature;
        return (T) this;
    }
}
