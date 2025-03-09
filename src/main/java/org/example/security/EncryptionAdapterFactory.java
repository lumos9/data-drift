package org.example.security;

import org.example.config.EncryptionConfig;

public class EncryptionAdapterFactory {
    public static EncryptionAdapter getAdapter(EncryptionConfig encryptionConfig) throws Exception {
        if (encryptionConfig == null) return new NoEncryptionAdapter();
        //case SSE_S3 -> new SseS3EncryptionAdapter();
        //case SSE_C -> new SseCustomerEncryptionAdapter();
        //case PGP -> new PgpEncryptionAdapter();
        //case GZIP -> new GzipEncryptionAdapter();
        //case CUSTOM -> new CustomEncryptionAdapter();
        return switch (encryptionConfig.getEncryptionType()) {
            case AES_256 -> new Aes256EncryptionAdapter(encryptionConfig);
            case SSE_KMS -> new SseKmsEncryptionAdapter(encryptionConfig);
            case NONE -> new NoEncryptionAdapter();
            default -> throw new IllegalArgumentException(
                    "Unknown Encryption type " + encryptionConfig.getEncryptionType());
        };
    }
}
