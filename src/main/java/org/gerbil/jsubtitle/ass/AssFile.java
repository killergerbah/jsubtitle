package org.gerbil.jsubtitle.ass;

import org.gerbil.jsubtitle.util.LineIterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public interface AssFile {

    static AssFile read(InputStream inputStream) throws IOException {
        try (var lineIterator = LineIterator.from(inputStream)) {
            return read(lineIterator);
        }
    }

    static AssFile read(byte[] bytes) throws IOException {
        try (var lineIterator = LineIterator.from(bytes)) {
            return read(lineIterator);
        }
    }

    static AssFile read(String string) throws IOException {
        try (var lineIterator = LineIterator.from(string)) {
            return read(lineIterator);
        }
    }

    static AssFile read(File file) throws IOException {
        try (var lineIterator = LineIterator.from(new FileInputStream(file))) {
            return read(lineIterator);
        }
    }

    private static AssFile read(LineIterator lineIterator) throws IOException {
        var parser = new AssFileParser();
        int lineNumber = 0;

        while (lineIterator.hasNext()) {
            var line = lineIterator.next();
            parser.consume(line, lineNumber++);
        }

        parser.finish();

        var sections = parser.getSections();
        ScriptInfoSection scriptInfoSection = null;
        StyleSection styleSection = null;
        EventSection eventSection = null;

        for (var s : sections) {
            if (s instanceof ScriptInfoSection) {
                if (scriptInfoSection == null) {
                    scriptInfoSection = (ScriptInfoSection)s;
                } else {
                    throw new AssParseException("Too many script info sections");
                }
            } else if (s instanceof StyleSection) {
                if (styleSection == null) {
                    styleSection = (StyleSection)s;
                } else {
                    throw new AssParseException("Too many style sections");
                }
            } else if (s instanceof EventSection) {
                if (eventSection == null) {
                    eventSection = (EventSection)s;
                } else {
                    throw new AssParseException("Too many event sections");
                }
            }
        }

        return new AssFileImpl(scriptInfoSection, styleSection, eventSection);
    }

    ScriptInfoSection getScriptInfoSection();

    StyleSection getStyleSection();

    EventSection getEventSection();
}
