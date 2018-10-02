package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentGroupFiltering;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Equipment grouped Invoker", description = "Invoker for Equipments grouped")

public class EquipmentGroupedInvoker implements ListingInvoker<EquipmentGroupHolder, EquipmentGroupFiltering> {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    @InvokerMethodInfo(displayName = "listAllEquipmentGeoHashes",description = "lists all Equipment Geo Hashed",relatedClasses = {Equipment.class})

    public PaginationResponse<EquipmentGroupHolder> listAll(EquipmentGroupFiltering equipmentGroupFiltering, SecurityContext securityContext) {
        equipmentService.validateFiltering(equipmentGroupFiltering,securityContext);
        return equipmentService.getAllEquipmentsGrouped(Equipment.class,equipmentGroupFiltering,securityContext);
    }

    @Override
    public Class<EquipmentGroupFiltering> getFilterClass() {
        return EquipmentGroupFiltering.class;
    }

    @Override
    public Class<?> getHandlingClass() {
        return EquipmentGroupHolder.class;
    }


}
