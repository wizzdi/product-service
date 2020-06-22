package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.ProductType;
import com.flexicore.product.model.ProductTypeFiltering;
import com.flexicore.security.SecurityContext;

import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "ProductType Invoker", description = "Invoker for Product Type")
@Extension
@Component
public class ProductTypeInvoker
		implements
			ListingInvoker<ProductType, ProductTypeFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentService equipmentService;

	@Override
	@InvokerMethodInfo(displayName = "listAllProductTypes", description = "lists all Product types", relatedClasses = {ProductType.class})
	public PaginationResponse<ProductType> listAll(
			ProductTypeFiltering productTypeFiltering,
			SecurityContext securityContext) {
		equipmentService.validate(productTypeFiltering, securityContext);
		return equipmentService.getAllProductTypes(productTypeFiltering,
				securityContext);
	}

	@Override
	public Class<ProductTypeFiltering> getFilterClass() {
		return ProductTypeFiltering.class;
	}

	@Override
	public Class<?> getHandlingClass() {
		return ProductType.class;
	}
}
