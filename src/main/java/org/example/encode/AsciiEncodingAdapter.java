package org.example.encode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AsciiEncodingAdapter implements EncodingAdapter {
    @Override
    public InputStream decode(InputStream encodedStream) throws Exception {
        if (encodedStream == null) throw new IllegalArgumentException("Input stream cannot be null");
        // Wrap the ASCII stream and convert to UTF-8 on-the-fly
        return new InputStream() {
            @Override
            public int read() throws IOException {
                int b = encodedStream.read();
                if (b == -1) return -1;
                // ASCII is 0-127; anything higher is invalid, replace with '?'
                return (b & 0xFF) <= 127 ? b : '?';
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                int bytesRead = encodedStream.read(b, off, len);
                if (bytesRead == -1) return -1;
                for (int i = off; i < off + bytesRead; i++) {
                    if ((b[i] & 0xFF) > 127) b[i] = (byte) '?'; // Replace non-ASCII
                }
                return bytesRead;
            }
        };
    }

    @Override
    public OutputStream encode(OutputStream outputStream) throws Exception {
        if (outputStream == null) throw new IllegalArgumentException("Output stream cannot be null");
        // Convert UTF-8 input to ASCII output (non-ASCII becomes '?')
        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                outputStream.write((b & 0xFF) <= 127 ? b : '?');
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                for (int i = off; i < off + len; i++) {
                    outputStream.write((b[i] & 0xFF) <= 127 ? b[i] : '?');
                }
            }

            @Override
            public void flush() throws IOException {
                outputStream.flush();
            }

            @Override
            public void close() throws IOException {
                outputStream.close();
            }
        };
    }
}