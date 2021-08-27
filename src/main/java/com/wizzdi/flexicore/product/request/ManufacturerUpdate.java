package com.wizzdi.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.product.model.Manufacturer;


public class ManufacturerUpdate extends ManufacturerCreate{

    private String id;
    @JsonIgnore
    private Manufacturer manufacturer;


    public String getId() {
        return id;
    }

    public <T extends ManufacturerUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public <T extends ManufacturerUpdate> T setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
        return (T) this;
    }
}
