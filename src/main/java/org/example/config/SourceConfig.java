package org.example.config;

public class SourceConfig {
    private String type; // "file", "s3", "hdfs", etc.
    private String path; // Path to the source (e.g., file location, s3 URI)
    private String region;
    private String bucketName;
    private DbConfig dbConfig;
    private KafkaConfig kafkaConfig;
    private AuthConfig authConfig; // Authentication configuration for cloud services like S3, HDFS, etc.
    private EncryptionConfig encryptionConfig; // Whether the source data is encrypted
    private CompressionConfig compressionConfig;
    private EncodingConfig encodingConfig;
    private String decryptionKey; // Key for decrypting the source if necessary

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public DbConfig getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public KafkaConfig getKafkaConfig() {
        return kafkaConfig;
    }

    public void setKafkaConfig(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    public AuthConfig getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    public EncryptionConfig getEncryptionConfig() {
        return encryptionConfig;
    }

    public void setEncryptionConfig(EncryptionConfig encryptionConfig) {
        this.encryptionConfig = encryptionConfig;
    }

    public CompressionConfig getCompressionConfig() {
        return compressionConfig;
    }

    public void setCompressionConfig(CompressionConfig compressionConfig) {
        this.compressionConfig = compressionConfig;
    }

    public EncodingConfig getEncodingConfig() {
        return encodingConfig;
    }

    public void setEncodingConfig(EncodingConfig encodingConfig) {
        this.encodingConfig = encodingConfig;
    }

    public String getDecryptionKey() {
        return decryptionKey;
    }

    public void setDecryptionKey(String decryptionKey) {
        this.decryptionKey = decryptionKey;
    }
}
