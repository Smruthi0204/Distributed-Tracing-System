package com.DistributedTracingSystem.Trace_Collector.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.DistributedTracingSystem.Trace_Collector.DTO.SpanNode;
import com.DistributedTracingSystem.Trace_Collector.Entity.Span;
import com.DistributedTracingSystem.Trace_Collector.Repository.SpanRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class FetchService {
    private final SpanRepository spanRepo;
    private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    public List<Span> fetchbyTraceId(String id){
        try {
        
            String cached = redisTemplate.opsForValue().get(id);
            if(cached != null) {
                return objectMapper.readValue(cached, new TypeReference<List<Span>>(){});
            }
            List<Span> spans = spanRepo.findByTraceId(id);
            String json = objectMapper.writeValueAsString(spans);
            redisTemplate.opsForValue().set(id, json);
            return spans;
        }

        catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Span> fetchbyStatus(String status){
        return spanRepo.findByStatus(status);
    }

    public List<Span> fetchbySPEED() {
        return spanRepo.findBySpeed();
    }

    private void sortChildren(SpanNode node) {
        node.getChildren().sort(Comparator.comparingLong(SpanNode::getStartTime));
        for (SpanNode child : node.getChildren()) {
            sortChildren(child);
        }
    }

    public List<SpanNode> buildTraceTree(String traceId){
        HashMap<String, SpanNode> map = new HashMap<>();
        List<Span> spans = spanRepo.findByTraceId(traceId);
        if (spans.isEmpty()) return Collections.emptyList();
        for(Span span: spans){
            SpanNode node = new SpanNode();
            node.setTraceId(span.getTraceId());
            node.setSpanId(span.getSpanId());
            node.setParentSpanId(span.getParentSpanId());
            node.setServiceName(span.getServiceName());
            node.setOperationName(span.getOperationName());
            node.setStartTime(span.getStartTime());
            node.setEndTime(span.getEndTime());
            node.setStatus(span.getStatus());
            node.setChildren(new ArrayList<>());
            map.put(span.getSpanId(), node);
        }

        List<SpanNode> roots = new ArrayList<>();
        for (SpanNode node : map.values()) {
            if (node.getParentSpanId() == null) {
                roots.add(node);
                if (roots.size() > 1) {
                    log.warn("Trace {} has {} root spans", traceId, roots.size());
                }
            } else {
                SpanNode parent = map.get(node.getParentSpanId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        for (SpanNode root : roots) {
            sortChildren(root);
        }

        return roots;

    }
}
