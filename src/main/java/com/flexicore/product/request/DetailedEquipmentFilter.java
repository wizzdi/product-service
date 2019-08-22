package com.flexicore.product.request;

import com.flexicore.model.FilteringInformationHolder;

import java.util.Set;

public class DetailedEquipmentFilter extends FilteringInformationHolder {

    private Set<String> equipmentByStatusEntryIds;

    public Set<String> getEquipmentByStatusEntryIds() {
        return equipmentByStatusEntryIds;
    }

    public <T extends DetailedEquipmentFilter> T setEquipmentByStatusEntryIds(Set<String> equipmentByStatusEntryIds) {
        this.equipmentByStatusEntryIds = equipmentByStatusEntryIds;
        return (T) this;
    }
}
