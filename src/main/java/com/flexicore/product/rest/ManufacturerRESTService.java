package com.flexicore.product.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;

import com.flexicore.annotations.ProtectedREST;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.organization.model.Manufacturer;
import com.flexicore.organization.model.Manufacturer_;
import com.flexicore.product.request.ManufacturerCreate;
import com.flexicore.product.request.ManufacturerFiltering;
import com.flexicore.product.request.ManufacturerUpdate;
import com.flexicore.product.service.ManufacturerService;
import com.flexicore.security.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
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
@Path("plugins/Manufacturer")
@Tag(name = "Manufacturer")
@Extension
@Component
public class ManufacturerRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private ManufacturerService service;

	@Autowired
	private Logger logger;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllEquipments", description = "Gets All Equipments Filtered")
	@Path("getAllEquipmentManufacturers")
	public PaginationResponse<Manufacturer> getAllEquipmentManufacturers(
			@HeaderParam("authenticationKey") String authenticationKey,
			ManufacturerFiltering filtering,
			@Context SecurityContext securityContext) {
		service.validateManufacturerFiltering(filtering, securityContext);
		return service.getAllManufacturers(filtering, securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createManufacturer", description = "Creates Equipment Manufacturer")
	@Path("createManufacturer")
	public Manufacturer createManufacturer(
			@HeaderParam("authenticationKey") String authenticationKey,
			ManufacturerCreate manufacturerCreate,
			@Context SecurityContext securityContext) {
		service.validate(manufacturerCreate, securityContext);

		return service.createManufacturer(manufacturerCreate, securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "updateManufacturer", description = "Updates Equipment Manufacturer")
	@Path("updateManufacturer")
	public Manufacturer updateManufacturer(
			@HeaderParam("authenticationKey") String authenticationKey,
			ManufacturerUpdate manufacturerUpdate,
			@Context SecurityContext securityContext) {

		Manufacturer manufacturer = service.getByIdOrNull(
				manufacturerUpdate.getId(), Manufacturer.class, Manufacturer_.security,
				securityContext);
		if (manufacturer == null) {
			throw new BadRequestException("No Manufacturer With id "
					+ manufacturerUpdate.getId());
		}
		manufacturerUpdate.setManufacturer(manufacturer);
		service.validate(manufacturerUpdate, securityContext);
		return service.updateManufacturer(manufacturerUpdate, securityContext);

	}

}
