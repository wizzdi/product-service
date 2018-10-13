package com.flexicore.product.data;

import com.flexicore.annotations.InjectProperties;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.Baselink_;
import com.flexicore.model.QueryInformationHolder;

import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.containers.response.EquipmentStatusGroup;
import com.flexicore.product.interfaces.IEquipmentRepository;
import com.flexicore.product.model.*;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class EquipmentRepository extends AbstractRepositoryPlugin implements com.flexicore.product.interfaces.IEquipmentRepository {
    @Inject
    private Logger logger;
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
    public <T extends Equipment> long countAllEquipments(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<T> r = q.from(c);

        List<Predicate> preds = new ArrayList<>();
        IEquipmentRepository.addEquipmentFiltering(filtering, cb, r, preds);


        QueryInformationHolder<T> queryInformationHolder = new QueryInformationHolder<>(filtering, c, securityContext);
        return countAllFiltered(queryInformationHolder, preds, cb, q, r);
    }


    @Override
    public <T extends Equipment> List<EquipmentGroupHolder> getAllEquipmentsGrouped(Class<T> c, EquipmentGroupFiltering filtering, SecurityContext securityContext) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipmentGroupHolder> q = cb.createQuery(EquipmentGroupHolder.class);
        Root<T> r = q.from(c);

        List<Predicate> preds = new ArrayList<>();
        String geoHashField = IEquipmentRepository.addEquipmentGeoHashFiltering(filtering, cb, r, preds);
        TypedQuery<EquipmentGroupHolder> query = prepareGeoHashQuery(c,EquipmentGroupHolder.class, filtering, securityContext, cb, q, r, preds, geoHashField);
        return query.getResultList();
    }

    public <T extends Equipment,E extends EquipmentGroupHolder> TypedQuery<E> prepareGeoHashQuery(Class<T> c, Class<E> holderClass, EquipmentGroupFiltering filtering, SecurityContext securityContext, CriteriaBuilder cb, CriteriaQuery<E> q, Root<T> r, List<Predicate> preds, String geoHashField) {
        QueryInformationHolder<T> queryInformationHolder = new QueryInformationHolder<>(filtering, c, securityContext);
        prepareQuery(queryInformationHolder, preds, cb, q, r);
        CompoundSelection<E> construct = cb.construct(holderClass, r.get(geoHashField), cb.count(r.get(Equipment_.id)));
        Predicate[] predsArray = new Predicate[preds.size()];
        predsArray =preds.toArray(predsArray);
        q.select(construct).where(predsArray).groupBy(r.get(geoHashField));

        return em.createQuery(q);
    }


    public List<Equipment> getEquipmentToSync(LocalDateTime now) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Equipment> q = cb.createQuery(Equipment.class);
        Root<Equipment> r = q.from(Equipment.class);
        Predicate pred = cb.or(r.get(Equipment_.nextSyncTime).isNull(),cb.lessThan(r.get(Equipment_.nextSyncTime),now));


        q.select(r).where(pred);
        TypedQuery<Equipment> query = em.createQuery(q);
        return query.getResultList();

    }





    public <T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatus(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipmentStatusGroup> q = cb.createQuery(EquipmentStatusGroup.class);
        Root<T> r = q.from(c);
        Join<T,ProductToStatus> join=r.join(Equipment_.productToStatusList);
        Join<ProductToStatus,ProductStatus> statusJoin=cb.treat(join.join(Baselink_.rightside),ProductStatus.class);

        List<Predicate> preds = new ArrayList<>();
        Predicate enabledOnly = cb.isTrue(join.get(ProductToStatus_.enabled));
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

    public long countAllProductStatus(ProductStatusFiltering productStatusFiltering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<ProductStatus> r = q.from(ProductStatus.class);

        List<Predicate> preds = new ArrayList<>();
        if(productStatusFiltering.getProductType()!=null){
            Join<ProductStatus,ProductTypeToProductStatus> join=r.join(ProductStatus_.productTypeToProductStatusList);
            preds.add(cb.equal(join.get(Baselink_.leftside),productStatusFiltering.getProductType()));

        }
        QueryInformationHolder<ProductStatus> queryInformationHolder = new QueryInformationHolder<>(productStatusFiltering, ProductStatus.class, securityContext);
        return countAllFiltered(queryInformationHolder);
    }


    public List<Gateway> getAllGateways(GatewayFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Gateway> q = cb.createQuery(Gateway.class);
        Root<Gateway> r = q.from(Gateway.class);

        List<Predicate> preds = new ArrayList<>();
        IEquipmentRepository.addEquipmentFiltering(filtering, cb, r, preds);

        addGatewayFiltering(filtering, r, preds);
        QueryInformationHolder<Gateway> queryInformationHolder = new QueryInformationHolder<>(filtering, Gateway.class, securityContext);
        return getAllFiltered(queryInformationHolder);

    }

    public void addGatewayFiltering(GatewayFiltering filtering, Root<Gateway> r, List<Predicate> preds) {
        if(filtering.getConsoleIds()!=null && !filtering.getConsoleIds().isEmpty()){
            preds.add(r.get(Gateway_.externalId).in(filtering.getConsoleIds().parallelStream().map(f->f.getSomeNumber()+"").collect(Collectors.toSet())));

        }
    }

    public long countAllGateways(GatewayFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Gateway> r = q.from(Gateway.class);

        List<Predicate> preds = new ArrayList<>();
        IEquipmentRepository.addEquipmentFiltering(filtering, cb, r, preds);

        addGatewayFiltering(filtering, r, preds);
        QueryInformationHolder<Gateway> queryInformationHolder = new QueryInformationHolder<>(filtering, Gateway.class, securityContext);
        return countAllFiltered(queryInformationHolder);
    }

    @Override
    public List<ProductToStatus> getStatusLinks(Set<String> equipmentIds) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductToStatus> q = cb.createQuery(ProductToStatus.class);
        Root<ProductToStatus> r = q.from(ProductToStatus.class);
        Join<ProductToStatus,Product> join=cb.treat(r.join(Baselink_.leftside),Product.class);
        Predicate pred = join.get(Product_.id).in(equipmentIds);


        q.select(r).where(pred);
        TypedQuery<ProductToStatus> query = em.createQuery(q);
        return query.getResultList();
    }

    @Override
    public List<ProductToStatus> getCurrentStatusLinks(Set<String> equipmentIds) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductToStatus> q = cb.createQuery(ProductToStatus.class);
        Root<ProductToStatus> r = q.from(ProductToStatus.class);
        Join<ProductToStatus,Product> join=cb.treat(r.join(Baselink_.leftside),Product.class);
        Predicate pred = cb.and(join.get(Product_.id).in(equipmentIds),cb.isTrue(r.get(ProductToStatus_.enabled)));


        q.select(r).where(pred);
        TypedQuery<ProductToStatus> query = em.createQuery(q);
        return query.getResultList();
    }

    @Override
    public List<ProductTypeToProductStatus> getAllProductTypeToStatusLinks(Set<String> statusIds) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductTypeToProductStatus> q = cb.createQuery(ProductTypeToProductStatus.class);
        Root<ProductTypeToProductStatus> r = q.from(ProductTypeToProductStatus.class);
        Predicate pred = r.get(Baselink_.rightside).in(statusIds);


        q.select(r).where(pred);
        TypedQuery<ProductTypeToProductStatus> query = em.createQuery(q);
        return query.getResultList();
    }
}
