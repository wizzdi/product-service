package com.flexicore.product.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.product.model.EquipmentToGroup;
import com.flexicore.product.model.GroupFiltering;
import com.flexicore.product.request.EquipmentToGroupFiltering;
import com.flexicore.security.SecurityContext;

import java.util.List;
import java.util.Set;

public interface IGroupService extends Plugin {
	PaginationResponse<EquipmentGroup> getAllEquipmentGroups(
			GroupFiltering filtering, SecurityContext securityContext);

	List<EquipmentGroup> listAllEquipmentGroups(GroupFiltering filtering,
			SecurityContext securityContext);

	List<EquipmentToGroup> getEquipmentToGroup(Set<String> equipmentIds);

	List<EquipmentToGroup> listAllEquipmentToGroup(
			EquipmentToGroupFiltering equipmentToGroupFiltering,
			SecurityContext securityContext);

	void validateGroupFiltering(GroupFiltering filtering,
			SecurityContext securityContext);
}
