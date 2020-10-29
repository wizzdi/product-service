package com.flexicore.product.response;

import com.flexicore.building.model.BuildingFloor;
import com.flexicore.building.model.Room;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentLocation;

public class EquipmentLocationContainer  {

    private EquipmentLocation equipmentLocation;
    private Equipment equipment;
    private Room room;
    private BuildingFloor buildingFloor;

    public EquipmentLocation getEquipmentLocation() {
        return equipmentLocation;
    }

    public <T extends EquipmentLocationContainer> T setEquipmentLocation(EquipmentLocation equipmentLocation) {
        this.equipmentLocation = equipmentLocation;
        return (T) this;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public <T extends EquipmentLocationContainer> T setEquipment(Equipment equipment) {
        this.equipment = equipment;
        return (T) this;
    }

    public Room getRoom() {
        return room;
    }

    public <T extends EquipmentLocationContainer> T setRoom(Room room) {
        this.room = room;
        return (T) this;
    }

    public BuildingFloor getBuildingFloor() {
        return buildingFloor;
    }

    public <T extends EquipmentLocationContainer> T setBuildingFloor(BuildingFloor buildingFloor) {
        this.buildingFloor = buildingFloor;
        return (T) this;
    }
}
