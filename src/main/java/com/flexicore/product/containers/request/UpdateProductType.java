package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FileResource;
import com.flexicore.product.model.ProductType;

public class UpdateProductType extends ProductTypeCreate {

    private String id;
    @JsonIgnore
    private ProductType productType;


    public String getId() {
        return id;
    }

    public UpdateProductType setId(String id) {
        this.id = id;
        return this;
    }

    @JsonIgnore
    public ProductType getProductType() {
        return productType;
    }

    public UpdateProductType setProductType(ProductType productType) {
        this.productType = productType;
        return this;
    }
}
