package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.interfaces.dynamic.IdRefFieldInfo;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.product.model.Gateway;
import com.flexicore.product.model.ProductType;

import java.time.LocalDateTime;

public class EquipmentCreate extends ProductCreate{


    @FieldInfo(displayName = "latitude",description = "gateway latitude")

    private Double lat;
    @FieldInfo(displayName = "longitude",description = "gateway longitude")

    private Double lon;

    @FieldInfo(description = "warranty Expiration")

    private LocalDateTime warrantyExpiration;

    private Boolean enable;

    @IdRefFieldInfo(description = "Communication Gateway used to connect to equipment",displayName = "Communication Gateway",refType = Gateway.class,list = false)

    private String communicationGatewayId;
    @JsonIgnore
    private Gateway gateway;

    @FieldInfo(description = "serial")

    private String serial;




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



    public LocalDateTime getWarrantyExpiration() {
        return warrantyExpiration;
    }

    public EquipmentCreate setWarrantyExpiration(LocalDateTime warrantyExpiration) {
        this.warrantyExpiration = warrantyExpiration;
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

    @Override
    public EquipmentCreate setName(String name) {
        return (EquipmentCreate)super.setName(name);
    }

    @Override
    public EquipmentCreate setDescription(String description) {
        return (EquipmentCreate)super.setDescription(description);
    }

    @Override
    public EquipmentCreate setSku(String sku) {
        return (EquipmentCreate)super.setSku(sku);
    }

    @Override
    public EquipmentCreate setProductTypeId(String productTypeId) {
        return (EquipmentCreate)super.setProductTypeId(productTypeId);
    }

    @Override
    public EquipmentCreate setProductType(com.flexicore.product.model.ProductType productType) {
        return (EquipmentCreate)super.setProductType(productType);
    }

    @Override
    public EquipmentCreate setClazzName(String clazzName) {
        return (EquipmentCreate)super.setClazzName(clazzName);
    }

    @Override
    public EquipmentCreate setClazz(Class<?> clazz) {
        return (EquipmentCreate) super.setClazz(clazz);
    }

    public String getSerial() {
        return serial;
    }

    public EquipmentCreate setSerial(String serial) {
        this.serial = serial;
        return this;
    }
}
