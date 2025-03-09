package org.example.security;

import org.example.config.EncryptionConfig;
import org.example.config.EncryptionType;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.util.Base64;

public class Aes256EncryptionAdapter implements EncryptionAdapter {
    private final SecretKey secretKey;
    private final IvParameterSpec iv;

    Aes256EncryptionAdapter(EncryptionConfig encryptionConfig) {
        if (!encryptionConfig.getEncryptionType().equals(EncryptionType.AES_256)) {
            throw new IllegalArgumentException("Invalid encryption type for AES-256 adapter");
        }

        byte[] keyBytes = Base64.getDecoder().decode(encryptionConfig.getAesKey());
        byte[] ivBytes = Base64.getDecoder().decode(encryptionConfig.getAesIV());

        this.secretKey = new SecretKeySpec(keyBytes, "AES");
        this.iv = new IvParameterSpec(ivBytes);
    }
    
    @Override
    public InputStream decrypt(InputStream encryptedStream) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        return new CipherInputStream(encryptedStream, cipher);
    }

    @Override
    public boolean supports(String encryptionType) {
        return EncryptionType.AES_256.getValue().equals(encryptionType);
    }
}
