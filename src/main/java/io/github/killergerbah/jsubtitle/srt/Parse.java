package io.github.killergerbah.jsubtitle.srt;

import java.util.concurrent.TimeUnit;

final class Parse {

    static long timestamp(String timestampString) {
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
                        throw new IllegalArgumentException("Improperly formatted timestamp: " + timestampString);
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
        MINUTES(TimeUnit.MINUTES, SECONDS, 2, ':'),
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
