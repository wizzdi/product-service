package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.product.data.ProductToStatusRepository;
import com.flexicore.product.interfaces.IProductToStatusService;
import com.flexicore.product.model.ProductToStatus;
import com.flexicore.product.request.ProductToStatusFilter;
import com.flexicore.product.request.ProductToStatusMassUpdate;
import com.flexicore.security.SecurityContext;

import javax.transaction.Transactional;
import java.util.List;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@Extension
@Component
public class ProductToStatusService implements IProductToStatusService {

	@PluginInfo(version = 1)
	@Autowired
	private ProductToStatusRepository productToStatusRepository;

	@Override
	public List<ProductToStatus> listAllProductToStatus(
			ProductToStatusFilter productToStatusFilter,
			SecurityContext securityContext) {
		return productToStatusRepository.listAllProductToStatus(
				productToStatusFilter, securityContext);
	}

	public int massUpdateProductToStatus(
			ProductToStatusMassUpdate productToStatusMassUpdate,
			SecurityContext securityContext) {
		return productToStatusRepository.massUpdateProductToStatus(
				productToStatusMassUpdate, securityContext);
	}

	@Transactional(Transactional.TxType.REQUIRED)
	public void massMerge(List<?> toMerge) {
		productToStatusRepository.massMerge(toMerge);
	}

	public void flush() {
		productToStatusRepository.flush();
	}
}
