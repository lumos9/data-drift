package org.example.security;

import java.io.InputStream;

public interface EncryptionAdapter {
    InputStream decrypt(InputStream encryptedStream) throws Exception;

    boolean supports(String encryptionType);
}
