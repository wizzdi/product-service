package com.flexicore.product.interfaces;

import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.model.ProductToStatus;
import com.flexicore.product.request.ProductToStatusFilter;
import com.flexicore.security.SecurityContext;

import java.util.List;

public interface IProductToStatusService extends ServicePlugin {
	List<ProductToStatus> listAllProductToStatus(
			ProductToStatusFilter productToStatusFilter,
			SecurityContext securityContext);
}
