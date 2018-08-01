package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.ProductStatus;
import com.flexicore.product.model.ProductType;

public class ProductStatusToTypeCreate {

   private String productTypeId;
   private String productStatusId;

   @JsonIgnore
   private ProductType productType;
   @JsonIgnore
   private ProductStatus productStatus;


    public String getProductTypeId() {
        return productTypeId;
    }

    public ProductStatusToTypeCreate setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
        return this;
    }

    public String getProductStatusId() {
        return productStatusId;
    }

    public ProductStatusToTypeCreate setProductStatusId(String productStatusId) {
        this.productStatusId = productStatusId;
        return this;
    }
    @JsonIgnore
    public ProductType getProductType() {
        return productType;
    }

    public ProductStatusToTypeCreate setProductType(ProductType productType) {
        this.productType = productType;
        return this;
    }
    @JsonIgnore
    public ProductStatus getProductStatus() {
        return productStatus;
    }

    public ProductStatusToTypeCreate setProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
        return this;
    }
}
