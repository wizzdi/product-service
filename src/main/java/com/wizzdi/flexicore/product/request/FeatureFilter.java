package com.wizzdi.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.wizzdi.flexicore.product.model.ProductType;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FeatureFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> productTypesIds=new HashSet<>();
    @JsonIgnore
    @TypeRetention(ProductType.class)
    private List<ProductType> productTypes;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends FeatureFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getProductTypesIds() {
        return productTypesIds;
    }

    public <T extends FeatureFilter> T setProductTypesIds(Set<String> productTypesIds) {
        this.productTypesIds = productTypesIds;
        return (T) this;
    }

    @JsonIgnore
    @TypeRetention(ProductType.class)
    public List<ProductType> getProductTypes() {
        return productTypes;
    }

    public <T extends FeatureFilter> T setProductTypes(List<ProductType> productTypes) {
        this.productTypes = productTypes;
        return (T) this;
    }
}
