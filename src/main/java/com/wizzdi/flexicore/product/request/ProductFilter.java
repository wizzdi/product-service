package com.wizzdi.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.product.model.Model;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> pricedProductsIds=new HashSet<>();
    @JsonIgnore
    @TypeRetention(PricedProduct.class)
    private List<PricedProduct> pricedProducts;

    private Set<String> modelsIds=new HashSet<>();
    @JsonIgnore
    @TypeRetention(Model.class)
    private List<Model> models;
    private Set<String> serialNumbers;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends ProductFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getPricedProductsIds() {
        return pricedProductsIds;
    }

    public <T extends ProductFilter> T setPricedProductsIds(Set<String> pricedProductsIds) {
        this.pricedProductsIds = pricedProductsIds;
        return (T) this;
    }

    @JsonIgnore
    @TypeRetention(PricedProduct.class)
    public List<PricedProduct> getPricedProducts() {
        return pricedProducts;
    }

    public <T extends ProductFilter> T setPricedProducts(List<PricedProduct> pricedProducts) {
        this.pricedProducts = pricedProducts;
        return (T) this;
    }

    public Set<String> getModelsIds() {
        return modelsIds;
    }

    public <T extends ProductFilter> T setModelsIds(Set<String> modelsIds) {
        this.modelsIds = modelsIds;
        return (T) this;
    }

    @JsonIgnore
    @TypeRetention(Model.class)
    public List<Model> getModels() {
        return models;
    }

    public <T extends ProductFilter> T setModels(List<Model> models) {
        this.models = models;
        return (T) this;
    }

    public Set<String> getSerialNumbers() {
        return serialNumbers;
    }

    public <T extends ProductFilter> T setSerialNumbers(Set<String> serialNumbers) {
        this.serialNumbers = serialNumbers;
        return (T) this;
    }
}
