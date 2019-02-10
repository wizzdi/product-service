package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.Baselink_;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.organization.model.Manufacturer;
import com.flexicore.organization.model.Manufacturer_;
import com.flexicore.product.model.*;
import com.flexicore.product.request.ModelFiltering;
import com.flexicore.security.SecurityContext;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class ModelRepository extends AbstractRepositoryPlugin {

    public List<Model> getAllModels(ModelFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Model> q = cb.createQuery(Model.class);
        Root<Model> r = q.from(Model.class);
        List<Predicate> preds = new ArrayList<>();
        getAllModelsPredicates(preds, filtering, r, cb);
        QueryInformationHolder<Model> queryInformationHolder = new QueryInformationHolder<>(filtering, Model.class, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);
    }

    private void getAllModelsPredicates(List<Predicate> preds, ModelFiltering filtering, Root<Model> r, CriteriaBuilder cb) {
        if (filtering.getManufacturers() != null && !filtering.getManufacturers().isEmpty()) {
            Set<String> ids = filtering.getManufacturers().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            Join<Model, Manufacturer> manufacturerJoin = r.join(Model_.manufacturer);
            preds.add(manufacturerJoin.get(Manufacturer_.id).in(ids));
        }
    }

    public long countAllModels(ModelFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Model> r = q.from(Model.class);
        List<Predicate> preds = new ArrayList<>();
        getAllModelsPredicates(preds, filtering, r, cb);
        QueryInformationHolder<Model> queryInformationHolder = new QueryInformationHolder<>(filtering, Model.class, securityContext);
        return countAllFiltered(queryInformationHolder, preds, cb, q, r);
    }
}
