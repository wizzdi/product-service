package com.flexicore.product.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;

import com.flexicore.annotations.ProtectedREST;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.product.containers.request.GroupCreate;
import com.flexicore.product.containers.request.GroupUpdate;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.product.model.GroupFiltering;
import com.flexicore.product.model.StatusLinkToImage;
import com.flexicore.product.request.StatusLinksToImageCreate;
import com.flexicore.product.request.StatusLinksToImageFilter;
import com.flexicore.product.request.StatusLinksToImageUpdate;
import com.flexicore.product.response.StatusLinkToImageContainer;
import com.flexicore.product.service.GroupService;
import com.flexicore.product.service.StatusLinkToImageService;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Asaf on 04/06/2017.
 */


@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/statusLinkToImage")

@Tag(name = "statusLinkToImage")

public class StatusLinkToImageRESTService implements RestServicePlugin {

    @Inject
    @PluginInfo(version = 1)
    private StatusLinkToImageService service;




    @POST
    @Produces("application/json")
    @Operation(summary = "getAllStatusLinksToImage", description = "Gets All StatusLinkToImage Filtered")
    @Path("getAllStatusLinksToImage")
    public PaginationResponse<StatusLinkToImageContainer> getAllStatusLinksToImage(
            @HeaderParam("authenticationKey") String authenticationKey,
            StatusLinksToImageFilter filtering,
            @Context SecurityContext securityContext) {
        service.validate(filtering, securityContext);
        PaginationResponse<StatusLinkToImage> paginationResponse = service.getAllStatusLinksToImage(filtering, securityContext);
        return new PaginationResponse<>(paginationResponse.getList().parallelStream().map(f->new StatusLinkToImageContainer(f)).collect(Collectors.toList()), filtering,paginationResponse.getTotalRecords());

    }


    @POST
    @Produces("application/json")
    @Operation(summary = "createStatusLinkToImage", description = "creates StatusLinkToImage")
    @Path("createStatusLinkToImage")
    public StatusLinkToImage createStatusLinkToImage(
            @HeaderParam("authenticationKey") String authenticationKey,
            StatusLinksToImageCreate statusLinksToImageCreate,
            @Context SecurityContext securityContext) {

        service.validateCreate(statusLinksToImageCreate,securityContext);
        return service.createStatusLinkToImage(statusLinksToImageCreate,securityContext);
    }


    @PUT
    @Produces("application/json")
    @Operation(summary = "updateStatusLinkToImage", description = "Updates StatusLinkToImage")
    @Path("updateStatusLinkToImage")
    public StatusLinkToImage updateStatusLinkToImage(
            @HeaderParam("authenticationKey") String authenticationKey,
            StatusLinksToImageUpdate statusLinksToImageUpdate,
            @Context SecurityContext securityContext) {
        StatusLinkToImage statusLinkToImage=statusLinksToImageUpdate.getId()!=null?service.getByIdOrNull(statusLinksToImageUpdate.getId(),StatusLinkToImage.class,null,securityContext):null;
        if(statusLinkToImage==null){
            throw new BadRequestException("No StatusLinksToImageUpdate with id "+statusLinksToImageUpdate.getId());
        }
        statusLinksToImageUpdate.setStatusLinkToImage(statusLinkToImage);
        service.validateUpdate(statusLinksToImageUpdate,securityContext);
        return service.updateStatusLinkToImage(statusLinksToImageUpdate, securityContext);
    }



}
