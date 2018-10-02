package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.product.model.Gateway;
import com.flexicore.product.model.GatewayFiltering;
import com.flexicore.product.model.GroupFiltering;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Gateway Invoker", description = "Invoker for Gateways")

public class GatewayInvoker implements ListingInvoker<Gateway,GatewayFiltering> {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    @InvokerMethodInfo(displayName = "listAllGateways",description = "lists all Gateways")
    public PaginationResponse<Gateway> listAll(GatewayFiltering gatewayFiltering, SecurityContext securityContext) {
        equipmentService.validateFiltering(gatewayFiltering,securityContext);
        return equipmentService.getAllGateways(gatewayFiltering, securityContext);
    }

    @Override
    public Class<GatewayFiltering> getFilterClass() {
        return GatewayFiltering.class;
    }

    @Override
    public Class<?> getHandlingClass() {
        return Gateway.class;
    }
}
