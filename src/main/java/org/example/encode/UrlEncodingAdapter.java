package org.example.encode;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlEncodingAdapter implements EncodingAdapter {
//    @Override
//    public InputStream decode(InputStream encodedStream) throws Exception {
//        if (encodedStream == null) throw new IllegalArgumentException("Input stream cannot be null");
//        // Read the stream as UTF-8 text, decode URL encoding, then re-encode as UTF-8 bytes
//        String encodedText = new String(encodedStream.readAllBytes(), StandardCharsets.UTF_8);
//        String decodedText = URLDecoder.decode(encodedText, StandardCharsets.UTF_8);
//        return new ByteArrayInputStream(decodedText.getBytes(StandardCharsets.UTF_8));
//    }
//
//    @Override
//    public OutputStream encode(OutputStream outputStream) throws Exception {
//        if (outputStream == null) throw new IllegalArgumentException("Output stream cannot be null");
//        // Wrap output stream to encode UTF-8 text as URL-encoded bytes
//        return new FilterOutputStream(outputStream) {
//            private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//
//            @Override
//            public void write(int b) throws IOException {
//                buffer.write(b);
//            }
//
//            @Override
//            public void flush() throws IOException {
//                if (buffer.size() > 0) {
//                    String text = buffer.toString(StandardCharsets.UTF_8);
//                    String encoded = URLEncoder.encode(text, StandardCharsets.UTF_8);
//                    outputStream.write(encoded.getBytes(StandardCharsets.UTF_8));
//                    buffer.reset();
//                }
//                outputStream.flush();
//            }
//
//            @Override
//            public void close() throws IOException {
//                flush();
//                outputStream.close();
//            }
//        };
//    }

    @Override
    public InputStream decode(InputStream encodedStream) throws Exception {
        if (encodedStream == null) throw new IllegalArgumentException("Input stream cannot be null");
        return new FilterInputStream(encodedStream) {
            private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            private int state = 0; // 0: normal, 1: %, 2: %X
            private int hex1;

            @Override
            public int read() throws IOException {
                while (true) {
                    int b = in.read();
                    if (b == -1) {
                        if (buffer.size() > 0) return flushBuffer();
                        return -1;
                    }
                    if (b == '%') {
                        state = 1;
                        continue;
                    }
                    if (state == 1) {
                        hex1 = Character.digit(b, 16);
                        state = 2;
                        continue;
                    }
                    if (state == 2) {
                        int hex2 = Character.digit(b, 16);
                        state = 0;
                        buffer.write((hex1 << 4) + hex2);
                        continue;
                    }
                    buffer.write(b);
                    return flushBuffer();
                }
            }

            private int flushBuffer() {
                if (buffer.size() == 0) return -1;
                byte[] bytes = buffer.toByteArray();
                buffer.reset();
                return bytes[0] & 0xFF; // Return first byte as int
            }
        };
    }

    @Override
    public OutputStream encode(OutputStream outputStream) throws Exception {
        if (outputStream == null) throw new IllegalArgumentException("Output stream cannot be null");
        return new FilterOutputStream(outputStream) {
            @Override
            public void write(int b) throws IOException {
                String ch = new String(new byte[]{(byte) b}, StandardCharsets.UTF_8);
                String encoded = URLEncoder.encode(ch, StandardCharsets.UTF_8);
                outputStream.write(encoded.getBytes(StandardCharsets.UTF_8));
            }
        };
    }
}