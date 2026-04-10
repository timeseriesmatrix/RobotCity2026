/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

public class ScrollableFlowPanel
extends JPanel
implements Scrollable {
    private ScrollableFlowLayout layout;
    private JPanel contentPane;

    public ScrollableFlowPanel() {
        this(1);
    }

    public ScrollableFlowPanel(int alignment) {
        super(new BorderLayout());
        this.layout = new ScrollableFlowLayout(alignment);
        this.contentPane = new JPanel(this.layout);
        super.add(this.contentPane);
    }

    @Override
    public Component add(Component comp) {
        return this.contentPane.add(comp);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return this.getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        preferredSize.height = this.layout.getLayoutHeight();
        return preferredSize;
    }

    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return (orientation == 1 ? visibleRect.height : visibleRect.width) - 10;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    class ScrollableFlowLayout
    extends FlowLayout {
        private int layoutHeight;

        public ScrollableFlowLayout() {
        }

        public ScrollableFlowLayout(int align) {
            super(align);
        }

        public ScrollableFlowLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        private int moveComponents(Container target, int x, int y, int width, int height, int rowStart, int rowEnd, boolean ltr, boolean useBaseline, int[] ascent, int[] descent) {
            switch (this.getAlignment()) {
                case 0: {
                    x += ltr ? 0 : width;
                    break;
                }
                case 1: {
                    x += width / 2;
                    break;
                }
                case 2: {
                    x += ltr ? width : 0;
                    break;
                }
                case 3: {
                    break;
                }
                case 4: {
                    x += width;
                }
            }
            int maxAscent = 0;
            int nonbaselineHeight = 0;
            int baselineOffset = 0;
            if (useBaseline) {
                int maxDescent = 0;
                for (int i = rowStart; i < rowEnd; ++i) {
                    Component m = target.getComponent(i);
                    if (!m.isVisible()) continue;
                    if (ascent[i] >= 0) {
                        maxAscent = Math.max(maxAscent, ascent[i]);
                        maxDescent = Math.max(maxDescent, descent[i]);
                        continue;
                    }
                    nonbaselineHeight = Math.max(m.getHeight(), nonbaselineHeight);
                }
                height = Math.max(maxAscent + maxDescent, nonbaselineHeight);
                baselineOffset = (height - maxAscent - maxDescent) / 2;
            }
            for (int i = rowStart; i < rowEnd; ++i) {
                Component m = target.getComponent(i);
                if (!m.isVisible()) continue;
                int cy = useBaseline && ascent[i] >= 0 ? y + baselineOffset + maxAscent - ascent[i] : y + (height - m.getSize().height) / 2;
                if (ltr) {
                    m.setLocation(x, cy);
                } else {
                    m.setLocation(target.getSize().width - x - m.getSize().width, cy);
                }
                x += m.getSize().width + this.getHgap();
            }
            return height;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void layoutContainer(Container target) {
            Object object = target.getTreeLock();
            synchronized (object) {
                this.layoutHeight = 0;
                Insets insets = target.getInsets();
                int maxwidth = target.getSize().width - (insets.left + insets.right + this.getHgap() * 2);
                int nmembers = target.getComponentCount();
                int x = 0;
                int y = insets.top + this.getVgap();
                int rowh = 0;
                int start = 0;
                boolean ltr = target.getComponentOrientation().isLeftToRight();
                boolean useBaseline = this.getAlignOnBaseline();
                int[] ascent = null;
                int[] descent = null;
                if (useBaseline) {
                    ascent = new int[nmembers];
                    descent = new int[nmembers];
                }
                for (int i = 0; i < nmembers; ++i) {
                    Component m = target.getComponent(i);
                    if (!m.isVisible()) continue;
                    Dimension d = m.getPreferredSize();
                    m.setSize(d.width, d.height);
                    if (useBaseline) {
                        int baseline = m.getBaseline(d.width, d.height);
                        if (baseline >= 0) {
                            ascent[i] = baseline;
                            descent[i] = d.height - baseline;
                        } else {
                            ascent[i] = -1;
                        }
                    }
                    if (x == 0 || x + d.width <= maxwidth) {
                        if (x > 0) {
                            x += this.getHgap();
                        }
                        x += d.width;
                        rowh = Math.max(rowh, d.height);
                        continue;
                    }
                    rowh = this.moveComponents(target, insets.left + this.getHgap(), y, maxwidth - x, rowh, start, i, ltr, useBaseline, ascent, descent);
                    x = d.width;
                    y += this.getVgap() + rowh;
                    rowh = d.height;
                    start = i;
                    this.layoutHeight += rowh + this.getVgap();
                }
                this.layoutHeight += this.moveComponents(target, insets.left + this.getHgap(), y, maxwidth - x, rowh, start, nmembers, ltr, useBaseline, ascent, descent);
                this.layoutHeight += this.getVgap() * 2;
            }
        }

        public int getLayoutHeight() {
            return this.layoutHeight;
        }
    }
}

