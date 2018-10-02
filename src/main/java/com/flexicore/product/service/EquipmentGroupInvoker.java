package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.product.model.GroupFiltering;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "EquipmentGroup Invoker", description = "Invoker for Equipment Groups")

public class EquipmentGroupInvoker implements ListingInvoker<EquipmentGroup,GroupFiltering> {

    @Inject
    @PluginInfo(version = 1)
    private GroupService groupService;

    @Override
    @InvokerMethodInfo(displayName = "listAllEquipmentGroups",description = "lists all Equipment Groups")

    public PaginationResponse<EquipmentGroup> listAll(GroupFiltering equipmentGroupFiltering, SecurityContext securityContext) {
        groupService.validateGroupFiltering(equipmentGroupFiltering,securityContext);
        return groupService.getAllEquipmentGroups(equipmentGroupFiltering,securityContext);
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
