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

import javax.inject.Inject;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "ProductStatus Invoker", description = "Invoker for Product Status")

public class ProductStatusInvoker implements ListingInvoker<ProductStatus,ProductStatusFiltering> {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    @InvokerMethodInfo(displayName = "listAllProductStatus",description = "lists all Product status",relatedClasses = {ProductStatus.class})
    public PaginationResponse<ProductStatus> listAll(ProductStatusFiltering productStatusFiltering, SecurityContext securityContext) {
        equipmentService.validateProductStatusFiltering(productStatusFiltering,securityContext);
        return equipmentService.getAllProductStatus(productStatusFiltering, null);
    }

    @InvokerMethodInfo(displayName = "updateProductStatus",description = "updateProductStatus",relatedClasses = {ProductStatus.class})
    public boolean updateProductStatus(UpdateProductStatus updateProductStatus, SecurityContext securityContext) {
        equipmentService.validate(updateProductStatus,securityContext);
        return equipmentService.updateProductStatus(updateProductStatus,securityContext);
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
