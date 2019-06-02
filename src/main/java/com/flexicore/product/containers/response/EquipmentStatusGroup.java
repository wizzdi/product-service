package com.flexicore.product.containers.response;

public class EquipmentStatusGroup {

    private Long count;
    private String statusName;
    private String statusDescription;
    private String statusId;
    private String tenantId;
    private String tenantName;
    private String productTypeId;
    private String productTypeName;


    public EquipmentStatusGroup( String statusId,String statusName, String statusDescription, Long count) {
        this.count = count;
        this.statusName = statusName;
        this.statusDescription = statusDescription;
        this.statusId = statusId;
    }

    public EquipmentStatusGroup(String tenantId,String tenantName, String statusId,String statusName, String statusDescription, Long count) {
        this.count = count;
        this.statusName = statusName;
        this.statusDescription = statusDescription;
        this.statusId = statusId;
        this.tenantId=tenantId;
        this.tenantName=tenantName;
    }

    public EquipmentStatusGroup( Long count,String statusId,String statusName, String statusDescription,String prodcutTypeId,String productTypeName ) {
       this(statusId,statusName,statusDescription,count);
       this.productTypeId=prodcutTypeId;
       this.productTypeName=productTypeName;
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

    public String getTenantId() {
        return tenantId;
    }

    public <T extends EquipmentStatusGroup> T setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return (T) this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public <T extends EquipmentStatusGroup> T setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return (T) this;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public <T extends EquipmentStatusGroup> T setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
        return (T) this;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public <T extends EquipmentStatusGroup> T setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
        return (T) this;
    }
}
