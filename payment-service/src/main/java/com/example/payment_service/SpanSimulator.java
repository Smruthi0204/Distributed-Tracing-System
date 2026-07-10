package com.example.payment_service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.payment_service.DTO.SpanRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpanSimulator implements CommandLineRunner {
    private final RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {
        SpanRequest span1 = new SpanRequest();
        span1.setTraceId("trace-A001");
        span1.setSpanId("ps-001");
        span1.setServiceName("payment-service");
        span1.setOperationName("POST /payment/charge");
        span1.setStartTime(1100);
        span1.setEndTime(1700);
        span1.setStatus("OK");
        span1.setParentSpanId("os-001");

        SpanRequest span2 = new SpanRequest();
        span2.setTraceId("trace-A002");
        span2.setSpanId("ps-002");
        span2.setServiceName("payment-service");
        span2.setOperationName("POST /payment/charge");
        span2.setStartTime(2100);
        span2.setEndTime(2500);
        span2.setStatus("ERROR");
        span2.setParentSpanId("os-002");

        List<SpanRequest> spans = new ArrayList<>();
        spans.add(span1);
        spans.add(span2);
        
        for(SpanRequest x: spans){
              HttpEntity<SpanRequest> entity = new HttpEntity<>(x);
              ResponseEntity<SpanRequest> response = restTemplate.exchange(
                "http://trace-collector:8081/api/spans",
                HttpMethod.POST,
                entity,
                SpanRequest.class
            );
            System.out.println(response.getBody());
        }
    }
}
