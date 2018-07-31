package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.SortParameter;
import com.flexicore.data.jsoncontainers.SortingOrder;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.Baselink_;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.product.containers.request.EquipmentFiltering;
import com.flexicore.product.containers.request.EquipmentGroupFiltering;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.interfaces.IEquipmentRepository;
import com.flexicore.product.model.*;
import com.flexicore.security.SecurityContext;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@PluginInfo(version = 1)
public class EquipmentRepository extends AbstractRepositoryPlugin implements com.flexicore.product.interfaces.IEquipmentRepository {

    @Override
    public <T extends Equipment> List<T> getAllEquipments(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(c);
        Root<T> r = q.from(c);

        List<Predicate> preds = new ArrayList<>();
        IEquipmentRepository.addEquipmentFiltering(filtering, cb, r, preds);


        QueryInformationHolder<T> queryInformationHolder = new QueryInformationHolder<>(filtering, c, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);
    }


    @Override
    public <T extends Equipment> List<EquipmentGroupHolder> getAllEquipmentsGrouped(Class<T> c, EquipmentGroupFiltering filtering, SecurityContext securityContext) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipmentGroupHolder> q = cb.createQuery(EquipmentGroupHolder.class);
        Root<T> r = q.from(c);

        List<Predicate> preds = new ArrayList<>();
        IEquipmentRepository.addEquipmentFiltering(filtering, cb, r, preds);

        String geoHashField = "geoHash" + filtering.getPrecision();
        List<SortParameter> sort = new ArrayList<>();
        sort.add(new SortParameter(geoHashField, SortingOrder.ASCENDING));
        filtering.setSort(sort);
        CompoundSelection<EquipmentGroupHolder> construct = cb.construct(EquipmentGroupHolder.class, r.get(geoHashField), cb.count(r.get(Equipment_.id)));
        q.select(construct).groupBy(r.get(geoHashField));
        QueryInformationHolder<T> queryInformationHolder = new QueryInformationHolder<>(filtering, c, securityContext);
        prepareQuery(queryInformationHolder, preds, cb, q, r);
        TypedQuery<EquipmentGroupHolder> query = em.createQuery(q);
        return query.getResultList();
    }


    @Override
    public void massMerge(List<Object> toMerge) {
        for (Object o : toMerge) {
            em.merge(o);
        }
    }
}
