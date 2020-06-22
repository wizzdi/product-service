package com.flexicore.product.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;

import com.flexicore.annotations.ProtectedREST;
import com.flexicore.model.FileResource;
import com.flexicore.product.containers.request.CreateAggregatedReport;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.containers.response.AggregationReport;
import com.flexicore.product.interfaces.IEventService;
import com.flexicore.product.model.Event;
import com.flexicore.product.request.AckEventsRequest;
import com.flexicore.product.request.EquipmentStatusEventFilter;
import com.flexicore.product.response.AckEventsResponse;
import com.flexicore.product.service.EquipmentService;
import com.flexicore.product.service.EventService;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Asaf on 04/06/2017.
 */

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/Events")
@Tag(name = "Events")
@Extension
@Component
public class EventRESTService implements IEventRESTService {

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentService equipmentService;

	@PluginInfo(version = 1)
	@Autowired
	private EventService service;

	@Autowired
	private Logger logger;

	@POST
	@Produces("application/json")
	@Operation(summary = "exportEquipmentStatusEventsToCSV", description = "exportEquipmentStatusEventsToCSV")
	@Path("exportEquipmentStatusEventsToCSV")
	public FileResource exportEquipmentStatusEventsToCSV(
			@HeaderParam("authenticationKey") String authenticationKey,
			EquipmentStatusEventFilter lightStatusEventFilter,
			@Context SecurityContext securityContext) {
		service.validate(lightStatusEventFilter, securityContext);
		return service.exportEquipmentStatusEventsToCSV(lightStatusEventFilter,
				securityContext);

	}

	@Override
	@POST
	@Produces("application/json")
	@Operation(summary = "getAllEvents", description = "return Events Filtered")
	@Path("getAllEvents")
	public <T extends Event> PaginationResponse<T> getAllEvents(
			@HeaderParam("authenticationKey") String authenticationKey,
			EventFiltering eventFiltering,
			@Context SecurityContext securityContext) {
		service.validateFiltering(eventFiltering, securityContext);
		Class<T> c = (Class<T>) Event.class;
		if (eventFiltering.getEventType() != null) {
			c = (Class<T>) IEventService.getClazzToRegisterMap().getOrDefault(
					eventFiltering.getEventType(), Event.class);
		}
		return service.getAllEvents(eventFiltering, c);

	}

	@Override
	@PUT
	@Produces("application/json")
	@Operation(summary = "ackEvents", description = "ack events")
	@Path("ackEvents")
	public AckEventsResponse ackEvents(
			@HeaderParam("authenticationKey") String authenticationKey,
			AckEventsRequest eventFiltering,
			@Context SecurityContext securityContext) {
		return service.ackEvents(eventFiltering, securityContext);

	}

	private static AtomicBoolean usersUsingReport = new AtomicBoolean(false);

	@Override
	@POST
	@Produces("application/json")
	@Operation(summary = "generateReport", description = "Generates report")
	@Path("generateReport")
	public AggregationReport generateReport(
			@HeaderParam("authenticationKey") String authenticationKey,
			CreateAggregatedReport filtering,
			@Context SecurityContext securityContext) {
		if (usersUsingReport.compareAndSet(false, true)) {
			try {
				service.validateFiltering(filtering, securityContext);

				return service.generateReport(securityContext, filtering);
			} finally {
				usersUsingReport.set(false);
			}
		}

		throw new ServiceUnavailableException(
				"generate report has exceeded its use , please wait for it to become available");
	}

}
