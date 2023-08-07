package com.markrai.plumairanalytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Selection {

    @JsonProperty("selectionType")
    private String selectionType;

    @JsonProperty("selectionMatch")
    private String selectionMatch;

    @JsonProperty("includeRuntime")
    private boolean includeRuntime;

    public Selection(String selectionType, String selectionMatch, boolean includeRuntime) {
        this.selectionType = selectionType;
        this.selectionMatch = selectionMatch;
        this.includeRuntime = includeRuntime;
    }

    // Getters and setters
    public String getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(String selectionType) {
        this.selectionType = selectionType;
    }

    public String getSelectionMatch() {
        return selectionMatch;
    }

    public void setSelectionMatch(String selectionMatch) {
        this.selectionMatch = selectionMatch;
    }

    public boolean isIncludeRuntime() {
        return includeRuntime;
    }

    public void setIncludeRuntime(boolean includeRuntime) {
        this.includeRuntime = includeRuntime;
    }
}
