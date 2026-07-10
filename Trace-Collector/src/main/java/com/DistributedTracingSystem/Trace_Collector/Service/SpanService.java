package com.DistributedTracingSystem.Trace_Collector.Service;

import org.springframework.stereotype.Service;

import com.DistributedTracingSystem.Trace_Collector.DTO.SpanRequest;
import com.DistributedTracingSystem.Trace_Collector.Entity.Span;
import com.DistributedTracingSystem.Trace_Collector.Repository.SpanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpanService {
    private final SpanRepository spanObj;

    public void saveSpan(SpanRequest request){
        Span span = new Span();
        span.setTraceId(request.getTraceId());
        span.setParentSpanId(request.getParentSpanId());
        span.setSpanId(request.getSpanId());
        span.setServiceName(request.getServiceName());
        span.setOperationName(request.getOperationName());
        span.setStartTime(request.getStartTime());
        span.setEndTime(request.getEndTime());
        span.setStatus(request.getStatus());
        spanObj.save(span);
    }
}
