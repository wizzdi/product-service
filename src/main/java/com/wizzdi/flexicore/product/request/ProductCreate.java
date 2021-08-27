package com.wizzdi.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.product.model.Model;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class ProductCreate extends BasicCreate {

    private String pricedProductId;
    @JsonIgnore
    private PricedProduct pricedProduct;

    private String modelId;
    @JsonIgnore
    private Model model;
    private String serialNumber;


    public String getPricedProductId() {
        return pricedProductId;
    }

    public <T extends ProductCreate> T setPricedProductId(String pricedProductId) {
        this.pricedProductId = pricedProductId;
        return (T) this;
    }

    @JsonIgnore
    public PricedProduct getPricedProduct() {
        return pricedProduct;
    }

    public <T extends ProductCreate> T setPricedProduct(PricedProduct pricedProduct) {
        this.pricedProduct = pricedProduct;
        return (T) this;
    }

    public String getModelId() {
        return modelId;
    }

    public <T extends ProductCreate> T setModelId(String modelId) {
        this.modelId = modelId;
        return (T) this;
    }

    @JsonIgnore
    public Model getModel() {
        return model;
    }

    public <T extends ProductCreate> T setModel(Model model) {
        this.model = model;
        return (T) this;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public <T extends ProductCreate> T setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return (T) this;
    }
}
