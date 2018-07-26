package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import com.flexicore.product.model.EquipmentGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupCreate {

    private String name;
    private String description;
    private String parentId;
    @JsonIgnore
    private EquipmentGroup parent;


    public String getName() {
        return name;
    }

    public GroupCreate setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public GroupCreate setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getParentId() {
        return parentId;
    }

    public GroupCreate setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    @JsonIgnore
    public EquipmentGroup getParent() {
        return parent;
    }

    public GroupCreate setParent(EquipmentGroup parent) {
        this.parent = parent;
        return this;
    }
}
