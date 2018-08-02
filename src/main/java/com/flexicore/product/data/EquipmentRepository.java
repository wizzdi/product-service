package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.SortParameter;
import com.flexicore.data.jsoncontainers.SortingOrder;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.Baselink_;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.product.containers.request.EquipmentFiltering;
import com.flexicore.product.containers.request.EquipmentGroupFiltering;
import com.flexicore.product.containers.request.ProductStatusFiltering;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.containers.response.EquipmentStatusGroup;
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

    public <T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatus(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipmentStatusGroup> q = cb.createQuery(EquipmentStatusGroup.class);
        Root<T> r = q.from(c);
        Join<T,ProductToStatus> join=r.join(Equipment_.productToStatusList);
        Join<ProductToStatus,ProductStatus> statusJoin=cb.treat(join.join(Baselink_.rightside),ProductStatus.class);

        List<Predicate> preds = new ArrayList<>();
        Predicate enabledOnly = cb.equal(join.get(ProductToStatus_.enabled),true);
        preds.add(enabledOnly);
        IEquipmentRepository.addEquipmentFiltering(filtering, cb, r, preds);
        QueryInformationHolder<T> queryInformationHolder = new QueryInformationHolder<>(filtering, c, securityContext);
        prepareQuery(queryInformationHolder, preds, cb, q, r);
        Predicate[] predsArray = new Predicate[preds.size()];
        predsArray =preds.toArray(predsArray);
        CompoundSelection<EquipmentStatusGroup> construct = cb.construct(EquipmentStatusGroup.class, statusJoin.get(ProductStatus_.id),statusJoin.get(ProductStatus_.name),statusJoin.get(ProductStatus_.description), cb.countDistinct(r.get(Equipment_.id)));
        q.select(construct).where(predsArray).groupBy(statusJoin.get(ProductStatus_.id),statusJoin.get(ProductStatus_.name),statusJoin.get(ProductStatus_.description)).distinct(true);

        q.orderBy(cb.asc( statusJoin.get(ProductStatus_.id)));
        TypedQuery<EquipmentStatusGroup> query = em.createQuery(q);
        return query.getResultList();
    }

    public List<ProductStatus> getAllProductStatus(ProductStatusFiltering productStatusFiltering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductStatus> q = cb.createQuery(ProductStatus.class);
        Root<ProductStatus> r = q.from(ProductStatus.class);

        List<Predicate> preds = new ArrayList<>();
        if(productStatusFiltering.getProductType()!=null){
            Join<ProductStatus,ProductTypeToProductStatus> join=r.join(ProductStatus_.productTypeToProductStatusList);
            preds.add(cb.equal(join.get(Baselink_.leftside),productStatusFiltering.getProductType()));

        }
        QueryInformationHolder<ProductStatus> queryInformationHolder = new QueryInformationHolder<>(productStatusFiltering, ProductStatus.class, securityContext);
        return getAllFiltered(queryInformationHolder);
    }
}
