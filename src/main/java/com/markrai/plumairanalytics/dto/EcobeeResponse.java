package com.markrai.plumairanalytics.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EcobeeResponse {

    @JsonProperty("thermostatList")
    private List<Thermostat> thermostatList;

    public List<Thermostat> getThermostatList() {
        return thermostatList;
    }

    public void setThermostatList(List<Thermostat> thermostatList) {
        this.thermostatList = thermostatList;
    }
}
