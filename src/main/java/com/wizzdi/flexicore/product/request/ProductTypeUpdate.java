package com.wizzdi.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.product.model.ProductType;


public class ProductTypeUpdate extends ProductTypeCreate{

    private String id;
    @JsonIgnore
    private ProductType productType;


    public String getId() {
        return id;
    }

    public <T extends ProductTypeUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public ProductType getProductType() {
        return productType;
    }

    public <T extends ProductTypeUpdate> T setProductType(ProductType productType) {
        this.productType = productType;
        return (T) this;
    }
}
