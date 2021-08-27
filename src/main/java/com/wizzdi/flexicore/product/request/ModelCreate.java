package com.wizzdi.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.product.model.Manufacturer;
import com.wizzdi.flexicore.product.model.ProductType;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class ModelCreate extends BasicCreate {
    private String manufacturerId;
    @JsonIgnore
    private Manufacturer manufacturer;

    private String productTypeId;
    @JsonIgnore
    private ProductType productType;
    private String pricedProductId;
    @JsonIgnore
    private PricedProduct pricedProduct;
    private String sku;

    public String getManufacturerId() {
        return manufacturerId;
    }

    public <T extends ModelCreate> T setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
        return (T) this;
    }

    @JsonIgnore
    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public <T extends ModelCreate> T setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
        return (T) this;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public <T extends ModelCreate> T setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
        return (T) this;
    }

    @JsonIgnore
    public ProductType getProductType() {
        return productType;
    }

    public <T extends ModelCreate> T setProductType(ProductType productType) {
        this.productType = productType;
        return (T) this;
    }

    public String getPricedProductId() {
        return pricedProductId;
    }

    public <T extends ModelCreate> T setPricedProductId(String pricedProductId) {
        this.pricedProductId = pricedProductId;
        return (T) this;
    }

    @JsonIgnore
    public PricedProduct getPricedProduct() {
        return pricedProduct;
    }

    public <T extends ModelCreate> T setPricedProduct(PricedProduct pricedProduct) {
        this.pricedProduct = pricedProduct;
        return (T) this;
    }

    public String getSku() {
        return sku;
    }

    public <T extends ModelCreate> T setSku(String sku) {
        this.sku = sku;
        return (T) this;
    }
}
