/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.swing.PosUIManager;
import java.awt.Dimension;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

public class POSToggleButton
extends JToggleButton {
    public POSToggleButton() {
        this((String)null);
    }

    public POSToggleButton(String text) {
        super(text);
        this.setFocusPainted(false);
        this.setFocusable(false);
    }

    @Override
    public String getUIClassID() {
        return "POSToggleButtonUI";
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = null;
        if (this.isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        if (this.ui != null) {
            size = this.ui.getPreferredSize(this);
        }
        if (size == null) {
            size = new Dimension(PosUIManager.getSize(80, 60));
        } else {
            int width = size.width < 80 ? 80 : size.width;
            int height = size.height < 60 ? 60 : size.height;
            size.setSize(PosUIManager.getSize(width, height));
        }
        return size;
    }

    static {
        UIManager.put("POSToggleButtonUI", "com.floreantpos.swing.POSToggleButtonUI");
    }
}

