package com.wizzdi.flexicore.product.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.product.data.FeatureRepository;
import com.wizzdi.flexicore.product.model.Feature;
import com.wizzdi.flexicore.product.model.ProductType;
import com.wizzdi.flexicore.product.model.ProductType_;
import com.wizzdi.flexicore.product.request.FeatureCreate;
import com.wizzdi.flexicore.product.request.FeatureFilter;
import com.wizzdi.flexicore.product.request.FeatureUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component

public class FeatureService implements Plugin {

    @Autowired
    private FeatureRepository repository;

    @Autowired
    private BasicService basicService;

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return repository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return repository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return repository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        repository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        repository.massMerge(toMerge);
    }

    public void validateFiltering(FeatureFilter featureFilter,
                                  SecurityContextBase securityContext) {
        basicService.validate(featureFilter, securityContext);
        Set<String> productTypesIds=featureFilter.getProductTypesIds();
        Map<String,ProductType> productTypeMap=productTypesIds.isEmpty()?new HashMap<>():listByIds(ProductType.class,productTypesIds,ProductType_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(),f->f));
        productTypesIds.removeAll(productTypeMap.keySet());
        if(!productTypesIds.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no product types with ids "+productTypesIds);
        }
        featureFilter.setProductTypes(new ArrayList<>(productTypeMap.values()));
    }

    public PaginationResponse<Feature> getAllFeatures(
            SecurityContextBase securityContext, FeatureFilter filtering) {
        List<Feature> list = listAllFeatures(securityContext, filtering);
        long count = repository.countAllFeatures(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<Feature> listAllFeatures(SecurityContextBase securityContext, FeatureFilter featureFilter) {
        return repository.getAllFeatures(securityContext, featureFilter);
    }

    public Feature createFeature(FeatureCreate creationContainer,
                                           SecurityContextBase securityContext) {
        Feature feature = createFeatureNoMerge(creationContainer, securityContext);
        repository.merge(feature);
        return feature;
    }

    public Feature createFeatureNoMerge(FeatureCreate creationContainer,
                                                  SecurityContextBase securityContext) {
        Feature feature = new Feature();
        feature.setId(Baseclass.getBase64ID());

        updateFeatureNoMerge(feature, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(feature, securityContext);
        return feature;
    }

    public boolean updateFeatureNoMerge(Feature feature,
                                             FeatureCreate featureCreate) {
        boolean update = basicService.updateBasicNoMerge(featureCreate, feature);
        if(featureCreate.getProductType()!=null&&(feature.getProductType()==null||!featureCreate.getProductType().getId().equals(feature.getProductType().getId()))){
            feature.setProductType(featureCreate.getProductType());
            update=true;
        }
        return update;
    }

    public Feature updateFeature(FeatureUpdate featureUpdate,
                                           SecurityContextBase securityContext) {
        Feature feature = featureUpdate.getFeature();
        if (updateFeatureNoMerge(feature, featureUpdate)) {
            repository.merge(feature);
        }
        return feature;
    }

    public void validate(FeatureCreate featureCreate,
                         SecurityContextBase securityContext) {
        basicService.validate(featureCreate, securityContext);
        String productTypeId=featureCreate.getProductTypeId();
        ProductType productType=productTypeId==null?null:getByIdOrNull(productTypeId,ProductType.class, ProductType_.security,securityContext);
        if(productTypeId!=null&&productType==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Product type with id "+productTypeId);
        }
        featureCreate.setProductType(productType);
    }
}