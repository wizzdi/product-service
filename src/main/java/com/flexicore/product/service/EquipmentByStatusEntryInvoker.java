package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.model.*;
import com.flexicore.security.SecurityContext;

import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "EquipmentByStatusEntry Invoker", description = "Invoker for Equipment Groups")
@Extension
@Component
public class EquipmentByStatusEntryInvoker
		implements
			ListingInvoker<EquipmentByStatusEntry, EquipmentByStatusEntryFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentService equipmentService;

	@Override
	@InvokerMethodInfo(displayName = "listAllEquipmentByStatusEntries", description = "lists all EquipmentByStatusEntry", relatedClasses = {Equipment.class})
	public PaginationResponse<EquipmentByStatusEntry> listAll(
			EquipmentByStatusEntryFiltering equipmentByStatusEntryFiltering,
			SecurityContext securityContext) {
		return equipmentService
				.getAllEquipmentByStatusEntries(equipmentByStatusEntryFiltering);
	}

	@Override
	public Class<EquipmentByStatusEntryFiltering> getFilterClass() {
		return EquipmentByStatusEntryFiltering.class;
	}

	@Override
	public Class<?> getHandlingClass() {
		return EquipmentByStatusEntry.class;
	}
}
