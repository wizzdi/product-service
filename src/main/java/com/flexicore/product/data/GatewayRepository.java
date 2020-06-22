package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.product.interfaces.IEquipmentRepository;
import com.flexicore.product.interfaces.IGatewayRepository;
import com.flexicore.product.model.Gateway;
import com.flexicore.product.model.GatewayFiltering;
import com.flexicore.security.SecurityContext;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

@PluginInfo(version = 1)
@Extension
@Component
public class GatewayRepository extends AbstractRepositoryPlugin
		implements
			IGatewayRepository {

	public List<Gateway> listAllGateways(GatewayFiltering filtering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Gateway> q = cb.createQuery(Gateway.class);
		Root<Gateway> r = q.from(Gateway.class);
		List<Predicate> preds = new ArrayList<>();
		IGatewayRepository.addGatewayPredicates(preds, r, cb, filtering);
		QueryInformationHolder<Gateway> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Gateway.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	public long countAllGateways(GatewayFiltering filtering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Gateway> r = q.from(Gateway.class);
		List<Predicate> preds = new ArrayList<>();
		IGatewayRepository.addGatewayPredicates(preds, r, cb, filtering);
		QueryInformationHolder<Gateway> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Gateway.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

}
