package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.iot.ExternalServer;
import com.flexicore.product.containers.request.ExternalServerFiltering;
import com.flexicore.security.SecurityContext;

import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "ExternalServer Invoker", description = "Invoker for ExternalServers")
@Extension
@Component
public class ExternalServerInvoker
		implements
			ListingInvoker<ExternalServer, ExternalServerFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private ExternalServerService externalServerService;

	@Override
	@InvokerMethodInfo(displayName = "listAllExternalServer", description = "lists all External servers", relatedClasses = {ExternalServer.class})
	public PaginationResponse<ExternalServer> listAll(
			ExternalServerFiltering externalServerFiltering,
			SecurityContext securityContext) {
		externalServerService
				.validate(externalServerFiltering, securityContext);
		return externalServerService.getAllExternalServers(
				externalServerFiltering, securityContext);
	}

	@Override
	public Class<ExternalServerFiltering> getFilterClass() {
		return ExternalServerFiltering.class;
	}

	@Override
	public Class<?> getHandlingClass() {
		return ExternalServer.class;
	}
}
