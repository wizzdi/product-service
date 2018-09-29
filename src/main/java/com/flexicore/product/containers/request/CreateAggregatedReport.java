package com.flexicore.product.containers.request;

import java.time.LocalDateTime;
import java.util.Set;

public class CreateAggregatedReport extends EventFiltering{

    private Set<LocalDateTime> endTimes;
    private Set<String> productStatus;

    public CreateAggregatedReport() {
        setEventType(null);
    }

    public CreateAggregatedReport(CreateAggregatedReport other) {
        super(other);
        this.endTimes = other.endTimes;
        this.productStatus = other.productStatus;
    }

    public Set<LocalDateTime> getEndTimes() {
        return endTimes;
    }

    public CreateAggregatedReport setEndTimes(Set<LocalDateTime> endTimes) {
        this.endTimes = endTimes;
        return this;
    }

    public Set<String> getProductStatus() {
        return productStatus;
    }

    public CreateAggregatedReport setProductStatus(Set<String> productStatus) {
        this.productStatus = productStatus;
        return this;
    }
}
