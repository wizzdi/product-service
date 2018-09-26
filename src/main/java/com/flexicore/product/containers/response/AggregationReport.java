package com.flexicore.product.containers.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AggregationReport {

    private Map<LocalDateTime,List<AggregationReportEntry>> result;


    public AggregationReport(Map<LocalDateTime, List<AggregationReportEntry>> result) {
        this.result = result;
    }


    public Map<LocalDateTime, List<AggregationReportEntry>> getResult() {
        return result;
    }

    public AggregationReport setResult(Map<LocalDateTime, List<AggregationReportEntry>> result) {
        this.result = result;
        return this;
    }
}
