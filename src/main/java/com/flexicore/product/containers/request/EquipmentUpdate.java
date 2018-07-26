package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentGroup;

import java.time.LocalDateTime;

public class EquipmentUpdate  extends EquipmentCreate{

    private String id;
    @JsonIgnore
    private Equipment equipment;

    public String getId() {
        return id;
    }

    public EquipmentUpdate setId(String id) {
        this.id = id;
        return this;
    }

    @JsonIgnore
    public Equipment getEquipment() {
        return equipment;
    }

    public EquipmentUpdate setEquipment(Equipment equipment) {
        this.equipment = equipment;
        return this;
    }
}
