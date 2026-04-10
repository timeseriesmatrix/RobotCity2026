/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

public class GlassPane
extends JPanel
implements MouseListener,
MouseMotionListener {
    private float opacity = 0.2f;

    public GlassPane() {
        this.setOpaque(false);
        this.setVisible(false);
        this.setLayout(new GridLayout());
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setCursor(Cursor.getPredefinedCursor(3));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        AlphaComposite ac = AlphaComposite.getInstance(3, this.opacity);
        g2.setComposite(ac);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    public float getOpacity() {
        return this.opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }
}

