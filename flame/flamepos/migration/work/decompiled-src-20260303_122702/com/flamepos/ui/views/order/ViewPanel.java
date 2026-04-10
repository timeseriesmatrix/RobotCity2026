/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.ui.views.IView;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JPanel;

public abstract class ViewPanel
extends JPanel
implements IView {
    public ViewPanel() {
    }

    public ViewPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    public ViewPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public ViewPanel(LayoutManager layout) {
        super(layout);
    }

    @Override
    public Component getViewComponent() {
        return this;
    }
}

