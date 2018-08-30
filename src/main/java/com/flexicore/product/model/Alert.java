package com.flexicore.product.model;

public class Alert extends Event{


    private int severity;
    private String alertType;

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
}
