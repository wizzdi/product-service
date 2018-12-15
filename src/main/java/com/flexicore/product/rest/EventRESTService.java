package com.flexicore.product.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.product.containers.request.CreateAggregatedReport;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.containers.response.AggregationReport;
import com.flexicore.product.model.Event;
import com.flexicore.product.service.EquipmentService;
import com.flexicore.product.service.EventService;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.logging.Logger;

/**
 * Created by Asaf on 04/06/2017.
 */

@PluginInfo(version = 1)
@OperationsInside
@Interceptors({SecurityImposer.class, DynamicResourceInjector.class})
@Path("plugins/Events")

@Tag(name = "Events")

public class EventRESTService implements RestServicePlugin {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Inject
    @PluginInfo(version = 1)
    private EventService service;

    @Inject
    private Logger logger;




    @POST
    @Produces("application/json")
    @Operation(summary = "getAllEvents", description = "return Events Filtered")
    @Path("getAllEvents")
    public PaginationResponse<Event> getAllEvents(
            @HeaderParam("authenticationKey") String authenticationKey,
            EventFiltering eventFiltering,
            @Context SecurityContext securityContext) {
        service.validateFiltering(eventFiltering, securityContext);
        return service.getAllEvents(eventFiltering,Event.class);

    }

    @POST
    @Produces("application/json")
    @Operation(summary = "generateReport", description = "Generates report")
    @Path("generateReport")
    public AggregationReport generateReport(
            @HeaderParam("authenticationKey") String authenticationKey,
            CreateAggregatedReport filtering,
            @Context SecurityContext securityContext) {
        service.validateFiltering(filtering, securityContext);

        return service.generateReport(securityContext, filtering);
    }


}
