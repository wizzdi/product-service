package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.product.model.Product;
import com.flexicore.product.model.ProductStatus;

import java.util.List;

public class ProductToStatusFilter extends FilteringInformationHolder {

    @JsonIgnore
    private List<? extends Product> products;
    private Boolean enabled;
    @JsonIgnore
    private List<ProductStatus> statuses;

    @JsonIgnore
    public List<? extends Product> getProducts() {
        return products;
    }

    public <T extends ProductToStatusFilter> T setProducts(List<? extends Product> products) {
        this.products = products;
        return (T) this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public <T extends ProductToStatusFilter> T setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return (T) this;
    }

    @JsonIgnore
    public List<ProductStatus> getStatuses() {
        return statuses;
    }

    public <T extends ProductToStatusFilter> T setStatuses(List<ProductStatus> statuses) {
        this.statuses = statuses;
        return (T) this;
    }
}
