package com.flexicore.product.containers.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.ProductStatus;

import java.util.List;
import java.util.stream.Collectors;

public class EquipmentShort {

    private String id;
    private double lon;
    private double lat;
    private String type;
    private List<ProductStatus> currentStatus;

    public EquipmentShort(Equipment other) {
        this.id = other.getId();
        this.lon = other.getLon();
        this.lat = other.getLat();
        this.type=other.getJsonType();
        currentStatus=other.getProductToStatusList().stream().filter(f->f.isEnabled()).map(f->f.getRightside()).collect(Collectors.toList());
    }

    public String getId() {
        return id;
    }

    public EquipmentShort setId(String id) {
        this.id = id;
        return this;
    }


    public double getLon() {
        return lon;
    }

    public EquipmentShort setLon(double lon) {
        this.lon = lon;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public EquipmentShort setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public String getType() {
        return type;
    }

    public EquipmentShort setType(String type) {
        this.type = type;
        return this;
    }

    public List<ProductStatus> getCurrentStatus() {
        return currentStatus;
    }

    public EquipmentShort setCurrentStatus(List<ProductStatus> currentStatus) {
        this.currentStatus = currentStatus;
        return this;
    }

    @JsonProperty("json-type")
    public String getJsonType(){
        return type;

    }
}



