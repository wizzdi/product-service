package com.flexicore.product.messages;

import com.flexicore.product.interfaces.IEvent;
import com.flexicore.product.model.Equipment;

public class EquipmentMovedEvent implements IEvent {

    private MoveState accelerometerState;
    private Equipment equipment;


    public MoveState getAccelerometerState() {
        return accelerometerState;
    }

    public <T extends EquipmentMovedEvent> T setAccelerometerState(MoveState accelerometerState) {
        this.accelerometerState = accelerometerState;
        return (T) this;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public <T extends EquipmentMovedEvent> T setEquipment(Equipment equipment) {
        this.equipment = equipment;
        return (T) this;
    }
}
