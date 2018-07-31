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
import com.flexicore.product.model.*;
import com.flexicore.security.SecurityContext;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@PluginInfo(version = 1)
public class EquipmentRepository extends AbstractRepositoryPlugin {

    public <T extends Equipment> List<T> getAllEquipments(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(c);
        Root<T> r = q.from(c);

        List<Predicate> preds = new ArrayList<>();
        addEquipmentFiltering(filtering, cb, r, preds);


        QueryInformationHolder<T> queryInformationHolder = new QueryInformationHolder<>(filtering, c, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);
    }

    public static <T extends Equipment> void addEquipmentFiltering(EquipmentFiltering filtering, CriteriaBuilder cb, Root<T> r, List<Predicate> preds) {
        if (!filtering.getEquipmentGroups().isEmpty()) {
            Join<T, EquipmentToGroup> join = r.join(Equipment_.equipmentToGroupList);
            Predicate pred = join.get(Baselink_.rightside).in(filtering.getEquipmentGroups());
            preds.add(pred);
        }
        if (filtering.getLocationArea() != null) {
            Predicate predicate = cb.between(r.get(Equipment_.lat), filtering.getLocationArea().getLatStart(), filtering.getLocationArea().getLatEnd());
            predicate = cb.and(predicate, cb.between(r.get(Equipment_.lon), filtering.getLocationArea().getLonStart(), filtering.getLocationArea().getLonEnd()));
            preds.add(predicate);
        }
        if (filtering.getProductType() != null) {
            Predicate predicate = cb.equal(r.get(Equipment_.productType), filtering.getProductType());
            preds.add(predicate);
        }
        if (!filtering.getProductStatusList().isEmpty()) {
            Join<T, ProductToStatus> join = r.join(Equipment_.productToStatusList);
            Predicate pred = join.get(Baselink_.rightside).in(filtering.getProductStatusList());
            preds.add(pred);
        }
    }


    public <T extends Equipment> List<EquipmentGroupHolder> getAllEquipmentsGrouped(Class<T> c, EquipmentGroupFiltering filtering, SecurityContext securityContext) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipmentGroupHolder> q = cb.createQuery(EquipmentGroupHolder.class);
        Root<T> r = q.from(c);

        List<Predicate> preds = new ArrayList<>();
        addEquipmentFiltering(filtering, cb, r, preds);

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


    public void massMerge(List<Object> toMerge) {
        for (Object o : toMerge) {
            em.merge(o);
        }
    }
}
