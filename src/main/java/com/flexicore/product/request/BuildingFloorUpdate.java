package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.BuildingFloor;

public class BuildingFloorUpdate extends BuildingFloorCreate{
    private String id;
    @JsonIgnore
    private BuildingFloor buildingFloor;

    public String getId() {
        return id;
    }

    public BuildingFloorUpdate setId(String id) {
        this.id = id;
        return this;
    }

    @JsonIgnore
    public BuildingFloor getBuildingFloor() {
        return buildingFloor;
    }

    public BuildingFloorUpdate setBuildingFloor(BuildingFloor buildingFloor) {
        this.buildingFloor = buildingFloor;
        return this;
    }
}
