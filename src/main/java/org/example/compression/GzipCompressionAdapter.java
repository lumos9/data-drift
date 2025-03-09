package org.example.compression;

import org.example.config.CompressionType;

import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class GzipCompressionAdapter implements CompressionAdapter {
    @Override
    public InputStream decompress(InputStream compressedStream) throws Exception {
        return new GZIPInputStream(compressedStream);
    }

    @Override
    public boolean supports(String compressionType) {
        return CompressionType.GZIP.getValue().equals(compressionType);
    }
}
