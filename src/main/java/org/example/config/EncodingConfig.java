package org.example.config;

public class EncodingConfig {
    private final EncodingType encodingType;

    public EncodingConfig(EncodingType encodingType) {
        this.encodingType = encodingType;
    }

    public EncodingType getEncodingType() {
        return encodingType;
    }
}