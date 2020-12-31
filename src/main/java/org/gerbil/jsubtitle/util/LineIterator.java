package org.gerbil.jsubtitle.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class LineIterator implements AutoCloseable {

    private final BufferedReader reader;

    private LineIterator(InputStream inputStream) {
        reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    public static LineIterator from(InputStream inputStream) {
        return new LineIterator(inputStream);
    }

    public static LineIterator from(byte[] bytes) {
        return new LineIterator(new ByteArrayInputStream(bytes));
    }

    public static LineIterator from(String string) {
        return new LineIterator(new ByteArrayInputStream(string.getBytes()));
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    public boolean hasNext() throws IOException {
        return reader.ready();
    }

    public String next() throws IOException {
        return reader.readLine().replace("\uFEFF", "");
    }
}
