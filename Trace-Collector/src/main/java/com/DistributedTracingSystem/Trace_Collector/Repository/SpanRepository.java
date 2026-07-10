package com.DistributedTracingSystem.Trace_Collector.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.DistributedTracingSystem.Trace_Collector.Entity.Span;

public interface SpanRepository extends JpaRepository<Span, Long> {
    List<Span> findByTraceId(String traceId);
    List<Span> findByStatus(String status);

    @Query("SELECT s FROM Span s ORDER BY (s.endTime - s.startTime) DESC")
    List<Span> findBySpeed();
}
