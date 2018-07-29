package com.flexicore.product.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Read;
import com.flexicore.annotations.rest.Update;
import com.flexicore.annotations.rest.Write;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.product.containers.request.*;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.model.*;
import com.flexicore.product.service.EquipmentService;
import com.flexicore.product.service.GroupService;
import com.flexicore.security.SecurityContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Asaf on 04/06/2017.
 */

@PluginInfo(version = 1)
@OperationsInside
@Interceptors({SecurityImposer.class, DynamicResourceInjector.class})
@Path("plugins/Equipments")
@SwaggerDefinition(tags = {
        @Tag(name = "Equipments", description = "Equipments Services"),
        @Tag(name = "EquipmentGroups", description = "EquipmentGroups Services")

})
@Api(tags = {"Equipments"})

public class EquipmentRESTService implements RestServicePlugin {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService service;

    @Inject
    @PluginInfo(version = 1)
    private GroupService groupService;

    @Inject
    private Logger logger;


    @POST
    @Produces("application/json")
    @Read
    @ApiOperation(value = "getAllEquipments", notes = "Gets All Equipments Filtered")
    @Path("getAllEquipments")
    public <T extends Equipment> List<T> getAllEquipments(
            @HeaderParam("authenticationKey") String authenticationKey,
            EquipmentFiltering filtering,
            @Context SecurityContext securityContext) {
        Class<T> c = validateFiltering(filtering, securityContext);

        return service.getAllEquipments(c,filtering, securityContext);
    }


    @POST
    @Produces("application/json")
    @Read
    @ApiOperation(value = "getAllEquipmentsGrouped", notes = "Gets All Equipments Filtered and Grouped")
    @Path("getAllEquipmentsGrouped")
    public <T extends Equipment> List<EquipmentGroupHolder> getAllEquipmentsGrouped(
            @HeaderParam("authenticationKey") String authenticationKey,
            EquipmentGroupFiltering filtering,
            @Context SecurityContext securityContext) {
        Class<T> c = validateFiltering(filtering, securityContext);
        if(filtering.getPrecision() > 12 || filtering.getPrecision() < 1){
            throw new BadRequestException(" Precision must be a value between 1 and 12");
        }

        return service.getAllEquipmentsGrouped(c,filtering, securityContext);
    }

    private <T extends Equipment> Class<T> validateFiltering(EquipmentFiltering filtering, @Context SecurityContext securityContext) {
        Class<T> c= (Class<T>) Equipment.class;
        if(filtering.getCanonicalClassName()!=null &&!filtering.getCanonicalClassName().isEmpty()){
            try {
                 c= (Class<T>) Class.forName(filtering.getCanonicalClassName());

            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE,"unable to get class: "+filtering.getCanonicalClassName());
                throw new BadRequestException("No Class with name "+filtering.getCanonicalClassName());

            }
        }
        List<EquipmentGroup> groups=filtering.getGroupIds().isEmpty()?new ArrayList<>():groupService.listByIds(EquipmentGroup.class,filtering.getGroupIds(),securityContext);
        filtering.getGroupIds().removeAll(groups.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
        if(!filtering.getGroupIds().isEmpty()){
            throw new BadRequestException("could not find groups with ids "+filtering.getGroupIds().parallelStream().collect(Collectors.joining(",")));
        }
        filtering.setEquipmentGroups(groups);
        ProductType productType=filtering.getProductTypeId()!=null?null:service.getByIdOrNull(filtering.getProductTypeId(),ProductType.class,null,securityContext);
        if(filtering.getProductTypeId()!=null && productType==null){
            throw new BadRequestException("No Product type with id "+filtering.getProductTypeId());
        }
        filtering.setProductType(productType);
        List<ProductStatus> status=filtering.getProductStatusIds().isEmpty()?new ArrayList<>():service.listByIds(ProductStatus.class,filtering.getProductStatusIds(),securityContext);
        filtering.getProductStatusIds().removeAll(status.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
        if(!filtering.getProductStatusIds().isEmpty()){
            throw new BadRequestException("could not find status with ids "+filtering.getProductStatusIds().parallelStream().collect(Collectors.joining(",")));
        }
        filtering.setProductStatusList(status);
        return c;
    }

    @POST
    @Produces("application/json")
    @Write
    @ApiOperation(value = "createProductType", notes = "Creates ProductType")
    @Path("createProductType")
    public ProductType createProductType(
            @HeaderParam("authenticationKey") String authenticationKey,
            ProductTypeCreate productTypeCreate,
            @Context SecurityContext securityContext) {

        return service.createProductType(productTypeCreate, securityContext);
    }

    @POST
    @Produces("application/json")
    @Write
    @ApiOperation(value = "getAllProductTypes", notes = "lists all ProductTypes")
    @Path("getAllProductTypes")
    public List<ProductType> getAllProductTypes(
            @HeaderParam("authenticationKey") String authenticationKey,
            ProductTypeFiltering productTypeFiltering,
            @Context SecurityContext securityContext) {

        return service.getAllProductTypes(productTypeFiltering, securityContext);
    }


    @POST
    @Produces("application/json")
    @Read
    @ApiOperation(value = "createEquipment", notes = "Creates Equipment")
    @Path("createEquipment")
    public<T extends Equipment> T createEquipment(
            @HeaderParam("authenticationKey") String authenticationKey,
            EquipmentCreate equipmentCreate,
            @Context SecurityContext securityContext) {
        validateEquipmentCreate(equipmentCreate,securityContext);
        Class<T> c= (Class<T>) Equipment.class;
        if(equipmentCreate.getClazzName()!=null){
            try {
                c= (Class<T>) Class.forName(equipmentCreate.getClazzName());
            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE,"unable to get class: "+equipmentCreate.getClazzName(),e);
                throw new BadRequestException("No Class with name "+equipmentCreate.getClazzName());
            }
        }
        return service.createEquipment(c,equipmentCreate, securityContext);
    }

