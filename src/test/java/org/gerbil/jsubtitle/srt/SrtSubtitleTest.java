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

        s = subtitles.get(5);
        assertEquals(6, s.getIndex());
        assertEquals(131210, s.getStart());
        assertEquals(133840, s.getEnd());
        assertEquals("（ジャン）サシャ つまみ食いでもしてみろ", s.getText());
    }

    @Test
    void parses_extra_line_between_subtitles() throws IOException {
        var subtitles = SrtSubtitle.read(getClass().getClassLoader().getResourceAsStream("testsubs2.srt"));

        assertEquals(2, subtitles.size());

        var s = subtitles.get(0);

        assertEquals(1, s.getIndex());
        assertEquals(3336, s.getStart());
        assertEquals(6923, s.getEnd());
        assertEquals("（クルーガー）九つの巨人にはそれぞれ名前がある", s.getText());

        s = subtitles.get(1);
        assertEquals(2, s.getIndex());
        assertEquals(7590, s.getStart());
        assertEquals(10427, s.getEnd());
        assertEquals("これからお前へ継承される巨人にもだ", s.getText());
    }
}
