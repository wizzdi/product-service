package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.MultiLatLonEquipment;

public class CreateLatLon {

    private Double lat;
    private Double lon;
    private String multiLatLonEquipmentId;
    @JsonIgnore
    private MultiLatLonEquipment multiLatLonEquipment;

    public Double getLat() {
        return lat;
    }

    public <T extends CreateLatLon> T setLat(Double lat) {
        this.lat = lat;
        return (T) this;
    }

    public Double getLon() {
        return lon;
    }

    public <T extends CreateLatLon> T setLon(Double lon) {
        this.lon = lon;
        return (T) this;
    }

    public String getMultiLatLonEquipmentId() {
        return multiLatLonEquipmentId;
    }

    public <T extends CreateLatLon> T setMultiLatLonEquipmentId(String multiLatLonEquipmentId) {
        this.multiLatLonEquipmentId = multiLatLonEquipmentId;
        return (T) this;
    }

    @JsonIgnore
    public MultiLatLonEquipment getMultiLatLonEquipment() {
        return multiLatLonEquipment;
    }

    public <T extends CreateLatLon> T setMultiLatLonEquipment(MultiLatLonEquipment multiLatLonEquipment) {
        this.multiLatLonEquipment = multiLatLonEquipment;
        return (T) this;
    }
}
