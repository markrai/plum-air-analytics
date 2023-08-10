package com.markrai.plumairanalytics.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markrai.plumairanalytics.dto.EcobeeResponse;
import com.markrai.plumairanalytics.dto.Selection;
import com.markrai.plumairanalytics.model.Detector;
import com.markrai.plumairanalytics.model.EcobeeToken;
import com.markrai.plumairanalytics.model.Metrics;
import com.markrai.plumairanalytics.repository.DetectorRepository;
import com.markrai.plumairanalytics.repository.EcobeeTokenRepository;
import com.markrai.plumairanalytics.repository.MetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
public class EcobeeService {

    @Autowired
    private EcobeeTokenRepository ecobeeTokenRepository;

    @Autowired
    private DetectorRepository detectorRepository;

    @Autowired
    private MetricsRepository metricsRepository;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String createSelectionJson() {
        Selection selection = new Selection("registered", "", true);
        try {
            return OBJECT_MAPPER.writeValueAsString(selection);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    final String urlTemplate = "https://api.ecobee.com/1/thermostat?format=json";

    final String body = "{\"selection\":{\"selectionType\":\"registered\",\"selectionMatch\":\"\",\"includeRuntime\":true}}";

    final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlTemplate)
            .queryParam("body", body);

    final URI uri = builder.build().encode().toUri();

    public void getThermostatData(Timestamp currentTimestamp) {
        EcobeeToken currentToken = getCurrentToken();
        String accessToken = currentToken.getAccessToken();

        // Fetch the detector entity for type 'ecobee'
        Optional<Detector> ecobeeDetectorOpt = detectorRepository.findByType("ecobee");
        if (ecobeeDetectorOpt.isEmpty()) {
            System.err.println("No detector found for type 'ecobee'");
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

        // Save the thermostat data in the database
        Metrics metrics = new Metrics();
        metrics.setDetectorId(detectorId);
        metrics.setPlacement(placement);
        metrics.setTimestamp(currentTimestamp);

        float temperatureInFahrenheit = ecobeeResponse.getThermostatList().get(0).getRuntime().getActualTemperature() / 10.0f;
        float temperatureInCelsius = (temperatureInFahrenheit - 32) / 1.8f;

        BigDecimal bd = new BigDecimal(Float.toString(temperatureInCelsius));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        temperatureInCelsius = bd.floatValue();
        metrics.setTemperature(temperatureInCelsius);

        metrics.setHumidity(ecobeeResponse.getThermostatList().get(0).getRuntime().getActualHumidity().floatValue());
        metrics.setCo2(ecobeeResponse.getThermostatList().get(0).getRuntime().getActualCO2());
        metrics.setGas_680((float) ecobeeResponse.getThermostatList().get(0).getRuntime().getActualVOC());

        System.out.println("writing Ecobee data: " + metrics);
        metricsRepository.save(metrics);
    }

    private EcobeeToken getCurrentToken() {
        EcobeeToken ecobeeToken = ecobeeTokenRepository.findEcobeeToken();
        if (ecobeeToken == null || ecobeeToken.getAccessTokenExpiresAt().toInstant().isBefore(Instant.now())) {
            throw new RuntimeException("No valid access token available");
        }
        return ecobeeToken;
    }

}
