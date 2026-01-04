package org.example.hostycore.metrics.dto;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

public record MetricsResponseDto(double cpuLoad, OptionalDouble cpuTemp, Map<String, Double> memoryLoad, List<Map<String, Object>> stores) {
}
