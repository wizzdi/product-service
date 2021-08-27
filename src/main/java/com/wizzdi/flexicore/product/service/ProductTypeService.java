package com.wizzdi.flexicore.product.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.product.data.ProductTypeRepository;
import com.wizzdi.flexicore.product.model.ProductType;
import com.wizzdi.flexicore.product.request.ProductTypeCreate;
import com.wizzdi.flexicore.product.request.ProductTypeFilter;
import com.wizzdi.flexicore.product.request.ProductTypeUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;

@Extension
@Component

public class ProductTypeService implements Plugin {

    @Autowired
    private ProductTypeRepository repository;

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

    public void validateFiltering(ProductTypeFilter filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);
    }

    public PaginationResponse<ProductType> getAllProductTypes(
            SecurityContextBase securityContext, ProductTypeFilter filtering) {
        List<ProductType> list = listAllProductTypes(securityContext, filtering);
        long count = repository.countAllProductTypes(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<ProductType> listAllProductTypes(SecurityContextBase securityContext, ProductTypeFilter filtering) {
        return repository.getAllProductTypes(securityContext, filtering);
    }

    public ProductType createProductType(ProductTypeCreate creationContainer,
                                           SecurityContextBase securityContext) {
        ProductType productType = createProductTypeNoMerge(creationContainer, securityContext);
        repository.merge(productType);
        return productType;
    }

    public ProductType createProductTypeNoMerge(ProductTypeCreate creationContainer,
                                                  SecurityContextBase securityContext) {
        ProductType productType = new ProductType();
        productType.setId(Baseclass.getBase64ID());

        updateProductTypeNoMerge(productType, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(productType, securityContext);
        return productType;
    }

    public boolean updateProductTypeNoMerge(ProductType productType,
                                             ProductTypeCreate creationContainer) {
        boolean update = basicService.updateBasicNoMerge(creationContainer, productType);

        return update;
    }

    public ProductType updateProductType(ProductTypeUpdate updateContainer,
                                           SecurityContextBase securityContext) {
        ProductType productType = updateContainer.getProductType();
        if (updateProductTypeNoMerge(productType, updateContainer)) {
            repository.merge(productType);
        }
        return productType;
    }

    public void validate(ProductTypeCreate creationContainer,
                         SecurityContextBase securityContext) {
        basicService.validate(creationContainer, securityContext);
    }
}