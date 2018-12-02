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

    @FieldInfo(description = "serial number")

    private String serial;

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

    public ProductCreate setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ProductCreate setDescription(String description) {
        this.description = description;
        return this;
    }




    public String getSerial() {
        return serial;
    }

    public ProductCreate setSerial(String serial) {
        this.serial = serial;
        return this;
    }



    public String getProductTypeId() {
        return productTypeId;
    }

    public ProductCreate setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
        return this;
    }

    @JsonIgnore
    public ProductType getProductType() {
        return ProductType;
    }

    public ProductCreate setProductType(ProductType productType) {
        ProductType = productType;
        return this;
    }

    public String getClazzName() {
        return clazzName;
    }

    public ProductCreate setClazzName(String clazzName) {
        this.clazzName = clazzName;
        return this;
    }

    @JsonIgnore
    public Class<?> getClazz() {
        return clazz;
    }


    public ProductCreate setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

}
