package org.example.compression;

import org.example.config.CompressionType;

import java.io.InputStream;

public class NoOpCompressionAdapter implements CompressionAdapter {
    @Override
    public InputStream decompress(InputStream compressedStream) throws Exception {
        return compressedStream; // No decompression needed
    }

    @Override
    public boolean supports(String compressionType) {
        return CompressionType.NONE.getValue().equals(compressionType);
    }
}
