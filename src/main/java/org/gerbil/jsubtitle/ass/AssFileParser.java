package org.gerbil.jsubtitle.ass;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

final class AssFileParser {

    private static final Pattern GARBAGE_SECTION_PATTERN = Pattern.compile("\\[.+\\]");

    private final List<AssFileSection> sections = new ArrayList<>();

    private AssFileSectionParser current;

    void consume(String line, int lineNumber) {
        if (current == null) {
            current = detectSection(line, lineNumber);
            return;
        }

        if (current.consume(line)) {
            return;
        }

        current.finish();
        sections.add((AssFileSection) current);
        current = detectSection(line, lineNumber);
    }

    void finish() {
        current.finish();
        sections.add((AssFileSection) current);
    }

    private AssFileSectionParser detectSection(String line, int lineNumber) {
        var trimmed = line.trim();

        if ("".equals(trimmed)) {
            return new EmptySectionParser();
        }

        if (trimmed.equals("[Script Info]")) {
            return new ScriptInfoSectionParser();
        }

        if (trimmed.equals("[V4+ Styles]")) {
            return new StyleSectionParser();
        }

        if (trimmed.equals("[Events]")) {
            return new EventSectionParser();
        }

        if (GARBAGE_SECTION_PATTERN.matcher(trimmed).matches()) {
            return new GarbageSectionParser();
        }

        throw new AssParseException("Unrecognized section at line " + lineNumber + ":\n" + line);
    }

    List<AssFileSection> getSections() {
        return sections;
    }

    private interface AssFileSectionParser {

        boolean consume(String line);

        default void finish() {
        }
    }

    private static final class EmptySectionParser implements AssFileSectionParser, AssFileSection {

        @Override
        public boolean consume(String line) {
            return "".equals(line.trim());
        }
    }

    private static final class GarbageSectionParser implements AssFileSectionParser, AssFileSection {

        @Override
        public boolean consume(String line) {
            return line.split(":").length == 2;
        }
    }

    private static final class ScriptInfoSectionParser implements AssFileSectionParser, ScriptInfoSection {

        private static final Map<String, BiConsumer<ScriptInfoSectionParser, String>> MUTATORS;

        static {
            MUTATORS = new HashMap<>(14);
            MUTATORS.put("Title", (parser, value) -> parser.title = value);
            MUTATORS.put("Original Script", (parser, value) -> parser.originalScript = value);
            MUTATORS.put("Original Translation", (parser, value) -> parser.originalTranslation = value);
            MUTATORS.put("Original Editing", (parser, value) -> parser.originalEditing = value);
            MUTATORS.put("Original Timing", (parser, value) -> parser.originalTiming = value);
            MUTATORS.put("Synch Point", (parser, value) -> parser.synchPoint = value);
            MUTATORS.put("Script Updated By", (parser, value) -> parser.scriptUpdatedBy = value);
            MUTATORS.put("Update Details", (parser, value) -> parser.updateDetails = value);
            MUTATORS.put("ScriptType", (parser, value) -> parser.scriptType = value);
            MUTATORS.put("Collisions", (parser, value) -> parser.collisions = value);
            MUTATORS.put("PlayResX", (parser, value) -> parser.playResX = Integer.parseInt(value.trim()));
            MUTATORS.put("PlayResY", (parser, value) -> parser.playResY = Integer.parseInt(value.trim()));
            MUTATORS.put("PlayDepth", (parser, value) -> parser.playDepth = Integer.parseInt(value.trim()));
            MUTATORS.put("Timer", (parser, value) -> parser.timer = Parse.decimal(value.trim()));
        }

        private final List<String> comments = new ArrayList<>();

        private String title;
        private String originalScript;
        private String originalTranslation;
        private String originalEditing;
        private String originalTiming;
        private String synchPoint;
        private String scriptUpdatedBy;
        private String updateDetails;
        private String scriptType;
        private String collisions;
        private int playResX;
        private int playResY;
        private int playDepth;
        private double timer;

        @Override
        public boolean consume(String line) {
            if (line.startsWith(";")) {
                comments.add(line.substring(1));
                return true;
            }

            var tokens = line.split(":", 2);
            if (tokens.length != 2) {
                return false;
            }

            var field = tokens[0];
            var value = tokens[1];
            var mutator = MUTATORS.get(field);

            if (mutator != null) {
                mutator.accept(this, value.trim());
            }

            return true;
        }

