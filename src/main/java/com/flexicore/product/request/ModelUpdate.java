package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.Model;

public class ModelUpdate extends ModelCreate{
    private String id;
    @JsonIgnore
    private Model model;

    public String getId() {
        return id;
    }

    public ModelUpdate setId(String id) {
        this.id = id;
        return this;
    }

    @JsonIgnore
    public Model getModel() {
        return model;
    }

    public ModelUpdate setModel(Model model) {
        this.model = model;
        return this;
    }
}
