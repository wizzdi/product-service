package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.product.model.ProductStatus;
import com.flexicore.product.model.ProductType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductTypeToProductStatusFilter  extends FilteringInformationHolder {
    private Set<String> statusIds=new HashSet<>();
    @JsonIgnore
    private List<ProductStatus> status;
    private Set<String> productTypeIds=new HashSet<>();
    @JsonIgnore
    private List<ProductType> productTypes;

    public Set<String> getStatusIds() {
        return statusIds;
    }

    public <T extends ProductTypeToProductStatusFilter> T setStatusIds(Set<String> statusIds) {
        this.statusIds = statusIds;
        return (T) this;
    }

    @JsonIgnore
    public List<ProductStatus> getStatus() {
        return status;
    }

    public <T extends ProductTypeToProductStatusFilter> T setStatus(List<ProductStatus> status) {
        this.status = status;
        return (T) this;
    }

    public Set<String> getProductTypeIds() {
        return productTypeIds;
    }

    public <T extends ProductTypeToProductStatusFilter> T setProductTypeIds(Set<String> productTypeIds) {
        this.productTypeIds = productTypeIds;
        return (T) this;
    }

    @JsonIgnore
    public List<ProductType> getProductTypes() {
        return productTypes;
    }

    public <T extends ProductTypeToProductStatusFilter> T setProductTypes(List<ProductType> productTypes) {
        this.productTypes = productTypes;
        return (T) this;
    }
}
