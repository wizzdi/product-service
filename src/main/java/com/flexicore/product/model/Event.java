package com.flexicore.product.model;

import com.flexicore.model.Baseclass;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class Event {

    @BsonId
    private String id;
    private Date eventDate;
    private String humanReadableText;
    private String eventType;
    private String baseclassId;
    private String baseclassName;
    private String clazzName;
    private String baseclassTenantId;
    private String eventSubType;
    private Set<String> equipmentGroupIds;
    private String communicationGatewayId;
    private Set<String> statusIds;
    private Double baseclassLat;
    private Double baseclassLon;
    private String productTypeId;


    public Event() {
        this.id = Baseclass.getBase64ID();
        setEventType(Event.class.getCanonicalName());
    }

    public Event(Equipment equipment) {
        this();
        this.setEventDate(Date.from(Instant.now()))
                .setBaseclassId(equipment.getId())
                .setBaseclassName(equipment.getName())
                .setClazzName(Baseclass.getClazzbyname(equipment.getClass().getCanonicalName()).getName())
                .setBaseclassTenantId(equipment.getTenant() != null ? equipment.getTenant().getId() : null)
                .setCommunicationGatewayId(equipment.getCommunicationGateway() != null ? equipment.getCommunicationGateway().getId() : null)
                .setStatusIds(equipment.getProductToStatusList().parallelStream().map(f -> f.getRightside().getId()).collect(Collectors.toSet()))
                .setEquipmentGroupIds(equipment.getEquipmentToGroupList().parallelStream().filter(f->!f.isSoftDelete()).map(f -> f.getRightside().getId()).collect(Collectors.toSet()))
                .setBaseclassLat(equipment.getLat())
                .setBaseclassLon(equipment.getLon())
                .setProductTypeId(equipment.getProductType()!=null?equipment.getProductType().getId():null);


    }

    public String getId() {
        return id;
    }

    public Event setId(String id) {
        this.id = id;
        return this;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public Event setEventDate(Date eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public String getHumanReadableText() {
        return humanReadableText;
    }

    public Event setHumanReadableText(String humanReadableText) {
        this.humanReadableText = humanReadableText;
        return this;
    }


    public String getEventType() {
        return eventType;
    }

    public Event setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public String getBaseclassId() {
        return baseclassId;
    }

    public Event setBaseclassId(String baseclassId) {
        this.baseclassId = baseclassId;
        return this;
    }

    public String getClazzName() {
        return clazzName;
    }

    public Event setClazzName(String clazzName) {
        this.clazzName = clazzName;
        return this;
    }

    public String getBaseclassName() {
        return baseclassName;
    }

    public Event setBaseclassName(String baseclassName) {
        this.baseclassName = baseclassName;
        return this;
    }

    public String getBaseclassTenantId() {
        return baseclassTenantId;
    }

    public Event setBaseclassTenantId(String baseclassTenantId) {
        this.baseclassTenantId = baseclassTenantId;
        return this;
    }

    public String getEventSubType() {
        return eventSubType;
    }

    public Event setEventSubType(String eventSubType) {
        this.eventSubType = eventSubType;
        return this;
    }

    public Set<String> getEquipmentGroupIds() {
        return equipmentGroupIds;
    }

    public Event setEquipmentGroupIds(Set<String> equipmentGroupIds) {
        this.equipmentGroupIds = equipmentGroupIds;
        return this;
    }

    public String getCommunicationGatewayId() {
        return communicationGatewayId;
    }

    public Event setCommunicationGatewayId(String communicationGatewayId) {
        this.communicationGatewayId = communicationGatewayId;
        return this;
    }

    public Set<String> getStatusIds() {
        return statusIds;
    }

    public Event setStatusIds(Set<String> statusIds) {
        this.statusIds = statusIds;
        return this;
    }

    public Double getBaseclassLat() {
        return baseclassLat;
    }

    public Event setBaseclassLat(Double baseclassLat) {
        this.baseclassLat = baseclassLat;
        return this;
    }

    public Double getBaseclassLon() {
        return baseclassLon;
    }

    public Event setBaseclassLon(Double baseclassLon) {
        this.baseclassLon = baseclassLon;
        return this;
    }


    public String getProductTypeId() {
        return productTypeId;
    }

    public Event setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
        return this;
    }
}
