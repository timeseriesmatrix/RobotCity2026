/*
 * Decompiled with CFR 0.151.
 */
package com.jstatcom.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.JPanel;

public class CardPanel
extends JPanel {
    public CardPanel() {
        super(new Layout());
    }

    protected int getVisibleChildIndex() {
        int nChildren = this.getComponentCount();
        for (int i = 0; i < nChildren; ++i) {
            Component child = this.getComponent(i);
            if (!child.isVisible()) continue;
            return i;
        }
        return -1;
    }

    public void showCard(Component card) {
        int index;
        if (card.getParent() != this) {
            this.add(card);
        }
        if ((index = this.getVisibleChildIndex()) != -1) {
            this.getComponent(index).setVisible(false);
        }
        card.setVisible(true);
        this.revalidate();
        this.repaint();
    }

    public void showCard(String name) {
        int nChildren = this.getComponentCount();
        for (int i = 0; i < nChildren; ++i) {
            Component child = this.getComponent(i);
            if (!child.getName().equals(name)) continue;
            this.showCard(child);
            break;
        }
    }

    public void showFirstCard() {
        if (this.getComponentCount() <= 0) {
            return;
        }
        this.showCard(this.getComponent(0));
    }

    public void showLastCard() {
        if (this.getComponentCount() <= 0) {
            return;
        }
        this.showCard(this.getComponent(this.getComponentCount() - 1));
    }

    public void showNextCard() {
        if (this.getComponentCount() <= 0) {
            return;
        }
        int index = this.getVisibleChildIndex();
        if (index == -1) {
            this.showCard(this.getComponent(0));
        } else if (index == this.getComponentCount() - 1) {
            this.showCard(this.getComponent(0));
        } else {
            this.showCard(this.getComponent(index + 1));
        }
    }

    public void showPreviousCard() {
        if (this.getComponentCount() <= 0) {
            return;
        }
        int index = this.getVisibleChildIndex();
        if (index == -1) {
            this.showCard(this.getComponent(0));
        } else if (index == 0) {
            this.showCard(this.getComponent(this.getComponentCount() - 1));
        } else {
            this.showCard(this.getComponent(index - 1));
        }
    }

    private static class Layout
    implements LayoutManager {
        private Layout() {
        }

        @Override
        public void addLayoutComponent(String name, Component child) {
            if (name != null) {
                child.setName(name);
            }
            child.setVisible(child.getParent().getComponentCount() == 1);
        }

        @Override
        public void removeLayoutComponent(Component child) {
            Container parent;
            if (child.isVisible() && (parent = child.getParent()).getComponentCount() > 0) {
                parent.getComponent(0).setVisible(true);
            }
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            int nChildren = parent.getComponentCount();
            Insets insets = parent.getInsets();
            int width = insets.left + insets.right;
            int height = insets.top + insets.bottom;
            for (int i = 0; i < nChildren; ++i) {
                Dimension d = parent.getComponent(i).getPreferredSize();
                if (d.width > width) {
                    width = d.width;
                }
                if (d.height <= height) continue;
                height = d.height;
            }
            return new Dimension(width, height);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            int nChildren = parent.getComponentCount();
            Insets insets = parent.getInsets();
            int width = insets.left + insets.right;
            int height = insets.top + insets.bottom;
            for (int i = 0; i < nChildren; ++i) {
                Dimension d = parent.getComponent(i).getMinimumSize();
                if (d.width > width) {
                    width = d.width;
                }
                if (d.height <= height) continue;
                height = d.height;
            }
            return new Dimension(width, height);
        }

        @Override
        public void layoutContainer(Container parent) {
            int nChildren = parent.getComponentCount();
            Insets insets = parent.getInsets();
            for (int i = 0; i < nChildren; ++i) {
                Component child = parent.getComponent(i);
                if (!child.isVisible()) continue;
                Rectangle r = parent.getBounds();
                int width = r.width - insets.left + insets.right;
                int height = r.height - insets.top + insets.bottom;
                child.setBounds(insets.left, insets.top, width, height);
                break;
            }
        }
    }
}

