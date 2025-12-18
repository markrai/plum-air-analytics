package com.markrai.plumairanalytics.service;

import com.markrai.plumairanalytics.dto.DetectorResponse;
import com.markrai.plumairanalytics.model.Detector;
import com.markrai.plumairanalytics.model.Metrics;
import com.markrai.plumairanalytics.repository.DetectorRepository;
import com.markrai.plumairanalytics.repository.MetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class PurpleAirService {

    private static final Logger logger = LoggerFactory.getLogger(PurpleAirService.class);

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DetectorRepository detectorRepository;

    @Autowired
    private MetricsRepository metricsRepository;

    @Autowired
    private EcobeeService ecobeeService;

    @Autowired
    private WeatherService weatherService;

    private final RestTemplate restTemplate;

    @Autowired
    public PurpleAirService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Scheduled(fixedRate = 1800000) // 30 minutes
    // @Scheduled(fixedRate = 120000) // 2 minutes - for testing
    public void queryDetectors() {
        logger.info("we are in queryDetectors()" + transactionManager.getClass().getName());
        Iterable<Detector> detectors = detectorRepository.findAll();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());  // Capture the timestamp here

        for (Detector detector : detectors) {
            String ip = detector.getIpAddr();
            String normalizedIp = (ip == null) ? null : ip.trim();

            if (normalizedIp != null && "API".equalsIgnoreCase(normalizedIp)) {
                // This is an API-based detector, handle it based on type
                if ("ecobee".equals(detector.getType())) {
                    ecobeeService.getThermostatData(currentTimestamp);
                } else if ("openweathermap".equals(detector.getType())) {
                    weatherService.getWeatherData(currentTimestamp);
                }
                // 'smartsensor' types are implicitly ignored here, as they are handled within EcobeeService
            } else {
                // This is a standard PurpleAir sensor with a local IP address
                collectData(detector.getIpAddr(), detector.getId(), currentTimestamp);
            }
        }
    }

    private Float handleNull(Float value) {
        return value != null && !Float.isNaN(value) ? value : null;
    }

    public void collectData(String ip, int id, Timestamp currentTimeStamp) {
        String normalizedIp = (ip == null) ? null : ip.trim();

        if (normalizedIp == null || "API".equalsIgnoreCase(normalizedIp)) {
            // This method should not be called for API-based or invalid detectors.
            logger.warn("Skipping collectData for non-IP detector id=" + id + " ip=" + ip);
            return;
        }

        String url = "http://" + normalizedIp + "/json";

        try {
            DetectorResponse response = restTemplate.getForObject(url, DetectorResponse.class);
            if (response != null) {
                writeMetricsData(response, id, currentTimeStamp);
            }
        } catch (RestClientException e) {
            logger.error("Error collecting data from " + url, e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void writeMetricsData(DetectorResponse response, int detectorId, Timestamp currentTimestamp) {
        Metrics metrics = new Metrics();

        metrics.setDetectorId(detectorId);
        metrics.setTimestamp(currentTimestamp);

        // Fetch the current name from the detector table and set it as the placement
        Optional<Detector> optionalDetector = detectorRepository.findById(detectorId);
        if (optionalDetector.isPresent()) {
            metrics.setPlacement(optionalDetector.get().getName());
        } else {
            // Handle the case where the detector is not found
            logger.error("No detector found with id: " + detectorId);
            return;
        }

        metrics.setP_0_3_um(handleNull(response.getP_0_3_um()));
        metrics.setP_0_3_um_b(handleNull(response.getP_0_3_um_b()));
        metrics.setP_2_5_um(handleNull(response.getP_2_5_um()));
        metrics.setP_2_5_um_b(handleNull(response.getP_2_5_um_b()));
        metrics.setGas_680(handleNull(response.getGas_680()));

        logger.info("writing PurpleAir data: " + metrics);

        try {
            Metrics savedMetrics = metricsRepository.save(metrics);
            metricsRepository.flush();
        } catch (Exception e) {
            logger.error("Error occurred while trying to save metrics: " + e.getMessage(), e);
        }
    }
}
