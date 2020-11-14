package org.gerbil.jsubtitle.ass;

public interface StyleSection extends AssFileSection {

    String getName();

    String getFontName();

    int getFontSize();

    String getPrimaryColor();

    String getSecondaryColor();

    String getOutlineColor();

    String getBackColor();

    boolean isBold();

    boolean isItalic();

    boolean isUnderline();

    boolean isStrikeOut();

    double getScaleX();

    double getScaleY();

    double getSpacing();

    double getAngle();

    int getBorderStyle();

    double getOutline();

    double getShadow();

    int getAlignment();

    int getMarginL();

    int getMarginR();

    int getMarginV();

    int getAlphaLevel();

    int getEncoding();
}
