package com.flexicore.product.messages;

import com.flexicore.product.interfaces.IEvent;
import com.flexicore.product.model.EquipmentLocation;

public class EquipmentLocationChanged implements IEvent {

    private final EquipmentLocation equipmentLocation;

    public EquipmentLocationChanged(EquipmentLocation equipmentLocation) {
        this.equipmentLocation = equipmentLocation;
    }

    public EquipmentLocation getEquipmentLocation() {
        return equipmentLocation;
    }

}
