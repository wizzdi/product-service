package com.flexicore.organization.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.Baselink_;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.organization.model.Supplier;
import com.flexicore.organization.model.SupplierToProduct;
import com.flexicore.organization.model.Supplier_;
import com.flexicore.organization.request.SupplierToProductFilter;
import com.flexicore.product.model.Product;
import com.flexicore.product.model.Product_;
import com.flexicore.security.SecurityContext;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
public class SupplierToProductRepository extends AbstractRepositoryPlugin {


	public List<SupplierToProduct> getAllSupplierToProducts(
			SecurityContext securityContext, SupplierToProductFilter filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SupplierToProduct> q = cb
				.createQuery(SupplierToProduct.class);
		Root<SupplierToProduct> r = q.from(SupplierToProduct.class);
		List<Predicate> preds = new ArrayList<>();
		addSupplierToProductPredicate(filtering, cb, r, preds);
		QueryInformationHolder<SupplierToProduct> queryInformationHolder = new QueryInformationHolder<>(
				filtering, SupplierToProduct.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	public Long countAllSupplierToProducts(SecurityContext securityContext,
			SupplierToProductFilter filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<SupplierToProduct> r = q.from(SupplierToProduct.class);
		List<Predicate> preds = new ArrayList<>();
		addSupplierToProductPredicate(filtering, cb, r, preds);
		QueryInformationHolder<SupplierToProduct> queryInformationHolder = new QueryInformationHolder<>(
				filtering, SupplierToProduct.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}


	private void addSupplierToProductPredicate(
			SupplierToProductFilter filtering, CriteriaBuilder cb,
			Root<SupplierToProduct> r, List<Predicate> preds) {
		if (filtering.getProducts() != null
				&& !filtering.getProducts().isEmpty()) {
			Set<String> ids = filtering.getProducts().parallelStream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			Join<SupplierToProduct, Product> productJoin = cb.treat(
					r.join(Baselink_.rightside), Product.class);
			preds.add(productJoin.get(Product_.id).in(ids));
		}

		if (filtering.getSuppliers() != null
				&& !filtering.getSuppliers().isEmpty()) {
			Set<String> ids = filtering.getSuppliers().parallelStream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			Join<SupplierToProduct, Supplier> productJoin = cb.treat(
					r.join(Baselink_.leftside), Supplier.class);
			preds.add(productJoin.get(Supplier_.id).in(ids));
		}

	}

}