/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.swing.PosButton;
import java.awt.Dimension;

public class PosSmallButton
extends PosButton {
    public PosSmallButton() {
        this((String)null);
    }

    public PosSmallButton(String text) {
        super(text);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        if (this.isPreferredSizeSet()) {
            return size;
        }
        if (ui != null) {
            size = ui.getPreferredSize(this);
        }
        if (size != null) {
            size.setSize(size.width + 20, 35);
        }
        return size != null ? size : super.getPreferredSize();
    }
}

