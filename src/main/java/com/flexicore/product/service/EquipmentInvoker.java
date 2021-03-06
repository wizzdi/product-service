package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.containers.request.EquipmentUpdate;
import com.flexicore.product.model.*;
import com.flexicore.product.request.UpdateEquipmentParameters;
import com.flexicore.product.response.TypeHolder;
import com.flexicore.security.SecurityContext;

import javax.ws.rs.BadRequestException;
import java.util.List;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Equipment Invoker", description = "Invoker for Equipments")
@Extension
@Component
public class EquipmentInvoker implements ListingInvoker<Equipment, EquipmentFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentService equipmentService;

	@Override
	@InvokerMethodInfo(displayName = "listAllEquipment", description = "lists all Equipment", relatedClasses = {Equipment.class})
	public PaginationResponse<Equipment> listAll(
			EquipmentFiltering equipmentFiltering,
			SecurityContext securityContext) {
		equipmentService.validateFiltering(equipmentFiltering, securityContext);
		return equipmentService.getAllEquipments(Equipment.class,
				equipmentFiltering, securityContext);
	}

	@InvokerMethodInfo(displayName = "updateEquipment", description = "update equipment", relatedClasses = {Equipment.class})
	public Equipment update(UpdateEquipmentParameters updateEquipmentParameters) {
		EquipmentUpdate equipmentUpdate = updateEquipmentParameters
				.getEquipmentUpdate();
		SecurityContext securityContext = updateEquipmentParameters
				.getSecurityContext();
		Equipment equipment = equipmentService
				.getByIdOrNull(equipmentUpdate.getId(), Equipment.class, null,
						securityContext);
		if (equipment == null) {
			throw new BadRequestException("No Equipment wit hid "
					+ equipmentUpdate.getId());
		}
		equipmentUpdate.setEquipment(equipment);
		equipmentService.validateEquipmentCreate(equipmentUpdate,
				securityContext);
		return equipmentService.updateEquipment(equipmentUpdate,
				securityContext);
	}

	@InvokerMethodInfo(displayName = "createEquipmentStatusEvent", description = "create Equipment Status Event Aggregation", relatedClasses = {Equipment.class})
	public List<EquipmentByStatusEvent> createEquipmentStatusEvent(
			EquipmentStatusRequest executionParametersHolder) throws Exception {
		return equipmentService.createEquipmentStatusEvent(
				executionParametersHolder.getEquipmentFiltering(),
				executionParametersHolder.getSecurityContext());
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
		return Equipment.class;
	}

}
