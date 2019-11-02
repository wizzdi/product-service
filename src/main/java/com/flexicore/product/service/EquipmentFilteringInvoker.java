package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.product.containers.request.GatewayCreate;
import com.flexicore.product.model.EquipmentFiltering;
import com.flexicore.product.model.Gateway;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "EquipmentFiltering Invoker", description = "Invoker for Product Status")

public class EquipmentFilteringInvoker implements ListingInvoker<EquipmentFiltering, FilteringInformationHolder> {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    @InvokerMethodInfo(displayName = "listAllEquipmentFiltering",description = "lists all Product status")

    public PaginationResponse<EquipmentFiltering> listAll(FilteringInformationHolder equipmentFilteringFiltering, SecurityContext securityContext) {
        return equipmentService.getAllEquipmentFiltering(equipmentFilteringFiltering, securityContext);
    }

    @InvokerMethodInfo(displayName = "create Equipment Filtering",description = "Creates Equipment Filtering")
    public EquipmentFiltering create(EquipmentFiltering equipmentFiltering, SecurityContext securityContext) {
        return equipmentService.createEquipmentFiltering(equipmentFiltering, securityContext);
    }



    @Override
    public Class<FilteringInformationHolder> getFilterClass() {
        return FilteringInformationHolder.class;
    }

    @Override
    public Class<?> getHandlingClass() {
        return EquipmentFiltering.class;
    }
}
