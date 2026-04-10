/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import java.awt.LayoutManager;
import javax.swing.JPanel;

public class TransparentPanel
extends JPanel {
    public TransparentPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        this.setOpaque(false);
    }

    public TransparentPanel(LayoutManager layout) {
        super(layout);
        this.setOpaque(false);
    }

    public TransparentPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        this.setOpaque(false);
    }

    public TransparentPanel() {
        this.setOpaque(false);
    }
}

