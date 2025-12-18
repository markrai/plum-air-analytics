package com.markrai.plumairanalytics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteSensor {
    private String name;
    private List<Capability> capability;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Capability> getCapability() {
        return capability;
    }

    public void setCapability(List<Capability> capability) {
        this.capability = capability;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Capability {
        private String type;
        private String value;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