    private void validateEquipmentCreate(EquipmentCreate equipmentCreate, SecurityContext securityContext) {

        ProductType productType=equipmentCreate.getProductTypeId()!=null?service.getByIdOrNull(equipmentCreate.getProductTypeId(),ProductType.class,null,securityContext):null;
        if(productType==null && equipmentCreate.getProductTypeId()!=null){
            throw new BadRequestException("No Product type with Id "+equipmentCreate.getProductTypeId());
        }
        equipmentCreate.setProductType(productType);
    }


    @POST
    @Produces("application/json")
    @Read
    @ApiOperation(value = "linkToGroup", notes = "Links Equipment to Group")
    @Path("linkToGroup")
    public EquipmentToGroup linkToGroup(
            @HeaderParam("authenticationKey") String authenticationKey,
            LinkToGroup linkToGroup,
            @Context SecurityContext securityContext) {
        Equipment equipment=linkToGroup.getEquipmentId()!=null?service.getByIdOrNull(linkToGroup.getEquipmentId(),Equipment.class,null,securityContext):null;
        if(equipment==null){
            throw new BadRequestException("no Equipment with id "+linkToGroup.getEquipmentId());
        }
        linkToGroup.setEquipment(equipment);
        EquipmentGroup equipmentGroup=linkToGroup.getGroupId()!=null?groupService.getByIdOrNull(linkToGroup.getGroupId(),EquipmentGroup.class,null,securityContext):null;
        if(equipmentGroup==null){
            throw new BadRequestException("no Equipment group with id "+linkToGroup.getGroupId());
        }
        linkToGroup.setEquipmentGroup(equipmentGroup);
        return service.createEquipmentToGroup(linkToGroup, securityContext);
    }


    @POST
    @Produces("application/json")
    @Update
    @ApiOperation(value = "updateEquipment", notes = "Updates Equipment")
    @Path("updateEquipment")
    public Equipment updateEquipment(
            @HeaderParam("authenticationKey") String authenticationKey,
            EquipmentUpdate equipmentUpdate,
            @Context SecurityContext securityContext) {
        Equipment equipment=equipmentUpdate.getId()!=null?service.getByIdOrNull(equipmentUpdate.getId(),Equipment.class,null,securityContext):null;
        if(equipment==null){
            throw new BadRequestException("no Equipment with id "+equipmentUpdate.getId());
        }
        equipmentUpdate.setEquipment(equipment);
        validateEquipmentCreate(equipmentUpdate,securityContext);


        return service.updateEquipment(equipmentUpdate,securityContext);

    }





}
