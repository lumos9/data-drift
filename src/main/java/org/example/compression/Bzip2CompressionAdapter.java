package org.example.compression;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.example.config.CompressionType;

import java.io.InputStream;

public class Bzip2CompressionAdapter implements CompressionAdapter {
    @Override
    public InputStream decompress(InputStream compressedStream) throws Exception {
        return new BZip2CompressorInputStream(compressedStream);
    }

    @Override
    public boolean supports(String compressionType) {
        return CompressionType.BZIP2.getValue().equals(compressionType);
    }
}
