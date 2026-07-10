package com.DistributedTracingSystem.Trace_Collector.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroqRequest {
    @JsonProperty("model")
    String Model;
    @JsonProperty("messages")
    List<GroqMessage> messages;
}
