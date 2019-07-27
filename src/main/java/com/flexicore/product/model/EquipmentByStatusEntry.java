package com.flexicore.product.model;

import com.flexicore.model.Baseclass;
import com.flexicore.product.containers.response.EquipmentStatusGroup;
import org.bson.codecs.pojo.annotations.BsonId;

public class EquipmentByStatusEntry {

    @BsonId
    private String id;
    private String productStatus;
    private String productTypeId;
    private long total;

    public EquipmentByStatusEntry() {
        this.id= Baseclass.getBase64ID();
    }

    public EquipmentByStatusEntry(EquipmentStatusGroup equipmentStatusGroup) {
        this();
        this.productStatus=equipmentStatusGroup.getStatusId();
        this.productTypeId=equipmentStatusGroup.getProductTypeId();
        this.total=equipmentStatusGroup.getCount();
    }


    public String getId() {
        return id;
    }

    public EquipmentByStatusEntry setId(String id) {
        this.id = id;
        return this;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public EquipmentByStatusEntry setProductStatus(String productStatus) {
        this.productStatus = productStatus;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public EquipmentByStatusEntry setTotal(long total) {
        this.total = total;
        return this;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public <T extends EquipmentByStatusEntry> T setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
        return (T) this;
    }
}
