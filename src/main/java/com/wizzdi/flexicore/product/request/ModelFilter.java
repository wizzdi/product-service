package com.wizzdi.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.product.model.Manufacturer;
import com.wizzdi.flexicore.product.model.ProductType;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;


    private Set<String> manufacturersIds=new HashSet<>();
    @JsonIgnore
    @TypeRetention(Manufacturer.class)
    private List<Manufacturer> manufacturers;


    private Set<String> productTypesIds=new HashSet<>();
    @JsonIgnore
    @TypeRetention(ProductType.class)
    private List<ProductType> productTypes;

    private Set<String> pricedProductsIds=new HashSet<>();
    @JsonIgnore
    @TypeRetention(PricedProduct.class)
    private List<PricedProduct> pricedProducts;
    private Set<String> skus;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends ModelFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getManufacturersIds() {
        return manufacturersIds;
    }

    public <T extends ModelFilter> T setManufacturersIds(Set<String> manufacturersIds) {
        this.manufacturersIds = manufacturersIds;
        return (T) this;
    }

    @JsonIgnore
    @TypeRetention(Manufacturer.class)
    public List<Manufacturer> getManufacturers() {
        return manufacturers;
    }

    public <T extends ModelFilter> T setManufacturers(List<Manufacturer> manufacturers) {
        this.manufacturers = manufacturers;
        return (T) this;
    }

    public Set<String> getProductTypesIds() {
        return productTypesIds;
    }

    public <T extends ModelFilter> T setProductTypesIds(Set<String> productTypesIds) {
        this.productTypesIds = productTypesIds;
        return (T) this;
    }

    @JsonIgnore
    public List<ProductType> getProductTypes() {
        return productTypes;
    }

    public <T extends ModelFilter> T setProductTypes(List<ProductType> productTypes) {
        this.productTypes = productTypes;
        return (T) this;
    }

    public Set<String> getPricedProductsIds() {
        return pricedProductsIds;
    }

    public <T extends ModelFilter> T setPricedProductsIds(Set<String> pricedProductsIds) {
        this.pricedProductsIds = pricedProductsIds;
        return (T) this;
    }

    @JsonIgnore
    public List<PricedProduct> getPricedProducts() {
        return pricedProducts;
    }

    public <T extends ModelFilter> T setPricedProducts(List<PricedProduct> pricedProducts) {
        this.pricedProducts = pricedProducts;
        return (T) this;
    }

    public Set<String> getSkus() {
        return skus;
    }

    public <T extends ModelFilter> T setSkus(Set<String> skus) {
        this.skus = skus;
        return (T) this;
    }
}
