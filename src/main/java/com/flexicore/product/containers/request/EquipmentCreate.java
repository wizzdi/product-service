package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.product.model.ProductType;

import java.time.LocalDateTime;

public class EquipmentCreate {

    private String name;
    private String description;
    private Double lat;
    private Double lon;
    private String serial;
    private LocalDateTime warrantyExpiration;
    private String productTypeId;
    @JsonIgnore
    private ProductType ProductType;

    private String clazzName;
    @JsonIgnore
    private Class<?> clazz;


    public String getName() {
        return name;
    }

    public EquipmentCreate setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public EquipmentCreate setDescription(String description) {
        this.description = description;
        return this;
    }





    public Double getLat() {
        return lat;
    }

    public EquipmentCreate setLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public Double getLon() {
        return lon;
    }

    public EquipmentCreate setLon(Double lon) {
        this.lon = lon;
        return this;
    }

    public String getSerial() {
        return serial;
    }

    public EquipmentCreate setSerial(String serial) {
        this.serial = serial;
        return this;
    }

    public LocalDateTime getWarrantyExpiration() {
        return warrantyExpiration;
    }

    public EquipmentCreate setWarrantyExpiration(LocalDateTime warrantyExpiration) {
        this.warrantyExpiration = warrantyExpiration;
        return this;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public EquipmentCreate setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
        return this;
    }

    @JsonIgnore
    public ProductType getProductType() {
        return ProductType;
    }

    public EquipmentCreate setProductType(ProductType productType) {
        ProductType = productType;
        return this;
    }

    public String getClazzName() {
        return clazzName;
    }

    public EquipmentCreate setClazzName(String clazzName) {
        this.clazzName = clazzName;
        return this;
    }

    @JsonIgnore
    public Class<?> getClazz() {
        return clazz;
    }


    public EquipmentCreate setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }
}
