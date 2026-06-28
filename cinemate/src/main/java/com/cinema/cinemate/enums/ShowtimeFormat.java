package com.cinema.cinemate.enums;

public enum ShowtimeFormat {
    TWO_D("2D"),
    THREE_D("3D"),
    IMAX("IMAX"),
    FOUR_DX("4DX");

    private final String displayName;

    ShowtimeFormat(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ShowtimeFormat fromString(String text) {
        if (text == null) {
            return null;
        }
        for (ShowtimeFormat f : values()) {
            if (f.displayName.equalsIgnoreCase(text) || f.name().equalsIgnoreCase(text)) {
                return f;
            }
        }
        return null;
    }
}
