package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.product.model.GroupFiltering;
import com.flexicore.security.SecurityContext;

import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "EquipmentGroup Invoker", description = "Invoker for Equipment Groups")
@Extension
@Component
public class EquipmentGroupInvoker
		implements
			ListingInvoker<EquipmentGroup, GroupFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private GroupService groupService;

	@Override
	@InvokerMethodInfo(displayName = "listAllEquipmentGroups", description = "lists all Equipment Groups", relatedClasses = {EquipmentGroup.class})
	public PaginationResponse<EquipmentGroup> listAll(
			GroupFiltering equipmentGroupFiltering,
			SecurityContext securityContext) {
		groupService.validateGroupFiltering(equipmentGroupFiltering,
				securityContext);
		return groupService.getAllEquipmentGroups(equipmentGroupFiltering,
				securityContext);
	}

	@Override
	public Class<GroupFiltering> getFilterClass() {
		return GroupFiltering.class;
	}

	@Override
	public Class<?> getHandlingClass() {
		return EquipmentGroup.class;
	}
}
