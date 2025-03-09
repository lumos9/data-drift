package org.example.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CompressionConfig {
    private static final Logger logger = LogManager.getLogger(CompressionConfig.class);
    private boolean enabled;
    private CompressionType compressionType;
    // ZIP-specific
    private String zipEntryName; // For ZIP files with multiple entries
    // Custom properties
    private Map<String, String> customProperties;

    public CompressionConfig() {
        this.enabled = false;
        this.compressionType = CompressionType.NONE;
        this.customProperties = new HashMap<>();
    }

    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public CompressionType getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(CompressionType compressionType) {
        logger.info("loading comp type now..");
        this.compressionType = compressionType;
    }

    public CompressionType getType() {
        return compressionType;
    }

    public void setType(String type) {
        logger.info("loading type now..");
        this.compressionType = CompressionType.fromValue(type);
    }

    public String getZipEntryName() {
        return zipEntryName;
    }

    public void setZipEntryName(String zipEntryName) {
        this.zipEntryName = zipEntryName;
    }

    public Map<String, String> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Map<String, String> customProperties) {
        this.customProperties = customProperties;
    }
}
