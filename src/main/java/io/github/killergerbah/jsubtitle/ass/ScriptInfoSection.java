package io.github.killergerbah.jsubtitle.ass;

import java.util.List;

public interface ScriptInfoSection extends AssFileSection {

    List<String> getComments();

    String getTitle();

    String getOriginalScript();

    String getOriginalTranslation();

    String getOriginalEditing();

    String getOriginalTiming();

    String getSynchPoint();

    String getScriptUpdatedBy();

    String getUpdateDetails();

    String getScriptType();

    String getCollisions();

    int getPlayResX();

    int getPlayResY();

    int getPlayDepth();

    double getTimer();
}
