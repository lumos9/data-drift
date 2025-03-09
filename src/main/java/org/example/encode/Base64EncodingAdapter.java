package org.example.encode;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public class Base64EncodingAdapter implements EncodingAdapter {
    @Override
    public InputStream decode(InputStream encodedStream) throws Exception {
        return Base64.getDecoder().wrap(encodedStream); // Wraps the stream for incremental decoding
    }

    @Override
    public OutputStream encode(OutputStream outputStream) throws Exception {
        return Base64.getEncoder().wrap(outputStream); // Wraps for incremental encoding
    }
}