        @Override
        public List<String> getComments() {
            return comments;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getOriginalScript() {
            return originalScript;
        }

        @Override
        public String getOriginalTranslation() {
            return originalTranslation;
        }

        @Override
        public String getOriginalEditing() {
            return originalEditing;
        }

        @Override
        public String getOriginalTiming() {
            return originalTiming;
        }

        @Override
        public String getSynchPoint() {
            return synchPoint;
        }

        @Override
        public String getScriptUpdatedBy() {
            return scriptUpdatedBy;
        }

        @Override
        public String getUpdateDetails() {
            return updateDetails;
        }

        @Override
        public String getScriptType() {
            return scriptType;
        }

        @Override
        public String getCollisions() {
            return collisions;
        }

        @Override
        public int getPlayResX() {
            return playResX;
        }

        @Override
        public int getPlayResY() {
            return playResY;
        }

        @Override
        public int getPlayDepth() {
            return playDepth;
        }

        @Override
        public double getTimer() {
            return timer;
        }
    }


    private static final class StyleSectionParser implements AssFileSectionParser, StyleSection {

        private static final Map<String, BiConsumer<StyleSectionParser, String>> MUTATORS;

        static {
            MUTATORS = new HashMap<>(25);
            MUTATORS.put("Name", (parser, value) -> parser.name = value);
            MUTATORS.put("Fontname", (parser, value) -> parser.fontName = value);
            MUTATORS.put("Fontsize", (parser, value) -> parser.fontSize = Double.parseDouble(value));
            MUTATORS.put("PrimaryColour", (parser, value) -> parser.primaryColor = value);
            MUTATORS.put("SecondaryColour", (parser, value) -> parser.secondaryColor = value);
            MUTATORS.put("TertiaryColour", (parser, value) -> parser.outlineColor = value);
            MUTATORS.put("OutlineColour", (parser, value) -> parser.outlineColor = value);
            MUTATORS.put("BackColour", (parser, value) -> parser.backColor = value);
            MUTATORS.put("Bold", (parser, value) -> parser.bold = Parse.bool(value));
            MUTATORS.put("Italic", (parser, value) -> parser.italic = Parse.bool(value));
            MUTATORS.put("Underline", (parser, value) -> parser.underline = Parse.bool(value));
            MUTATORS.put("StrikeOut", (parser, value) -> parser.strikeOut = Parse.bool(value));
            MUTATORS.put("ScaleX", (parser, value) -> parser.scaleX = Parse.decimal(value));
            MUTATORS.put("ScaleY", (parser, value) -> parser.scaleY = Parse.decimal(value));
            MUTATORS.put("Spacing", (parser, value) -> parser.spacing = Parse.decimal(value));
            MUTATORS.put("Angle", (parser, value) -> parser.angle = Parse.decimal(value));
            MUTATORS.put("BorderStyle", (parser, value) -> parser.borderStyle = Integer.parseInt(value));
            MUTATORS.put("Outline", (parser, value) -> parser.outline = Parse.decimal(value));
            MUTATORS.put("Shadow", (parser, value) -> parser.shadow = Parse.decimal(value));
            MUTATORS.put("Alignment", (parser, value) -> parser.alignment = Integer.parseInt(value));
            MUTATORS.put("MarginL", (parser, value) -> parser.marginL = Integer.parseInt(value));
            MUTATORS.put("MarginR", (parser, value) -> parser.marginR = Integer.parseInt(value));
            MUTATORS.put("MarginV", (parser, value) -> parser.marginV = Integer.parseInt(value));
            MUTATORS.put("AlphaLevel", (parser, value) -> parser.alphaLevel = Integer.parseInt(value));
            MUTATORS.put("Encoding", (parser, value) -> parser.encoding = Integer.parseInt(value));
        }

        private String[] format;
        private String name;
        private String fontName;
        private double fontSize;
        private String primaryColor;
        private String secondaryColor;
        private String outlineColor;
        private String backColor;
        private boolean bold;
        private boolean italic;
        private boolean underline;
        private boolean strikeOut;
        private double scaleX;
        private double scaleY;
        private double spacing;
        private double angle;
        private int borderStyle;
        private double outline;
        private double shadow;
        private int alignment;
        private int marginL;
        private int marginR;
        private int marginV;
        private int alphaLevel;
        private int encoding;

