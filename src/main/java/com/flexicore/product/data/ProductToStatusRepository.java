package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.Baselink_;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.product.model.*;
import com.flexicore.product.request.ProductToStatusFilter;
import com.flexicore.product.request.ProductToStatusMassUpdate;
import com.flexicore.security.SecurityContext;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

@PluginInfo(version = 1)
@Extension
@Component
public class ProductToStatusRepository extends AbstractRepositoryPlugin {

	public List<ProductToStatus> listAllProductToStatus(
			ProductToStatusFilter productToStatusFilter,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductToStatus> q = cb
				.createQuery(ProductToStatus.class);
		Root<ProductToStatus> r = q.from(ProductToStatus.class);
		List<Predicate> preds = new ArrayList<>();
		addProductToStatusPredicates(r, cb, preds, productToStatusFilter);
		QueryInformationHolder<ProductToStatus> queryInformationHolder = new QueryInformationHolder<>(
				productToStatusFilter, ProductToStatus.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	public long countAllProductToStatus(
			ProductToStatusFilter productToStatusFilter,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<ProductToStatus> r = q.from(ProductToStatus.class);
		List<Predicate> preds = new ArrayList<>();
		addProductToStatusPredicates(r, cb, preds, productToStatusFilter);
		QueryInformationHolder<ProductToStatus> queryInformationHolder = new QueryInformationHolder<>(
				productToStatusFilter, ProductToStatus.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addProductToStatusPredicates(Root<ProductToStatus> r,
			CriteriaBuilder cb, List<Predicate> preds,
			ProductToStatusFilter productToStatusFilter) {
		if (productToStatusFilter.getEnabled() != null) {
			preds.add(cb.equal(r.get(ProductToStatus_.enabled),
					productToStatusFilter.getEnabled()));
		}
		if (productToStatusFilter.getProducts() != null
				&& !productToStatusFilter.getProducts().isEmpty()) {
			Set<String> ids = productToStatusFilter.getProducts().stream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			Join<ProductToStatus, Product> join = cb.treat(
					r.join(Baselink_.leftside), Product.class);
			preds.add(join.get(Product_.id).in(ids));
		}
		if (productToStatusFilter.getStatuses() != null
				&& !productToStatusFilter.getStatuses().isEmpty()) {
			Set<String> ids = productToStatusFilter.getStatuses().stream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			Join<ProductToStatus, ProductStatus> join = cb.treat(
					r.join(Baselink_.rightside), ProductStatus.class);
			preds.add(join.get(ProductStatus_.id).in(ids));
		}
	}

	public int massUpdateProductToStatus(
			ProductToStatusMassUpdate productToStatusMassUpdate,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<ProductToStatus> q = cb
				.createCriteriaUpdate(ProductToStatus.class);
		Root<ProductToStatus> r = q.from(ProductToStatus.class);
		ProductToStatusFilter productToStatusFilter = productToStatusMassUpdate
				.getProductToStatusFilter();

		Subquery<String> subquery = getProductToStatusSubQuery(securityContext,
				cb, q, r, productToStatusFilter);
		q.set(r.get(ProductToStatus_.enabled),
				productToStatusMassUpdate.isEnable()).where(
				r.get(ProductToStatus_.id).in(subquery));
		return em.createQuery(q).executeUpdate();
	}

	public Subquery<String> getProductToStatusSubQuery(
			SecurityContext securityContext, CriteriaBuilder cb,
			CriteriaUpdate<ProductToStatus> q, Root<ProductToStatus> r,
			ProductToStatusFilter productToStatusFilter) {
		Subquery<String> subquery = q.subquery(String.class);
		Root<ProductToStatus> subqueryRoot = subquery
				.from(ProductToStatus.class);
		List<Predicate> subPreds = new ArrayList<>();
		addProductToStatusPredicates(subqueryRoot, cb, subPreds,
				productToStatusFilter);
		QueryInformationHolder<ProductToStatus> subQueryInfoHolder = new QueryInformationHolder<>(
				productToStatusFilter, ProductToStatus.class, securityContext);
		prepareQuery(subQueryInfoHolder, subPreds, cb, subquery, subqueryRoot);
		Predicate[] predsArr = new Predicate[subPreds.size()];
		subPreds.toArray(predsArr);
		subquery.select(r.get(ProductToStatus_.id)).where(predsArr);
		return subquery;
	}
}
