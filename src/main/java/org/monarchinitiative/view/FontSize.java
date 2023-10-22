package org.monarchinitiative.view;

public enum FontSize {
    SMALL,
    MEDIUM,
    BIG;


    public static String getCssFileName(FontSize size) {
        return switch (size) {
            case SMALL -> "fontSmall.css";
            case MEDIUM -> "fontMedium.css";
            case BIG -> "fontBig.css";
        };
    }
}
