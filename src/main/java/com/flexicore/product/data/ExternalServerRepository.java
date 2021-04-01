package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.iot.ExternalServer;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.product.containers.request.ExternalServerFiltering;
import com.flexicore.product.interfaces.IEquipmentRepository;
import com.flexicore.product.model.ProductType;
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
public class ExternalServerRepository extends AbstractRepositoryPlugin implements Plugin {

	public List<ExternalServer> listAllExternalServers(
			ExternalServerFiltering externalServerFiltering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExternalServer> q = cb.createQuery(ExternalServer.class);
		Root<ExternalServer> r = q.from(ExternalServer.class);
		List<Predicate> preds = new ArrayList<>();
		addExternalServerPredicates(externalServerFiltering, preds, q, cb, r,
				securityContext);
		QueryInformationHolder<ExternalServer> queryInformationHolder = new QueryInformationHolder<>(
				externalServerFiltering, ExternalServer.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	public long countAllExternalServers(
			ExternalServerFiltering externalServerFiltering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<ExternalServer> r = q.from(ExternalServer.class);
		List<Predicate> preds = new ArrayList<>();
		addExternalServerPredicates(externalServerFiltering, preds, q, cb, r,
				securityContext);
		QueryInformationHolder<ExternalServer> queryInformationHolder = new QueryInformationHolder<>(
				externalServerFiltering, ExternalServer.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);

	}

	private void addExternalServerPredicates(
			ExternalServerFiltering externalServerFiltering,
			List<Predicate> preds, CriteriaQuery<?> q, CriteriaBuilder cb,
			Root<ExternalServer> r, SecurityContext securityContext) {
		IEquipmentRepository.addEquipmentFiltering(externalServerFiltering, cb,
				r, preds);
	}
}
