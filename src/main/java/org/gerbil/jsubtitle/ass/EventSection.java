package org.gerbil.jsubtitle.ass;

import java.util.List;

public interface EventSection extends AssFileSection {

    List<DialogueEvent> getEvents();
}
