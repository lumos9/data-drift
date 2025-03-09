package org.example.compression;

import org.example.config.CompressionConfig;
import org.example.config.CompressionType;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipCompressionAdapter implements CompressionAdapter {
    private final String entryName;

    public ZipCompressionAdapter(CompressionConfig compressionConfig) throws Exception {
        if (!compressionConfig.getType().equals(CompressionType.ZIP)) {
            throw new IllegalArgumentException("Invalid compression type for ZIP adapter");
        }
        this.entryName = compressionConfig.getZipEntryName();
    }

    @Override
    public InputStream decompress(InputStream compressedStream) throws Exception {
        ZipInputStream zis = new ZipInputStream(compressedStream);
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (entryName == null || entry.getName().equals(entryName)) {
                return zis; // Return the stream for the first or specified entry
            }
        }
        throw new IOException("No matching ZIP entry found: " + entryName);
    }

    @Override
    public boolean supports(String compressionType) {
        return CompressionType.ZIP.getValue().equals(compressionType);
    }
}