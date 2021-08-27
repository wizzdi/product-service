package com.wizzdi.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.product.model.Model;


public class ModelUpdate extends ModelCreate{

    private String id;
    @JsonIgnore
    private Model model;


    public String getId() {
        return id;
    }

    public <T extends ModelUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public Model getModel() {
        return model;
    }

    public <T extends ModelUpdate> T setModel(Model model) {
        this.model = model;
        return (T) this;
    }
}
