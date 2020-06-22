package com.flexicore.product.containers.response;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class AggregationReport {

	private Map<OffsetDateTime, List<AggregationReportEntry>> result;

	public AggregationReport(
			Map<OffsetDateTime, List<AggregationReportEntry>> result) {
		this.result = result;
	}

	public Map<OffsetDateTime, List<AggregationReportEntry>> getResult() {
		return result;
	}

	public AggregationReport setResult(
			Map<OffsetDateTime, List<AggregationReportEntry>> result) {
		this.result = result;
		return this;
	}
}
