package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.containers.request.FlexiCoreGatewayCreate;
import com.flexicore.product.containers.request.FlexiCoreGatewayUpdate;
import com.flexicore.product.containers.request.FlexiCoreGatewayUpdateParameters;
import com.flexicore.product.model.FlexiCoreGateway;
import com.flexicore.product.model.FlexiCoreGatewayFiltering;
import com.flexicore.product.model.Gateway;
import com.flexicore.product.request.FlexiCoreGatewayCreateParameters;
import com.flexicore.security.SecurityContext;

import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "FlexiCore Gateway Invoker", description = "Invoker for FlexiCore Gateways")
@Extension
@Component
public class FlexiCoreGatewayInvoker
		implements
			ListingInvoker<FlexiCoreGateway, FlexiCoreGatewayFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentService equipmentService;

	@Override
	@InvokerMethodInfo(displayName = "listAllGateways", description = "lists all Gateways", relatedClasses = {Gateway.class})
	public PaginationResponse<FlexiCoreGateway> listAll(
			FlexiCoreGatewayFiltering gatewayFiltering,
			SecurityContext securityContext) {
		equipmentService.validateFiltering(gatewayFiltering, securityContext);
		return equipmentService.getAllFlexiCoreGateways(gatewayFiltering,
				securityContext);
	}

	@InvokerMethodInfo(displayName = "create FlexiCore Gateway", description = "Creates FlexiCoreGateway", relatedClasses = {FlexiCoreGateway.class})
	public FlexiCoreGateway create(
			FlexiCoreGatewayCreateParameters flexiCoreGatewayCreateParameters) {
		FlexiCoreGatewayCreate gatewayCreate = flexiCoreGatewayCreateParameters
				.getFlexiCoreGatewayCreate();
		SecurityContext securityContext = flexiCoreGatewayCreateParameters
				.getSecurityContext();
		equipmentService
				.validateEquipmentCreate(gatewayCreate, securityContext);
		return equipmentService.createFlexiCoreGateway(gatewayCreate,
				securityContext);
	}

	@InvokerMethodInfo(displayName = "updates FlexiCore Gateway", description = "Updates FlexiCoreGateway", relatedClasses = {FlexiCoreGateway.class})
	public FlexiCoreGateway update(
			FlexiCoreGatewayUpdateParameters flexiCoreGatewayCreateParameters) {
		FlexiCoreGatewayUpdate gatewayCreate = flexiCoreGatewayCreateParameters
				.getFlexiCoreGatewayUpdate();
		SecurityContext securityContext = flexiCoreGatewayCreateParameters
				.getSecurityContext();
		equipmentService.validate(gatewayCreate, securityContext);
		return equipmentService.updateFlexiCoreGateway(gatewayCreate,
				securityContext);
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
