package com.flexicore.organization.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Basic;
import com.flexicore.organization.model.Supplier_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.data.SupplierToProductRepository;
import com.flexicore.organization.model.Supplier;
import com.flexicore.organization.model.SupplierToProduct;
import com.flexicore.organization.request.SupplierToProductContainer;
import com.flexicore.organization.request.SupplierToProductCreate;
import com.flexicore.organization.request.SupplierToProductFilter;
import com.flexicore.organization.request.SupplierToProductUpdate;
import com.flexicore.product.model.Product;
import com.flexicore.security.SecurityContext;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.SingularAttribute;
import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
@Primary
public class SupplierToProductService implements Plugin {

	@PluginInfo(version = 1)
	@Autowired
	private SupplierToProductRepository repository;
	@Autowired
	private SecuredBasicRepository securedBasicRepository;
	@Autowired
	private Logger logger;


	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return securedBasicRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return securedBasicRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return securedBasicRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return securedBasicRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return securedBasicRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return securedBasicRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		securedBasicRepository.merge(base);
	}

	public List<SupplierToProduct> getAllSupplierToProducts(
			SecurityContext securityContext, SupplierToProductFilter filtering) {
		return repository.getAllSupplierToProducts(securityContext, filtering);
	}

	
	@Transactional
	public void massMerge(List<?> toMerge) {
		repository.massMerge(toMerge);
	}

	public void validateFiltering(SupplierToProductFilter filtering,
			SecurityContext securityContext) {
		Set<String> productIds = filtering.getProductIds();
		Map<String, Product> products = productIds.isEmpty()
				? new HashMap<>()
				: listByIds(Product.class, productIds, securityContext)
						.parallelStream().collect(
								Collectors.toMap(f -> f.getId(), f -> f));
		productIds.removeAll(products.keySet());
		if (!productIds.isEmpty()) {
			throw new BadRequestException("No Product with id " + productIds);
		}
		filtering.setProducts(new ArrayList<>(products.values()));

		Set<String> supplierIds = filtering.getSupplierIds();
		Map<String, Supplier> suppliers = supplierIds.isEmpty()
				? new HashMap<>()
				: securedBasicRepository.listByIds(Supplier.class, supplierIds, Supplier_.security, securityContext)
						.parallelStream().collect(
								Collectors.toMap(f -> f.getId(), f -> f));
		supplierIds.removeAll(suppliers.keySet());
		if (!supplierIds.isEmpty()) {
			throw new BadRequestException("No Suppliers with id " + supplierIds);
		}
		filtering.setSuppliers(new ArrayList<>(suppliers.values()));

	}


	public void validate(SupplierToProductCreate creationContainer,
			SecurityContext securityContext) {

		String productId = creationContainer.getProductId();
		Product product = productId == null ? null : getByIdOrNull(productId,
				Product.class, null, securityContext);
		if (product == null && productId != null) {
			throw new BadRequestException("No Product with id " + productId);
		}
		creationContainer.setProduct(product);

		String supplierId = creationContainer.getSupplierId();
		Supplier supplier = supplierId == null ? null : securedBasicRepository.getByIdOrNull(
				supplierId, Supplier.class, Supplier_.security, securityContext);
		if (supplier == null && supplierId != null) {
			throw new BadRequestException("No Supplier with id " + supplierId);
		}
		creationContainer.setSupplier(supplier);
	}

	
	public SupplierToProduct createSupplierToProduct(
			SupplierToProductCreate creationContainer,
			SecurityContext securityContext) {
		SupplierToProduct supplierToProduct = createSupplierToProductNoMerge(
				creationContainer, securityContext);
		repository.merge(supplierToProduct);
		return supplierToProduct;

	}

	
	public SupplierToProduct createSupplierToProductNoMerge(
			SupplierToProductCreate creationContainer,
			SecurityContext securityContext) {
		SupplierToProduct supplierToProduct = new SupplierToProduct("link", securityContext);
		supplierToProduct.setSupplier(creationContainer.getSupplier());
		supplierToProduct.setProduct(creationContainer.getProduct());
		updateSupplierToProductNoMerge(supplierToProduct, creationContainer);
		return supplierToProduct;
	}

	
	public boolean updateSupplierToProductNoMerge(
			SupplierToProduct supplierToProduct,
			SupplierToProductCreate creationContainer) {
		boolean update = false;
		if (creationContainer.getPrice() != null
				&& creationContainer.getPrice() != supplierToProduct.getPrice()) {
			supplierToProduct.setPrice(creationContainer.getPrice());
			update = true;
		}
		if (creationContainer.getProduct() != null
				&& (supplierToProduct.getProduct() == null || !creationContainer
						.getProduct().getId()
						.equals(supplierToProduct.getProduct().getId()))) {
			supplierToProduct.setProduct(creationContainer.getProduct());
			update = true;
		}
		if (creationContainer.getSupplier() != null
				&& (supplierToProduct.getSupplier() == null || !creationContainer
						.getSupplier().getId()
						.equals(supplierToProduct.getSupplier().getId()))) {
			supplierToProduct.setSupplier(creationContainer.getSupplier());
			update = true;
		}
		return update;

	}

	public PaginationResponse<SupplierToProductContainer> listAllSuppliersToProductLinks(
			SecurityContext securityContext, SupplierToProductFilter filtering) {
		List<SupplierToProduct> list = repository.getAllSupplierToProducts(
				securityContext, filtering);
		long count = repository.countAllSupplierToProducts(securityContext,
				filtering);
		return new PaginationResponse<>(list.parallelStream()
				.map(f -> new SupplierToProductContainer(f))
				.collect(Collectors.toList()), filtering, count);
	}

	public SupplierToProduct updateSupplierToProduct(
			SupplierToProductUpdate updateContainer,
			SecurityContext securityContext) {
		SupplierToProduct supplierToProduct = updateContainer
				.getSupplierToProduct();
		if (updateSupplierToProductNoMerge(supplierToProduct, updateContainer)) {
			repository.merge(supplierToProduct);
		}
		return supplierToProduct;
	}

	public void deleteSupplier(Supplier supplier,
			SecurityContext securityContext) {
		if (supplier.getSupplierApi() != null
				&& !supplier.getSupplierApi().isSoftDelete()) {
			throw new BadRequestException(
					"Cannot delete supplier , it has supplier api");
		}

		List<SupplierToProduct> supplierToProducts = repository
				.getAllSupplierToProducts(null, new SupplierToProductFilter()
						.setSuppliers(Collections.singletonList(supplier))
						.setPageSize(1).setCurrentPage(0));
		if (!supplierToProducts.isEmpty()) {
			throw new BadRequestException(
					"cannot delete supplier it has products connected to it");
		}
		supplier.setSoftDelete(true);
		repository.merge(supplier);

	}
}