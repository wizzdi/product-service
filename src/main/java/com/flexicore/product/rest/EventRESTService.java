package com.flexicore.product.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Read;
import com.flexicore.annotations.rest.Update;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.product.containers.request.*;
import com.flexicore.product.containers.response.AggregationReport;
import com.flexicore.product.containers.response.AggregationReportEntry;
import com.flexicore.product.model.*;
import com.flexicore.product.service.EventService;
import com.flexicore.product.service.EquipmentService;
import com.flexicore.security.SecurityContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Asaf on 04/06/2017.
 */

@PluginInfo(version = 1)
@OperationsInside
@Interceptors({SecurityImposer.class, DynamicResourceInjector.class})
@Path("plugins/Events")

@Api(tags = {"Events"})

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
    @Update
    @ApiOperation(value = "getAllEvents", notes = "return Events Filtered")
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
    @Read
    @ApiOperation(value = "generateReport", notes = "Generates report")
    @Path("generateReport")
    public AggregationReport generateReport(
            @HeaderParam("authenticationKey") String authenticationKey,
            CreateAggregatedReport filtering,
            @Context SecurityContext securityContext) {
        service.validateFiltering(filtering, securityContext);

        return service.generateReport(securityContext, filtering);
    }


}
