package org.example.compression;

import org.example.config.CompressionConfig;


public class CompressionAdapterFactory {
    public static CompressionAdapter getAdapter(CompressionConfig compressionConfig) throws Exception {
        if (compressionConfig == null) return new NoOpCompressionAdapter();
        //            case XZ -> new XzCompressionAdapter();
        //            case SNAPPY -> new SnappyCompressionAdapter();
        //            case LZ4 -> new Lz4CompressionAdapter();
        //            case CUSTOM -> new CustomCompressionAdapter();
        return switch (compressionConfig.getType()) {
            case GZIP -> new GzipCompressionAdapter();
            case BZIP2 -> new Bzip2CompressionAdapter();
            case NONE -> new NoOpCompressionAdapter();
            case ZIP -> new ZipCompressionAdapter(compressionConfig);
            default -> throw new IllegalArgumentException("Unknown compression type " + compressionConfig.getType());
        };
    }
}

