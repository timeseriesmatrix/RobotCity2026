/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import java.awt.AWTEvent;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

public class TouchScrollHandler
extends MouseAdapter
implements AWTEventListener {
    private Point origin;
    private boolean wasDragging;

    @Override
    public void mousePressed(MouseEvent e) {
        this.origin = new Point(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.wasDragging) {
            e.getComponent().setCursor(Cursor.getDefaultCursor());
            this.wasDragging = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        JViewport viewPort;
        if (this.origin != null && (viewPort = (JViewport)SwingUtilities.getAncestorOfClass(JViewport.class, e.getComponent())) != null) {
            Rectangle view = viewPort.getViewRect();
            int deltaX = this.origin.x - e.getX();
            int deltaY = this.origin.y - e.getY();
            view.x += deltaX;
            view.y += deltaY;
            e.getComponent().setCursor(Cursor.getPredefinedCursor(13));
            ((JComponent)e.getComponent()).scrollRectToVisible(view);
            this.wasDragging = true;
            e.consume();
        }
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        switch (event.getID()) {
            case 501: {
                this.mousePressed((MouseEvent)event);
                break;
            }
            case 502: {
                this.mouseReleased((MouseEvent)event);
                break;
            }
            case 506: {
                this.mouseDragged((MouseEvent)event);
                break;
            }
        }
    }
}

