/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.swing.PosUIManager;
import java.awt.Component;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class PosScrollPane
extends JScrollPane {
    public PosScrollPane() {
        JScrollBar scrollBar = this.getVerticalScrollBar();
        if (scrollBar != null) {
            scrollBar.setPreferredSize(PosUIManager.getSize(40, 40));
        }
    }

    public PosScrollPane(Component view) {
        super(view);
        JScrollBar scrollBar = this.getVerticalScrollBar();
        if (scrollBar != null) {
            scrollBar.setPreferredSize(PosUIManager.getSize(40, 40));
        }
    }

    public PosScrollPane(int vsbPolicy, int hsbPolicy) {
        super(vsbPolicy, hsbPolicy);
        JScrollBar scrollBar = this.getVerticalScrollBar();
        if (scrollBar != null) {
            scrollBar.setPreferredSize(PosUIManager.getSize(40, 40));
        }
    }

    public PosScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
        JScrollBar scrollBar = this.getVerticalScrollBar();
        if (scrollBar != null) {
            scrollBar.setPreferredSize(PosUIManager.getSize(40, 40));
        }
    }
}

