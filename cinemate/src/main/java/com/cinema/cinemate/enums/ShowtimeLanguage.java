package com.cinema.cinemate.enums;

public enum ShowtimeLanguage {
    PHU_DE("Phụ đề"),
    LONG_TIENG("Lồng tiếng"),
    SUBTITLED("Subtitled"),
    DUBBED("Dubbed");

    private final String displayName;

    ShowtimeLanguage(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ShowtimeLanguage fromString(String text) {
        if (text == null) {
            return null;
        }
        for (ShowtimeLanguage l : values()) {
            if (l.displayName.equalsIgnoreCase(text) || l.name().equalsIgnoreCase(text)) {
                return l;
            }
        }
        return null;
    }
}
