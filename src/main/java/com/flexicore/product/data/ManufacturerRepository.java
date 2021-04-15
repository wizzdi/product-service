package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.organization.model.Manufacturer;
import com.flexicore.product.request.ManufacturerFiltering;
import com.flexicore.security.SecurityContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@PluginInfo(version = 1)
@Extension
@Component
public class ManufacturerRepository implements Plugin {

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private SecuredBasicRepository securedBasicRepository;

	public List<Manufacturer> getAllManufacturers(
			ManufacturerFiltering filtering, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Manufacturer> q = cb.createQuery(Manufacturer.class);
		Root<Manufacturer> r = q.from(Manufacturer.class);
		List<Predicate> preds = new ArrayList<>();
		getAllManufacturersPredicates(preds, filtering, r, cb,q,securityContext);
		q.select(r).where(preds.toArray(new Predicate[0]));
		TypedQuery<Manufacturer> query=em.createQuery(q);
		BasicRepository.addPagination(filtering,query);
		return query.getResultList();
	}

	public <T extends Manufacturer> void  getAllManufacturersPredicates(List<Predicate> preds, ManufacturerFiltering filtering, From<?,T> r, CriteriaBuilder cb, CommonAbstractCriteria q, SecurityContextBase securityContextBase) {
		securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(), cb,q,r,preds,securityContextBase);

	}

	public long countAllManufacturers(ManufacturerFiltering filtering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Manufacturer> r = q.from(Manufacturer.class);
		List<Predicate> preds = new ArrayList<>();
		getAllManufacturersPredicates(preds, filtering, r, cb,q,securityContext);
		q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
		TypedQuery<Long> query=em.createQuery(q);
		BasicRepository.addPagination(filtering,query);
		return query.getSingleResult();
	}

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

	@Transactional
	public void massMerge(List<?> toMerge) {
		securedBasicRepository.massMerge(toMerge);
	}
}
