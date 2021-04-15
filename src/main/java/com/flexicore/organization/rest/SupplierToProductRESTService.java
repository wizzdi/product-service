package com.flexicore.organization.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.organization.model.Supplier;
import com.flexicore.organization.model.SupplierToProduct;
import com.flexicore.organization.model.Supplier_;
import com.flexicore.organization.request.*;
import com.flexicore.organization.service.SupplierToProductService;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@RequestScoped
@Path("plugins/Supplier")
@Tag(name = "Supplier")
@Extension
@Component
public class SupplierToProductRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private SupplierToProductService service;



	@POST
	@Produces("application/json")
	@Operation(summary = "listAllSuppliersToProductLinks", description = "Lists all Supplier links")
	@IOperation(Name = "listAllSuppliersToProductLinks", Description = "Lists all Supplier links")
	@Path("listAllSuppliersToProductLinks")
	public PaginationResponse<SupplierToProductContainer> listAllSuppliersToProductLinks(
			@HeaderParam("authenticationKey") String authenticationKey,
			SupplierToProductFilter filtering,
			@Context SecurityContext securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.listAllSuppliersToProductLinks(securityContext,
				filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/createSupplierToProduct")
	@Operation(summary = "createSupplierToProduct", description = "Creates SupplierToProduct")
	@IOperation(Name = "createSupplierToProduct", Description = "Creates SupplierToProduct")
	public SupplierToProduct createSupplierToProduct(
			@HeaderParam("authenticationKey") String authenticationKey,
			SupplierToProductCreate creationContainer,
			@Context SecurityContext securityContext) {

		service.validate(creationContainer, securityContext);

		return service.createSupplierToProduct(creationContainer,
				securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/updateSupplierToProduct")
	@Operation(summary = "updateSupplierToProduct", description = "Updates SupplierToProduct")
	@IOperation(Name = "updateSupplierToProduct", Description = "Updates SupplierToProduct")
	public SupplierToProduct updateSupplierToProduct(
			@HeaderParam("authenticationKey") String authenticationKey,
			SupplierToProductUpdate updateContainer,
			@Context SecurityContext securityContext) {
		SupplierToProduct supplierToProduct = service.getByIdOrNull(
				updateContainer.getId(), SupplierToProduct.class, null,
				securityContext);
		if (supplierToProduct == null) {
			throw new BadRequestException("no SupplierToProduct with id "
					+ updateContainer.getId());
		}
		updateContainer.setSupplierToProduct(supplierToProduct);

		service.validate(updateContainer, securityContext);

		return service.updateSupplierToProduct(updateContainer, securityContext);
	}

	@DELETE
	@Produces("application/json")
	@Path("/deleteSupplier/{id}")
	@Operation(summary = "deleteSupplier", description = "Deletes Supplier")
	public void deleteSupplier(
			@HeaderParam("authenticationKey") String authenticationKey,
			@PathParam("id") String id, @Context SecurityContext securityContext) {
		Supplier supplier = service.getByIdOrNull(id, Supplier.class, Supplier_.security,
				securityContext);
		if (supplier == null) {
			throw new BadRequestException("no Supplier with id " + id);
		}

		service.deleteSupplier(supplier, securityContext);
	}

}