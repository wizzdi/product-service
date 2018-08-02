package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import com.flexicore.product.model.ProductType;

public class ProductStatusFiltering extends FilteringInformationHolder {

    private String productTypeId;
    @JsonIgnore
    private ProductType productType;

    public String getProductTypeId() {
        return productTypeId;
    }

    public ProductStatusFiltering setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
        return this;
    }

    public ProductType getProductType() {
        return productType;
    }

    public ProductStatusFiltering setProductType(ProductType productType) {
        this.productType = productType;
        return this;
    }
}
