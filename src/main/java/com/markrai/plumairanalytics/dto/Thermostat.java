package com.markrai.plumairanalytics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Thermostat {
    private Runtime runtime;
    private List<RemoteSensor> remoteSensors;

    public Runtime getRuntime() {
        return runtime;
    }

    public void setRuntime(Runtime runtime) {
        this.runtime = runtime;
    }

    public List<RemoteSensor> getRemoteSensors() {
        return remoteSensors;
    }

    public void setRemoteSensors(List<RemoteSensor> remoteSensors) {
        this.remoteSensors = remoteSensors;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Runtime {
        private Integer actualTemperature;
        private Integer actualHumidity;

        @JsonProperty("actualVOC")
        private Integer actualVOC;

        @JsonProperty("actualCO2")
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
