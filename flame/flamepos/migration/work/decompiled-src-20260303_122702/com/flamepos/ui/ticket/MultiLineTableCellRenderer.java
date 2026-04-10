/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.ticket;

import com.floreantpos.swing.PosUIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class MultiLineTableCellRenderer
extends JTextPane
implements TableCellRenderer {
    public MultiLineTableCellRenderer() {
        this.setOpaque(true);
        this.setEditorKit(new MyEditorKit());
        this.setBorder(new EmptyBorder(10, 2, 10, 2));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        int colWidth = table.getTableHeader().getColumnModel().getColumn(column).getWidth();
        this.setSize(new Dimension(colWidth, 240));
        int height = this.getPreferredSize().height;
        height = height < 60 ? 60 : height;
        height = PosUIManager.getSize(height);
        if (table.getRowHeight() < height) {
            table.setRowHeight(height);
        }
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
            MultiLineTableCellRenderer.setForeground(this, table.getSelectionForeground());
        } else {
            this.setBackground(table.getBackground());
            MultiLineTableCellRenderer.setForeground(this, table.getForeground());
        }
        if (value != null) {
            this.setText(value.toString());
        } else {
            this.setText("");
        }
        return this;
    }

    public static void setForeground(JTextPane jtp, Color c) {
        MutableAttributeSet attrs = jtp.getInputAttributes();
        StyleConstants.setForeground(attrs, c);
        StyledDocument doc = jtp.getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
    }

    static class CenteredBoxView
    extends BoxView {
        public CenteredBoxView(Element elem, int axis) {
            super(elem, axis);
        }

        @Override
        protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
            int i;
            super.layoutMajorAxis(targetSpan, axis, offsets, spans);
            int textBlockHeight = 0;
            int offset = 0;
            for (i = 0; i < spans.length; ++i) {
                textBlockHeight = spans[i];
            }
            offset = (targetSpan - textBlockHeight) / 2;
            i = 0;
            while (i < offsets.length) {
                int n = i++;
                offsets[n] = offsets[n] + offset;
            }
        }
    }

    public static class MyEditorKit
    extends StyledEditorKit {
        @Override
        public ViewFactory getViewFactory() {
            return new StyledViewFactory();
        }

        static class StyledViewFactory
        implements ViewFactory {
            StyledViewFactory() {
            }

            @Override
            public View create(Element elem) {
                String kind = elem.getName();
                if (kind != null) {
                    if (kind.equals("content")) {
                        return new LabelView(elem);
                    }
                    if (kind.equals("paragraph")) {
                        return new ParagraphView(elem);
                    }
                    if (kind.equals("section")) {
                        return new CenteredBoxView(elem, 1);
                    }
                    if (kind.equals("component")) {
                        return new ComponentView(elem);
                    }
                    if (kind.equals("icon")) {
                        return new IconView(elem);
                    }
                }
                return new LabelView(elem);
            }
        }
    }
}

