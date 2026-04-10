/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.config;

import com.floreantpos.swing.PosUIManager;
import java.awt.Font;
import javax.swing.UIManager;

public class UIConfig {
    public static final Font buttonFont = UIManager.getFont("Button.font").deriveFont(1, PosUIManager.getDefaultFontSize());
    public static final Font largeFont = UIManager.getFont("Button.font").deriveFont(1, PosUIManager.getLargeFontSize());

    public static Font getButtonFont() {
        return buttonFont;
    }
}

