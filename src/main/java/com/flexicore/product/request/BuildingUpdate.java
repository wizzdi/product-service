package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.Building;

public class BuildingUpdate extends BuildingCreate{

    private String id;
    @JsonIgnore
    private Building building;

    public String getId() {
        return id;
    }

    public BuildingUpdate setId(String id) {
        this.id = id;
        return this;
    }

    @JsonIgnore
    public Building getBuilding() {
        return building;
    }

    public BuildingUpdate setBuilding(Building building) {
        this.building = building;
        return this;
    }
}
