package com.flexicore.product.model;

import com.flexicore.model.Baseclass;
import org.bson.codecs.pojo.annotations.BsonId;

import java.util.Date;

public class Alert {

    @BsonId
    private String id;
    private Date alertDate;
    private String humanReadableText;
    private int severity;
    private String alertType;
    private String baseclassId;
    private String baseclassName;
    private String clazzName;
    private String baseclassTenantId;


    public Alert() {
        this.id=Baseclass.getBase64ID();
    }

    public String getId() {
        return id;
    }

    public Alert setId(String id) {
        this.id = id;
        return this;
    }

    public Date getAlertDate() {
        return alertDate;
    }

    public Alert setAlertDate(Date alertDate) {
        this.alertDate = alertDate;
        return this;
    }

    public String getHumanReadableText() {
        return humanReadableText;
    }

    public Alert setHumanReadableText(String humanReadableText) {
        this.humanReadableText = humanReadableText;
        return this;
    }

    public int getSeverity() {
        return severity;
    }

    public Alert setSeverity(int severity) {
        this.severity = severity;
        return this;
    }

    public String getAlertType() {
        return alertType;
    }

    public Alert setAlertType(String alertType) {
        this.alertType = alertType;
        return this;
    }

    public String getBaseclassId() {
        return baseclassId;
    }

    public Alert setBaseclassId(String baseclassId) {
        this.baseclassId = baseclassId;
        return this;
    }

    public String getClazzName() {
        return clazzName;
    }

    public Alert setClazzName(String clazzName) {
        this.clazzName = clazzName;
        return this;
    }

    public String getBaseclassName() {
        return baseclassName;
    }

    public Alert setBaseclassName(String baseclassName) {
        this.baseclassName = baseclassName;
        return this;
    }

    public String getBaseclassTenantId() {
        return baseclassTenantId;
    }

    public Alert setBaseclassTenantId(String baseclassTenantId) {
        this.baseclassTenantId = baseclassTenantId;
        return this;
    }
}
