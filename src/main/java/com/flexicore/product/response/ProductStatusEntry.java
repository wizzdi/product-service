package com.flexicore.product.response;

import com.flexicore.product.model.Product;
import com.flexicore.product.model.ProductStatus;
import com.flexicore.product.model.ProductToStatus;

public class ProductStatusEntry {

    private String linkId;
    private Product product;
    private ProductStatus productStatus;

    public ProductStatusEntry(ProductToStatus link) {
        this.linkId=link.getId();
        this.product=link.getLeftside();
        this.productStatus=link.getRightside();
    }

    public Product getProduct() {
        return product;
    }

    public ProductStatusEntry setProduct(Product product) {
        this.product = product;
        return this;
    }

    public ProductStatus getProductStatus() {
        return productStatus;
    }

    public ProductStatusEntry setProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
        return this;
    }

    public String getLinkId() {
        return linkId;
    }

    public ProductStatusEntry setLinkId(String linkId) {
        this.linkId = linkId;
        return this;
    }
}
