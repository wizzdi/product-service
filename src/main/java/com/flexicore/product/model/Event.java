package com.flexicore.product.model;

import com.flexicore.model.Baseclass;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

@BsonDiscriminator
public class Event {

    @BsonId
    private String id;
    private Date eventDate;
    private String humanReadableText;
    @BsonProperty(useDiscriminator = true)
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
    private String userAcked;
    private String ackNotes;
    private String targetBaseclassId;



    public Event() {
        this.id = Baseclass.getBase64ID();
        setEventType(getClass().getCanonicalName());
    }

    public Event(Equipment equipment) {
        this();
        this.setEventDate(Date.from(Instant.now()))
                .setBaseclassId(equipment.getId())
                .setBaseclassName(equipment.getName())
                .setClazzName(Baseclass.getClazzbyname(equipment.getClass().getCanonicalName()).getName())
                .setBaseclassTenantId(equipment.getTenant() != null ? equipment.getTenant().getId() : null)
                .setCommunicationGatewayId(equipment.getCommunicationGateway() != null ? equipment.getCommunicationGateway().getId() : null)
                .setBaseclassLat(equipment.getLat())
                .setBaseclassLon(equipment.getLon())
                .setProductTypeId(equipment.getProductType()!=null?equipment.getProductType().getId():null);


    }

    public String getId() {
        return id;
    }

    public <T extends Event> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public <T extends Event> T setEventDate(Date eventDate) {
        this.eventDate = eventDate;
        return (T) this;
    }

    public String getHumanReadableText() {
        return humanReadableText;
    }

    public <T extends Event> T setHumanReadableText(String humanReadableText) {
        this.humanReadableText = humanReadableText;
        return (T) this;
    }

    public String getEventType() {
        return eventType;
    }

    public <T extends Event> T setEventType(String eventType) {
        this.eventType = eventType;
        return (T) this;
    }

    public String getBaseclassId() {
        return baseclassId;
    }

    public <T extends Event> T setBaseclassId(String baseclassId) {
        this.baseclassId = baseclassId;
        return (T) this;
    }

    public String getBaseclassName() {
        return baseclassName;
    }

    public <T extends Event> T setBaseclassName(String baseclassName) {
        this.baseclassName = baseclassName;
        return (T) this;
    }

    public String getClazzName() {
        return clazzName;
    }

    public <T extends Event> T setClazzName(String clazzName) {
        this.clazzName = clazzName;
        return (T) this;
    }

    public String getBaseclassTenantId() {
        return baseclassTenantId;
    }

    public <T extends Event> T setBaseclassTenantId(String baseclassTenantId) {
        this.baseclassTenantId = baseclassTenantId;
        return (T) this;
    }

    public String getEventSubType() {
        return eventSubType;
    }

    public <T extends Event> T setEventSubType(String eventSubType) {
        this.eventSubType = eventSubType;
        return (T) this;
    }

    public Set<String> getEquipmentGroupIds() {
        return equipmentGroupIds;
    }

    public <T extends Event> T setEquipmentGroupIds(Set<String> equipmentGroupIds) {
        this.equipmentGroupIds = equipmentGroupIds;
        return (T) this;
    }

    public String getCommunicationGatewayId() {
        return communicationGatewayId;
    }

    public <T extends Event> T setCommunicationGatewayId(String communicationGatewayId) {
        this.communicationGatewayId = communicationGatewayId;
        return (T) this;
    }

    public Set<String> getStatusIds() {
        return statusIds;
    }

    public <T extends Event> T setStatusIds(Set<String> statusIds) {
        this.statusIds = statusIds;
        return (T) this;
    }

    public Double getBaseclassLat() {
        return baseclassLat;
    }

    public <T extends Event> T setBaseclassLat(Double baseclassLat) {
        this.baseclassLat = baseclassLat;
        return (T) this;
    }

    public Double getBaseclassLon() {
        return baseclassLon;
    }

    public <T extends Event> T setBaseclassLon(Double baseclassLon) {
        this.baseclassLon = baseclassLon;
        return (T) this;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public <T extends Event> T setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
        return (T) this;
    }

    public String getUserAcked() {
        return userAcked;
    }

    public <T extends Event> T setUserAcked(String userAcked) {
        this.userAcked = userAcked;
        return (T) this;
    }

    public String getAckNotes() {
        return ackNotes;
    }

    public Event setAckNotes(String ackNotes) {
        this.ackNotes = ackNotes;
        return this;
    }

    public String getTargetBaseclassId() {
        return targetBaseclassId;
    }

    public Event setTargetBaseclassId(String targetBaseclassId) {
        this.targetBaseclassId = targetBaseclassId;
        return this;
    }
}
