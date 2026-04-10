/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class POSTitleLabel
extends JLabel {
    private static Font font = UIManager.getFont("Label.font").deriveFont(1, 12.0f);
    private static Color forground = Color.black;

    public POSTitleLabel() {
        this.setFont(font);
        this.setForeground(forground);
    }
}

