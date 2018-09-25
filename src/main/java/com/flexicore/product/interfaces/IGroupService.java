package com.flexicore.product.interfaces;

import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.model.GroupFiltering;
import com.flexicore.security.SecurityContext;

public interface IGroupService extends ServicePlugin {
    void validateGroupFiltering(GroupFiltering filtering, SecurityContext securityContext);
}
