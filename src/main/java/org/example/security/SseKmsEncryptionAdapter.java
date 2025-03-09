package org.example.security;

import org.example.config.EncryptionConfig;
import org.example.config.EncryptionType;

import java.io.InputStream;

public class SseKmsEncryptionAdapter implements EncryptionAdapter {
    //private AWSKMS kmsClient;
    private String keyArn;

    SseKmsEncryptionAdapter(EncryptionConfig encryptionConfig) {
        if (!encryptionConfig.getEncryptionType().equals(EncryptionType.SSE_KMS)) {
            throw new IllegalArgumentException("Invalid encryption type for SSE-KMS adapter");
        }
        this.keyArn = encryptionConfig.getKmsKeyArn();
        //this.kmsClient = AWSKMSClientBuilder.defaultClient();
    }

    @Override
    public InputStream decrypt(InputStream encryptedStream) throws Exception {
        // Implementation would use AWS SDK to decrypt using KMS
        return encryptedStream; // Simplified for example
    }

    @Override
    public boolean supports(String encryptionType) {
        return EncryptionType.SSE_KMS.getValue().equals(encryptionType);
    }
}
