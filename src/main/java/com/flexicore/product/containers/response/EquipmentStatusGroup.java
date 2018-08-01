package com.flexicore.product.containers.response;

import com.flexicore.product.model.ProductStatus;

public class EquipmentStatusGroup {

    private Long count;
    private String statusName;
    private String statusDescription;
    private String statusId;


    public EquipmentStatusGroup( String statusId,String statusName, String statusDescription, Long count) {
        this.count = count;
        this.statusName = statusName;
        this.statusDescription = statusDescription;
        this.statusId = statusId;
    }

    public Long getCount() {
        return count;
    }

    public EquipmentStatusGroup setCount(Long count) {
        this.count = count;
        return this;
    }

    public String getStatusName() {
        return statusName;
    }

    public EquipmentStatusGroup setStatusName(String statusName) {
        this.statusName = statusName;
        return this;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public EquipmentStatusGroup setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
        return this;
    }

    public String getStatusId() {
        return statusId;
    }

    public EquipmentStatusGroup setStatusId(String statusId) {
        this.statusId = statusId;
        return this;
    }
}
