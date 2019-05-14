package com.flexicore.product.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.product.model.BuildingFloor;
import com.flexicore.product.model.BuildingFloorFiltering;
import com.flexicore.product.request.BuildingFloorCreate;
import com.flexicore.product.request.BuildingFloorUpdate;
import com.flexicore.product.service.BuildingFloorService;
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
@Path("plugins/buildingFloor")

@Tag(name = "buildingFloor")

public class BuildingFloorRESTService implements RestServicePlugin {

    @Inject
    @PluginInfo(version = 1)
    private BuildingFloorService service;




    @POST
    @Produces("application/json")
    @Operation(summary = "getAllBuildingFloors", description = "Gets All BuildingFloor Filtered")
    @Path("getAllBuildingFloors")
    public PaginationResponse<BuildingFloor> getAllBuildingFloors(
            @HeaderParam("authenticationKey") String authenticationKey,
            BuildingFloorFiltering filtering,
            @Context SecurityContext securityContext) {
        service.validate(filtering, securityContext);
        return service.getAllBuildingFloors(filtering,securityContext);

    }


    @POST
    @Produces("application/json")
    @Operation(summary = "createBuildingFloor", description = "creates BuildingFloor")
    @Path("createBuildingFloor")
    public BuildingFloor createBuildingFloor(
            @HeaderParam("authenticationKey") String authenticationKey,
            BuildingFloorCreate buildingFloorsCreate,
            @Context SecurityContext securityContext) {

        service.validateCreate(buildingFloorsCreate,securityContext);
        return service.createBuildingFloor(buildingFloorsCreate,securityContext);
    }


    @PUT
    @Produces("application/json")
    @Operation(summary = "updateBuildingFloor", description = "Updates BuildingFloor")
    @Path("updateBuildingFloor")
    public BuildingFloor updateBuildingFloor(
            @HeaderParam("authenticationKey") String authenticationKey,
            BuildingFloorUpdate buildingFloorsUpdate,
            @Context SecurityContext securityContext) {
        BuildingFloor buildingFloor=buildingFloorsUpdate.getId()!=null?service.getByIdOrNull(buildingFloorsUpdate.getId(),BuildingFloor.class,null,securityContext):null;
        if(buildingFloor==null){
            throw new BadRequestException("No BuildingFloorsUpdate with id "+buildingFloorsUpdate.getId());
        }
        buildingFloorsUpdate.setBuildingFloor(buildingFloor);
        service.validateUpdate(buildingFloorsUpdate,securityContext);
        return service.updateBuildingFloor(buildingFloorsUpdate, securityContext);
    }



}
