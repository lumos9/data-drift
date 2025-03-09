package org.example.compression;

import java.io.InputStream;

public interface CompressionAdapter {
    InputStream decompress(InputStream compressedStream) throws Exception;

    boolean supports(String compressionType);
}
