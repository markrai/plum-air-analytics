package com.markrai.plumairanalytics.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markrai.plumairanalytics.dto.TokenResponse;
import com.markrai.plumairanalytics.model.Detector;
import com.markrai.plumairanalytics.model.EcobeeToken;
import com.markrai.plumairanalytics.repository.EcobeeTokenRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;

@Component
public class EcobeeTokenRefresher {

    @Autowired
    private EcobeeTokenRepository ecobeeTokenRepository;

    private static final long BUFFER = 300;  // 5 minutes

    @PostConstruct
    @Scheduled(fixedRate = 3000000) // Run every 50 minutes
    public void refreshTokens() {
        EcobeeToken currentToken = ecobeeTokenRepository.findEcobeeToken();
        String refreshToken = currentToken.getRefreshToken();
        Detector detector = currentToken.getDetector();  // Get the Detector object
        int detectorId = detector.getId();  // Get the id from the Detector
        String apiKey = System.getenv("ECOBEE_API_KEY");

        // Prepare the request
        String url = "https://api.ecobee.com/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("code", refreshToken);
        body.add("client_id", apiKey);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // Make the HTTP POST request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        // Parse the response to get the new tokens and expires_in value
        String responseBody = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TokenResponse tokenResponse;
        try {
            tokenResponse = mapper.readValue(responseBody, TokenResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse token response", e);
        }
        String accessToken = tokenResponse.getAccess_token();
        long expiresIn = tokenResponse.getExpires_in();

        // Calculate the expiration time
        Instant accessTokenExpiresAt = Instant.now().plusSeconds(expiresIn - BUFFER);
        Instant refreshTokenExpiresAt = Instant.now().plusSeconds(31536000); // 1 year in seconds

        // Update the existing EcobeeToken entity
        currentToken.setAccessToken(accessToken);
        currentToken.setAccessTokenExpiresAt(accessTokenExpiresAt);
        currentToken.setRefreshToken(refreshToken);
        currentToken.setRefreshTokenExpiresAt(refreshTokenExpiresAt);
        currentToken.setDetector(detector);

        // Save the updated tokens and expiration time in storage
        ecobeeTokenRepository.save(currentToken);
    }
}
