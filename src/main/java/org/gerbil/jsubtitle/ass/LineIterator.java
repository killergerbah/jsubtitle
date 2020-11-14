package org.gerbil.jsubtitle.ass;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

final class LineIterator implements AutoCloseable {

    private final BufferedReader reader;

    private LineIterator(InputStream inputStream) {
        reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    static LineIterator from(InputStream inputStream) {
        return new LineIterator(inputStream);
    }

    static LineIterator from(byte[] bytes) {
        return new LineIterator(new ByteArrayInputStream(bytes));
    }

    static LineIterator from(String string) {
        return new LineIterator(new ByteArrayInputStream(string.getBytes()));
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    boolean hasNext() throws IOException {
        return reader.ready();
    }

    String next() throws IOException {
        return reader.readLine().replace("\uFEFF", "");
    }
}
