package com.flexicore.product.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.model.FileResource;
import com.flexicore.product.containers.request.*;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.containers.response.EquipmentShort;
import com.flexicore.product.containers.response.EquipmentStatusGroup;
import com.flexicore.product.model.*;
import com.flexicore.product.service.EquipmentService;
import com.flexicore.product.service.EventService;
import com.flexicore.product.service.GroupService;
import com.flexicore.product.service.IOTService;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
@OpenAPIDefinition(tags = {
        @Tag(name = "Events", description = "Events Services"),
        @Tag(name = "Equipments", description = "Equipments Services"),
        @Tag(name = "EquipmentGroups", description = "EquipmentGroups Services")

})
@Tag(name = "Equipments")

public class EquipmentRESTService implements RestServicePlugin {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService service;

    @Inject
    @PluginInfo(version = 1)
    private GroupService groupService;

    @Inject
    @PluginInfo(version = 1)
    private EventService eventService;

    @Inject
    @PluginInfo(version = 1)
    private IOTService iotService;

    @Inject
    private Logger logger;


    @POST
    @Produces("application/json")
    @Operation(summary = "getAllEquipments", description = "Gets All Equipments Filtered")
    @Path("getAllEquipments")
    public <T extends Equipment> PaginationResponse<T> getAllEquipments(
            @HeaderParam("authenticationKey") String authenticationKey,
            EquipmentFiltering filtering,
            @Context SecurityContext securityContext) {
        Class<T> c = service.validateFiltering(filtering, securityContext);

        return service.getAllEquipments(c, filtering, securityContext);
    }


    @PUT
    @Produces("application/json")
    @Operation(summary = "enableEquipment", description = "enable Equipment")
    @Path("enableEquipment")
    public List<Equipment> enableEquipment(
            @HeaderParam("authenticationKey") String authenticationKey,
            EnableEquipments enableLights, @Context SecurityContext securityContext) {

        Set<String> ids = enableLights.getEquipmentIds();
        List<Equipment> lights = service.getEquipmentByIds(ids, securityContext);
        ids.removeAll(lights.parallelStream().map(f -> f.getId()).collect(Collectors.toSet()));
        if (!ids.isEmpty()) {
            throw new BadRequestException("No equipment with ids " + ids.parallelStream().collect(Collectors.joining(",")));
        }
        enableLights.setEquipmentList(lights);
        return service.enableEquipment(enableLights, securityContext);

    }


    @POST
    @Produces("application/json")
    @Operation(summary = "getAllEquipmentsShort", description = "Gets All Equipments (short) Filtered")
    @Path("getAllEquipmentsShort")
    public <T extends Equipment> PaginationResponse<EquipmentShort> getAllEquipmentsShort(
            @HeaderParam("authenticationKey") String authenticationKey,
            EquipmentFiltering filtering,
            @Context SecurityContext securityContext) {
        Class<T> c = service.validateFiltering(filtering, securityContext);

        return service.getAllEquipmentsShort(c, filtering, securityContext);
    }


    @POST
    @Produces("application/json")
    @Operation(summary = "getAllEquipments", description = "Gets All Equipments Filtered")
    @Path("countAllEquipments")
    public <T extends Equipment> long countAllEquipments(
            @HeaderParam("authenticationKey") String authenticationKey,
            EquipmentFiltering filtering,
            @Context SecurityContext securityContext) {
        Class<T> c = service.validateFiltering(filtering, securityContext);

        return service.countAllEquipments(c, filtering, securityContext);
    }


    @POST
    @Produces("application/json")
    @Operation(summary = "getAllEquipmentsGrouped", description = "Gets All Equipments Filtered and Grouped")
    @Path("getAllEquipmentsGrouped")
    public <T extends Equipment> PaginationResponse<EquipmentGroupHolder> getAllEquipmentsGrouped(
            @HeaderParam("authenticationKey") String authenticationKey,
            EquipmentGroupFiltering filtering,
            @Context SecurityContext securityContext) {
        Class<T> c = service.validateFiltering(filtering, securityContext);
        if (filtering.getPrecision() > 12 || filtering.getPrecision() < 1) {
            throw new BadRequestException(" Precision must be a value between 1 and 12");
        }

        return service.getAllEquipmentsGrouped(c, filtering, securityContext);
    }

    @POST
    @Produces("application/json")
    @Operation(summary = "createProductType", description = "Creates ProductType")
    @Path("createProductType")
    public ProductType createProductType(
            @HeaderParam("authenticationKey") String authenticationKey,
            ProductTypeCreate productTypeCreate,
            @Context SecurityContext securityContext) {

        return service.createProductType(productTypeCreate, securityContext);
    }