        @Override
        public boolean consume(String line) {
            var tokens = line.split(":");
            if (tokens.length != 2) {
                return false;
            }

            var field = tokens[0];
            var value = tokens[1];

            if (field.equals("Format")) {
                format = Parse.format(value);
                return true;
            }

            if (field.equals("Style")) {
                var styleValues = value.split(",");

                for (int i = 0; i < styleValues.length; ++i) {
                    var styleField = format[i];
                    var styleValue = styleValues[i].trim();
                    var mutator = MUTATORS.get(styleField);

                    if (mutator != null) {
                        mutator.accept(this, styleValue);
                    }
                }

                return true;
            }

            return false;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getFontName() {
            return fontName;
        }

        @Override
        public double getFontSize() {
            return fontSize;
        }

        @Override
        public String getPrimaryColor() {
            return primaryColor;
        }

        @Override
        public String getSecondaryColor() {
            return secondaryColor;
        }

        @Override
        public String getOutlineColor() {
            return outlineColor;
        }

        @Override
        public String getBackColor() {
            return backColor;
        }

        @Override
        public boolean isBold() {
            return bold;
        }

        @Override
        public boolean isItalic() {
            return italic;
        }

        @Override
        public boolean isUnderline() {
            return underline;
        }

        @Override
        public boolean isStrikeOut() {
            return strikeOut;
        }

        @Override
        public double getScaleX() {
            return scaleX;
        }

        @Override
        public double getScaleY() {
            return scaleY;
        }

        @Override
        public double getSpacing() {
            return spacing;
        }

        @Override
        public double getAngle() {
            return angle;
        }

        @Override
        public int getBorderStyle() {
            return borderStyle;
        }

        @Override
        public double getOutline() {
            return outline;
        }

        @Override
        public double getShadow() {
            return shadow;
        }

        @Override
        public int getAlignment() {
            return alignment;
        }

        @Override
        public int getMarginL() {
            return marginL;
        }

        @Override
        public int getMarginR() {
            return marginR;
        }

        @Override
        public int getMarginV() {
            return marginV;
        }

        @Override
        public int getAlphaLevel() {
            return alphaLevel;
        }

        @Override
        public int getEncoding() {
            return encoding;
        }
    }

    private static final class EventSectionParser implements AssFileSectionParser, EventSection {

        private static final Map<String, BiConsumer<Event, String>> MUTATORS;

        static {
            MUTATORS = new HashMap<>(11);
            MUTATORS.put("Marked", (parser, value) -> parser.marked = Parse.bool(value));
            MUTATORS.put("Layer", (parser, value) -> parser.layer = Integer.parseInt(value.trim()));
            MUTATORS.put("Start", (parser, value) -> parser.start = Parse.time(value));
            MUTATORS.put("End", (parser, value) -> parser.end = Parse.time(value));
            MUTATORS.put("Style", (parser, value) -> parser.style = value);
            MUTATORS.put("Name", (parser, value) -> parser.name = value);
            MUTATORS.put("MarginL", (parser, value) -> parser.marginL = value);
            MUTATORS.put("MarginR", (parser, value) -> parser.marginR = value);
            MUTATORS.put("MarginV", (parser, value) -> parser.marginV = value);
            MUTATORS.put("Effect", (parser, value) -> parser.effect = value);
            MUTATORS.put("Text", (parser, value) -> parser.text = value);
        }

        private SortedSet<Event> sortedEvents = new TreeSet<>();
        private List<DialogueEvent> events;
        private String[] format;

        @Override
        public boolean consume(String line) {
            var tokens = line.split(":", 2);

            if (tokens.length != 2) {
                return "".equals(line.trim());
            }

            var field = tokens[0];
            var value = tokens[1];

            if (field.equals("Format")) {
                format = Parse.format(value);
                return true;
            }

            if (field.equals("Dialogue")) {
                var event = new Event();
                var eventValues = value.split(",", format.length);

                for (int i = 0; i < eventValues.length; ++i) {
                    var eventField = format[i];
                    var eventValue = eventValues[i];
                    var mutator = MUTATORS.get(eventField);

                    if (mutator != null) {
                        mutator.accept(event, eventValue);
                    }
                }

                sortedEvents.add(event);
            }

            return true;
        }

        @Override
        public void finish() {
            events = new ArrayList<>(sortedEvents);
            sortedEvents = null;
        }

        @Override
        public List<DialogueEvent> getEvents() {
            return events;
        }
    }

    private static final class Event implements Comparable<Event>, DialogueEvent {

        private boolean marked;
        private int layer;
        private long start;
        private long end;
        private String style;
        private String name;
        private String marginL;
        private String marginR;
        private String marginV;
        private String effect;
        private String text;

        @Override
        public int compareTo(Event o) {
            return Long.compare(start, o.start);
        }

        @Override
        public boolean isMarked() {
            return marked;
        }

        @Override
        public int getLayer() {
            return layer;
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
        public String getStyle() {
            return style;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getMarginL() {
            return marginL;
        }

        @Override
        public String getMarginR() {
            return marginR;
        }

        @Override
        public String getMarginV() {
            return marginV;
        }

        @Override
        public String getEffect() {
            return effect;
        }

        @Override
        public String getText() {
            return text;
        }
    }
}
