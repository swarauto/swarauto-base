package com.swarauto.util;

import java.io.File;

public interface OcrUtil {

    void initialize();

    boolean isInitialized();

    String text(final File imageFile);

    String text(final File imageFile, final Rectangle box);
}
