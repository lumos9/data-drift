package org.example.encode;

import java.io.InputStream;
import java.io.OutputStream;

// Example implementations
public class Utf8EncodingAdapter implements EncodingAdapter {
    @Override
    public InputStream decode(InputStream encodedStream) throws Exception {
        return encodedStream; // UTF-8 is the target, no transformation needed
    }

    @Override
    public OutputStream encode(OutputStream outputStream) throws Exception {
        return outputStream; // Pass-through for writing
    }
}