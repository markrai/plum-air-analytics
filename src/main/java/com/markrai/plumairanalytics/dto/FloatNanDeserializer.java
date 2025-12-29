package com.markrai.plumairanalytics.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Custom deserializer for Float that handles "nan", "NaN", "inf", "Infinity" strings
 * that may be returned by the PurpleAir API.
 * These are converted to null since Float.NaN and Float.POSITIVE_INFINITY are not
 * meaningful values for sensor data.
 */
public class FloatNanDeserializer extends JsonDeserializer<Float> {

    @Override
    public Float deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        
        if (value == null) {
            return null;
        }
        
        // Handle "nan" and "NaN" strings
        if ("nan".equalsIgnoreCase(value)) {
            return null;
        }
        
        // Handle "inf", "infinity", "-inf", "-infinity" strings
        String lowerValue = value.toLowerCase();
        if ("inf".equals(lowerValue) || "infinity".equals(lowerValue) || 
            "-inf".equals(lowerValue) || "-infinity".equals(lowerValue)) {
            return null;
        }
        
        // Try to parse as a normal float
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            // If parsing fails, return null instead of throwing an exception
            return null;
        }
    }
}

