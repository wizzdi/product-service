package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.model.territories.Street;
import com.flexicore.product.model.ProductType;
import com.flexicore.product.model.ProductTypeFiltering;
import com.flexicore.product.model.StreetFiltering;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Street Invoker", description = "Invoker for Street")

public class StreetInvoker implements ListingInvoker<Street, StreetFiltering> {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    @InvokerMethodInfo(displayName = "listAllStreets",description = "lists all Streets",relatedClasses = {Street.class})

    public PaginationResponse<Street> listAll(StreetFiltering streetFiltering, SecurityContext securityContext) {
        return equipmentService.getAllStreets(streetFiltering, securityContext);
    }

    @Override
    public Class<StreetFiltering> getFilterClass() {
        return StreetFiltering.class;
    }

    @Override
    public Class<?> getHandlingClass() {
        return Street.class;
    }
}
