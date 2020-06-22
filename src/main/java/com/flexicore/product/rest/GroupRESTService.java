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
import com.flexicore.product.service.GroupService;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.logging.Logger;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Asaf on 04/06/2017.
 */

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/EquipmentGroups")
@Tag(name = "EquipmentGroups")
@Extension
@Component
public class GroupRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private GroupService service;

	@Autowired
	private Logger logger;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllEquipments", description = "Gets All Equipments Filtered")
	@Path("getAllEquipmentGroups")
	public PaginationResponse<EquipmentGroup> getAllEquipmentGroups(
			@HeaderParam("authenticationKey") String authenticationKey,
			GroupFiltering filtering, @Context SecurityContext securityContext) {
		service.validateGroupFiltering(filtering, securityContext);
		return service.getAllEquipmentGroups(filtering, securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "getRootEquipmentGroup", description = "return Root EquipmentGroupHolder")
	@Path("getRootEquipmentGroup")
	public EquipmentGroup getRootEquipmentGroup(
			@HeaderParam("authenticationKey") String authenticationKey,
			@Context SecurityContext securityContext) {

		return service.getRootEquipmentGroup(securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createGroup", description = "Creates Equipment Group")
	@Path("createGroup")
	public EquipmentGroup createGroup(
			@HeaderParam("authenticationKey") String authenticationKey,
			GroupCreate groupCreate, @Context SecurityContext securityContext) {
		EquipmentGroup equipmentGroup = groupCreate.getParentId() != null
				? service.getByIdOrNull(groupCreate.getParentId(),
						EquipmentGroup.class, null, securityContext) : null;
		if (equipmentGroup == null && groupCreate.getParentId() != null) {
			throw new BadRequestException("No Equipment Group with id "
					+ groupCreate.getParentId());
		}
		groupCreate.setParent(equipmentGroup);

		return service.createGroup(groupCreate, securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "updateGroup", description = "Updates Equipment Group")
	@Path("updateGroup")
	public EquipmentGroup updateGroup(
			@HeaderParam("authenticationKey") String authenticationKey,
			GroupUpdate groupUpdate, @Context SecurityContext securityContext) {
		EquipmentGroup equipmentGroup = groupUpdate.getId() != null ? service
				.getByIdOrNull(groupUpdate.getId(), EquipmentGroup.class, null,
						securityContext) : null;
		if (equipmentGroup == null) {
			throw new BadRequestException("no Equipment group with id "
					+ groupUpdate.getId());
		}
		groupUpdate.setEquipmentGroup(equipmentGroup);
		EquipmentGroup parent = groupUpdate.getParentId() != null ? service
				.getByIdOrNull(groupUpdate.getParentId(), EquipmentGroup.class,
						null, securityContext) : null;
		if (parent == null && groupUpdate.getParentId() != null) {
			throw new BadRequestException("No Equipment Group with id "
					+ groupUpdate.getParentId());
		}
		groupUpdate.setParent(equipmentGroup);
		return service.updateGroup(groupUpdate, securityContext);

	}

}
