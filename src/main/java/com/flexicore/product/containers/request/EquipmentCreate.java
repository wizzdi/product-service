package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.interfaces.dynamic.IdRefFieldInfo;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.product.model.Gateway;
import com.flexicore.product.model.ProductType;

import java.time.LocalDateTime;

public class EquipmentCreate {

    @FieldInfo(mandatory = true,description = "gateway name")

    private String name;
    @FieldInfo(description = "gateway description")

    private String description;
    @FieldInfo(displayName = "latitude",description = "gateway latitude")

    private Double lat;
    @FieldInfo(displayName = "longitude",description = "gateway longitude")

    private Double lon;
    @FieldInfo(description = "serial number")

    private String serial;
    @FieldInfo(description = "warranty Expiration")

    private LocalDateTime warrantyExpiration;
    @IdRefFieldInfo(description = "product type",displayName = "Product Type",refType = ProductType.class,list = false)

    private String productTypeId;
    @JsonIgnore
    private ProductType ProductType;


    private String clazzName;
    private Boolean enable;
    @JsonIgnore
    private Class<?> clazz;

    @IdRefFieldInfo(description = "Communication Gateway used to connect to equipment",displayName = "Communication Gateway",refType = Gateway.class,list = false)

    private String communicationGatewayId;
    @JsonIgnore
    private Gateway gateway;


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

    public String getCommunicationGatewayId() {
        return communicationGatewayId;
    }

    public EquipmentCreate setCommunicationGatewayId(String communicationGatewayId) {
        this.communicationGatewayId = communicationGatewayId;
        return this;
    }

    @JsonIgnore
    public Gateway getGateway() {
        return gateway;
    }

    public EquipmentCreate setGateway(Gateway gateway) {
        this.gateway = gateway;
        return this;
    }

    public Boolean getEnable() {
        return enable;
    }

    public EquipmentCreate setEnable(Boolean enable) {
        this.enable = enable;
        return this;
    }
}
