package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.containers.request.UpdateProductStatus;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.ProductStatus;
import com.flexicore.product.model.ProductStatusFiltering;
import com.flexicore.security.SecurityContext;

import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "ProductStatus Invoker", description = "Invoker for Product Status")
@Extension
@Component
public class ProductStatusInvoker
		implements
			ListingInvoker<ProductStatus, ProductStatusFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentService equipmentService;

	@Override
	@InvokerMethodInfo(displayName = "listAllProductStatus", description = "lists all Product status", relatedClasses = {ProductStatus.class})
	public PaginationResponse<ProductStatus> listAll(
			ProductStatusFiltering productStatusFiltering,
			SecurityContext securityContext) {
		equipmentService.validateProductStatusFiltering(productStatusFiltering,
				securityContext);
		return equipmentService.getAllProductStatus(productStatusFiltering,
				null);
	}

	@Override
	public Class<ProductStatusFiltering> getFilterClass() {
		return ProductStatusFiltering.class;
	}

	@Override
	public Class<?> getHandlingClass() {
		return ProductStatus.class;
	}
}
