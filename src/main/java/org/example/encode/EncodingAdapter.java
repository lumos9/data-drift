package org.example.encode;

import java.io.InputStream;
import java.io.OutputStream;

public interface EncodingAdapter {
    OutputStream encode(OutputStream decodedStream) throws Exception;

    InputStream decode(InputStream encodedStream) throws Exception;
}
