/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.jgoodies.looks.plastic.PlasticButtonUI
 */
package com.floreantpos.swing;

import com.jgoodies.looks.plastic.PlasticButtonUI;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class POSButtonUI
extends PlasticButtonUI {
    private static final POSButtonUI INSTANCE = new POSButtonUI();

    public static ComponentUI createUI(JComponent b) {
        return INSTANCE;
    }

    public void update(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            AbstractButton b = (AbstractButton)c;
            if (this.isToolBarButton(b)) {
                c.setOpaque(false);
            } else if (b.isContentAreaFilled()) {
                g.setColor(c.getBackground());
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
                if (this.is3D(b)) {
                    Color color1 = c.getBackground();
                    Color color2 = color1.brighter();
                    int x = 0;
                    int y = 0;
                    int width = c.getWidth();
                    int height = c.getHeight();
                    GradientPaint gp = new GradientPaint(x, y, color2, width - 2, height - 2, color1, true);
                    Graphics2D g2 = (Graphics2D)g;
                    g2.setPaint(gp);
                    g2.fillRect(x, y, width, height);
                }
            }
        }
        this.paint(g, c);
    }
}

