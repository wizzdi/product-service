package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.Baselink_;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.product.model.*;
import com.flexicore.product.request.StatusLinksToImageFilter;
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
public class StatusLinkToImageRepository extends AbstractRepositoryPlugin {

	public List<StatusLinkToImage> listAllStatusLinksToImage(
			StatusLinksToImageFilter filtering, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatusLinkToImage> q = cb
				.createQuery(StatusLinkToImage.class);
		Root<StatusLinkToImage> r = q.from(StatusLinkToImage.class);
		List<Predicate> preds = new ArrayList<>();
		addStatusLinkToImagePredicates(preds, r, cb, filtering);
		QueryInformationHolder<StatusLinkToImage> queryInformationHolder = new QueryInformationHolder<>(
				filtering, StatusLinkToImage.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	public long countAllStatusLinksToImage(StatusLinksToImageFilter filtering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<StatusLinkToImage> r = q.from(StatusLinkToImage.class);
		List<Predicate> preds = new ArrayList<>();
		addStatusLinkToImagePredicates(preds, r, cb, filtering);
		QueryInformationHolder<StatusLinkToImage> queryInformationHolder = new QueryInformationHolder<>(
				filtering, StatusLinkToImage.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addStatusLinkToImagePredicates(List<Predicate> preds,
			Root<StatusLinkToImage> r, CriteriaBuilder cb,
			StatusLinksToImageFilter filtering) {
		Join<StatusLinkToImage, ProductTypeToProductStatus> statusLinkJoin = null;

		if (filtering.getProductTypes() != null
				&& !filtering.getProductTypes().isEmpty()) {
			Set<String> ids = filtering.getProductTypes().parallelStream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			statusLinkJoin = statusLinkJoin != null ? statusLinkJoin : r
					.join(StatusLinkToImage_.statusLink);
			Join<ProductTypeToProductStatus, ProductType> productTypeJoin = cb
					.treat(statusLinkJoin.join(Baselink_.leftside),
							ProductType.class);
			preds.add(productTypeJoin.get(ProductType_.id).in(ids));
		}

		if (filtering.getStatus() != null && !filtering.getStatus().isEmpty()) {
			Set<String> ids = filtering.getStatus().parallelStream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			statusLinkJoin = statusLinkJoin != null ? statusLinkJoin : r
					.join(StatusLinkToImage_.statusLink);
			Join<ProductTypeToProductStatus, ProductStatus> productStatusJoin = cb
					.treat(statusLinkJoin.join(Baselink_.rightside),
							ProductStatus.class);
			preds.add(productStatusJoin.get(ProductStatus_.id).in(ids));
		}

		if (filtering.getStatusLinks() != null
				&& !filtering.getStatusLinks().isEmpty()) {
			Set<String> ids = filtering.getStatusLinks().parallelStream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			statusLinkJoin = statusLinkJoin != null ? statusLinkJoin : r
					.join(StatusLinkToImage_.statusLink);
			preds.add(statusLinkJoin.get(ProductTypeToProductStatus_.id)
					.in(ids));
		}
	}
}