    @POST
    @Produces("application/json")
    @Operation(summary = "getAllProductTypes", description = "lists all ProductTypes")
    @Path("getAllProductTypes")
    public PaginationResponse<ProductType> getAllProductTypes(
            @HeaderParam("authenticationKey") String authenticationKey,
            ProductTypeFiltering productTypeFiltering,
            @Context SecurityContext securityContext) {

        return service.getAllProductTypes(productTypeFiltering, securityContext);
    }


    @POST
    @Produces("application/json")
    @Operation(summary = "createProductStatus", description = "Creates ProductStatus")
    @Path("createProductStatus")
    public ProductStatus createProductStatus(
            @HeaderParam("authenticationKey") String authenticationKey,
            ProductStatusCreate productStatusCreate,
            @Context SecurityContext securityContext) {

        return service.getOrCreateProductStatus(productStatusCreate, securityContext);
    }

    @POST
    @Produces("application/json")
    @Operation(summary = "getAllProductStatus", description = "lists all ProductStatus")
    @Path("getAllProductStatus")
    public PaginationResponse<ProductStatus> getAllProductStatus(
            @HeaderParam("authenticationKey") String authenticationKey,
            ProductStatusFiltering productTypeFiltering,
            @Context SecurityContext securityContext) {
        service.validateProductStatusFiltering(productTypeFiltering, securityContext);

        return service.getAllProductStatus(productTypeFiltering, securityContext);
    }


    @POST
    @Produces("application/json")
    @Operation(summary = "getProductGroupedByStatus", description = "returns product stats grouped by status")
    @Path("getProductGroupedByStatus")
    public <T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatus(
            @HeaderParam("authenticationKey") String authenticationKey,
            EquipmentFiltering equipmentFiltering,
            @Context SecurityContext securityContext) {
        Class<T> c = service.validateFiltering(equipmentFiltering, securityContext);

        return service.getProductGroupedByStatus(c, equipmentFiltering, securityContext);
    }

    @POST
    @Produces("application/json")
    @Operation(summary = "disableGateway", description = "disables Gateway")
    @Path("disableGateway")
    public <T extends Equipment> List<EquipmentStatusGroup> disableGateway(
            @HeaderParam("authenticationKey") String authenticationKey,
            GatewayFiltering equipmentFiltering,
            @Context SecurityContext securityContext) {
        Class<T> c = service.validateFiltering(equipmentFiltering, securityContext);

        return service.getProductGroupedByStatus(c, equipmentFiltering, securityContext);
    }

