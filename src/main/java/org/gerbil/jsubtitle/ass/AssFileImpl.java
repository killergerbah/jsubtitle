package org.gerbil.jsubtitle.ass;

final class AssFileImpl implements AssFile {

    private final ScriptInfoSection scriptInfoSection;
    private final StyleSection styleSection;
    private final EventSection eventSection;

    AssFileImpl(ScriptInfoSection scriptInfoSection, StyleSection styleSection, EventSection eventSection) {
        this.scriptInfoSection = scriptInfoSection;
        this.styleSection = styleSection;
        this.eventSection = eventSection;
    }

    @Override
    public ScriptInfoSection getScriptInfoSection() {
        return scriptInfoSection;
    }

    @Override
    public StyleSection getStyleSection() {
        return styleSection;
    }

    @Override
    public EventSection getEventSection() {
        return eventSection;
    }
}
