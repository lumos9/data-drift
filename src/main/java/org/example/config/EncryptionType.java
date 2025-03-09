package org.example.config;

public enum EncryptionType {
    NONE("NONE"),
    SSE_S3("SSE-S3"),           // AWS S3 Managed Encryption
    SSE_KMS("SSE-KMS"),         // AWS KMS Managed Keys
    SSE_C("SSE-C"),            // AWS Customer Provided Keys
    AES_256("AES-256"),        // Standard AES-256 Encryption
    PGP("PGP"),               // Pretty Good Privacy
    GZIP("GZIP"),             // Compression (often used with encryption)
    CUSTOM("CUSTOM");         // For custom implementations

    private final String value;

    EncryptionType(String value) {
        this.value = value;
    }

    // Custom method to look up enum by YAML value
    public static EncryptionType fromValue(String yamlValue) {
        for (EncryptionType type : values()) {
            if (type.value.equalsIgnoreCase(yamlValue)) { // Case-insensitive if desired
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown encryption type: " + yamlValue);
    }

    public String getValue() {
        return value;
    }
}
