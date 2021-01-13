package io.github.killergerbah.jsubtitle.ass;

import java.util.List;

public interface EventSection extends AssFileSection {

    List<DialogueEvent> getEvents();
}
