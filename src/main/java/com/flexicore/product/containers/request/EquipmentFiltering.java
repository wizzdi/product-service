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

public class EquipmentFiltering extends FilteringInformationHolder {

    private Set<String> groupIds =new HashSet<>();
    @JsonIgnore
    private List<EquipmentGroup> equipmentGroups=new ArrayList<>();

    private LocationArea locationArea;
    private String productTypeId;
    @JsonIgnore
    private ProductType productType;
    private Set<String> productStatusIds=new HashSet<>();
    @JsonIgnore
    private List<ProductStatus> productStatusList=new ArrayList<>();

    private String canonicalClassName;



    public Set<String> getGroupIds() {
        return groupIds;
    }

    public EquipmentFiltering setGroupIds(Set<String> groupIds) {
        this.groupIds = groupIds;
        return this;
    }


    public LocationArea getLocationArea() {
        return locationArea;
    }

    public EquipmentFiltering setLocationArea(LocationArea locationArea) {
        this.locationArea = locationArea;
        return this;
    }

    @JsonIgnore
    public List<EquipmentGroup> getEquipmentGroups() {
        return equipmentGroups;
    }

    public EquipmentFiltering setEquipmentGroups(List<EquipmentGroup> equipmentGroups) {
        this.equipmentGroups = equipmentGroups;
        return this;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public EquipmentFiltering setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
        return this;
    }

    @JsonIgnore
    public ProductType getProductType() {
        return productType;
    }

    public EquipmentFiltering setProductType(ProductType productType) {
        this.productType = productType;
        return this;
    }

    public Set<String> getProductStatusIds() {
        return productStatusIds;
    }

    public EquipmentFiltering setProductStatusIds(Set<String> productStatusIds) {
        this.productStatusIds = productStatusIds;
        return this;
    }

    @JsonIgnore
    public List<ProductStatus> getProductStatusList() {
        return productStatusList;
    }

    public EquipmentFiltering setProductStatusList(List<ProductStatus> productStatusList) {
        this.productStatusList = productStatusList;
        return this;
    }

    public String getCanonicalClassName() {
        return canonicalClassName;
    }

    public EquipmentFiltering setCanonicalClassName(String canonicalClassName) {
        this.canonicalClassName = canonicalClassName;
        return this;
    }

}
