package com.flexicore.product.containers.response;

public class AggregationReportEntry {

  private String productStatusId;
  private int count;

    public AggregationReportEntry(String productStatusId, int count) {
        this.productStatusId = productStatusId;
        this.count = count;
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
}
