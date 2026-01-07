package com.markrai.plumairanalytics.controller;

import com.markrai.plumairanalytics.model.Detector;
import com.markrai.plumairanalytics.repository.DetectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/detectors")
public class DetectorController {

    @Autowired
    private DetectorRepository detectorRepository;

    @GetMapping("/purpleair")
    public Map<String, Object> getPurpleAirDetectors() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> purpleAirSensors = new ArrayList<>();

        Iterable<Detector> allDetectors = detectorRepository.findAll();
        
        for (Detector detector : allDetectors) {
            // Only include PurpleAir sensors (those with actual IP addresses, not "API")
            String ip = detector.getIpAddr();
            if (ip != null && !ip.trim().equalsIgnoreCase("API")) {
                Map<String, Object> sensorInfo = new HashMap<>();
                sensorInfo.put("detectorId", detector.getId());
                sensorInfo.put("name", detector.getName());
                sensorInfo.put("ipAddress", ip);
                sensorInfo.put("type", detector.getType());
                purpleAirSensors.add(sensorInfo);
            }
        }

        response.put("purpleAirSensors", purpleAirSensors);
        response.put("count", purpleAirSensors.size());
        
        return response;
    }

    @GetMapping("/all")
    public Map<String, Object> getAllDetectors() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> allDetectors = new ArrayList<>();

        Iterable<Detector> detectors = detectorRepository.findAll();
        
        for (Detector detector : detectors) {
            Map<String, Object> detectorInfo = new HashMap<>();
            detectorInfo.put("detectorId", detector.getId());
            detectorInfo.put("name", detector.getName());
            detectorInfo.put("ipAddress", detector.getIpAddr());
            detectorInfo.put("type", detector.getType());
            allDetectors.add(detectorInfo);
        }

        response.put("detectors", allDetectors);
        response.put("count", allDetectors.size());
        
        return response;
    }
}
