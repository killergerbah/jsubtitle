package org.gerbil.jsubtitle.ass;

public interface DialogueEvent {

    boolean isMarked();

    int getLayer();

    long getStart();

    long getEnd();

    String getStyle();

    String getName();

    String getMarginL();

    String getMarginR();

    String getMarginV();

    String getEffect();

    String getText();
}
