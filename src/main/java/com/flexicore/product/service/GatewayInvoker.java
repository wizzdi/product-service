package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.containers.request.GatewayCreate;
import com.flexicore.product.model.*;
import com.flexicore.security.SecurityContext;

import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Gateway Invoker", description = "Invoker for Gateways")
@Extension
@Component
public class GatewayInvoker
		implements
			ListingInvoker<Gateway, GatewayFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentService equipmentService;

	@Override
	@InvokerMethodInfo(displayName = "listAllGateways", description = "lists all Gateways", relatedClasses = {Gateway.class})
	public PaginationResponse<Gateway> listAll(
			GatewayFiltering gatewayFiltering, SecurityContext securityContext) {
		equipmentService.validateFiltering(gatewayFiltering, securityContext);
		return equipmentService.getAllGateways(gatewayFiltering,
				securityContext);
	}

	@InvokerMethodInfo(displayName = "create Gateway", description = "Creates Gateway", relatedClasses = {Gateway.class})
	public Gateway create(GatewayCreate gatewayCreate,
			SecurityContext securityContext) {
		equipmentService
				.validateEquipmentCreate(gatewayCreate, securityContext);
		return equipmentService.createEquipment(Gateway.class, gatewayCreate,
				securityContext);
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
