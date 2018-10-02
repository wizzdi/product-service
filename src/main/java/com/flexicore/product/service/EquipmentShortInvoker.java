package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.containers.response.EquipmentShort;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentFiltering;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Equipment short Invoker", description = "Invoker for Equipments short")

public class EquipmentShortInvoker implements ListingInvoker<EquipmentShort,EquipmentFiltering>{

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    @InvokerMethodInfo(displayName = "listAllEquipmentShort",description = "lists all Equipment Short")

    public PaginationResponse<EquipmentShort> listAll(EquipmentFiltering equipmentFiltering, SecurityContext securityContext) {
        equipmentService.validateFiltering(equipmentFiltering,securityContext);
        return equipmentService.getAllEquipmentsShort(Equipment.class,equipmentFiltering,securityContext);
    }

    @Override
    public Class<EquipmentFiltering> getFilterClass() {
        return EquipmentFiltering.class;
    }

    @Override
    public Class<?> getHandlingClass() {
        return EquipmentShort.class;
    }


}
