package com.example.api_gateway;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.api_gateway.DTO.SpanRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpanSimulator implements CommandLineRunner {
    private final RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {
        SpanRequest span1 = new SpanRequest();
        span1.setTraceId("trace-A001");
        span1.setSpanId("ag-001");
        span1.setServiceName("api-gateway");
        span1.setOperationName("POST /api/place-order");
        span1.setStartTime(1000);
        span1.setEndTime(1900);
        span1.setStatus("OK");
        span1.setParentSpanId(null);

        SpanRequest span2 = new SpanRequest();
        span2.setTraceId("trace-A002");
        span2.setSpanId("ag-002");
        span2.setServiceName("api-gateway");
        span2.setOperationName("POST /api/place-order");
        span2.setStartTime(2000);
        span2.setEndTime(2650);
        span2.setStatus("ERROR");
        span2.setParentSpanId(null);

        List<SpanRequest> spans = List.of(span1, span2);

        for (SpanRequest x : spans) {
            HttpEntity<SpanRequest> entity = new HttpEntity<>(x);
            restTemplate.exchange("http://trace-collector:8081/api/spans", HttpMethod.POST, entity, SpanRequest.class);
        }
    }
}
