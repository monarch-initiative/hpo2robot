package org.robinsonpn.view;

public enum ColorTheme {
    LIGHT,
    DEFAULT,
    DARK;


    public static String getCssFileName(ColorTheme theme) {
        return switch (theme) {
            case DARK -> "themeDark.css";
            case DEFAULT -> "themeDefault.css";
            case LIGHT -> "themeLight.css";
        };
    }
}
