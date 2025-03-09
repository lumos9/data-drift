package org.example.config;

import java.util.Map;

public class EncryptionConfig {
    private boolean enabled;
    private EncryptionType encryptionType; // "SSE-S3", "SSE-KMS", "SSE-C", "NONE"
    private String kmsKeyArn; // Required for SSE-KMS
    private String sseCustomerAlgorithm; // Required for SSE-C
    private String sseCustomerKey; // Required for SSE-C (Base64 encoded)

    // AES-256
    private String aesKey;        // Secret key for AES
    private String aesIV;         // Initialization Vector
    // PGP
    private String pgpPublicKey;
    private String pgpPrivateKey;
    // Custom
    private Map<String, String> customProperties;

    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public EncryptionType getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(EncryptionType encryptionType) {
        this.encryptionType = encryptionType;
    }

    public EncryptionType getType() {
        return encryptionType;
    }

    public void setType(String type) {
        this.encryptionType = EncryptionType.fromValue(type);
    }

    public String getKmsKeyArn() {
        return kmsKeyArn;
    }

    public void setKmsKeyArn(String kmsKeyArn) {
        this.kmsKeyArn = kmsKeyArn;
    }

    public String getSseCustomerAlgorithm() {
        return sseCustomerAlgorithm;
    }

    public void setSseCustomerAlgorithm(String sseCustomerAlgorithm) {
        this.sseCustomerAlgorithm = sseCustomerAlgorithm;
    }

    public String getSseCustomerKey() {
        return sseCustomerKey;
    }

    public void setSseCustomerKey(String sseCustomerKey) {
        this.sseCustomerKey = sseCustomerKey;
    }

    public String getAesIV() {
        return aesIV;
    }

    public void setAesIV(String aesIV) {
        this.aesIV = aesIV;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getPgpPrivateKey() {
        return pgpPrivateKey;
    }

    public void setPgpPrivateKey(String pgpPrivateKey) {
        this.pgpPrivateKey = pgpPrivateKey;
    }

    public String getPgpPublicKey() {
        return pgpPublicKey;
    }

    public void setPgpPublicKey(String pgpPublicKey) {
        this.pgpPublicKey = pgpPublicKey;
    }

    public Map<String, String> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Map<String, String> customProperties) {
        this.customProperties = customProperties;
    }
}

