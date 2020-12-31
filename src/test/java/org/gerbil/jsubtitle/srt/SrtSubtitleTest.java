package org.gerbil.jsubtitle.srt;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SrtSubtitleTest {

    @Test
    void parses() throws IOException {
        var subtitles = SrtSubtitle.read(getClass().getClassLoader().getResourceAsStream("testsubs1.srt"));

        assertEquals(6, subtitles.size());

        var s = subtitles.get(0);

        assertEquals(1, s.getIndex());
        assertEquals(13920, s.getStart());
        assertEquals(18180, s.getEnd());
        assertEquals("（エレン）壁の向こうには海があるとアルミンが言った", s.getText());
    }
}
