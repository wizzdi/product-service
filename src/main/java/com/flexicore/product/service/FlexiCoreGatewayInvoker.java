package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.containers.request.FlexiCoreGatewayCreate;
import com.flexicore.product.containers.request.GatewayCreate;
import com.flexicore.product.model.FlexiCoreGateway;
import com.flexicore.product.model.FlexiCoreGatewayFiltering;
import com.flexicore.product.model.Gateway;
import com.flexicore.product.model.GatewayFiltering;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "FlexiCore Gateway Invoker", description = "Invoker for FlexiCore Gateways")

public class FlexiCoreGatewayInvoker implements ListingInvoker<FlexiCoreGateway, FlexiCoreGatewayFiltering> {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    @InvokerMethodInfo(displayName = "listAllGateways",description = "lists all Gateways",relatedClasses = {Gateway.class})
    public PaginationResponse<FlexiCoreGateway> listAll(FlexiCoreGatewayFiltering gatewayFiltering, SecurityContext securityContext) {
        equipmentService.validateFiltering(gatewayFiltering,securityContext);
        return equipmentService.getAllFlexiCoreGateways(gatewayFiltering, securityContext);
    }

    @InvokerMethodInfo(displayName = "create FlexiCore Gateway",description = "Creates FlexiCoreGateway",relatedClasses = {FlexiCoreGateway.class})
    public FlexiCoreGateway create(FlexiCoreGatewayCreate gatewayCreate, SecurityContext securityContext) {
        equipmentService.validateEquipmentCreate(gatewayCreate,securityContext);
        return equipmentService.createFlexiCoreGateway(gatewayCreate, securityContext);
    }

    @Override
    public Class<FlexiCoreGatewayFiltering> getFilterClass() {
        return FlexiCoreGatewayFiltering.class;
    }

    @Override
    public Class<?> getHandlingClass() {
        return FlexiCoreGateway.class;
    }
}
