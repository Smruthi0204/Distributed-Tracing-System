package com.DistributedTracingSystem.Trace_Collector.DTO;

import java.util.List;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SpanNode {
    private String traceId;
    private String spanId;
    private String parentSpanId;
    private String serviceName;
    private String operationName;
    private long startTime;
    private long endTime;
    private String status;
    private List<SpanNode> children;
}
