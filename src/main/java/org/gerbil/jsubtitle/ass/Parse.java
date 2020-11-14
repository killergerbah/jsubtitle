package org.gerbil.jsubtitle.ass;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

final class Parse {

    private static final NumberFormat DECIMAL_NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);
    private static final NumberFormat COMMA_NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.FRANCE);

    private Parse() {
    }

    static double decimal(String string) {
        try {
            if (string.contains(".")) {
                return DECIMAL_NUMBER_FORMAT.parse(string).doubleValue();
            }

            if (string.contains(",")) {
                return COMMA_NUMBER_FORMAT.parse(string).doubleValue();
            }

            return Double.parseDouble(string);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static boolean bool(String string) {
        return "-1".equals(string);
    }

    static long time(String string) {
        var tokens = string.split(":");

        var hoursString = tokens[0];
        var minutesString = tokens[1];
        String secondsString;
        String hundredthsString;

        if (tokens[2].contains(".")) {
            var subTokens = tokens[2].split("\\.");
            secondsString = subTokens[0];
            hundredthsString = subTokens[1];
        } else {
            secondsString = tokens[2];
            hundredthsString = tokens[3];
        }

        return TimeUnit.HOURS.toMillis(Long.parseLong(hoursString))
                + TimeUnit.MINUTES.toMillis(Long.parseLong(minutesString))
                + TimeUnit.SECONDS.toMillis(Long.parseLong(secondsString))
                + 10 * Long.parseLong(hundredthsString);
    }

    static String[] format(String string) {
        return Arrays.stream(string.split(",")).map(String::trim).toArray(String[]::new);
    }
}
