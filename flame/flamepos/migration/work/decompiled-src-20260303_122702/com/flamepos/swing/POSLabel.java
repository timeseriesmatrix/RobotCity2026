/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.config.UIConfig;
import javax.swing.Icon;
import javax.swing.JLabel;

public class POSLabel
extends JLabel {
    public POSLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        this.setFont(UIConfig.largeFont);
    }

    public POSLabel(String text, int horizontalAlignment) {
        this(text, null, horizontalAlignment);
    }

    public POSLabel(String text) {
        this(text, null, 2);
    }

    public POSLabel(Icon image, int horizontalAlignment) {
        this("", image, horizontalAlignment);
    }

    public POSLabel(Icon image) {
        this("", image, 2);
    }

    public POSLabel() {
        this("", null, 2);
    }
}

