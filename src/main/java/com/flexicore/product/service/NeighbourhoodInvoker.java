package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.Street;
import com.flexicore.product.model.NeighbourhoodFiltering;
import com.flexicore.product.model.StreetFiltering;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Street Neighbourhood", description = "Invoker for Neighbourhood")

public class NeighbourhoodInvoker implements ListingInvoker<Neighbourhood, NeighbourhoodFiltering> {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    @InvokerMethodInfo(displayName = "listAllNeighbourhoods",description = "lists all Neighbourhoods",relatedClasses = {Neighbourhood.class})

    public PaginationResponse<Neighbourhood> listAll(NeighbourhoodFiltering neighbourhoodFiltering, SecurityContext securityContext) {
        return equipmentService.getAllNeighbourhoods(neighbourhoodFiltering, securityContext);
    }

    @Override
    public Class<NeighbourhoodFiltering> getFilterClass() {
        return NeighbourhoodFiltering.class;
    }

    @Override
    public Class<?> getHandlingClass() {
        return Neighbourhood.class;
    }
}
