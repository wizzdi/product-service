package com.flexicore.product.rest;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.product.containers.request.CreateAggregatedReport;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.containers.response.AggregationReport;
import com.flexicore.product.model.Event;
import com.flexicore.product.request.AckEventsRequest;
import com.flexicore.product.response.AckEventsResponse;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("plugins/Events")
public interface IEventRESTService extends RestServicePlugin {
	@POST
	@Produces("application/json")
	@Operation(summary = "getAllEvents", description = "return Events Filtered")
	@Path("getAllEvents")
	<T extends Event> PaginationResponse<T> getAllEvents(
			@HeaderParam("authenticationKey") String authenticationKey,
			EventFiltering eventFiltering,
			@Context SecurityContext securityContext);

	@PUT
	@Produces("application/json")
	@Operation(summary = "ackEvents", description = "ack events")
	@Path("ackEvents")
	AckEventsResponse ackEvents(
			@HeaderParam("authenticationKey") String authenticationKey,
			AckEventsRequest eventFiltering,
			@Context SecurityContext securityContext);

	@POST
	@Produces("application/json")
	@Operation(summary = "generateReport", description = "Generates report")
	@Path("generateReport")
	AggregationReport generateReport(
			@HeaderParam("authenticationKey") String authenticationKey,
			CreateAggregatedReport filtering,
			@Context SecurityContext securityContext);
}
