package com.flexicore.product.interfaces;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.product.model.ProductToStatus;
import com.flexicore.product.request.ProductToStatusFilter;
import com.flexicore.security.SecurityContext;

import java.util.List;

public interface IProductToStatusService extends Plugin {
	List<ProductToStatus> listAllProductToStatus(
			ProductToStatusFilter productToStatusFilter,
			SecurityContext securityContext);
}
