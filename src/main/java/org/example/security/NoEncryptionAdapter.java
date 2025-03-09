package org.example.security;

import org.example.config.EncryptionType;

import java.io.InputStream;

public class NoEncryptionAdapter implements EncryptionAdapter {
    @Override
    public InputStream decrypt(InputStream encryptedStream) throws Exception {
        return encryptedStream;
    }

    @Override
    public boolean supports(String encryptionType) {
        return EncryptionType.NONE.getValue().equals(encryptionType);
    }
}
