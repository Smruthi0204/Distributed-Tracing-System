package com.DistributedTracingSystem.Trace_Collector.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroqMessage {
    @JsonProperty("role")
    String role;
    @JsonProperty("content")
    String Content;
}
