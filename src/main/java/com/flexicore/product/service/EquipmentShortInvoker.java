package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.containers.response.EquipmentShort;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentFiltering;
import com.flexicore.product.model.EquipmentGroupFiltering;
import com.flexicore.product.response.TypeHolder;
import com.flexicore.security.SecurityContext;

import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Equipment short Invoker", description = "Invoker for Equipments short")
@Extension
@Component
public class EquipmentShortInvoker implements ListingInvoker<EquipmentShort, EquipmentFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentService equipmentService;

	@Override
	@InvokerMethodInfo(displayName = "listAllEquipmentShort", description = "lists all Equipment Short", relatedClasses = {Equipment.class})
	public PaginationResponse<EquipmentShort> listAll(
			EquipmentFiltering equipmentFiltering,
			SecurityContext securityContext) {
		equipmentService.validateFiltering(equipmentFiltering, securityContext);
		return equipmentService.getAllEquipmentsShort(Equipment.class,
				equipmentFiltering, securityContext);
	}

	@InvokerMethodInfo(displayName = "listAllTypes", description = "lists all Equipment types", relatedClasses = {Equipment.class})
	public List<TypeHolder> listAllTypes(EquipmentGroupFiltering equipmentGroupFiltering, SecurityContext securityContext) {
		equipmentService.validateFiltering(equipmentGroupFiltering, securityContext);
		return equipmentService.listAllEquipmentTypes(equipmentGroupFiltering, securityContext);
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
