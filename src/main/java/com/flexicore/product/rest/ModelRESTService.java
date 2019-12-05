package com.flexicore.product.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;

import com.flexicore.annotations.ProtectedREST;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.product.model.Model;
import com.flexicore.product.request.ModelCreate;
import com.flexicore.product.request.ModelFiltering;
import com.flexicore.product.request.ModelUpdate;
import com.flexicore.product.service.ModelService;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.logging.Logger;

/**
 * Created by Asaf on 04/06/2017.
 */


@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/ProductModel")

@Tag(name = "ProductModel")

public class ModelRESTService implements RestServicePlugin {

    @Inject
    @PluginInfo(version = 1)
    private ModelService service;

    @Inject
    private Logger logger;


    @POST
    @Produces("application/json")
    @Operation(summary = "getAllEquipments", description = "Gets All Equipments Filtered")
    @Path("getAllEquipmentModels")
    public PaginationResponse<Model> getAllEquipmentModels(
            @HeaderParam("authenticationKey") String authenticationKey,
            ModelFiltering filtering,
            @Context SecurityContext securityContext) {
        service.validateModelFiltering(filtering, securityContext);
        return service.getAllModels(filtering, securityContext);

    }


    @POST
    @Produces("application/json")
    @Operation(summary = "createModel", description = "Creates Equipment Model")
    @Path("createModel")
    public Model createModel(
            @HeaderParam("authenticationKey") String authenticationKey,
            ModelCreate modelCreate,
            @Context SecurityContext securityContext) {
        service.validate(modelCreate, securityContext);

        return service.createModel(modelCreate, securityContext);
    }


    @POST
    @Produces("application/json")
    @Operation(summary = "updateModel", description = "Updates Equipment Model")
    @Path("updateModel")
    public Model updateModel(
            @HeaderParam("authenticationKey") String authenticationKey,
            ModelUpdate modelUpdate,
            @Context SecurityContext securityContext) {

        Model model=service.getByIdOrNull(modelUpdate.getId(),Model.class,null,securityContext);
        if(model==null){
            throw new BadRequestException("No Model With id "+modelUpdate.getId());
        }
        modelUpdate.setModel(model);
        service.validate(modelUpdate, securityContext);
        return service.updateModel(modelUpdate, securityContext);

    }


}
