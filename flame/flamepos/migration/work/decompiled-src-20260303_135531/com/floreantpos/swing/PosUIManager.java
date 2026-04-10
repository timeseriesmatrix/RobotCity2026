/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.config.TerminalConfig;
import java.awt.Dimension;

public class PosUIManager {
    private static final int DEFAULT_FONT_SIZE = 12;
    private static final int TITLE_FONT_SIZE = 18;
    private static final int LARGE_FONT_SIZE = 16;
    private static final int NUMBER_FIELD_FONT_SIZE = 24;
    private static final int TABLE_NUMBER_FONT_SIZE = 30;
    private static final double SCREEN_SCALE_FACTOR = TerminalConfig.getScreenScaleFactor();

    public static Dimension getSize(int w, int h) {
        int width = (int)((double)w * SCREEN_SCALE_FACTOR);
        int height = (int)((double)h * SCREEN_SCALE_FACTOR);
        return new Dimension(width, height);
    }

    public static int getSize(int size) {
        return (int)((double)size * SCREEN_SCALE_FACTOR);
    }

    public static int getFontSize(int fontSize) {
        return (int)((double)fontSize * SCREEN_SCALE_FACTOR);
    }

    public static Dimension getSize_w100_h70() {
        int width = (int)(100.0 * SCREEN_SCALE_FACTOR);
        int height = (int)(70.0 * SCREEN_SCALE_FACTOR);
        return new Dimension(width, height);
    }

    public static Dimension getSize_w120_h70() {
        int width = (int)(120.0 * SCREEN_SCALE_FACTOR);
        int height = (int)(70.0 * SCREEN_SCALE_FACTOR);
        return new Dimension(width, height);
    }

    public static double getScreenScaleFactor() {
        return SCREEN_SCALE_FACTOR;
    }

    public static int getDefaultFontSize() {
        return (int)(12.0 * SCREEN_SCALE_FACTOR);
    }

    public static int getTitleFontSize() {
        return (int)(18.0 * SCREEN_SCALE_FACTOR);
    }

    public static int getLargeFontSize() {
        return (int)(16.0 * SCREEN_SCALE_FACTOR);
    }

    public static int getNumberFieldFontSize() {
        return (int)(24.0 * SCREEN_SCALE_FACTOR);
    }

    public static int getTableNumberFontSize() {
        return (int)(30.0 * SCREEN_SCALE_FACTOR);
    }
}

