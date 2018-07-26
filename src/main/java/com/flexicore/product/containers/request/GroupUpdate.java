package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.EquipmentGroup;

public class GroupUpdate extends GroupCreate{

  private String id;
  @JsonIgnore
  private EquipmentGroup equipmentGroup;

    public String getId() {
        return id;
    }

    public GroupUpdate setId(String id) {
        this.id = id;
        return this;
    }
    @JsonIgnore
    public EquipmentGroup getEquipmentGroup() {
        return equipmentGroup;
    }

    public GroupUpdate setEquipmentGroup(EquipmentGroup equipmentGroup) {
        this.equipmentGroup = equipmentGroup;
        return this;
    }
}
