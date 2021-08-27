package com.wizzdi.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.product.model.ProductType;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class FeatureCreate extends BasicCreate {

    private String productTypeId;
    @JsonIgnore
    private ProductType productType;

    public String getProductTypeId() {
        return productTypeId;
    }

    public <T extends FeatureCreate> T setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
        return (T) this;
    }

    @JsonIgnore
    public ProductType getProductType() {
        return productType;
    }

    public <T extends FeatureCreate> T setProductType(ProductType productType) {
        this.productType = productType;
        return (T) this;
    }
}
