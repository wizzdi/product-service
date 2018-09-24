package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.containers.response.EquipmentShort;
import com.flexicore.product.interfaces.EquipmentShortListerInvoker;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentFiltering;
import com.flexicore.product.model.Gateway;
import com.flexicore.product.model.GatewayFiltering;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Equipment Invoker", description = "Invoker for Equipments")

public class EquipmentInvoker implements ListingInvoker<Equipment,EquipmentFiltering>,EquipmentShortListerInvoker<EquipmentShort,EquipmentFiltering> {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    public PaginationResponse<Equipment> listAll(EquipmentFiltering equipmentFiltering, SecurityContext securityContext) {
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

    @Override
    public PaginationResponse<EquipmentShort> listAllShort(EquipmentFiltering filter, SecurityContext securityContext) {
        return equipmentService.getAllEquipmentsShort(Equipment.class,filter,securityContext);
    }

    @Override
    public Class<EquipmentFiltering> getShortFilteringClass() {
        return EquipmentFiltering.class;
    }

    @Override
    public Class<EquipmentShort> getShortClass() {
        return EquipmentShort.class;
    }
}
