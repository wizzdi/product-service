package com.flexicore.product.model;

import java.util.List;

public class EquipmentByStatusEvent extends Event {


    private List<EquipmentByStatusEntry> entries;



    public List<EquipmentByStatusEntry> getEntries() {
        return entries;
    }

    public EquipmentByStatusEvent setEntries(List<EquipmentByStatusEntry> entries) {
        this.entries = entries;
        return this;
    }
}
