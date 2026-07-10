package com.DistributedTracingSystem.Trace_Collector.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.DistributedTracingSystem.Trace_Collector.DTO.SpanNode;
import com.DistributedTracingSystem.Trace_Collector.Entity.Span;
import com.DistributedTracingSystem.Trace_Collector.Service.AnalysisService;
import com.DistributedTracingSystem.Trace_Collector.Service.FetchService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class GetDataController {
    private final FetchService fetchService;
    private final AnalysisService analysisService;

    @GetMapping("/api/traces/{traceID}")
    public ResponseEntity<?> getByTraceID(@PathVariable String traceID) {
        List<Span> spans = fetchService.fetchbyTraceId(traceID);

        if (spans.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No trace found for id: " + traceID);
        }

        return ResponseEntity.ok(spans);
    }

    @GetMapping("/api/traces/failed")
    public ResponseEntity<?> getByStatus() {
        List<Span> spans = fetchService.fetchbyStatus("ERROR");

        if (spans.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No failed traces found.");
        }

        return ResponseEntity.ok(spans);
    }

    @GetMapping("/api/traces/slowest")
    public ResponseEntity<?> getBySpeed() {
        List<Span> spans = fetchService.fetchbySPEED();

        if (spans.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No traces found.");
        }

        return ResponseEntity.ok(spans);
    }
    
    @GetMapping("/api/traces/{traceID}/analyze")
    public ResponseEntity<?> analyzeByTraceID(@PathVariable String traceID) {
        List<Span> spans = fetchService.fetchbyTraceId(traceID);

        if (spans.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("No trace found for id: " + traceID);
        }

        return ResponseEntity.ok(analysisService.analyzeTrace(spans));
    }

    @GetMapping("/api/traces/{traceID}/tree")
    public ResponseEntity<?> GetTreeByTraceID(@PathVariable String traceID) {
        List<SpanNode> roots = fetchService.buildTraceTree(traceID);
        if (roots.isEmpty()) {
            return ResponseEntity.status(404).body("No trace found for id: " + traceID);
        }
        return ResponseEntity.ok(roots);
    }



    
}
