package com.flexicore.product.invokers;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentLocation;
import com.flexicore.product.request.EquipmentLocationFiltering;
import com.flexicore.product.response.EquipmentLocationContainer;
import com.flexicore.product.service.EquipmentLocationService;
import com.flexicore.security.SecurityContext;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "EquipmentLocation Invoker", description = "Invoker for Equipment Locations")
@Extension
@Component
public class EquipmentLocationInvoker implements ListingInvoker<EquipmentLocation, EquipmentLocationFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentLocationService equipmentLocationService;



	@Override
	@InvokerMethodInfo(displayName = "List Equipment Location", description = "lists all EquipmentLocation", relatedClasses = {Equipment.class})
	public PaginationResponse<EquipmentLocation> listAll(EquipmentLocationFiltering equipmentLocationFiltering, SecurityContext securityContext) {
		return equipmentLocationService.getAllEquipmentLocations(equipmentLocationFiltering);
	}

	@Override
	public Class<EquipmentLocationFiltering> getFilterClass() {
		return EquipmentLocationFiltering.class;
	}

	@Override
	public Class<?> getHandlingClass() {
		return EquipmentLocation.class;
	}
}
