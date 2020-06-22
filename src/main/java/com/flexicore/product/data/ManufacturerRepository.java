package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.organization.model.Manufacturer;
import com.flexicore.product.request.ManufacturerFiltering;
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
public class ManufacturerRepository extends AbstractRepositoryPlugin {

	public List<Manufacturer> getAllManufacturers(
			ManufacturerFiltering filtering, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Manufacturer> q = cb.createQuery(Manufacturer.class);
		Root<Manufacturer> r = q.from(Manufacturer.class);
		List<Predicate> preds = new ArrayList<>();
		getAllManufacturersPredicates(preds, filtering, r, cb);
		QueryInformationHolder<Manufacturer> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Manufacturer.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void getAllManufacturersPredicates(List<Predicate> preds,
			ManufacturerFiltering filtering, Root<Manufacturer> r,
			CriteriaBuilder cb) {

	}

	public long countAllManufacturers(ManufacturerFiltering filtering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Manufacturer> r = q.from(Manufacturer.class);
		List<Predicate> preds = new ArrayList<>();
		getAllManufacturersPredicates(preds, filtering, r, cb);
		QueryInformationHolder<Manufacturer> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Manufacturer.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}
}
