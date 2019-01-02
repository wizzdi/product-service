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
    private String externalId;


    public Double getLat() {
        return lat;
    }

    public <T extends EquipmentCreate> T setLat(Double lat) {
        this.lat = lat;
        return (T) this;
    }

    public Double getLon() {
        return lon;
    }

    public <T extends EquipmentCreate> T setLon(Double lon) {
        this.lon = lon;
        return (T) this;
    }

    public LocalDateTime getWarrantyExpiration() {
        return warrantyExpiration;
    }

    public <T extends EquipmentCreate> T setWarrantyExpiration(LocalDateTime warrantyExpiration) {
        this.warrantyExpiration = warrantyExpiration;
        return (T) this;
    }

    public Boolean getEnable() {
        return enable;
    }

    public <T extends EquipmentCreate> T setEnable(Boolean enable) {
        this.enable = enable;
        return (T) this;
    }

    public String getCommunicationGatewayId() {
        return communicationGatewayId;
    }

    public <T extends EquipmentCreate> T setCommunicationGatewayId(String communicationGatewayId) {
        this.communicationGatewayId = communicationGatewayId;
        return (T) this;
    }

    @JsonIgnore
    public Gateway getGateway() {
        return gateway;
    }

    public <T extends EquipmentCreate> T setGateway(Gateway gateway) {
        this.gateway = gateway;
        return (T) this;
    }

    public String getSerial() {
        return serial;
    }

    public <T extends EquipmentCreate> T setSerial(String serial) {
        this.serial = serial;
        return (T) this;
    }

    public String getExternalId() {
        return externalId;
    }

    public <T extends EquipmentCreate> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }
}
