package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.Product;
import com.flexicore.product.model.ProductStatus;

public class UpdateProductStatus {

    private String productId;
    @JsonIgnore
    private Product product;
    private String statusId;
    @JsonIgnore
    private ProductStatus productStatus;

    public String getProductId() {
        return productId;
    }

    public UpdateProductStatus setProductId(String productId) {
        this.productId = productId;
        return this;
    }
    @JsonIgnore

    public Product getProduct() {
        return product;
    }

    public UpdateProductStatus setProduct(Product product) {
        this.product = product;
        return this;
    }

    public String getStatusId() {
        return statusId;
    }

    public UpdateProductStatus setStatusId(String statusId) {
        this.statusId = statusId;
        return this;
    }
    @JsonIgnore

    public ProductStatus getProductStatus() {
        return productStatus;
    }

    public UpdateProductStatus setProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
        return this;
    }
}
