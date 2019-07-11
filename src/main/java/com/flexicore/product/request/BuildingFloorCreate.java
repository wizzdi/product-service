package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FileResource;
import com.flexicore.product.model.Building;

public class BuildingFloorCreate {

    private String name;
    private String description;
    private Integer floorNumber;
    private String diagramId;
    @JsonIgnore
    private FileResource diagram;
    private String buildingId;
    @JsonIgnore
    private Building building;
    private String diagram3DId;
    @JsonIgnore
    private FileResource diagram3D;

    public String getName() {
        return name;
    }

    public BuildingFloorCreate setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public BuildingFloorCreate setDescription(String description) {
        this.description = description;
        return this;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public BuildingFloorCreate setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
        return this;
    }

    public String getDiagramId() {
        return diagramId;
    }

    public BuildingFloorCreate setDiagramId(String diagramId) {
        this.diagramId = diagramId;
        return this;
    }

    @JsonIgnore
    public FileResource getDiagram() {
        return diagram;
    }

    public BuildingFloorCreate setDiagram(FileResource diagram) {
        this.diagram = diagram;
        return this;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public BuildingFloorCreate setBuildingId(String buildingId) {
        this.buildingId = buildingId;
        return this;
    }

    @JsonIgnore
    public Building getBuilding() {
        return building;
    }

    public BuildingFloorCreate setBuilding(Building building) {
        this.building = building;
        return this;
    }

    public String getDiagram3DId() {
        return diagram3DId;
    }

    public <T extends BuildingFloorCreate> T setDiagram3DId(String diagram3DId) {
        this.diagram3DId = diagram3DId;
        return (T) this;
    }

    @JsonIgnore
    public FileResource getDiagram3D() {
        return diagram3D;
    }

    public <T extends BuildingFloorCreate> T setDiagram3D(FileResource diagram3D) {
        this.diagram3D = diagram3D;
        return (T) this;
    }
}
