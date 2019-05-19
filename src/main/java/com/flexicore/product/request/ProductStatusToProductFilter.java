package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.product.model.Product;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductStatusToProductFilter extends FilteringInformationHolder {

    private Set<String> productIds=new HashSet<>();

    @JsonIgnore
    private List<Product> products;

    public Set<String> getProductIds() {
        return productIds;
    }

    public ProductStatusToProductFilter setProductIds(Set<String> productIds) {
        this.productIds = productIds;
        return this;
    }

    @JsonIgnore
    public List<Product> getProducts() {
        return products;
    }

    public ProductStatusToProductFilter setProducts(List<Product> products) {
        this.products = products;
        return this;
    }
}
