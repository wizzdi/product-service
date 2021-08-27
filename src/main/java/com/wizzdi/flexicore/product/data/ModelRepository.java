package com.wizzdi.flexicore.product.data;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct_;
import com.wizzdi.flexicore.product.model.*;
import com.wizzdi.flexicore.product.request.ModelFilter;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Extension
@Component
public class ModelRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<Model> getAllModels(SecurityContextBase securityContext,
                                           ModelFilter filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Model> q = cb.createQuery(Model.class);
        Root<Model> r = q.from(Model.class);
        List<Predicate> preds = new ArrayList<>();
        addModelPredicates(filtering, cb, q, r, preds, securityContext);
        q.select(r).where(preds.toArray(Predicate[]::new)).orderBy(cb.desc(r.get(Model_.name)));
        TypedQuery<Model> query = em.createQuery(q);
        BasicRepository.addPagination(filtering, query);
        return query.getResultList();
    }

    public long countAllModels(SecurityContextBase securityContext,
                                   ModelFilter filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Model> r = q.from(Model.class);
        List<Predicate> preds = new ArrayList<>();
        addModelPredicates(filtering, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }

    public <T extends Model> void addModelPredicates(ModelFilter filtering,
                                                           CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> preds, SecurityContextBase securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);
        if(filtering.getProductTypes()!=null&&!filtering.getProductTypes().isEmpty()){
            Set<String> ids=filtering.getProductTypes().stream().map(f->f.getId()).collect(Collectors.toSet());
            Join<T, ProductType> join=r.join(Model_.productType);
            preds.add(join.get(ProductType_.id).in(ids));
        }

        if(filtering.getManufacturers()!=null&&!filtering.getManufacturers().isEmpty()){
            Set<String> ids=filtering.getManufacturers().stream().map(f->f.getId()).collect(Collectors.toSet());
            Join<T, Manufacturer> join=r.join(Model_.manufacturer);
            preds.add(join.get(Model_.id).in(ids));
        }

        if(filtering.getPricedProducts()!=null&&!filtering.getPricedProducts().isEmpty()){
            Set<String> ids=filtering.getPricedProducts().stream().map(f->f.getId()).collect(Collectors.toSet());
            Join<T, PricedProduct> join=r.join(Model_.pricedProduct);
            preds.add(join.get(PricedProduct_.id).in(ids));
        }

        Set<String> skus = filtering.getSkus();
        if(skus !=null&&!skus.isEmpty()){
            preds.add(r.get(Model_.sku).in(skus));
        }

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