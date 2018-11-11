package com.flexicore.product.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.product.model.GroupFiltering;
import com.flexicore.security.SecurityContext;

public interface IGroupService extends ServicePlugin {
    PaginationResponse<EquipmentGroup> getAllEquipmentGroups(GroupFiltering filtering, SecurityContext securityContext);

    void validateGroupFiltering(GroupFiltering filtering, SecurityContext securityContext);
}
