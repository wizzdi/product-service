package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.EquipmentGroup;

import java.time.LocalDateTime;

public class EquipmentCreate {

    private String name;
    private String description;
    private Double lat;
    private Double lon;
    private String serial;
    private LocalDateTime warrantyExpiration;


    public String getName() {
        return name;
    }

    public EquipmentCreate setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public EquipmentCreate setDescription(String description) {
        this.description = description;
        return this;
    }





    public Double getLat() {
        return lat;
    }

    public EquipmentCreate setLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public Double getLon() {
        return lon;
    }

    public EquipmentCreate setLon(Double lon) {
        this.lon = lon;
        return this;
    }

    public String getSerial() {
        return serial;
    }

    public EquipmentCreate setSerial(String serial) {
        this.serial = serial;
        return this;
    }

    public LocalDateTime getWarrantyExpiration() {
        return warrantyExpiration;
    }

    public EquipmentCreate setWarrantyExpiration(LocalDateTime warrantyExpiration) {
        this.warrantyExpiration = warrantyExpiration;
        return this;
    }
}
