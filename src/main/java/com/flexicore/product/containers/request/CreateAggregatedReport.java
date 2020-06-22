package com.flexicore.product.containers.request;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;

public class CreateAggregatedReport extends EventFiltering {

	private Set<OffsetDateTime> endTimes;
	private Set<String> productStatus;

	public CreateAggregatedReport() {
		setEventType(null);
	}

	public CreateAggregatedReport(CreateAggregatedReport other) {
		super(other);
		this.endTimes = other.endTimes;
		this.productStatus = other.productStatus;
	}

	public Set<OffsetDateTime> getEndTimes() {
		return endTimes;
	}

	public CreateAggregatedReport setEndTimes(Set<OffsetDateTime> endTimes) {
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
