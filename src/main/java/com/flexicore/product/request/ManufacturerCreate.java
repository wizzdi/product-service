package com.flexicore.product.request;

public class ManufacturerCreate {
    private String name;
    private String description;


    public String getName() {
        return name;
    }

    public ManufacturerCreate setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ManufacturerCreate setDescription(String description) {
        this.description = description;
        return this;
    }


}
