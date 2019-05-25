package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.model.Event;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Event Invoker", description = "Invoker for Events")

public class EventsInvoker implements ListingInvoker<Event, EventFiltering>{

    @Inject
    @PluginInfo(version = 1)
    private EventService eventService;

    @Override
    @InvokerMethodInfo(displayName = "listAllEvents",description = "lists all Events")

    public PaginationResponse<Event> listAll(EventFiltering eventFiltering, SecurityContext securityContext) {
        eventService.validateFiltering(eventFiltering,securityContext);
        return eventService.getAllEvents(eventFiltering,Event.class);
    }


    @Override
    public Class<EventFiltering> getFilterClass() {
        return EventFiltering.class;
    }

    @Override
    public Class<?> getHandlingClass() {
        return Event.class;
    }

}
