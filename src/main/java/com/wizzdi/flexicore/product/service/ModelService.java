package com.wizzdi.flexicore.product.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct_;
import com.wizzdi.flexicore.product.data.ModelRepository;
import com.wizzdi.flexicore.product.model.*;
import com.wizzdi.flexicore.product.request.ModelCreate;
import com.wizzdi.flexicore.product.request.ModelFilter;
import com.wizzdi.flexicore.product.request.ModelUpdate;
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

public class ModelService implements Plugin {

    @Autowired
    private ModelRepository repository;

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

    public void validateFiltering(ModelFilter filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);
        Set<String> manufacturersIds=filtering.getManufacturersIds();
        Map<String, Manufacturer> manufacturerMap=manufacturersIds.isEmpty()?new HashMap<>():listByIds(Manufacturer.class,manufacturersIds, Manufacturer_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
        manufacturersIds.removeAll(manufacturerMap.keySet());
        if(!manufacturersIds.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no manufacturers with ids "+manufacturersIds);
        }
        filtering.setManufacturers(new ArrayList<>(manufacturerMap.values()));

        Set<String> pricedProductsIds=filtering.getPricedProductsIds();
        Map<String, PricedProduct> pricedProductMap=pricedProductsIds.isEmpty()?new HashMap<>():listByIds(PricedProduct.class,pricedProductsIds, PricedProduct_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
        pricedProductsIds.removeAll(pricedProductMap.keySet());
        if(!pricedProductsIds.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no PricedProduct with ids "+pricedProductsIds);
        }
        filtering.setPricedProducts(new ArrayList<>(pricedProductMap.values()));

        Set<String> productTypesIds=filtering.getProductTypesIds();
        Map<String, ProductType> productTypeMap=productTypesIds.isEmpty()?new HashMap<>():listByIds(ProductType.class,productTypesIds, ProductType_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
        productTypesIds.removeAll(productTypeMap.keySet());
        if(!productTypesIds.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no ProductType with ids "+productTypesIds);
        }
        filtering.setProductTypes(new ArrayList<>(productTypeMap.values()));
    }

    public PaginationResponse<Model> getAllModels(
            SecurityContextBase securityContext, ModelFilter filtering) {
        List<Model> list = listAllModels(securityContext, filtering);
        long count = repository.countAllModels(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<Model> listAllModels(SecurityContextBase securityContext, ModelFilter filtering) {
        return repository.getAllModels(securityContext, filtering);
    }

    public Model createModel(ModelCreate creationContainer,
                                           SecurityContextBase securityContext) {
        Model model = createModelNoMerge(creationContainer, securityContext);
        repository.merge(model);
        return model;
    }

    public Model createModelNoMerge(ModelCreate creationContainer,
                                                  SecurityContextBase securityContext) {
        Model model = new Model();
        model.setId(Baseclass.getBase64ID());

        updateModelNoMerge(model, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(model, securityContext);
        return model;
    }

    public boolean updateModelNoMerge(Model model,
                                             ModelCreate modelCreate) {
        boolean update = basicService.updateBasicNoMerge(modelCreate, model);
        if(modelCreate.getManufacturer()!=null&&(model.getManufacturer()==null||!modelCreate.getManufacturer().getId().equals(model.getManufacturer().getId()))){
            model.setManufacturer(modelCreate.getManufacturer());
            update=true;
        }

        if(modelCreate.getPricedProduct()!=null&&(model.getPricedProduct()==null||!modelCreate.getPricedProduct().getId().equals(model.getPricedProduct().getId()))){
            model.setPricedProduct(modelCreate.getPricedProduct());
            update=true;
        }

        if(modelCreate.getProductType()!=null&&(model.getProductType()==null||!modelCreate.getProductType().getId().equals(model.getProductType().getId()))){
            model.setProductType(modelCreate.getProductType());
            update=true;
        }

        if(modelCreate.getSku()!=null&&!modelCreate.getSku().equals(model.getSku())){
            model.setSku(modelCreate.getSku());
            update=true;
        }
        return update;
    }

    public Model updateModel(ModelUpdate updateContainer,
                                           SecurityContextBase securityContext) {
        Model model = updateContainer.getModel();
        if (updateModelNoMerge(model, updateContainer)) {
            repository.merge(model);
        }
        return model;
    }

    public void validate(ModelCreate modelCreate,
                         SecurityContextBase securityContext) {
        basicService.validate(modelCreate, securityContext);
        String manufacturerId=modelCreate.getManufacturerId();
        Manufacturer manufacturer=manufacturerId==null?null:getByIdOrNull(manufacturerId,Manufacturer.class, Manufacturer_.security,securityContext);
        if(manufacturerId!=null&&manufacturer==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Manufacturer with id "+manufacturerId);
        }
        modelCreate.setManufacturer(manufacturer);

        String pricedProductId=modelCreate.getPricedProductId();
        PricedProduct pricedProduct=pricedProductId==null?null:getByIdOrNull(pricedProductId,PricedProduct.class, PricedProduct_.security,securityContext);
        if(pricedProductId!=null&&pricedProduct==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No PricedProduct with id "+pricedProductId);
        }
        modelCreate.setPricedProduct(pricedProduct);


        String productTypeId=modelCreate.getProductTypeId();
        ProductType productType=productTypeId==null?null:getByIdOrNull(productTypeId,ProductType.class, ProductType_.security,securityContext);
        if(productTypeId!=null&&productType==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No ProductType with id "+productTypeId);
        }
        modelCreate.setProductType(productType);
    }
}