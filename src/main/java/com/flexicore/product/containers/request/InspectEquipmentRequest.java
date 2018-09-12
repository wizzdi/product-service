package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.Equipment;

import java.util.List;
import java.util.Set;

public class InspectEquipmentRequest {


    private Set<String> equipmentIds;
    @JsonIgnore
    private List<Equipment> equipments;
    private int maxThreads;


    public Set<String> getEquipmentIds() {
        return equipmentIds;
    }

    public InspectEquipmentRequest setEquipmentIds(Set<String> equipmentIds) {
        this.equipmentIds = equipmentIds;
        return this;
    }

    @JsonIgnore
    public List<Equipment> getEquipments() {
        return equipments;
    }

    public InspectEquipmentRequest setEquipments(List<Equipment> equipments) {
        this.equipments = equipments;
        return this;
    }

    public InspectEquipmentRequest setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        return this;
    }

    public int getMaxThreads() {
        return maxThreads;
    }
}
