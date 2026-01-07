package com.markrai.plumairanalytics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markrai.plumairanalytics.dto.EcobeeResponse;
import com.markrai.plumairanalytics.dto.RemoteSensor;
import com.markrai.plumairanalytics.dto.Thermostat;
import com.markrai.plumairanalytics.model.EcobeeToken;
import com.markrai.plumairanalytics.repository.EcobeeTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class SensorDebugController {

    private static final Logger logger = LoggerFactory.getLogger(SensorDebugController.class);

    @Autowired
    private EcobeeTokenRepository ecobeeTokenRepository;

    @GetMapping("/sensors")
    public Map<String, Object> getSensorIds() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, String>> sensors = new ArrayList<>();

        try {
            EcobeeToken currentToken = ecobeeTokenRepository.findEcobeeToken();
            if (currentToken == null || currentToken.getAccessTokenExpiresAt().toInstant().isBefore(Instant.now())) {
                response.put("error", "No valid access token available");
                return response;
            }

            String accessToken = currentToken.getAccessToken();
            String urlTemplate = "https://api.ecobee.com/1/thermostat?format=json";
            String body = "{\"selection\":{\"selectionType\":\"registered\",\"selectionMatch\":\"\",\"includeRuntime\":true,\"includeSensors\":true}}";

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlTemplate)
                    .queryParam("body", body);
            var uri = builder.build().encode().toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);

            String responseBody = responseEntity.getBody();
            response.put("rawResponse", responseBody);

            ObjectMapper mapper = new ObjectMapper();
            EcobeeResponse ecobeeResponse = mapper.readValue(responseBody, EcobeeResponse.class);

            if (ecobeeResponse.getThermostatList() != null && !ecobeeResponse.getThermostatList().isEmpty()) {
                Thermostat mainThermostat = ecobeeResponse.getThermostatList().get(0);

                if (mainThermostat.getRemoteSensors() != null) {
                    for (RemoteSensor sensor : mainThermostat.getRemoteSensors()) {
                        Map<String, String> sensorInfo = new HashMap<>();
                        sensorInfo.put("id", sensor.getId());
                        sensorInfo.put("name", sensor.getName());
                        if (sensor.getCapability() != null && !sensor.getCapability().isEmpty()) {
                            sensorInfo.put("capabilities", sensor.getCapability().toString());
                        }
                        sensors.add(sensorInfo);
                    }
                }
            }

            response.put("sensors", sensors);
            response.put("count", sensors.size());

        } catch (Exception e) {
            logger.error("Error fetching sensor IDs", e);
            response.put("error", e.getMessage());
        }

        return response;
    }
}
