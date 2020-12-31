package org.gerbil.jsubtitle.srt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

            subtitle.start = parse(timestampStrings[0]);
            subtitle.end = parse(timestampStrings[1]);

            return new TextState(subtitle);
        }

        private long parse(String timestampString) {
            var tokenBuilder = new StringBuilder();
            var unit = Unit.HOURS;
            long timestamp = 0;
            var iterator = timestampString.chars().iterator();

            while (iterator.hasNext()) {
                int c = iterator.next();

                if (unit.shouldMoveNext(tokenBuilder, c)) {
                    timestamp += unit.toMilliseconds(tokenBuilder.toString());

                    if (iterator.hasNext()) {
                        tokenBuilder = new StringBuilder();
                        unit = unit.next();

                        if (unit == null) {
                            throw new IllegalArgumentException("Improperly formatted timestamp");
                        }
                    }
                } else {
                    tokenBuilder.append((char) c);
                }
            }

            timestamp += unit.toMilliseconds(tokenBuilder.toString());

            return timestamp;
        }

        private enum Unit {

            MILLISECONDS(TimeUnit.MILLISECONDS, null, 3, '\0') {
                @Override
                boolean shouldMoveNext(StringBuilder builder, int c) {
                    return builder.length() == 3;
                }
            },
            SECONDS(TimeUnit.SECONDS, MILLISECONDS, 2, ','),
            MINUTES(TimeUnit.MILLISECONDS, SECONDS, 2, ':'),
            HOURS(TimeUnit.HOURS, MINUTES, 2, ':');

            private final TimeUnit timeUnit;
            private final Unit nextUnit;
            private final int length;
            private final char delimiter;

            Unit(TimeUnit timeUnit, Unit nextUnit, int length, char delimiter) {
                this.timeUnit = timeUnit;
                this.nextUnit = nextUnit;
                this.length = length;
                this.delimiter = delimiter;
            }

            boolean shouldMoveNext(StringBuilder builder, int c) {
                return delimiter == (char) c;
            }

            long toMilliseconds(String token) {
                if (token.length() != length) {
                    throw new IllegalArgumentException("Invalid token length in timestamp");
                }

                return timeUnit.toMillis(Integer.parseInt(token));
            }

            Unit next() {
                return nextUnit;
            }
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
