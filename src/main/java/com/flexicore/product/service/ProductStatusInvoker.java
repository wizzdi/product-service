package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.model.ProductStatus;
import com.flexicore.product.model.ProductStatusFiltering;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "ProductStatus Invoker", description = "Invoker for Product Status")

public class ProductStatusInvoker implements ListingInvoker<ProductStatus,ProductStatusFiltering> {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    public PaginationResponse<ProductStatus> listAll(ProductStatusFiltering productStatusFiltering, SecurityContext securityContext) {
        equipmentService.validateProductStatusFiltering(productStatusFiltering,securityContext);
        return equipmentService.getAllProductStatus(productStatusFiltering, securityContext);
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
