package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.model.ProductType;
import com.flexicore.product.model.ProductTypeFiltering;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "ProductType Invoker", description = "Invoker for Product Type")

public class ProductTypeInvoker implements ListingInvoker<ProductType,ProductTypeFiltering> {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    public PaginationResponse<ProductType> listAll(ProductTypeFiltering productTypeFiltering, SecurityContext securityContext) {
        return equipmentService.getAllProductTypes(productTypeFiltering, securityContext);
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
