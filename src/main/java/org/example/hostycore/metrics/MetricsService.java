package org.example.hostycore.metrics;

import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.*;

@Service
public class MetricsService {
  private final SystemInfo si = new SystemInfo();
  private final OperatingSystem os = si.getOperatingSystem();
  private final HardwareAbstractionLayer hal = si.getHardware();
  private final CentralProcessor cpu = hal.getProcessor();
  private final GlobalMemory memory = hal.getMemory();
  private final Sensors sensors = hal.getSensors();
  private long[] prevTicks = new long[CentralProcessor.TickType.values().length];

  public double getCpuLoad() {
    double cpuLoad = cpu.getSystemCpuLoadBetweenTicks(prevTicks);
    prevTicks = cpu.getSystemCpuLoadTicks();

    return cpuLoad * 100;
  }

  public OptionalDouble getCpuTemp() {
    double cpuTemp = sensors.getCpuTemperature();

    if (cpuTemp <= 0 || Double.isNaN(cpuTemp)) {
      return OptionalDouble.empty();
    }

    return OptionalDouble.of(cpuTemp);
  }

  public Map<String, Double> getMemoryLoad(String unit) {
    long totalBytes = memory.getTotal();
    long availableBytes = memory.getAvailable();
    long usableBytes = totalBytes - availableBytes;

    Map<String, Double> gibMemory = toGib(totalBytes, availableBytes, usableBytes, unit);

    Map<String, Double> memory = new HashMap<>();
    memory.put("total", gibMemory.get("total"));
    memory.put("available", gibMemory.get("available"));
    memory.put("usable", gibMemory.get("usable"));

    return memory;
  }

  public List<Map<String, Object>> getStores(String unit) {
    List<Map<String, Object>> stores = new ArrayList<>();

    for (OSFileStore store : os.getFileSystem().getFileStores()) {
      Map<String, Object> map = new HashMap<>();

      long totalBytes = store.getTotalSpace();
      long availableBytes = store.getFreeSpace();
      long usableBytes = totalBytes - availableBytes;

      Map<String, Double> gibStore = toGib(totalBytes, availableBytes, usableBytes, unit);

      map.put("mount", store.getMount());
      map.put("name", store.getName());
      map.put("type", store.getType());
      map.put("total", gibStore.get("total"));
      map.put("usable", gibStore.get("usable"));
      map.put("available", gibStore.get("available"));

      stores.add(map);
    }

    return stores;
  }

  private Map<String, Double> toGib(long totalBytes, long availableBytes, long usableBytes, String unit) {
    double total;
    double available;
    double usable;

    if ("gb".equalsIgnoreCase(unit)) {
      // GiB
      double devisor = 1024.0 * 1024 * 1024;
      total = totalBytes / devisor;
      available = availableBytes / devisor;
      usable = usableBytes / devisor;
    } else {
      total = totalBytes;
      available = availableBytes;
      usable = usableBytes;
    }

    Map<String, Double> map = new HashMap<>();
    map.put("total", total);
    map.put("available", available);
    map.put("usable", usable);

    return map;
  }
}
