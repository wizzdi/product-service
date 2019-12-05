package com.flexicore.product.model;

import com.flexicore.service.BaseclassService;
import com.flexicore.utils.InheritanceUtils;

import java.util.Date;
import java.util.Set;

public class Alert extends Event{

    static{
        InheritanceUtils.registerInheretingClass(Alert.class);
    }



    private int severity;
    private String externalGatewayId;
    private String gatewayName;
    private String streetId;
    private String streetExternalId;
    private String streetName;

    public Alert() {
        super();
    }

    public Alert(Equipment equipment) {
        super(equipment);
        this.externalGatewayId=equipment.getCommunicationGateway()!=null?equipment.getCommunicationGateway().getId():null;
        this.gatewayName=equipment.getCommunicationGateway()!=null?equipment.getCommunicationGateway().getName():null;
        this.streetId=equipment.getAddress()!=null&&equipment.getAddress().getStreet()!=null?equipment.getAddress().getStreet().getId():null;
        this.streetExternalId=equipment.getAddress()!=null&&equipment.getAddress().getStreet()!=null?equipment.getAddress().getStreet().getExternalId():null;
        this.streetName=equipment.getAddress()!=null&&equipment.getAddress().getStreet()!=null?equipment.getAddress().getStreet().getName():null;
    }

    public int getSeverity() {
        return severity;
    }

    public <T extends Alert> T setSeverity(int severity) {
        this.severity = severity;
        return (T) this;
    }

    public String getExternalGatewayId() {
        return externalGatewayId;
    }

    public <T extends Alert> T setExternalGatewayId(String externalGatewayId) {
        this.externalGatewayId = externalGatewayId;
        return (T) this;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public <T extends Alert> T setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
        return (T) this;
    }

    public String getStreetId() {
        return streetId;
    }

    public <T extends Alert> T setStreetId(String streetId) {
        this.streetId = streetId;
        return (T) this;
    }

    public String getStreetExternalId() {
        return streetExternalId;
    }

    public <T extends Alert> T setStreetExternalId(String streetExternalId) {
        this.streetExternalId = streetExternalId;
        return (T) this;
    }

    public String getStreetName() {
        return streetName;
    }

    public <T extends Alert> T setStreetName(String streetName) {
        this.streetName = streetName;
        return (T) this;
    }

}
