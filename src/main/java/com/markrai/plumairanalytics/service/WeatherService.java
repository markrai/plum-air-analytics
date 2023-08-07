package com.markrai.plumairanalytics.service;

import com.markrai.plumairanalytics.config.WeatherConfig;
import com.markrai.plumairanalytics.dto.WeatherResponse;
import com.markrai.plumairanalytics.model.Detector;
import com.markrai.plumairanalytics.model.Metrics;
import com.markrai.plumairanalytics.repository.DetectorRepository;
import com.markrai.plumairanalytics.repository.MetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class WeatherService {

    @Autowired
    private WeatherConfig weatherConfig;

    @Autowired
    private MetricsRepository metricsRepository;

    @Autowired
    private DetectorRepository detectorRepository;

    public void getWeatherData(Timestamp currentTimestamp) {
        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s,%s,%s&appid=%s&units=%s",
                weatherConfig.getCity(),
                weatherConfig.getState(),
                weatherConfig.getCountry(),
                apiKey,
                weatherConfig.getUnits());

        RestTemplate restTemplate = new RestTemplate();

        WeatherResponse response = restTemplate.exchange(url, HttpMethod.GET, null, WeatherResponse.class).getBody();

        Metrics metrics = new Metrics();
        metrics.setTimestamp(currentTimestamp);

        // Fetch the detector entity for type 'openweathermap'
        Optional<Detector> weatherDetectorOpt = detectorRepository.findByType("openweathermap");
        if (weatherDetectorOpt.isPresent()) {
            Detector weatherDetector = weatherDetectorOpt.get();
            metrics.setDetectorId(weatherDetector.getId());
            metrics.setPlacement(weatherDetector.getName());
        } else {
            System.err.println("No detector found for type 'openweathermap'");
            return;
        }

        metrics.setOutdoorTemperature(response.getMain().getTemp().floatValue());
        metrics.setOutdoorHumidity(response.getMain().getHumidity().floatValue());
        System.out.println("writing Weather data: " + metrics);
        metricsRepository.save(metrics);
    }
}
