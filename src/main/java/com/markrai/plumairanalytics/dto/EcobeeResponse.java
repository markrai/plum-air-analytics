package com.markrai.plumairanalytics.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EcobeeResponse {
    private List<Thermostat> thermostatList;

    public List<Thermostat> getThermostatList() {
        return thermostatList;
    }

    public void setThermostatList(List<Thermostat> thermostatList) {
        this.thermostatList = thermostatList;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thermostat {
        private Runtime runtime;


        public Runtime getRuntime() {
            return runtime;
        }

        public void setRuntime(Runtime runtime) {
            this.runtime = runtime;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Runtime {
        private Integer actualTemperature;
        private Integer actualHumidity;
        private Integer actualVOC;
        private Integer actualCO2;

        public Integer getActualTemperature() {
            return actualTemperature;
        }

        public void setActualTemperature(Integer actualTemperature) {
            this.actualTemperature = actualTemperature;
        }

        public Integer getActualHumidity() {
            return actualHumidity;
        }

        public void setActualHumidity(Integer actualHumidity) {
            this.actualHumidity = actualHumidity;
        }

        public Integer getActualVOC() {
            return actualVOC;
        }

        public void setActualVOC(Integer actualVOC) {
            this.actualVOC = actualVOC;
        }

        public Integer getActualCO2() {
            return actualCO2;
        }

        public void setActualCO2(Integer actualCO2) {
            this.actualCO2 = actualCO2;
        }
    }
}
