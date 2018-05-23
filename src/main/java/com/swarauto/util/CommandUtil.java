package com.swarauto.util;

import com.swarauto.game.GameStatus;

import java.io.File;

public interface CommandUtil {
    String capturePhoneScreen();

    boolean runCmd(final String... params);

    void tapScreen(final String x, final String y);

    void tapScreenCenter();

    void screenLog(final GameStatus status, final File folder);
}
