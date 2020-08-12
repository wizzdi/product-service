package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.model.Clazz;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentGroupFiltering;
import com.flexicore.product.response.TypeHolder;
import com.flexicore.security.SecurityContext;

import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Equipment grouped Invoker", description = "Invoker for Equipments grouped")
@Extension
@Component
public class EquipmentGroupedInvoker implements ListingInvoker<EquipmentGroupHolder, EquipmentGroupFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentService equipmentService;

	@Override
	@InvokerMethodInfo(displayName = "listAllEquipmentGeoHashes", description = "lists all Equipment Geo Hashed", relatedClasses = {Equipment.class})
	public PaginationResponse<EquipmentGroupHolder> listAll(
			EquipmentGroupFiltering equipmentGroupFiltering,
			SecurityContext securityContext) {
		equipmentService.validateFiltering(equipmentGroupFiltering,
				securityContext);
		return equipmentService.getAllEquipmentsGrouped(Equipment.class,
				equipmentGroupFiltering, securityContext);
	}

	@InvokerMethodInfo(displayName = "listAllTypes", description = "lists all Equipment types", relatedClasses = {Equipment.class})
	public List<TypeHolder> listAllTypes(EquipmentGroupFiltering equipmentGroupFiltering, SecurityContext securityContext) {
		equipmentService.validateFiltering(equipmentGroupFiltering, securityContext);
		return equipmentService.listAllEquipmentTypes(equipmentGroupFiltering, securityContext);
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
