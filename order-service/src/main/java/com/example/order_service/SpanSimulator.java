package com.example.order_service;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.order_service.DTO.SpanRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpanSimulator implements CommandLineRunner {
    private final RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {
        SpanRequest span1 = new SpanRequest();
        span1.setTraceId("trace-A001");
        span1.setSpanId("os-001");
        span1.setServiceName("order-service");
        span1.setOperationName("POST /orders/create");
        span1.setStartTime(1050);
        span1.setEndTime(1850);
        span1.setStatus("OK");
        span1.setParentSpanId("ag-001");

        SpanRequest span2 = new SpanRequest();
        span2.setTraceId("trace-A002");
        span2.setSpanId("os-002");
        span2.setServiceName("order-service");
        span2.setOperationName("POST /orders/create");
        span2.setStartTime(2050);
        span2.setEndTime(2600);
        span2.setStatus("ERROR");
        span2.setParentSpanId("ag-002");
        
        List<SpanRequest> spans = List.of(span1, span2);

        for (SpanRequest x : spans) {
            HttpEntity<SpanRequest> entity = new HttpEntity<>(x);
            restTemplate.exchange("http://trace-collector:8081/api/spans", HttpMethod.POST, entity, SpanRequest.class);
        }
    }
}

