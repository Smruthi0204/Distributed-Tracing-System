package com.DistributedTracingSystem.Trace_Collector.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.DistributedTracingSystem.Trace_Collector.DTO.GroqMessage;
import com.DistributedTracingSystem.Trace_Collector.DTO.GroqRequest;
import com.DistributedTracingSystem.Trace_Collector.Entity.Span;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisService {
     @Value("${groq.api.key}")
    private String groqApiKey;
    RestTemplate restTemplate = new RestTemplate();

    public String analyzeTrace(List<Span> spans){
        GroqMessage message = new GroqMessage();
        GroqRequest request = new GroqRequest();
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are a distributed systems diagnostics assistant. Below are all spans ")
            .append("for a single trace. Each span shows the service, operation, duration, status, ")
            .append("its own spanId, and its parentSpanId (the span that called it — null means it is ")
            .append("the root/entry point).\n\n");

        for (Span span : spans) {
            prompt.append("- spanId=").append(span.getSpanId())
                .append(", parentSpanId=").append(span.getParentSpanId() == null ? "null (root)" : span.getParentSpanId())
                .append(", service=").append(span.getServiceName())
                .append(", operation=").append(span.getOperationName())
                .append(", duration=").append(span.getEndTime() - span.getStartTime()).append("ms")
                .append(", status=").append(span.getStatus())
                .append("\n");
        }

        prompt.append("\nFirst, reconstruct the call hierarchy yourself using parentSpanId. Then diagnose ")
            .append("this trace by checking, in order:\n")
            .append("1. ERRORS: If any span has status ERROR, identify the deepest failing span in the ")
            .append("call chain whose children (if any) are not errored — this is the likely root cause. ")
            .append("Explain how the failure propagated upward to its callers.\n")
            .append("2. LATENCY: Identify any span whose duration is unusually high relative to its ")
            .append("siblings or the total trace duration — this may indicate a bottleneck even with no error.\n")
            .append("3. HEALTHY: If there are no errors and no notable latency outliers, state clearly that ")
            .append("the trace completed normally — do not invent a problem.\n\n")
            .append("Base your answer only on the data above. Do not give generic advice unless directly ")
            .append("justified by something observed in this specific trace.");


        message.setRole("user");
        message.setContent(prompt.toString());
        request.setModel("llama-3.3-70b-versatile");
        request.setMessages(List.of(message));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + groqApiKey);

        HttpEntity<GroqRequest> httpEntity = new HttpEntity<>(request, headers);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(request));
        String response;
        try {
            System.out.println(prompt.toString());
            response = restTemplate.postForObject(
                "https://api.groq.com/openai/v1/chat/completions",
                httpEntity,
                String.class
            );
        } catch (RestClientException e) {
            log.error("Groq API call failed for trace analysis", e);
            return "AI diagnosis unavailable at this time. Please try again later.";
        }

        ObjectMapper mapper1 = new ObjectMapper();
        JsonNode root = mapper1.readTree(response);
        return root.path("choices").get(0).path("message").path("content").asString();
    }
}
