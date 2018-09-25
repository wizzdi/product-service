package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FileResource;
import com.flexicore.product.model.ProductType;

public class UpdateProductType extends ProductStatusCreate {

    private String id;
    @JsonIgnore
    private ProductType productType;
    private String iconId;
    @JsonIgnore
    private FileResource icon;

    @Override
    public UpdateProductType setName(String name) {
        return (UpdateProductType) super.setName(name);
    }

    @Override
    public UpdateProductType setDescription(String description) {
        return (UpdateProductType) super.setDescription(description);
    }

    public String getIconId() {
        return iconId;
    }

    public UpdateProductType setIconId(String iconId) {
        this.iconId = iconId;
        return this;
    }

    @JsonIgnore
    public FileResource getIcon() {
        return icon;
    }

    public UpdateProductType setIcon(FileResource icon) {
        this.icon = icon;
        return this;
    }

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