    @POST
    @Produces("application/json")
    @Operation(summary = "createEquipment", description = "Creates Equipment")
    @Path("createEquipment")
    public <T extends Equipment> T createEquipment(
            @HeaderParam("authenticationKey") String authenticationKey,
            EquipmentCreate equipmentCreate,
            @Context SecurityContext securityContext) {
        service.validateEquipmentCreate(equipmentCreate, securityContext);
        Class<T> c = (Class<T>) Equipment.class;
        if (equipmentCreate.getClazzName() != null) {
            try {
                c = (Class<T>) Class.forName(equipmentCreate.getClazzName());
            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE, "unable to get class: " + equipmentCreate.getClazzName(), e);
                throw new BadRequestException("No Class with name " + equipmentCreate.getClazzName());
            }
        }
        return service.createEquipment(c, equipmentCreate, securityContext);
    }



    @POST
    @Produces("application/json")
    @Operation(summary = "linkToGroup", description = "Links Equipment to Group")
    @Path("linkToGroup")
    public EquipmentToGroup linkToGroup(
            @HeaderParam("authenticationKey") String authenticationKey,
            LinkToGroup linkToGroup,
            @Context SecurityContext securityContext) {
        Equipment equipment = linkToGroup.getEquipmentId() != null ? service.getByIdOrNull(linkToGroup.getEquipmentId(), Equipment.class, null, securityContext) : null;
        if (equipment == null) {
            throw new BadRequestException("no Equipment with id " + linkToGroup.getEquipmentId());
        }
        linkToGroup.setEquipment(equipment);
        EquipmentGroup equipmentGroup = linkToGroup.getGroupId() != null ? groupService.getByIdOrNull(linkToGroup.getGroupId(), EquipmentGroup.class, null, securityContext) : null;
        if (equipmentGroup == null) {
            throw new BadRequestException("no Equipment group with id " + linkToGroup.getGroupId());
        }
        linkToGroup.setEquipmentGroup(equipmentGroup);
        return service.createEquipmentToGroup(linkToGroup, securityContext);
    }


    @PUT
    @Produces("application/json")
    @Operation(summary = "updateProductType", description = "Updates product type")
    @Path("updateProductType")
    public ProductType updateProductType(
            @HeaderParam("authenticationKey") String authenticationKey,
            UpdateProductType updateProductType,
            @Context SecurityContext securityContext) {
        ProductType productStatus = updateProductType.getId() != null ? service.getByIdOrNull(updateProductType.getId(), ProductType.class, null, securityContext) : null;
        if (productStatus == null) {
            throw new BadRequestException("no productStatus with id " + updateProductType.getId());
        }
        updateProductType.setProductType(productStatus);
        FileResource newIcon = updateProductType.getIconId() != null ? service.getByIdOrNull(updateProductType.getIconId(), FileResource.class, null, securityContext) : null;
        if (newIcon == null && updateProductType.getIconId() != null) {
            throw new BadRequestException("no file resource with id " + updateProductType.getIconId());
        }
        updateProductType.setIcon(newIcon);


        return service.updateProductType(updateProductType, securityContext);

    }


    @PUT
    @Produces("application/json")
    @Operation(summary = "updateProductStatus", description = "Updates product status")
    @Path("updateProductStatus")
    public void updateProductStatus(
            @HeaderParam("authenticationKey") String authenticationKey,
            UpdateProductStatus updateProductStatus,
            @Context SecurityContext securityContext) {
        ProductStatus productStatus = updateProductStatus.getStatusId() != null ? service.getByIdOrNull(updateProductStatus.getStatusId(), ProductStatus.class, null, securityContext) : null;
        if (productStatus == null) {
            throw new BadRequestException("no productStatus with id " + updateProductStatus.getStatusId());
        }
        updateProductStatus.setProductStatus(productStatus);

        Equipment equipment = updateProductStatus.getEquipmentId() != null ? service.getByIdOrNull(updateProductStatus.getEquipmentId(), Equipment.class, null, securityContext) : null;
        if (equipment == null) {
            throw new BadRequestException("no Equipment with id " + updateProductStatus.getEquipmentId());
        }
        updateProductStatus.setEquipment(equipment);


        if (service.updateProductStatus(updateProductStatus, securityContext)) {
            Event event = new Event(equipment)
                    .setStatusIds(new HashSet<>(Arrays.asList(updateProductStatus.getProductStatus().getId())));
            eventService.merge(event);

        }

    }

    @PUT
    @Produces("application/json")
    @Operation(summary = "updateProductStatusToType", description = "Updates product status to type link ")
    @Path("updateProductStatusToType")
    public ProductTypeToProductStatus updateProductStatusToType(
            @HeaderParam("authenticationKey") String authenticationKey,
            UpdateProductStatusToType updateProductStatus,
            @Context SecurityContext securityContext) {
        ProductStatus productStatus = updateProductStatus.getProductStatusId() != null ? service.getByIdOrNull(updateProductStatus.getProductStatusId(), ProductStatus.class, null, securityContext) : null;
        if (productStatus == null) {
            throw new BadRequestException("no productStatus with id " + updateProductStatus.getProductStatusId());
        }
        updateProductStatus.setProductStatus(productStatus);

        ProductType productType = updateProductStatus.getProductTypeId() != null ? service.getByIdOrNull(updateProductStatus.getProductTypeId(), ProductType.class, null, securityContext) : null;
        if (productType == null) {
            throw new BadRequestException("no productType with id " + updateProductStatus.getProductTypeId());
        }
        updateProductStatus.setProductType(productType);
        FileResource newIcon = updateProductStatus.getIconId() != null ? service.getByIdOrNull(updateProductStatus.getIconId(), FileResource.class, null, securityContext) : null;
        if (newIcon == null) {
            throw new BadRequestException("no file resource with id " + updateProductStatus.getIconId());
        }
        updateProductStatus.setIcon(newIcon);


        return service.updateProductStatusToType(updateProductStatus, securityContext);

    }


    @POST
    @Produces("application/json")
    @Operation(summary = "updateEquipment", description = "Updates Equipment")
    @Path("updateEquipment")
    public Equipment updateEquipment(
            @HeaderParam("authenticationKey") String authenticationKey,
            EquipmentUpdate equipmentUpdate,
            @Context SecurityContext securityContext) {
        Equipment equipment = equipmentUpdate.getId() != null ? service.getByIdOrNull(equipmentUpdate.getId(), Equipment.class, null, securityContext) : null;
        if (equipment == null) {
            throw new BadRequestException("no Equipment with id " + equipmentUpdate.getId());
        }
        equipmentUpdate.setEquipment(equipment);
        service.validateEquipmentCreate(equipmentUpdate, securityContext);


        return service.updateEquipment(equipmentUpdate, securityContext);

    }


}
