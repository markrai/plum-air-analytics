package com.markrai.plumairanalytics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markrai.plumairanalytics.dto.EcobeeResponse;
import com.markrai.plumairanalytics.dto.RemoteSensor;
import com.markrai.plumairanalytics.dto.Thermostat;
import com.markrai.plumairanalytics.model.Detector;
import com.markrai.plumairanalytics.model.EcobeeToken;
import com.markrai.plumairanalytics.model.Metrics;
import com.markrai.plumairanalytics.repository.DetectorRepository;
import com.markrai.plumairanalytics.repository.EcobeeTokenRepository;
import com.markrai.plumairanalytics.repository.MetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
public class EcobeeService {

    private static final Logger logger = LoggerFactory.getLogger(EcobeeService.class);

    @Autowired
    private EcobeeTokenRepository ecobeeTokenRepository;

    @Autowired
    private DetectorRepository detectorRepository;

    @Autowired
    private MetricsRepository metricsRepository;

    final String urlTemplate = "https://api.ecobee.com/1/thermostat?format=json";

    final String body = "{\"selection\":{\"selectionType\":\"registered\",\"selectionMatch\":\"\",\"includeRuntime\":true,\"includeSensors\":true}}";

    final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlTemplate)
            .queryParam("body", body);

    final URI uri = builder.build().encode().toUri();

    public void getThermostatData(Timestamp currentTimestamp) {
        try {
            EcobeeToken currentToken = getCurrentToken();
            String accessToken = currentToken.getAccessToken();

        // Fetch the detector entity for type 'ecobee'
        Optional<Detector> ecobeeDetectorOpt = detectorRepository.findByType("ecobee");
        if (ecobeeDetectorOpt.isEmpty()) {
            logger.error("No detector found for type 'ecobee'");
            return;
        }
        Detector detector = ecobeeDetectorOpt.get();

        int detectorId = detector.getId();  // Get the id from the Detector
        String placement = detector.getName();  // Get the placement from the Detector
        // Prepare the request
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the HTTP GET request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);


        // Parse the response to get the thermostat data
        String responseBody = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        EcobeeResponse ecobeeResponse;
        try {
            ecobeeResponse = mapper.readValue(responseBody, EcobeeResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse ecobee response", e);
        }

        Thermostat mainThermostat = ecobeeResponse.getThermostatList().get(0);

        // Save the thermostat data in the database
        Metrics metrics = new Metrics();
        metrics.setDetectorId(detectorId);
        metrics.setPlacement(placement);
        metrics.setTimestamp(currentTimestamp);

        float temperatureInFahrenheit = mainThermostat.getRuntime().getActualTemperature() / 10.0f;
        float temperatureInCelsius = (temperatureInFahrenheit - 32) / 1.8f;

        BigDecimal bd = new BigDecimal(Float.toString(temperatureInCelsius));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        temperatureInCelsius = bd.floatValue();
        metrics.setTemperature(temperatureInCelsius);

        metrics.setHumidity(mainThermostat.getRuntime().getActualHumidity().floatValue());
        metrics.setCo2(mainThermostat.getRuntime().getActualCO2());
        metrics.setGas_680(mainThermostat.getRuntime().getActualVOC() / 100.0f);

        logger.info("writing Ecobee data: " + metrics);
        metricsRepository.save(metrics);

        // Process remote sensors
        processRemoteSensors(mainThermostat.getRemoteSensors(), currentTimestamp);
        } catch (Exception e) {
            logger.error("Error in getThermostatData(): {}", e.getMessage(), e);
            throw e; // Re-throw to be caught by caller
        }
    }

    private void processRemoteSensors(java.util.List<RemoteSensor> remoteSensors, Timestamp currentTimestamp) {
        for (RemoteSensor sensor : remoteSensors) {
            String sensorName = sensor.getName();
            String placement;
            int detectorId;

            switch (sensorName) {
                case "Bedroom Sensor": // Updated API sensor name
                    placement = "SmartSensorNour";
                    detectorId = 7;
                    break;
                case "Rania":
                    placement = "SmartSensorRania";
                    detectorId = 8;
                    break;
                case "Rumi":
                    placement = "SmartSensorRumi";
                    detectorId = 9;
                    break;
                default:
                    logger.warn("Skipping remote sensor '{}' - no matching detector configured.", sensorName);
                    continue;
            }

            Metrics metrics = new Metrics();
            metrics.setDetectorId(detectorId);
            metrics.setPlacement(placement);
            metrics.setTimestamp(currentTimestamp);

            for (RemoteSensor.Capability capability : sensor.getCapability()) {
                if ("temperature".equals(capability.getType())) {
                    Float raw = parseNullableFloat(capability.getValue());
                    if (raw != null) {
                        float temperatureInFahrenheit = raw / 10.0f;
                        float temperatureInCelsius = (temperatureInFahrenheit - 32) / 1.8f;
                        BigDecimal bd = new BigDecimal(Float.toString(temperatureInCelsius));
                        bd = bd.setScale(2, RoundingMode.HALF_UP);
                        temperatureInCelsius = bd.floatValue();
                        metrics.setTemperature(temperatureInCelsius);
                    }
                } else if ("humidity".equals(capability.getType())) {
                    Float humidity = parseNullableFloat(capability.getValue());
                    if (humidity != null) {
                        metrics.setHumidity(humidity);
                    }
                }
            }

            logger.info("writing Ecobee sensor data: " + metrics);
            metricsRepository.save(metrics);
        }
    }

    private Float parseNullableFloat(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.isEmpty() || "unknown".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            logger.warn("Skipping non-numeric sensor value: '{}'", value);
            return null;
        }
    }

    private EcobeeToken getCurrentToken() {
        EcobeeToken ecobeeToken = ecobeeTokenRepository.findEcobeeToken();
        if (ecobeeToken == null || ecobeeToken.getAccessTokenExpiresAt().toInstant().isBefore(Instant.now())) {
            throw new RuntimeException("No valid access token available");
        }
        return ecobeeToken;
    }

}
