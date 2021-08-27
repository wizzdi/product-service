package com.wizzdi.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.product.model.Product;


public class ProductUpdate extends ProductCreate{

    private String id;
    @JsonIgnore
    private Product product;


    public String getId() {
        return id;
    }

    public <T extends ProductUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public Product getProduct() {
        return product;
    }

    public <T extends ProductUpdate> T setProduct(Product product) {
        this.product = product;
        return (T) this;
    }
}
