package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.Product;
import com.flexicore.product.model.ProductStatus;

public class UpdateProductStatus {

    private String equipmentId;
    @JsonIgnore
    private Equipment equipment;
    private String statusId;
    @JsonIgnore
    private ProductStatus productStatus;

    public String getEquipmentId() {
        return equipmentId;
    }

    public UpdateProductStatus setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
        return this;
    }
    @JsonIgnore

    public Product getEquipment() {
        return equipment;
    }

    public UpdateProductStatus setEquipment(Equipment equipment) {
        this.equipment = equipment;
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
