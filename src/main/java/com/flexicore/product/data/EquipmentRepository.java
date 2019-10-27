package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.*;

import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.containers.response.EquipmentSpecificTypeGroup;
import com.flexicore.product.containers.response.EquipmentStatusGroup;
import com.flexicore.product.interfaces.IEquipmentRepository;
import com.flexicore.product.model.*;
import com.flexicore.product.request.EquipmentAndType;
import com.flexicore.product.request.LatLonFilter;
import com.flexicore.product.request.ProductStatusNoProductContainer;
import com.flexicore.product.request.ProductTypeToProductStatusFilter;
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

    public <T extends Equipment> List<EquipmentAndType> getEquipmentAndType(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipmentAndType> q = cb.createQuery(EquipmentAndType.class);
        Root<T> r = q.from(c);

        List<Predicate> preds = new ArrayList<>();
        IEquipmentRepository.addEquipmentFiltering(filtering, cb, r, preds);


        QueryInformationHolder<T> queryInformationHolder = new QueryInformationHolder<>(filtering, c, securityContext);

        prepareQuery(queryInformationHolder, preds, cb, q, r);

        Predicate[] predsArray = new Predicate[preds.size()];
        predsArray =preds.toArray(predsArray);

        q.select(cb.construct(EquipmentAndType.class,r.get(Equipment_.id),r.get(Equipment_.productType))).where(predsArray).orderBy(cb.desc(r.get(Equipment_.id)));
        TypedQuery<EquipmentAndType> query = em.createQuery(q);
        if(filtering.getPageSize()!=null && filtering.getPageSize() > 0 && filtering.getCurrentPage()!=null && filtering.getPageSize() > -1){
            setPageQuery(filtering.getPageSize(), filtering.getCurrentPage(),query);

        }
        return query.getResultList();
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
        addProductStatusPredicates(productStatusFiltering,q, cb, r, preds,securityContext);
        QueryInformationHolder<ProductStatus> queryInformationHolder = new QueryInformationHolder<>(productStatusFiltering, ProductStatus.class, securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);
    }

    public void addProductStatusPredicates(ProductStatusFiltering productStatusFiltering,CriteriaQuery<?> q, CriteriaBuilder cb, Root<ProductStatus> r, List<Predicate> preds,SecurityContext securityContext) {
        if (productStatusFiltering.getProductType() != null) {
            Join<ProductStatus, ProductTypeToProductStatus> join = r.join(ProductStatus_.productTypeToProductStatusList);
            preds.add(cb.equal(join.get(Baselink_.leftside), productStatusFiltering.getProductType()));

        }

        if(productStatusFiltering.getEquipmentFiltering()!=null){
            Subquery<String> subquery= getUsedProductsSubQuery(productStatusFiltering.getEquipmentFiltering(), q, cb, securityContext);
            Join<ProductStatus,ProductToStatus> productLinkJoin=r.join(ProductStatus_.productToStatusList);
            Join<ProductToStatus,Product> productJoin=cb.treat(productLinkJoin.join(Baselink_.leftside),Product.class);

            preds.add(cb.and(productJoin.get(Product_.id).in(subquery),cb.isTrue(productLinkJoin.get(ProductToStatus_.enabled))));
        }
    }

    public long countAllProductStatus(ProductStatusFiltering productStatusFiltering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<ProductStatus> r = q.from(ProductStatus.class);
        List<Predicate> preds = new ArrayList<>();
        addProductStatusPredicates(productStatusFiltering,q, cb, r, preds,securityContext);
        QueryInformationHolder<ProductStatus> queryInformationHolder = new QueryInformationHolder<>(productStatusFiltering, ProductStatus.class, securityContext);
        return countAllFiltered(queryInformationHolder,preds,cb,q,r);
    }


    public List<Gateway> getAllGateways(GatewayFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Gateway> q = cb.createQuery(Gateway.class);
        Root<Gateway> r = q.from(Gateway.class);

        List<Predicate> preds = new ArrayList<>();
        IEquipmentRepository.addEquipmentFiltering(filtering, cb, r, preds);

        addGatewayFiltering(filtering, r, preds);
        QueryInformationHolder<Gateway> queryInformationHolder = new QueryInformationHolder<>(filtering, Gateway.class, securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);

    }

    public List<FlexiCoreGateway> getAllFlexiCoreGateways(FlexiCoreGatewayFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FlexiCoreGateway> q = cb.createQuery(FlexiCoreGateway.class);
        Root<FlexiCoreGateway> r = q.from(FlexiCoreGateway.class);

        List<Predicate> preds = new ArrayList<>();
        IEquipmentRepository.addEquipmentFiltering(filtering, cb, r, preds);

        addFlexiCoreGatewayFiltering(filtering, cb, r, preds);
        QueryInformationHolder<FlexiCoreGateway> queryInformationHolder = new QueryInformationHolder<>(filtering, FlexiCoreGateway.class, securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);

    }

    public void addFlexiCoreGatewayFiltering(FlexiCoreGatewayFiltering filtering, CriteriaBuilder cb, Root<FlexiCoreGateway> r, List<Predicate> preds) {
        addGatewayFiltering(filtering, r, preds);
        if(filtering.getFlexiCoreServer()!=null){
            preds.add(cb.equal(r.get(FlexiCoreGateway_.flexiCoreServer),filtering.getFlexiCoreServer()));
        }

    }

    public<T extends Gateway> void addGatewayFiltering(GatewayFiltering filtering, Root<T> r, List<Predicate> preds) {
        if(filtering.getConsoleIds()!=null && !filtering.getConsoleIds().isEmpty()){
            preds.add(r.get(Gateway_.externalId).in(filtering.getConsoleIds().parallelStream().map(f->f.getId()+"").collect(Collectors.toSet())));

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
        return countAllFiltered(queryInformationHolder,preds,cb,q,r);
    }

    public long countAllFlexiCoreGateways(FlexiCoreGatewayFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<FlexiCoreGateway> r = q.from(FlexiCoreGateway.class);

        List<Predicate> preds = new ArrayList<>();
        IEquipmentRepository.addEquipmentFiltering(filtering, cb, r, preds);

        addFlexiCoreGatewayFiltering(filtering,cb, r, preds);
        QueryInformationHolder<FlexiCoreGateway> queryInformationHolder = new QueryInformationHolder<>(filtering, FlexiCoreGateway.class, securityContext);
        return countAllFiltered(queryInformationHolder,preds,cb,q,r);
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

    public List<ProductStatusNoProductContainer> getCurrentStatusLinksContainers(Set<String> equipmentIds) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductStatusNoProductContainer> q = cb.createQuery(ProductStatusNoProductContainer.class);
        Root<ProductToStatus> r = q.from(ProductToStatus.class);
        Join<ProductToStatus,Product> join=cb.treat(r.join(Baselink_.leftside),Product.class);
        Predicate pred = cb.and(join.get(Product_.id).in(equipmentIds),cb.isTrue(r.get(ProductToStatus_.enabled)));


        q.select(cb.construct(ProductStatusNoProductContainer.class,r.get(ProductToStatus_.id),join.get(Product_.id),cb.treat(r.get(Baselink_.rightside),ProductStatus.class))).where(pred);
        TypedQuery<ProductStatusNoProductContainer> query = em.createQuery(q);
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


    public <T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatusAndTenant(Class<T> c, EquipmentFiltering equipmentFiltering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipmentStatusGroup> q = cb.createQuery(EquipmentStatusGroup.class);
        Root<T> r = q.from(c);
        Join<T,ProductToStatus> join=r.join(Equipment_.productToStatusList);
        Join<ProductToStatus,ProductStatus> statusJoin=cb.treat(join.join(Baselink_.rightside),ProductStatus.class);
        Join<T, Tenant> tenantJoin=r.join(Equipment_.tenant);

        List<Predicate> preds = new ArrayList<>();
        Predicate enabledOnly = cb.isTrue(join.get(ProductToStatus_.enabled));
        preds.add(enabledOnly);
        IEquipmentRepository.addEquipmentFiltering(equipmentFiltering, cb, r, preds);
        QueryInformationHolder<T> queryInformationHolder = new QueryInformationHolder<>(equipmentFiltering, c, securityContext);
        prepareQuery(queryInformationHolder, preds, cb, q, r);
        Predicate[] predsArray = new Predicate[preds.size()];
        predsArray =preds.toArray(predsArray);
        CompoundSelection<EquipmentStatusGroup> construct = cb.construct(EquipmentStatusGroup.class, tenantJoin.get(Tenant_.id),tenantJoin.get(Tenant_.name),statusJoin.get(ProductStatus_.id),statusJoin.get(ProductStatus_.name),statusJoin.get(ProductStatus_.description), cb.countDistinct(r.get(Equipment_.id)));
        q.select(construct).where(predsArray).groupBy(tenantJoin.get(Tenant_.id),tenantJoin.get(Tenant_.name),statusJoin.get(ProductStatus_.id),statusJoin.get(ProductStatus_.name),statusJoin.get(ProductStatus_.description)).distinct(true);

        q.orderBy(cb.asc( statusJoin.get(ProductStatus_.id)));
        TypedQuery<EquipmentStatusGroup> query = em.createQuery(q);
        return query.getResultList();

    }

    public <T extends Equipment> List<EquipmentSpecificTypeGroup> getProductGroupedBySpecificType(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipmentSpecificTypeGroup> q = cb.createQuery(EquipmentSpecificTypeGroup.class);
        Root<T> r = q.from(c);

        List<Predicate> preds = new ArrayList<>();
        IEquipmentRepository.addEquipmentFiltering(filtering, cb, r, preds);
        QueryInformationHolder<T> queryInformationHolder = new QueryInformationHolder<>(filtering, c, securityContext);
        prepareQuery(queryInformationHolder, preds, cb, q, r);
        Predicate[] predsArray = new Predicate[preds.size()];
        predsArray =preds.toArray(predsArray);
        CompoundSelection<EquipmentSpecificTypeGroup> construct = cb.construct(EquipmentSpecificTypeGroup.class, r.get(Baseclass_.dtype), cb.countDistinct(r.get(Equipment_.id)));
        q.select(construct).where(predsArray).groupBy(r.get(Baseclass_.dtype)).distinct(true);

        q.orderBy(cb.asc(r.get(Baseclass_.dtype)));
        TypedQuery<EquipmentSpecificTypeGroup> query = em.createQuery(q);
        return query.getResultList();

    }

    public List<LatLon> getAllLatLons(LatLonFilter latLonFilter, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LatLon> q = cb.createQuery(LatLon.class);
        Root<LatLon> r = q.from(LatLon.class);
        List<Predicate> preds = new ArrayList<>();
        addLatLonPredicates(latLonFilter,preds,cb,r);
        QueryInformationHolder<LatLon> queryInformationHolder = new QueryInformationHolder<>(latLonFilter, LatLon.class, securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);

    }

    public long countAllLatLons(LatLonFilter latLonFilter, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<LatLon> r = q.from(LatLon.class);
        List<Predicate> preds = new ArrayList<>();
        addLatLonPredicates(latLonFilter,preds,cb,r);
        QueryInformationHolder<LatLon> queryInformationHolder = new QueryInformationHolder<>(latLonFilter, LatLon.class, securityContext);
        return countAllFiltered(queryInformationHolder,preds,cb,q,r);

    }

    private void addLatLonPredicates(LatLonFilter latLonFilter, List<Predicate> preds, CriteriaBuilder cb, Root<LatLon> r) {
        if(latLonFilter.getMultiLatLonEquipments()!=null&&!latLonFilter.getMultiLatLonEquipments().isEmpty()){
            Set<String> ids=latLonFilter.getMultiLatLonEquipments().parallelStream().map(f->f.getId()).collect(Collectors.toSet());
            Join<LatLon,MultiLatLonEquipment> join=r.join(LatLon_.multiLatLonEquipment);
            preds.add(join.get(MultiLatLonEquipment_.id).in(ids));
        }
    }

    public List<ProductTypeToProductStatus> listAllProductTypeToProductStatus(ProductTypeToProductStatusFilter filter, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductTypeToProductStatus> q = cb.createQuery(ProductTypeToProductStatus.class);
        Root<ProductTypeToProductStatus> r = q.from(ProductTypeToProductStatus.class);
        List<Predicate> preds = new ArrayList<>();
        addProductTypeToProductStatusPredicates(filter,preds,cb,r);
        QueryInformationHolder<ProductTypeToProductStatus> queryInformationHolder = new QueryInformationHolder<>(filter, ProductTypeToProductStatus.class, securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);
    }

    private void addProductTypeToProductStatusPredicates(ProductTypeToProductStatusFilter filter, List<Predicate> preds, CriteriaBuilder cb, Root<ProductTypeToProductStatus> r) {
        if(filter.getProductTypes()!=null && !filter.getProductTypes().isEmpty()){
            Set<String> ids=filter.getProductTypes().parallelStream().map(f->f.getId()).collect(Collectors.toSet());
            Join<ProductTypeToProductStatus,ProductType> join=cb.treat(r.join(Baselink_.leftside),ProductType.class);
            preds.add(join.get(ProductType_.id).in(ids));
        }

        if(filter.getStatus()!=null && !filter.getStatus().isEmpty()){
            Set<String> ids=filter.getStatus().parallelStream().map(f->f.getId()).collect(Collectors.toSet());
            Join<ProductTypeToProductStatus,ProductStatus> join=cb.treat(r.join(Baselink_.rightside),ProductStatus.class);
            preds.add(join.get(ProductStatus_.id).in(ids));
        }
    }

    public long countAllProductTypeToProductStatus(ProductTypeToProductStatusFilter filter, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<ProductTypeToProductStatus> r = q.from(ProductTypeToProductStatus.class);
        List<Predicate> preds = new ArrayList<>();
        addProductTypeToProductStatusPredicates(filter,preds,cb,r);
        QueryInformationHolder<ProductTypeToProductStatus> queryInformationHolder = new QueryInformationHolder<>(filter, ProductTypeToProductStatus.class, securityContext);
        return countAllFiltered(queryInformationHolder,preds,cb,q,r);
    }

    public <T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatusAndType(Class<T> c, EquipmentFiltering equipmentFiltering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipmentStatusGroup> q = cb.createQuery(EquipmentStatusGroup.class);
        Root<T> r = q.from(c);
        Join<T,ProductToStatus> join=r.join(Equipment_.productToStatusList);
        Join<ProductToStatus,ProductStatus> statusJoin=cb.treat(join.join(Baselink_.rightside),ProductStatus.class);
        Join<T,ProductType> productTypeJoin=r.join(Equipment_.productType);

        List<Predicate> preds = new ArrayList<>();
        Predicate enabledOnly = cb.isTrue(join.get(ProductToStatus_.enabled));
        preds.add(enabledOnly);
        IEquipmentRepository.addEquipmentFiltering(equipmentFiltering, cb, r, preds);
        QueryInformationHolder<T> queryInformationHolder = new QueryInformationHolder<>(equipmentFiltering, c, securityContext);
        prepareQuery(queryInformationHolder, preds, cb, q, r);
        Predicate[] predsArray = new Predicate[preds.size()];
        predsArray =preds.toArray(predsArray);
        CompoundSelection<EquipmentStatusGroup> construct = cb.construct(EquipmentStatusGroup.class,cb.countDistinct(r.get(Equipment_.id)),statusJoin.get(ProductStatus_.id),statusJoin.get(ProductStatus_.name),statusJoin.get(ProductStatus_.description),productTypeJoin.get(ProductType_.id),productTypeJoin.get(ProductType_.name));
        q.select(construct).where(predsArray).groupBy(statusJoin.get(ProductStatus_.id),statusJoin.get(ProductStatus_.name),statusJoin.get(ProductStatus_.description),productTypeJoin.get(ProductType_.id),productTypeJoin.get(ProductType_.name)).distinct(true);

        q.orderBy(cb.asc( statusJoin.get(ProductStatus_.id)));
        TypedQuery<EquipmentStatusGroup> query = em.createQuery(q);
        return query.getResultList();
    }

    public Long countAllProductTypes(ProductTypeFiltering productTypeFiltering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<ProductType> r = q.from(ProductType.class);
        List<Predicate> preds = new ArrayList<>();
        addProductTypePredicates(productTypeFiltering,preds,q,cb,r,securityContext);
        QueryInformationHolder<ProductType> queryInformationHolder = new QueryInformationHolder<>(productTypeFiltering, ProductType.class, securityContext);
        return countAllFiltered(queryInformationHolder,preds,cb,q,r);
    }

    public List<ProductType> listAllProductTypes(ProductTypeFiltering productTypeFiltering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductType> q = cb.createQuery(ProductType.class);
        Root<ProductType> r = q.from(ProductType.class);
        List<Predicate> preds = new ArrayList<>();
        addProductTypePredicates(productTypeFiltering,preds,q,cb,r,securityContext);
        QueryInformationHolder<ProductType> queryInformationHolder = new QueryInformationHolder<>(productTypeFiltering, ProductType.class, securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);
    }

    private void addProductTypePredicates(ProductTypeFiltering productTypeFiltering, List<Predicate> preds, CriteriaQuery<?> q, CriteriaBuilder cb, Root<ProductType> r,SecurityContext securityContext) {
        if(productTypeFiltering.getEquipmentFiltering()!=null ){
            Subquery<String> subquery= getUsedProductsSubQuery(productTypeFiltering.getEquipmentFiltering(),q, cb, securityContext);
            Join<ProductType,Product> productJoin=r.join(ProductType_.products);
            preds.add(productJoin.get(Product_.id).in(subquery));
        }
    }

    private Subquery<String> getUsedProductsSubQuery(EquipmentFiltering equipmentFiltering, CriteriaQuery<?> q, CriteriaBuilder cb, SecurityContext securityContext) {
        Subquery<String> subquery=q.subquery(String.class);
        Root<Equipment> sr=subquery.from(Equipment.class);
        List<Predicate> subPreds=new ArrayList<>();
        IEquipmentRepository.addEquipmentFiltering(equipmentFiltering,cb,sr,subPreds);
        QueryInformationHolder<Equipment> queryInformationHolder=new QueryInformationHolder<>(equipmentFiltering,Equipment.class,securityContext);
        prepareQuery(queryInformationHolder,subPreds,cb,subquery,sr);
        Predicate[] subPredsArr=new Predicate[subPreds.size()];
        subPreds.toArray(subPredsArr);
        subquery.select(sr.get(Equipment_.id)).where(subPredsArr);
        return subquery;
    }


}
