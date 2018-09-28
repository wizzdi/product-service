package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentFiltering;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Equipment Invoker", description = "Invoker for Equipments")

public class EquipmentInvoker implements ListingInvoker<Equipment,EquipmentFiltering>{

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    public PaginationResponse<Equipment> listAll(EquipmentFiltering equipmentFiltering, SecurityContext securityContext) {
        equipmentService.validateFiltering(equipmentFiltering,securityContext);
        return equipmentService.getAllEquipments(Equipment.class,equipmentFiltering,securityContext);
    }

    @Override
    public Class<EquipmentFiltering> getFilterClass() {
        return EquipmentFiltering.class;
    }

    @Override
    public Class<?> getHandlingClass() {
        return Equipment.class;
    }

}
