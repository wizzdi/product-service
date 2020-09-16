package com.flexicore.product.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.model.EquipmentLocation;
import com.flexicore.product.model.Event;
import com.flexicore.product.request.EquipmentLocationFiltering;
import com.flexicore.product.service.EquipmentLocationService;
import com.flexicore.product.service.EquipmentService;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

/**
 * Created by Asaf on 04/06/2017.
 */

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/EquipmentLocation")
@Tag(name = "EquipmentLocation")
@Extension
@Component
public class EquipmentLocationRESTService implements RestServicePlugin {


	@PluginInfo(version = 1)
	@Autowired
	private EquipmentLocationService service;


	private static final Logger logger= LoggerFactory.getLogger(EquipmentLocationRESTService.class);



	@POST
	@Produces("application/json")
	@Operation(summary = "getAllEquipmentLocations", description = "return EquipmentLocation Filtered")
	@Path("getAllEquipmentLocations")
	public PaginationResponse<EquipmentLocation> getAllEquipmentLocations(
			@HeaderParam("authenticationKey") String authenticationKey,
			EquipmentLocationFiltering equipmentLocationFiltering,
			@Context SecurityContext securityContext) {
		service.validateFiltering(equipmentLocationFiltering, securityContext);
		return service.getAllEquipmentLocations(equipmentLocationFiltering);

	}



}
