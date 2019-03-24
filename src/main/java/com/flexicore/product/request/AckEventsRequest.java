package com.flexicore.product.request;

import java.util.HashSet;
import java.util.Set;

public class AckEventsRequest {
    private Set<String> eventIds =new HashSet<>();

    public Set<String> getEventIds() {
        return eventIds;
    }

    public <T extends AckEventsRequest> T setEventIds(Set<String> eventIds) {
        this.eventIds = eventIds;
        return (T) this;
    }


}
