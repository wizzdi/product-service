package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.model.Event;
import com.flexicore.security.SecurityContext;

import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Event Invoker", description = "Invoker for Events")
@Extension
@Component
public class EventsInvoker implements ListingInvoker<Event, EventFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private EventService eventService;

	@Override
	@InvokerMethodInfo(displayName = "listAllEvents", description = "lists all Events")
	public PaginationResponse<Event> listAll(EventFiltering eventFiltering,
			SecurityContext securityContext) {
		eventService.validateFiltering(eventFiltering, securityContext);
		return eventService.getAllEvents(eventFiltering, Event.class);
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
