package com.DistributedTracingSystem.Trace_Collector.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.DistributedTracingSystem.Trace_Collector.DTO.SpanRequest;
import com.DistributedTracingSystem.Trace_Collector.Service.SpanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/spans")
@RequiredArgsConstructor
public class SpanController {

    private final SpanService spanServiceObj;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void receiveSpan(@RequestBody SpanRequest span) {
        spanServiceObj.saveSpan(span);
    }
}
