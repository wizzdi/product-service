package com.wizzdi.flexicore.product.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct_;
import com.wizzdi.flexicore.product.data.ProductRepository;
import com.wizzdi.flexicore.product.model.Model;
import com.wizzdi.flexicore.product.model.Model_;
import com.wizzdi.flexicore.product.model.Product;
import com.wizzdi.flexicore.product.request.ProductCreate;
import com.wizzdi.flexicore.product.request.ProductFilter;
import com.wizzdi.flexicore.product.request.ProductUpdate;
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

public class ProductService implements Plugin {

    @Autowired
    private ProductRepository repository;

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

    public void validateFiltering(ProductFilter filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);

        Set<String> pricedProductsIds=filtering.getPricedProductsIds();
        Map<String, PricedProduct> pricedProductMap=pricedProductsIds.isEmpty()?new HashMap<>():listByIds(PricedProduct.class,pricedProductsIds, PricedProduct_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
        pricedProductsIds.removeAll(pricedProductMap.keySet());
        if(!pricedProductsIds.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no PricedProduct with ids "+pricedProductsIds);
        }
        filtering.setPricedProducts(new ArrayList<>(pricedProductMap.values()));

        Set<String> modelsIds=filtering.getModelsIds();
        Map<String, Model> modelMap=modelsIds.isEmpty()?new HashMap<>():listByIds(Model.class,modelsIds, Model_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
        modelsIds.removeAll(modelMap.keySet());
        if(!modelsIds.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Model with ids "+modelsIds);
        }
        filtering.setModels(new ArrayList<>(modelMap.values()));
    }

    public PaginationResponse<Product> getAllProducts(
            SecurityContextBase securityContext, ProductFilter filtering) {
        List<Product> list = listAllProducts(securityContext, filtering);
        long count = repository.countAllProducts(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<Product> listAllProducts(SecurityContextBase securityContext, ProductFilter filtering) {
        return repository.getAllProducts(securityContext, filtering);
    }

    public Product createProduct(ProductCreate creationContainer,
                                           SecurityContextBase securityContext) {
        Product product = createProductNoMerge(creationContainer, securityContext);
        repository.merge(product);
        return product;
    }

    public Product createProductNoMerge(ProductCreate creationContainer,
                                                  SecurityContextBase securityContext) {
        Product product = new Product();
        product.setId(Baseclass.getBase64ID());

        updateProductNoMerge(product, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(product, securityContext);
        return product;
    }

    public boolean updateProductNoMerge(Product product,
                                             ProductCreate productCreate) {
        boolean update = basicService.updateBasicNoMerge(productCreate, product);
        if(productCreate.getPricedProduct()!=null&&(product.getPricedProduct()==null||!productCreate.getPricedProduct().getId().equals(product.getPricedProduct().getId()))){
            product.setPricedProduct(productCreate.getPricedProduct());
            update=true;
        }
        if(productCreate.getModel()!=null&&(product.getModel()==null||!productCreate.getModel().getId().equals(product.getModel().getId()))){
            product.setModel(productCreate.getModel());
            update=true;
        }
        if(productCreate.getSerialNumber()!=null&&!productCreate.getSerialNumber().equals(product.getSerialNumber())){
            product.setSerialNumber(productCreate.getSerialNumber());
            update=true;
        }
        return update;
    }

    public Product updateProduct(ProductUpdate updateContainer,
                                           SecurityContextBase securityContext) {
        Product product = updateContainer.getProduct();
        if (updateProductNoMerge(product, updateContainer)) {
            repository.merge(product);
        }
        return product;
    }

    public void validate(ProductCreate productCreate,
                         SecurityContextBase securityContext) {
        basicService.validate(productCreate, securityContext);

        String pricedProductId=productCreate.getPricedProductId();
        PricedProduct pricedProduct=pricedProductId==null?null:getByIdOrNull(pricedProductId,PricedProduct.class, PricedProduct_.security,securityContext);
        if(pricedProductId!=null&&pricedProduct==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No PricedProduct with id "+pricedProductId);
        }
        productCreate.setPricedProduct(pricedProduct);

        String modelId=productCreate.getModelId();
        Model model=modelId==null?null:getByIdOrNull(modelId,Model.class, Model_.security,securityContext);
        if(modelId!=null&&model==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Model with id "+modelId);
        }
        productCreate.setModel(model);

    }

}