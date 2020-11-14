package org.gerbil.jsubtitle.ass;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AssFileTest {

    @Test
    void parses() throws IOException {
        var assFile = AssFile.read(getClass().getClassLoader().getResourceAsStream("testsubs1.ass"));
        var scriptInfo = assFile.getScriptInfoSection();

        assertNotNull(scriptInfo);
        assertEquals("漫游字幕", scriptInfo.getTitle());
        assertEquals("漫游字幕组", scriptInfo.getOriginalScript());
        assertEquals("0", scriptInfo.getSynchPoint());
        assertEquals("v4.00+", scriptInfo.getScriptType());
        assertEquals("Normal", scriptInfo.getCollisions());
        assertEquals(1280, scriptInfo.getPlayResX());
        assertEquals(720, scriptInfo.getPlayResY());
        assertEquals(100, scriptInfo.getTimer());

        var styleSection = assFile.getStyleSection();

        assertNotNull(styleSection);
        assertEquals("Default", styleSection.getName());
        assertEquals("MS Gothic", styleSection.getFontName());
        assertEquals(40, styleSection.getFontSize());
        assertEquals("&H00FFFFFF", styleSection.getPrimaryColor());
        assertEquals("&HF0000000", styleSection.getSecondaryColor());
        assertEquals("&H00000000", styleSection.getOutlineColor());
        assertEquals("&HF0000000", styleSection.getBackColor());
        assertTrue(styleSection.isBold());
        assertFalse(styleSection.isItalic());
        assertFalse(styleSection.isUnderline());
        assertFalse(styleSection.isStrikeOut());
        assertEquals(100, styleSection.getScaleX());
        assertEquals(100, styleSection.getScaleY());
        assertEquals(0, styleSection.getSpacing());
        assertEquals(0, styleSection.getAngle());
        assertEquals(1, styleSection.getBorderStyle());
        assertEquals(1, styleSection.getOutline());
        assertEquals(0, styleSection.getShadow());
        assertEquals(2, styleSection.getAlignment());
        assertEquals(30, styleSection.getMarginL());
        assertEquals(30, styleSection.getMarginR());
        assertEquals(10, styleSection.getMarginV());
        assertEquals(1, styleSection.getEncoding());

        var eventSection = assFile.getEventSection();

        assertNotNull(eventSection);

        var events = eventSection.getEvents();

        assertNotNull(events);
        assertFalse(events.isEmpty());

        var event = events.get(0);

        assertEquals(0, event.getLayer());
        assertEquals(560, event.getStart());
        assertEquals(4790, event.getEnd());
        assertEquals("*Default", event.getStyle());
        assertEquals("0000", event.getMarginL());
        assertEquals("0000", event.getMarginR());
        assertEquals("0000", event.getMarginV());
        assertEquals("", event.getEffect());
        assertEquals("{\\an1\\fs35\\fad(300,300)}ただ一人　迷い込む", event.getText());
    }
}
