package com.flexicore.product.request;

import java.util.HashSet;
import java.util.Set;

public class AckEventsRequest {
    private Set<String> eventIds =new HashSet<>();
    private String ackNotes;
    private boolean falseAlarm;

    public Set<String> getEventIds() {
        return eventIds;
    }

    public <T extends AckEventsRequest> T setEventIds(Set<String> eventIds) {
        this.eventIds = eventIds;
        return (T) this;
    }

    public String getAckNotes() {
        return ackNotes;
    }

    public AckEventsRequest setAckNotes(String ackNotes) {
        this.ackNotes = ackNotes;
        return this;
    }

    public boolean isFalseAlarm() {
        return falseAlarm;
    }

    public AckEventsRequest setFalseAlarm(boolean falseAlarm) {
        this.falseAlarm = falseAlarm;
        return this;
    }
}
