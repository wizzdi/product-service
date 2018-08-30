package com.flexicore.product.model;

import com.flexicore.model.Baseclass;
import org.bson.codecs.pojo.annotations.BsonId;

import java.util.Date;

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


    public Event() {
        this.id=Baseclass.getBase64ID();
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
}
