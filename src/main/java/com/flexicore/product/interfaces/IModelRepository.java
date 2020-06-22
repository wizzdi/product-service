package com.flexicore.product.interfaces;


import com.flexicore.organization.model.Manufacturer;
import com.flexicore.organization.model.Manufacturer_;
import com.flexicore.product.model.Model;
import com.flexicore.product.model.Model_;
import com.flexicore.product.request.ModelFiltering;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface IModelRepository {

	static <T extends Model> void getAllModelsPredicates(List<Predicate> preds,
			ModelFiltering filtering, Root<T> r, CriteriaBuilder cb) {
		if (filtering.getManufacturers() != null
				&& !filtering.getManufacturers().isEmpty()) {
			Set<String> ids = filtering.getManufacturers().parallelStream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, Manufacturer> manufacturerJoin = r
					.join(Model_.manufacturer);
			preds.add(manufacturerJoin.get(Manufacturer_.id).in(ids));
		}
	}
}
