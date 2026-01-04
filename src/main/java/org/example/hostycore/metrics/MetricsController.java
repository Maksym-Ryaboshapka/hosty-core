package org.example.hostycore.metrics;

import org.example.hostycore.metrics.dto.MetricsResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

@RestController
@RequestMapping("/metrics")
public class MetricsController {
  private final MetricsService service;

  public MetricsController(MetricsService service) {
    this.service = service;
  }

  @GetMapping
  public MetricsResponseDto getMetrics(@RequestParam(required = false) String unit) {
    double cpuLoad = this.service.getCpuLoad();
    OptionalDouble cpuTemp = this.service.getCpuTemp();
    Map<String, Double> memoryLoad = this.service.getMemoryLoad(unit);
    List<Map<String, Object>> stores = this.service.getStores(unit);

    return new MetricsResponseDto(cpuLoad, cpuTemp, memoryLoad, stores);
  }
}
