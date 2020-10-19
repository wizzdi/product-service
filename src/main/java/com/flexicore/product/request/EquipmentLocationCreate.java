package com.flexicore.product.request;

import com.flexicore.request.BaseclassNoSQLCreate;

import java.time.OffsetDateTime;

public class EquipmentLocationCreate extends BaseclassNoSQLCreate {
    private OffsetDateTime dateAtLocation;
    private Double lat;
    private Double lon;
    private Double x;
    private Double y;
    private String buildingFloorId;
    private String equipmentId;
    private String roomId;

    public OffsetDateTime getDateAtLocation() {
        return dateAtLocation;
    }

    public <T extends EquipmentLocationCreate> T setDateAtLocation(OffsetDateTime dateAtLocation) {
        this.dateAtLocation = dateAtLocation;
        return (T) this;
    }

    public Double getLat() {
        return lat;
    }

    public <T extends EquipmentLocationCreate> T setLat(Double lat) {
        this.lat = lat;
        return (T) this;
    }

    public Double getLon() {
        return lon;
    }

    public <T extends EquipmentLocationCreate> T setLon(Double lon) {
        this.lon = lon;
        return (T) this;
    }

    public Double getX() {
        return x;
    }

    public <T extends EquipmentLocationCreate> T setX(Double x) {
        this.x = x;
        return (T) this;
    }

    public Double getY() {
        return y;
    }

    public <T extends EquipmentLocationCreate> T setY(Double y) {
        this.y = y;
        return (T) this;
    }

    public String getBuildingFloorId() {
        return buildingFloorId;
    }

    public <T extends EquipmentLocationCreate> T setBuildingFloorId(String buildingFloorId) {
        this.buildingFloorId = buildingFloorId;
        return (T) this;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public <T extends EquipmentLocationCreate> T setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
        return (T) this;
    }

    public String getRoomId() {
        return roomId;
    }

    public <T extends EquipmentLocationCreate> T setRoomId(String roomId) {
        this.roomId = roomId;
        return (T) this;
    }
}
