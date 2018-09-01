package com.flexicore.product.model;

import java.util.Date;

public class Alert extends Event{



    private int severity;

    public Alert() {
        super();
        setEventType(Alert.class.getCanonicalName());
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
}
