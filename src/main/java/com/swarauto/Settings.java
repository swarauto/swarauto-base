package com.swarauto;

public abstract class Settings {
    public static final String PROFILES_DIR_NAME = "profiles";
    public static final String SOLD_RUNES_DIR_NAME = "sold-runes";

    public abstract String getHomeFolderPath();

    public String getProfilesFolderPath() {
        return getHomeFolderPath() + "/" + PROFILES_DIR_NAME;
    }

    public String getSoldRunesFolderPath() {
        return getHomeFolderPath() + "/" + SOLD_RUNES_DIR_NAME;
    }
}
