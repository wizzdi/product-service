package com.flexicore.product.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;

import com.flexicore.annotations.ProtectedREST;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.model.FileResource;
import com.flexicore.product.containers.request.*;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.containers.response.EquipmentShort;
import com.flexicore.product.containers.response.EquipmentSpecificTypeGroup;
import com.flexicore.product.containers.response.EquipmentStatusGroup;
import com.flexicore.product.model.*;
import com.flexicore.product.request.*;
import com.flexicore.product.response.ProductStatusEntry;
import com.flexicore.product.service.EquipmentService;
import com.flexicore.product.service.EventService;
import com.flexicore.product.service.GroupService;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Asaf on 04/06/2017.
 */

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/Equipments")
@Tag(name = "Equipments")
@Extension
@Component
public class EquipmentRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentService service;

	@PluginInfo(version = 1)
	@Autowired
	private GroupService groupService;

	@PluginInfo(version = 1)
	@Autowired
	private EventService eventService;

	@Autowired
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
			EnableEquipments enableLights,
			@Context SecurityContext securityContext) {

		Set<String> ids = enableLights.getEquipmentIds();
		List<Equipment> lights = service
				.getEquipmentByIds(ids, securityContext);
		ids.removeAll(lights.parallelStream().map(f -> f.getId())
				.collect(Collectors.toSet()));
		if (!ids.isEmpty()) {
			throw new BadRequestException("No equipment with ids "
					+ ids.parallelStream().collect(Collectors.joining(",")));
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
			throw new BadRequestException(
					" Precision must be a value between 1 and 12");
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
		service.validate(productTypeCreate, securityContext);
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

		return service
				.getAllProductTypes(productTypeFiltering, securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createProductStatus", description = "Creates ProductStatus")
	@Path("createProductStatus")
	public ProductStatus createProductStatus(
			@HeaderParam("authenticationKey") String authenticationKey,
			ProductStatusCreate productStatusCreate,
			@Context SecurityContext securityContext) {

		return service.getOrCreateProductStatus(productStatusCreate,
				securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllProductStatus", description = "lists all ProductStatus")
	@Path("getAllProductStatus")
	public PaginationResponse<ProductStatus> getAllProductStatus(
			@HeaderParam("authenticationKey") String authenticationKey,
			ProductStatusFiltering productTypeFiltering,
			@Context SecurityContext securityContext) {
		service.validateProductStatusFiltering(productTypeFiltering,
				securityContext);

		return service.getAllProductStatus(productTypeFiltering,
				securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "getProductStatusForProducts", description = "lists all Equipment status")
	@Path("getProductStatusForProducts")
	public PaginationResponse<ProductStatusEntry> getProductStatusForProducts(
			@HeaderParam("authenticationKey") String authenticationKey,
			ProductStatusToProductFilter productTypeFiltering,
			@Context SecurityContext securityContext) {
		service.validate(productTypeFiltering, securityContext);

		return service.getProductStatusForProducts(productTypeFiltering,
				securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "getProductGroupedByStatus", description = "returns product stats grouped by status")
	@Path("getProductGroupedByStatus")
	public <T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatus(
			@HeaderParam("authenticationKey") String authenticationKey,
			EquipmentFiltering equipmentFiltering,
			@Context SecurityContext securityContext) {
		Class<T> c = service.validateFiltering(equipmentFiltering,
				securityContext);

		return service.getProductGroupedByStatus(c, equipmentFiltering,
				securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "getProductGroupedBySpecificType", description = "returns product stats grouped by specific type")
	@Path("getProductGroupedBySpecificType")
	public <T extends Equipment> List<EquipmentSpecificTypeGroup> getProductGroupedBySpecificType(
			@HeaderParam("authenticationKey") String authenticationKey,
			EquipmentFiltering equipmentFiltering,
			@Context SecurityContext securityContext) {
		Class<T> c = service.validateFiltering(equipmentFiltering,
				securityContext);

		return service.getProductGroupedBySpecificType(c, equipmentFiltering,
				securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "getProductGroupedByStatusAndTenant", description = "returns product stats grouped by status")
	@Path("getProductGroupedByStatusAndTenant")
	public <T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatusAndTenant(
			@HeaderParam("authenticationKey") String authenticationKey,
			EquipmentFiltering equipmentFiltering,
			@Context SecurityContext securityContext) {
		Class<T> c = service.validateFiltering(equipmentFiltering,
				securityContext);

		return service.getProductGroupedByStatusAndTenant(c,
				equipmentFiltering, securityContext);
	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "setProductToStatusLinksName", description = "returns product stats grouped by status")
	@Path("setProductToStatusLinksName")
	public void setProductToStatusLinksName(
			@HeaderParam("authenticationKey") String authenticationKey,
			@Context SecurityContext securityContext) {

		service.setProductToStatusLinksName();
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "getProductGroupedByStatusAndType", description = "returns product stats grouped by status")
	@Path("getProductGroupedByStatusAndType")
	public <T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatusAndType(
			@HeaderParam("authenticationKey") String authenticationKey,
			EquipmentFiltering equipmentFiltering,
			@Context SecurityContext securityContext) {
		Class<T> c = service.validateFiltering(equipmentFiltering,
				securityContext);

		return service.getProductGroupedByStatusAndType(c, equipmentFiltering,
				securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createEquipmentStatusEvent", description = "create Equipment status Event")
	@Path("createEquipmentStatusEvent")
	public List<EquipmentByStatusEvent> createEquipmentStatusEvent(
			@HeaderParam("authenticationKey") String authenticationKey,
			EquipmentFiltering inspectLights,
			@Context SecurityContext securityContext) {

		return service.createEquipmentStatusEvent(inspectLights,
				securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "disableGateway", description = "disables Gateway")
	@Path("disableGateway")
	public <T extends Equipment> List<EquipmentStatusGroup> disableGateway(
			@HeaderParam("authenticationKey") String authenticationKey,
			GatewayFiltering equipmentFiltering,
			@Context SecurityContext securityContext) {
		Class<T> c = service.validateFiltering(equipmentFiltering,
				securityContext);

		return service.getProductGroupedByStatus(c, equipmentFiltering,
				securityContext);
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
				logger.log(Level.SEVERE, "unable to get class: "
						+ equipmentCreate.getClazzName(), e);
				throw new BadRequestException("No Class with name "
						+ equipmentCreate.getClazzName());
			}
		}
		return service.createEquipment(c, equipmentCreate, securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createFCGateway", description = "Creates FC Gateway")
	@Path("createFCGateway")
	public FlexiCoreGateway createFCGateway(
			@HeaderParam("authenticationKey") String authenticationKey,
			FlexiCoreGatewayCreate equipmentCreate,
			@Context SecurityContext securityContext) {
		service.validateCreate(equipmentCreate, securityContext);

		return service.createFlexiCoreGateway(equipmentCreate, securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "massUpsertLatLons", description = "upsert latlon")
	@Path("massUpsertLatLons")
	public List<LatLon> massUpsertLatLons(
			@HeaderParam("authenticationKey") String authenticationKey,
			MassUpsertLatLonRequest massUpsertLatLonRequest,
			@Context SecurityContext securityContext) {

		service.validate(massUpsertLatLonRequest, securityContext);

		return service.massUpsertLatLons(massUpsertLatLonRequest,
				securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createLatLon", description = "Creates LatLon")
	@Path("createLatLon")
	public LatLon createLatLon(
			@HeaderParam("authenticationKey") String authenticationKey,
			CreateLatLon createLatLon, @Context SecurityContext securityContext) {
		service.validateLatLon(createLatLon, securityContext);

		return service.createLatLon(createLatLon, securityContext);
	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "updateLatLon", description = "Updates LatLon")
	@Path("updateLatLon")
	public LatLon updateLatLon(
			@HeaderParam("authenticationKey") String authenticationKey,
			UpdateLatLon updateLatLon, @Context SecurityContext securityContext) {
		service.validateLatLon(updateLatLon, securityContext);

		LatLon latLon = service.getByIdOrNull(updateLatLon.getId(),
				LatLon.class, null, securityContext);
		if (latLon == null) {
			throw new BadRequestException("No Lat Lon with id "
					+ updateLatLon.getId());
		}
		return service.updateLatLon(updateLatLon, securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllLatLons", description = "return lat lons")
	@Path("getAllLatLons")
	public PaginationResponse<LatLon> getAllLatLons(
			@HeaderParam("authenticationKey") String authenticationKey,
			LatLonFilter latLonFilter, @Context SecurityContext securityContext) {
		service.validateLatLon(latLonFilter, securityContext);
		return service.getAllLatLons(latLonFilter, securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "updateFCGateway", description = "Updates FC Gateway")
	@Path("updateFCGateway")
	public FlexiCoreGateway updateFCGateway(
			@HeaderParam("authenticationKey") String authenticationKey,
			FlexiCoreGatewayUpdate equipmentCreate,
			@Context SecurityContext securityContext) {
		service.validate(equipmentCreate, securityContext);

		return service.updateFlexiCoreGateway(equipmentCreate, securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "linkToGroup", description = "Links Equipment to Group")
	@Path("linkToGroup")
	public EquipmentToGroup linkToGroup(
			@HeaderParam("authenticationKey") String authenticationKey,
			LinkToGroup linkToGroup, @Context SecurityContext securityContext) {
		Equipment equipment = linkToGroup.getEquipmentId() != null ? service
				.getByIdOrNull(linkToGroup.getEquipmentId(), Equipment.class,
						null, securityContext) : null;
		if (equipment == null) {
			throw new BadRequestException("no Equipment with id "
					+ linkToGroup.getEquipmentId());
		}
		linkToGroup.setEquipment(equipment);
		EquipmentGroup equipmentGroup = linkToGroup.getGroupId() != null
				? groupService.getByIdOrNull(linkToGroup.getGroupId(),
						EquipmentGroup.class, null, securityContext) : null;
		if (equipmentGroup == null) {
			throw new BadRequestException("no Equipment group with id "
					+ linkToGroup.getGroupId());
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
		String id = updateProductType.getId();
		ProductType productType = id != null ? service.getByIdOrNull(id,
				ProductType.class, null, securityContext) : null;
		if (productType == null) {
			throw new BadRequestException("no ProductType with id " + id);
		}
		updateProductType.setProductType(productType);
		service.validate(updateProductType, securityContext);
		return service.updateProductType(updateProductType, securityContext);

	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "updateProductStatusToType", description = "Updates product status to type link ")
	@Path("updateProductStatusToType")
	public ProductTypeToProductStatus updateProductStatusToType(
			@HeaderParam("authenticationKey") String authenticationKey,
			UpdateProductStatusToType updateProductStatus,
			@Context SecurityContext securityContext) {
		ProductStatus productStatus = updateProductStatus.getProductStatusId() != null
				? service.getByIdOrNull(
						updateProductStatus.getProductStatusId(),
						ProductStatus.class, null, securityContext) : null;
		if (productStatus == null) {
			throw new BadRequestException("no productStatus with id "
					+ updateProductStatus.getProductStatusId());
		}
		updateProductStatus.setProductStatus(productStatus);

		ProductType productType = updateProductStatus.getProductTypeId() != null
				? service.getByIdOrNull(updateProductStatus.getProductTypeId(),
						ProductType.class, null, securityContext) : null;
		if (productType == null) {
			throw new BadRequestException("no productType with id "
					+ updateProductStatus.getProductTypeId());
		}
		updateProductStatus.setProductType(productType);
		FileResource newIcon = updateProductStatus.getIconId() != null
				? service.getByIdOrNull(updateProductStatus.getIconId(),
						FileResource.class, null, securityContext) : null;
		if (newIcon == null) {
			throw new BadRequestException("no file resource with id "
					+ updateProductStatus.getIconId());
		}
		updateProductStatus.setIcon(newIcon);

		return service.updateProductStatusToType(updateProductStatus,
				securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllDetailedEquipmentStatus", description = "return Detailed Equipment Status ")
	@Path("getAllDetailedEquipmentStatus")
	public PaginationResponse<DetailedEquipmentStatus> getAllDetailedEquipmentStatus(
			@HeaderParam("authenticationKey") String authenticationKey,
			DetailedEquipmentFilter filter,
			@Context SecurityContext securityContext) {
		service.validate(filter);
		return service.getAllDetailedEquipmentStatus(filter);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllEquipmentByStatusEntry", description = "return getAllEquipmentByStatusEntry ")
	@Path("getAllEquipmentByStatusEntry")
	public PaginationResponse<EquipmentByStatusEntry> getAllEquipmentByStatusEntry(
			@HeaderParam("authenticationKey") String authenticationKey,
			EquipmentByStatusEntryFiltering filter,
			@Context SecurityContext securityContext) {
		return service.getAllEquipmentByStatusEntries(filter);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllProductTypeToProductStatus", description = "return status links ")
	@Path("getAllProductTypeToProductStatus")
	public PaginationResponse<ProductTypeToProductStatus> getAllProductTypeToProductStatus(
			@HeaderParam("authenticationKey") String authenticationKey,
			ProductTypeToProductStatusFilter filter,
			@Context SecurityContext securityContext) {
		service.validate(filter, securityContext);

		return service
				.getAllProductTypeToProductStatus(filter, securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "updateEquipment", description = "Updates Equipment")
	@Path("updateEquipment")
	public Equipment updateEquipment(
			@HeaderParam("authenticationKey") String authenticationKey,
			EquipmentUpdate equipmentUpdate,
			@Context SecurityContext securityContext) {
		Equipment equipment = equipmentUpdate.getId() != null ? service
				.getByIdOrNull(equipmentUpdate.getId(), Equipment.class, null,
						securityContext) : null;
		if (equipment == null) {
			throw new BadRequestException("no Equipment with id "
					+ equipmentUpdate.getId());
		}
		equipmentUpdate.setEquipment(equipment);
		service.validateEquipmentCreate(equipmentUpdate, securityContext);

		return service.updateEquipment(equipmentUpdate, securityContext);

	}

}
