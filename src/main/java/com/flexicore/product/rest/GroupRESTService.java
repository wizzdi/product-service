package com.flexicore.product.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Read;
import com.flexicore.annotations.rest.Write;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.product.containers.request.GroupCreate;
import com.flexicore.product.containers.request.GroupFiltering;
import com.flexicore.product.containers.request.GroupUpdate;
import com.flexicore.product.containers.response.PaginationResponse;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.product.service.GroupService;
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
@Path("plugins/EquipmentGroups")

@Api(tags = {"EquipmentGroups"})

public class GroupRESTService implements RestServicePlugin {

    @Inject
    @PluginInfo(version = 1)
    private GroupService service;

    @Inject
    private Logger logger;


    @POST
    @Produces("application/json")
    @Read
    @ApiOperation(value = "getAllEquipments", notes = "Gets All Equipments Filtered")
    @Path("getAllEquipmentGroups")
    public PaginationResponse<EquipmentGroup> getAllEquipmentGroups(
            @HeaderParam("authenticationKey") String authenticationKey,
            GroupFiltering filtering,
            @Context SecurityContext securityContext) {
        List<EquipmentGroup> groups=filtering.getGroupIds().isEmpty()?new ArrayList<>():service.listByIds(EquipmentGroup.class,filtering.getGroupIds(),securityContext);
        filtering.getGroupIds().removeAll(groups.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
        if(!filtering.getGroupIds().isEmpty()){
            throw new BadRequestException("could not find groups with ids "+filtering.getGroupIds().parallelStream().collect(Collectors.joining(",")));
        }
        filtering.setEquipmentGroups(groups);
        return service.getAllEquipmentGroups(filtering,securityContext);

    }


    @POST
    @Produces("application/json")
    @Read
    @ApiOperation(value = "getRootEquipmentGroup", notes = "return Root EquipmentGroupHolder")
    @Path("getRootEquipmentGroup")
    public EquipmentGroup getRootEquipmentGroup(
            @HeaderParam("authenticationKey") String authenticationKey,
            @Context SecurityContext securityContext) {

        return service.getRootEquipmentGroup(securityContext);
    }


    @POST
    @Produces("application/json")
    @Read
    @ApiOperation(value = "createGroup", notes = "Creates Equipment Group")
    @Path("createGroup")
    public EquipmentGroup createGroup(
            @HeaderParam("authenticationKey") String authenticationKey,
            GroupCreate groupCreate,
            @Context SecurityContext securityContext) {
        EquipmentGroup equipmentGroup=groupCreate.getParentId()!=null?service.getByIdOrNull(groupCreate.getParentId(),EquipmentGroup.class,null,securityContext):null;
        if(equipmentGroup==null&&groupCreate.getParentId()!=null){
            throw new BadRequestException("No Equipment Group with id "+groupCreate.getParentId());
        }
        groupCreate.setParent(equipmentGroup);

        return service.createGroup(groupCreate, securityContext);
    }


    @POST
    @Produces("application/json")
    @Write
    @ApiOperation(value = "updateGroup", notes = "Updates Equipment Group")
    @Path("updateGroup")
    public EquipmentGroup updateGroup(
            @HeaderParam("authenticationKey") String authenticationKey,
            GroupUpdate groupUpdate,
            @Context SecurityContext securityContext) {
        EquipmentGroup equipmentGroup=groupUpdate.getId()!=null?service.getByIdOrNull(groupUpdate.getId(),EquipmentGroup.class,null,securityContext):null;
        if(equipmentGroup==null){
            throw new BadRequestException("no Equipment group with id "+groupUpdate.getId());
        }
        groupUpdate.setEquipmentGroup(equipmentGroup);
        EquipmentGroup parent=groupUpdate.getParentId()!=null?service.getByIdOrNull(groupUpdate.getParentId(),EquipmentGroup.class,null,securityContext):null;
        if(parent==null&&groupUpdate.getParentId()!=null){
            throw new BadRequestException("No Equipment Group with id "+groupUpdate.getParentId());
        }
        groupUpdate.setParent(equipmentGroup);
        return service.updateGroup(groupUpdate,securityContext);

    }



}
