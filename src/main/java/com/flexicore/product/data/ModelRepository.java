package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.Baselink_;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.organization.model.Manufacturer;
import com.flexicore.organization.model.Manufacturer_;
import com.flexicore.product.interfaces.IModelRepository;
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
public class ModelRepository extends AbstractRepositoryPlugin implements IModelRepository {

    public List<Model> getAllModels(ModelFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Model> q = cb.createQuery(Model.class);
        Root<Model> r = q.from(Model.class);
        List<Predicate> preds = new ArrayList<>();
        IModelRepository.getAllModelsPredicates(preds, filtering, r, cb);
        QueryInformationHolder<Model> queryInformationHolder = new QueryInformationHolder<>(filtering, Model.class, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);
    }



    public long countAllModels(ModelFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Model> r = q.from(Model.class);
        List<Predicate> preds = new ArrayList<>();
        IModelRepository.getAllModelsPredicates(preds, filtering, r, cb);
        QueryInformationHolder<Model> queryInformationHolder = new QueryInformationHolder<>(filtering, Model.class, securityContext);
        return countAllFiltered(queryInformationHolder, preds, cb, q, r);
    }
}
