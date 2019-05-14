package com.flexicore.product.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.product.model.Building;
import com.flexicore.product.model.BuildingFiltering;
import com.flexicore.product.request.BuildingCreate;
import com.flexicore.product.request.BuildingUpdate;
import com.flexicore.product.service.BuildingService;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

/**
 * Created by Asaf on 04/06/2017.
 */


@PluginInfo(version = 1)
@OperationsInside
@Interceptors({SecurityImposer.class, DynamicResourceInjector.class})
@Path("plugins/building")

@Tag(name = "building")

public class BuildingRESTService implements RestServicePlugin {

    @Inject
    @PluginInfo(version = 1)
    private BuildingService service;




    @POST
    @Produces("application/json")
    @Operation(summary = "getAllBuildings", description = "Gets All Building Filtered")
    @Path("getAllBuildings")
    public PaginationResponse<Building> getAllBuildings(
            @HeaderParam("authenticationKey") String authenticationKey,
            BuildingFiltering filtering,
            @Context SecurityContext securityContext) {
        service.validate(filtering, securityContext);
        return service.getAllBuildings(filtering,securityContext);

    }


    @POST
    @Produces("application/json")
    @Operation(summary = "createBuilding", description = "creates Building")
    @Path("createBuilding")
    public Building createBuilding(
            @HeaderParam("authenticationKey") String authenticationKey,
            BuildingCreate buildingsCreate,
            @Context SecurityContext securityContext) {

        service.validateCreate(buildingsCreate,securityContext);
        return service.createBuilding(buildingsCreate,securityContext);
    }


    @PUT
    @Produces("application/json")
    @Operation(summary = "updateBuilding", description = "Updates Building")
    @Path("updateBuilding")
    public Building updateBuilding(
            @HeaderParam("authenticationKey") String authenticationKey,
            BuildingUpdate buildingsUpdate,
            @Context SecurityContext securityContext) {
        Building building=buildingsUpdate.getId()!=null?service.getByIdOrNull(buildingsUpdate.getId(),Building.class,null,securityContext):null;
        if(building==null){
            throw new BadRequestException("No BuildingsUpdate with id "+buildingsUpdate.getId());
        }
        buildingsUpdate.setBuilding(building);
        service.validateUpdate(buildingsUpdate,securityContext);
        return service.updateBuilding(buildingsUpdate, securityContext);
    }



}
