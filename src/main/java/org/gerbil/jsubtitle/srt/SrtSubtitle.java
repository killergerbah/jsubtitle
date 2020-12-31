package org.gerbil.jsubtitle.srt;

import org.gerbil.jsubtitle.util.LineIterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface SrtSubtitle {

    static List<SrtSubtitle> read(InputStream inputStream) throws IOException {
        try (var lineIterator = LineIterator.from(inputStream)) {
            return read(lineIterator);
        }
    }

    static List<SrtSubtitle> read(byte[] bytes) throws IOException {
        try (var lineIterator = LineIterator.from(bytes)) {
            return read(lineIterator);
        }
    }

    static List<SrtSubtitle> read(String string) throws IOException {
        try (var lineIterator = LineIterator.from(string)) {
            return read(lineIterator);
        }
    }

    static List<SrtSubtitle> read(File file) throws IOException {
        try (var lineIterator = LineIterator.from(new FileInputStream(file))) {
            return read(lineIterator);
        }
    }

    private static List<SrtSubtitle> read(LineIterator lineIterator) throws IOException {
        var parser = new SrtFileParser();

        while (lineIterator.hasNext()) {
            var line = lineIterator.next();
            parser.consume(line);
        }

        return parser.getSubtitles();
    }

    int getIndex();

    long getStart();

    long getEnd();

    String getText();
}
