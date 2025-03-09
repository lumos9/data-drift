package org.example.config;

public enum CompressionType {
    NONE("NONE"),           // No compression
    GZIP("GZIP"),          // Gzip compression (.gz)
    ZIP("ZIP"),            // Zip compression (.zip)
    BZIP2("BZIP2"),        // Bzip2 compression (.bz2)
    XZ("XZ"),             // XZ compression (.xz)
    SNAPPY("SNAPPY"),     // Snappy compression (often used in big data)
    LZ4("LZ4"),           // LZ4 compression (fast compression)
    CUSTOM("CUSTOM");     // For custom implementations

    private final String value;

    CompressionType(String value) {
        this.value = value;
    }

    // Lookup by value (for YAML compatibility if needed)
    public static CompressionType fromValue(String value) {
        for (CompressionType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown compression type: " + value);
    }

    public String getValue() {
        return value;
    }
}