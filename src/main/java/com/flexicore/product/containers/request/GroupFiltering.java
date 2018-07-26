package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.product.model.ProductStatus;
import com.flexicore.product.model.ProductType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupFiltering extends FilteringInformationHolder {

    private Set<String> groupIds =new HashSet<>();
    @JsonIgnore
    private List<EquipmentGroup> equipmentGroups=new ArrayList<>();


    public Set<String> getGroupIds() {
        return groupIds;
    }

    public GroupFiltering setGroupIds(Set<String> groupIds) {
        this.groupIds = groupIds;
        return this;
    }
    @JsonIgnore
    public List<EquipmentGroup> getEquipmentGroups() {
        return equipmentGroups;
    }

    public GroupFiltering setEquipmentGroups(List<EquipmentGroup> equipmentGroups) {
        this.equipmentGroups = equipmentGroups;
        return this;
    }
}
