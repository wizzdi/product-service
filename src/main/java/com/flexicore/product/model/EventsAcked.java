package com.flexicore.product.model;

import com.flexicore.product.interfaces.IEvent;

import java.util.Set;

public class EventsAcked implements IEvent {

    private Set<String> ackedEvents;
    private String ackedUserId;

    public Set<String> getAckedEvents() {
        return ackedEvents;
    }

    public <T extends EventsAcked> T setAckedEvents(Set<String> ackedEvents) {
        this.ackedEvents = ackedEvents;
        return (T) this;
    }

    public String getAckedUserId() {
        return ackedUserId;
    }

    public <T extends EventsAcked> T setAckedUserId(String ackedUserId) {
        this.ackedUserId = ackedUserId;
        return (T) this;
    }
}
