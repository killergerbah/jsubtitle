package org.gerbil.jsubtitle.srt;

import java.util.ArrayList;
import java.util.List;

final class SrtFileParser {

    private final List<SrtSubtitle> subtitles = new ArrayList<>();
    private State state = new IndexState(new SrtSubtitleImpl());

    void consume(String line) {
        var oldState = state;
        state = state.consume(line);

        if (state instanceof IndexState) {
            subtitles.add(((TextState) oldState).subtitle);
        }
    }

    List<SrtSubtitle> getSubtitles() {
        return subtitles;
    }

    private interface State {

        State consume(String line);
    }

    private static final class IndexState implements State {

        private final SrtSubtitleImpl subtitle;

        IndexState(SrtSubtitleImpl subtitle) {
            this.subtitle = subtitle;
        }

        @Override
        public State consume(String line) {
            if (line.trim().equals("")) {
                return this;
            }

            subtitle.index = Integer.parseInt(line);
            return new TimestampState(subtitle);
        }
    }

    private static final class TimestampState implements State {

        private final SrtSubtitleImpl subtitle;

        TimestampState(SrtSubtitleImpl subtitle) {
            this.subtitle = subtitle;
        }

        @Override
        public State consume(String line) {
            var timestampStrings = line.split(" --> ");

            if (timestampStrings.length != 2) {
                throw new IllegalArgumentException("Improperly formatted timestamps: " + line);
            }

            subtitle.start = Parse.timestamp(timestampStrings[0]);
            subtitle.end = Parse.timestamp(timestampStrings[1]);

            return new TextState(subtitle);
        }
    }

    private static final class TextState implements State {

        private final SrtSubtitleImpl subtitle;
        private StringBuilder text = new StringBuilder();

        TextState(SrtSubtitleImpl subtitle) {
            this.subtitle = subtitle;
        }

        @Override
        public State consume(String line) {
            if (line.trim().equals("")) {
                subtitle.text = text.toString();
                return new IndexState(new SrtSubtitleImpl());
            }

            text.append(line);
            return this;
        }
    }

    private static final class SrtSubtitleImpl implements SrtSubtitle {

        private int index;
        private long start;
        private long end;
        private String text;

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public long getStart() {
            return start;
        }

        @Override
        public long getEnd() {
            return end;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return "SrtSubtitleImpl{" +
                    "index=" + index +
                    ", start=" + start +
                    ", end=" + end +
                    ", text='" + text + '\'' +
                    '}';
        }
    }
}
