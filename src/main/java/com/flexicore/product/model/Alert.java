package com.flexicore.product.model;

import com.flexicore.service.BaseclassService;

import java.util.Date;
import java.util.Set;

public class Alert extends Event{

    static{
        BaseclassService.registerInheretingClass(Alert.class);
    }



    private int severity;
    private String externalGatewayId;
    private String gatewayName;
    private String streetId;
    private String streetExternalId;
    private String streetName;

    public Alert() {
        super();
        setEventType(Alert.class.getCanonicalName());
    }

    public Alert(Equipment equipment) {
        super(equipment);
        setEventType(Alert.class.getCanonicalName());
        this.externalGatewayId=equipment.getCommunicationGateway()!=null?equipment.getCommunicationGateway().getId():null;
        this.gatewayName=equipment.getCommunicationGateway().getName();
        this.streetId=equipment.getAddress()!=null&&equipment.getAddress().getStreet()!=null?equipment.getAddress().getStreet().getId():null;
        this.streetExternalId=equipment.getAddress()!=null&&equipment.getAddress().getStreet()!=null?equipment.getAddress().getStreet().getExternalId():null;
        this.streetName=equipment.getAddress()!=null&&equipment.getAddress().getStreet()!=null?equipment.getAddress().getStreet().getName():null;
    }

    public int getSeverity() {
        return severity;
    }

    public Alert setSeverity(int severity) {
        this.severity = severity;
        return this;
    }

    @Override
    public Alert setEventDate(Date eventDate) {
        return (Alert)super.setEventDate(eventDate);
    }

    @Override
    public Alert setHumanReadableText(String humanReadableText) {
        return (Alert)super.setHumanReadableText(humanReadableText);
    }

    @Override
    public Alert setEventType(String eventType) {
        return (Alert)super.setEventType(eventType);
    }

    @Override
    public Alert setBaseclassId(String baseclassId) {
        return (Alert)super.setBaseclassId(baseclassId);
    }

    @Override
    public Alert setClazzName(String clazzName) {
        return (Alert)super.setClazzName(clazzName);
    }

    @Override
    public Alert setBaseclassName(String baseclassName) {
        return (Alert)super.setBaseclassName(baseclassName);
    }

    @Override
    public Alert setBaseclassTenantId(String baseclassTenantId) {
        return (Alert)super.setBaseclassTenantId(baseclassTenantId);
    }

    @Override
    public Alert setEventSubType(String eventSubType) {
        return (Alert)super.setEventSubType(eventSubType);
    }

    @Override
    public Alert setEquipmentGroupIds(Set<String> equipmentGroupIds) {
        return (Alert) super.setEquipmentGroupIds(equipmentGroupIds);
    }

    @Override
    public Alert setCommunicationGatewayId(String communicationGatewayId) {
        return (Alert) super.setCommunicationGatewayId(communicationGatewayId);
    }

    @Override
    public Alert setStatusIds(Set<String> statusIds) {
        return (Alert) super.setStatusIds(statusIds);
    }

    public String getExternalGatewayId() {
        return externalGatewayId;
    }

    public Alert setExternalGatewayId(String externalGatewayId) {
        this.externalGatewayId = externalGatewayId;
        return this;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public Alert setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
        return this;
    }

    public String getStreetId() {
        return streetId;
    }

    public Alert setStreetId(String streetId) {
        this.streetId = streetId;
        return this;
    }

    public String getStreetExternalId() {
        return streetExternalId;
    }

    public Alert setStreetExternalId(String streetExternalId) {
        this.streetExternalId = streetExternalId;
        return this;
    }

    public String getStreetName() {
        return streetName;
    }

    public Alert setStreetName(String streetName) {
        this.streetName = streetName;
        return this;
    }


}
