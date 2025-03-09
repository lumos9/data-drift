package org.example.encode;

import org.example.config.EncodingConfig;

public class EncodingAdapterFactory {
    public static EncodingAdapter getAdapter(EncodingConfig encodingConfig) throws Exception {
        if (encodingConfig == null) return new Utf8EncodingAdapter();
        return switch (encodingConfig.getEncodingType()) {
            case UTF_8, NONE -> new Utf8EncodingAdapter();
            case BASE_64 -> new Base64EncodingAdapter();
            case URL -> new UrlEncodingAdapter();
            case ASCII -> new AsciiEncodingAdapter();
        };
    }
}
