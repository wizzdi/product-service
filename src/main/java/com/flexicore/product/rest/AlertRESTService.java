package com.flexicore.product.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Update;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.product.containers.request.*;
import com.flexicore.product.model.*;
import com.flexicore.product.service.AlertService;
import com.flexicore.product.service.EquipmentService;
import com.flexicore.security.SecurityContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Asaf on 04/06/2017.
 */

@PluginInfo(version = 1)
@OperationsInside
@Interceptors({SecurityImposer.class, DynamicResourceInjector.class})
@Path("plugins/Alerts")

@Api(tags = {"Alerts"})

public class AlertRESTService implements RestServicePlugin {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Inject
    @PluginInfo(version = 1)
    private AlertService service;

    @Inject
    private Logger logger;




    @POST
    @Produces("application/json")
    @Update
    @ApiOperation(value = "getAllAlerts", notes = "return Alerts Filtered")
    @Path("getAllAlerts")
    public List<Alert> getAllAlerts(
            @HeaderParam("authenticationKey") String authenticationKey,
            AlertFiltering alertFiltering,
            @Context SecurityContext securityContext) {
        List<Baseclass> baseclasses=alertFiltering.getBaseclassIds().isEmpty()?new ArrayList<>(): equipmentService.listByIds(Baseclass.class,alertFiltering.getBaseclassIds(),securityContext);
        alertFiltering.getBaseclassIds().removeAll(baseclasses.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
        if(!alertFiltering.getBaseclassIds().isEmpty()){
            throw new BadRequestException(" no baseclass with ids "+alertFiltering.getBaseclassIds().parallelStream().collect(Collectors.joining(",")));
        }
        alertFiltering.setBaseclass(baseclasses);

        if(alertFiltering.getClazzName()!=null){
            alertFiltering.setClazz(Baseclass.getClazzbyname(alertFiltering.getClazzName()));
            if(alertFiltering.getClazz()==null){
                throw new BadRequestException("No Clazz by name "+alertFiltering.getClazzName());
            }

        }

        return service.getAllAlerts(alertFiltering);

    }





}
