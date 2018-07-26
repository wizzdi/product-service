package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.EquipmentGroup;

public class ProductTypeCreate {

    private String name;
    private String description;



    public String getName() {
        return name;
    }

    public ProductTypeCreate setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ProductTypeCreate setDescription(String description) {
        this.description = description;
        return this;
    }




}
