package com.flexicore.product.containers.response;

public class AggregationReportEntry {

  private String productStatusId;
  private String productTypeId;
  private String productStatusName;
  private String productTypeName;
  private String tenantId;
  private String tenantName;
  private int count;

    public AggregationReportEntry(String productStatusId,String productTypeId,String tenantId, int count) {
        this.productTypeId=productTypeId;
        this.productStatusId = productStatusId;
        this.count = count;
        this.tenantId=tenantId;
    }

    public String getProductStatusId() {
        return productStatusId;
    }

    public AggregationReportEntry setProductStatusId(String productStatusId) {
        this.productStatusId = productStatusId;
        return this;
    }

    public int getCount() {
        return count;
    }

    public AggregationReportEntry setCount(int count) {
        this.count = count;
        return this;
    }

    public String getProductStatusName() {
        return productStatusName;
    }

    public AggregationReportEntry setProductStatusName(String productStatusName) {
        this.productStatusName = productStatusName;
        return this;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public AggregationReportEntry setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
        return this;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public AggregationReportEntry setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public AggregationReportEntry setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public AggregationReportEntry setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }
}
