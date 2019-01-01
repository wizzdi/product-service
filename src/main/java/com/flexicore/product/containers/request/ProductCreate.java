package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.interfaces.dynamic.IdRefFieldInfo;
import com.flexicore.product.model.Gateway;
import com.flexicore.product.model.ProductType;

import java.time.LocalDateTime;

public class ProductCreate {

    @FieldInfo(mandatory = true,description = "name")

    private String name;
    @FieldInfo(description = "description")

    private String description;

    @FieldInfo(description = "sku")

    private String sku;

    @IdRefFieldInfo(description = "product type",displayName = "Product Type",refType = ProductType.class,list = false)

    private String productTypeId;
    @JsonIgnore
    private ProductType ProductType;


    private String clazzName;
    @JsonIgnore
    private Class<?> clazz;

    public String getName() {
        return name;
    }

    public <T extends ProductCreate> T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public String getDescription() {
        return description;
    }

    public <T extends ProductCreate> T setDescription(String description) {
        this.description = description;
        return (T) this;
    }

    public String getSku() {
        return sku;
    }

    public <T extends ProductCreate> T setSku(String sku) {
        this.sku = sku;
        return (T) this;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public <T extends ProductCreate> T setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
        return (T) this;
    }

    @JsonIgnore
    public ProductType getProductType() {
        return ProductType;
    }

    public <T extends ProductCreate> T setProductType(ProductType productType) {
        ProductType = productType;
        return (T) this;
    }

    public String getClazzName() {
        return clazzName;
    }

    public <T extends ProductCreate> T setClazzName(String clazzName) {
        this.clazzName = clazzName;
        return (T) this;
    }

    @JsonIgnore
    public Class<?> getClazz() {
        return clazz;
    }

    public <T extends ProductCreate> T setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return (T) this;
    }
}
