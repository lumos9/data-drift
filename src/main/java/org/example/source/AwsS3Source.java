package org.example.source;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.AuthConfig;
import org.example.config.EncryptionConfig;
import org.example.config.EncryptionType;
import org.example.config.SourceConfig;
import org.example.pipeline.DataPipeline;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;

public class AwsS3Source extends ETLDataSource {
    private static final Logger logger = LogManager.getLogger(AwsS3Source.class);
    private final S3Client s3;
    private final GetObjectRequest request;

    public AwsS3Source(SourceConfig sourceConfig) throws Exception {
        super(sourceConfig);
        AwsCredentialsProvider credentialsProvider = getCredentialsProvider(sourceConfig.getAuthConfig());
        try {
            this.s3 = S3Client.builder()
                    .region(getAwsRegion(sourceConfig.getRegion())) // Change to your S3 bucket region
                    .credentialsProvider(credentialsProvider)
                    .build();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        // Build request
        this.request = buildGetObjectRequest(sourceConfig.getBucketName(), sourceConfig.getPath());
        logger.info("Configured AwsS3Source with path '{}'", sourceConfig.getPath());
    }

    /**
     * Determine credentials provider based on YAML settings
     */
    private static AwsCredentialsProvider getCredentialsProvider(AuthConfig authConfig) {
        if (authConfig == null) return null;
        String method = authConfig.getMethod();

        return switch (method) {
            case "iam-role" -> DefaultCredentialsProvider.create(); // Uses instance profile
            case "access-keys" -> StaticCredentialsProvider.create(AwsBasicCredentials.create(
                    authConfig.getAccessKey(), authConfig.getSecretKey()));
            case "session-token" -> StaticCredentialsProvider.create(AwsSessionCredentials.create(
                    authConfig.getAccessKey(), authConfig.getSecretKey(), authConfig.getSessionToken()));
            default -> throw new IllegalArgumentException("Invalid auth method: " + method);
        };
    }

    public static Region getAwsRegion(String regionString) {
        try {
            return Region.of(regionString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid AWS region: '" + regionString + "'", e);
        }
    }

    @Override
    public InputStream getInputStream() {
        return s3.getObject(request);
    }

    /**
     * Build a GetObjectRequest dynamically based on encryption settings
     */
    private GetObjectRequest buildGetObjectRequest(String bucket, String key) {
        GetObjectRequest.Builder requestBuilder = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key);

        // Handle encryption options
        EncryptionConfig encryptionConfig = sourceConfig.getEncryptionConfig();
        if (encryptionConfig != null && encryptionConfig.isEnabled()) {
            EncryptionType encryptionType = encryptionConfig.getEncryptionType();
            switch (encryptionType) {
                case SSE_KMS -> {
                    // No need to pass KMS key ARN; AWS handles it automatically via IAM permissions
                }
                case SSE_C -> requestBuilder
                        .sseCustomerAlgorithm(encryptionConfig.getSseCustomerAlgorithm())
                        .sseCustomerKey(encryptionConfig.getSseCustomerKey());
                case SSE_S3, NONE -> {
                    // AWS handles SSE-S3 automatically, no parameters needed
                }
                default -> throw new IllegalArgumentException("Unknown S3 encryption type: " + encryptionType);
            }
        } else {
            logger.warn("Building unencrypted request for AWS S3 source");
        }

        return requestBuilder.build();
    }

    @Override
    public void submitTo(DataPipeline pipeline) {
        logger.info("Attempting to stream s3 file '{}' from bucket '{}' in region '{}'",
                sourceConfig.getPath(), sourceConfig.getBucketName(), sourceConfig.getRegion());
        streamAsMap(pipeline);
    }
}
