package org.gerbil.jsubtitle.srt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParseTest {

    @Test
    void timestamp() {
        long timestamp = Parse.timestamp("00:02:11,210");
        assertEquals(131210, timestamp);
    }
}
