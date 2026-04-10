/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.jgoodies.looks.plastic.PlasticButtonUI
 *  com.jgoodies.looks.plastic.PlasticLookAndFeel
 *  com.jgoodies.looks.plastic.PlasticTheme
 */
package com.floreantpos.swing;

import com.jgoodies.looks.plastic.PlasticButtonUI;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

public class ButtonUI
extends PlasticButtonUI {
    static ButtonUI ui = new ButtonUI();
    private static float FRACTION_3D = 0.5f;

    public static ComponentUI createUI(JComponent b) {
        return ui;
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
                    Rectangle r = new Rectangle(1, 1, c.getWidth() - 2, c.getHeight() - 1);
                    ButtonUI.add3DEffekt(g, r);
                }
            }
        }
        this.paint(g, c);
    }

    static void drawDark3DBorder(Graphics g, int x, int y, int w, int h) {
        ButtonUI.drawFlush3DBorder(g, x, y, w, h);
        g.setColor(PlasticLookAndFeel.getControl());
        g.drawLine(x + 1, y + 1, 1, h - 3);
        g.drawLine(y + 1, y + 1, w - 3, 1);
    }

    static void drawDisabledBorder(Graphics g, int x, int y, int w, int h) {
        g.setColor(PlasticLookAndFeel.getControlShadow());
        ButtonUI.drawRect(g, x, y, w - 1, h - 1);
    }

    static void drawFlush3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate(x, y);
        g.setColor(PlasticLookAndFeel.getControlHighlight());
        ButtonUI.drawRect(g, 1, 1, w - 2, h - 2);
        g.drawLine(0, h - 1, 0, h - 1);
        g.drawLine(w - 1, 0, w - 1, 0);
        g.setColor(PlasticLookAndFeel.getControlDarkShadow());
        ButtonUI.drawRect(g, 0, 0, w - 2, h - 2);
        g.translate(-x, -y);
    }

    static void drawPressed3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate(x, y);
        ButtonUI.drawFlush3DBorder(g, 0, 0, w, h);
        g.setColor(PlasticLookAndFeel.getControlShadow());
        g.drawLine(1, 1, 1, h - 3);
        g.drawLine(1, 1, w - 3, 1);
        g.translate(-x, -y);
    }

    static void drawButtonBorder(Graphics g, int x, int y, int w, int h, boolean active) {
        if (active) {
            ButtonUI.drawActiveButtonBorder(g, x, y, w, h);
        } else {
            ButtonUI.drawFlush3DBorder(g, x, y, w, h);
        }
    }

    static void drawActiveButtonBorder(Graphics g, int x, int y, int w, int h) {
        ButtonUI.drawFlush3DBorder(g, x, y, w, h);
        g.setColor(PlasticLookAndFeel.getPrimaryControl());
        g.drawLine(x + 1, y + 1, x + 1, h - 3);
        g.drawLine(x + 1, y + 1, w - 3, x + 1);
        g.setColor(PlasticLookAndFeel.getPrimaryControlDarkShadow());
        g.drawLine(x + 2, h - 2, w - 2, h - 2);
        g.drawLine(w - 2, y + 2, w - 2, h - 2);
    }

    static void drawDefaultButtonBorder(Graphics g, int x, int y, int w, int h, boolean active) {
        ButtonUI.drawButtonBorder(g, x + 1, y + 1, w - 1, h - 1, active);
        g.translate(x, y);
        g.setColor(PlasticLookAndFeel.getControlDarkShadow());
        ButtonUI.drawRect(g, 0, 0, w - 3, h - 3);
        g.drawLine(w - 2, 0, w - 2, 0);
        g.drawLine(0, h - 2, 0, h - 2);
        g.setColor(PlasticLookAndFeel.getControl());
        g.drawLine(w - 1, 0, w - 1, 0);
        g.drawLine(0, h - 1, 0, h - 1);
        g.translate(-x, -y);
    }

    static void drawDefaultButtonPressedBorder(Graphics g, int x, int y, int w, int h) {
        ButtonUI.drawPressed3DBorder(g, x + 1, y + 1, w - 1, h - 1);
        g.translate(x, y);
        g.setColor(PlasticLookAndFeel.getControlDarkShadow());
        ButtonUI.drawRect(g, 0, 0, w - 3, h - 3);
        g.drawLine(w - 2, 0, w - 2, 0);
        g.drawLine(0, h - 2, 0, h - 2);
        g.setColor(PlasticLookAndFeel.getControl());
        g.drawLine(w - 1, 0, w - 1, 0);
        g.drawLine(0, h - 1, 0, h - 1);
        g.translate(-x, -y);
    }

    static void drawThinFlush3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate(x, y);
        g.setColor(PlasticLookAndFeel.getControlHighlight());
        g.drawLine(0, 0, w - 2, 0);
        g.drawLine(0, 0, 0, h - 2);
        g.setColor(PlasticLookAndFeel.getControlDarkShadow());
        g.drawLine(w - 1, 0, w - 1, h - 1);
        g.drawLine(0, h - 1, w - 1, h - 1);
        g.translate(-x, -y);
    }

    static void drawThinPressed3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate(x, y);
        g.setColor(PlasticLookAndFeel.getControlDarkShadow());
        g.drawLine(0, 0, w - 2, 0);
        g.drawLine(0, 0, 0, h - 2);
        g.setColor(PlasticLookAndFeel.getControlHighlight());
        g.drawLine(w - 1, 0, w - 1, h - 1);
        g.drawLine(0, h - 1, w - 1, h - 1);
        g.translate(-x, -y);
    }

    static boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }

    static boolean is3D(String keyPrefix) {
        Object value = UIManager.get(keyPrefix + "is3DEnabled");
        return Boolean.TRUE.equals(value);
    }

    static boolean force3D(JComponent c) {
        Object value = c.getClientProperty("Plastic.is3D");
        return Boolean.TRUE.equals(value);
    }

    static boolean forceFlat(JComponent c) {
        Object value = c.getClientProperty("Plastic.is3D");
        return Boolean.FALSE.equals(value);
    }

    private static void add3DEffekt(Graphics g, Rectangle r, boolean isHorizontal, Color startC0, Color stopC0, Color startC1, Color stopC1) {
        int yd1;
        int xd1;
        int yd0;
        int xd0;
        int yb1;
        int xb1;
        int yb0;
        int xb0;
        int height;
        int width;
        Graphics2D g2 = (Graphics2D)g;
        if (isHorizontal) {
            width = r.width;
            height = (int)((float)r.height * FRACTION_3D);
            xb0 = r.x;
            yb0 = r.y;
            xb1 = xb0;
            yb1 = yb0 + height;
            xd0 = xb1;
            yd0 = yb1;
            xd1 = xd0;
            yd1 = r.y + r.height;
        } else {
            width = (int)((float)r.width * FRACTION_3D);
            height = r.height;
            xb0 = r.x;
            yb0 = r.y;
            xb1 = xb0 + width;
            yb1 = yb0;
            xd0 = xb1;
            yd0 = yb0;
            xd1 = r.x + r.width;
            yd1 = yd0;
        }
        g2.setPaint(new GradientPaint(xb0, yb0, stopC0, xb1, yb1, startC0));
        g2.fillRect(r.x, r.y, width, height);
        g2.setPaint(new GradientPaint(xd0, yd0, startC1, xd1, yd1, stopC1));
        g2.fillRect(xd0, yd0, width, height);
    }

    static void add3DEffekt(Graphics g, Rectangle r) {
        Color brightenStop = UIManager.getColor("Plastic.brightenStop");
        if (null == brightenStop) {
            brightenStop = PlasticTheme.BRIGHTEN_STOP;
        }
        Graphics2D g2 = (Graphics2D)g;
        int border = 10;
        g2.setPaint(new GradientPaint(r.x, r.y, brightenStop, r.x + border, r.y, PlasticTheme.BRIGHTEN_START));
        g2.fillRect(r.x, r.y, border, r.height);
        int x = r.x + r.width - border;
        int y = r.y;
        g2.setPaint(new GradientPaint(x, y, PlasticTheme.DARKEN_START, x + border, y, PlasticTheme.LT_DARKEN_STOP));
        g2.fillRect(x, y, border, r.height);
        ButtonUI.add3DEffekt(g, r, true, PlasticTheme.BRIGHTEN_START, brightenStop, PlasticTheme.DARKEN_START, PlasticTheme.LT_DARKEN_STOP);
    }

    static void addLight3DEffekt(Graphics g, Rectangle r, boolean isHorizontal) {
        Color ltBrightenStop = UIManager.getColor("Plastic.ltBrightenStop");
        if (null == ltBrightenStop) {
            ltBrightenStop = PlasticTheme.LT_BRIGHTEN_STOP;
        }
        ButtonUI.add3DEffekt(g, r, isHorizontal, PlasticTheme.BRIGHTEN_START, ltBrightenStop, PlasticTheme.DARKEN_START, PlasticTheme.LT_DARKEN_STOP);
    }

    public static void addLight3DEffekt(Graphics g, Rectangle r) {
        Color ltBrightenStop = UIManager.getColor("Plastic.ltBrightenStop");
        if (null == ltBrightenStop) {
            ltBrightenStop = PlasticTheme.LT_BRIGHTEN_STOP;
        }
        ButtonUI.add3DEffekt(g, r, true, PlasticTheme.DARKEN_START, PlasticTheme.LT_DARKEN_STOP, PlasticTheme.BRIGHTEN_START, ltBrightenStop);
    }

    private static void drawRect(Graphics g, int x, int y, int w, int h) {
        g.fillRect(x, y, w + 1, 1);
        g.fillRect(x, y + 1, 1, h);
        g.fillRect(x + 1, y + h, w, 1);
        g.fillRect(x + w, y + 1, 1, h);
    }
}

