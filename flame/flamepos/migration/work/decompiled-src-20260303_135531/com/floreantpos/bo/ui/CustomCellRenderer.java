/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.ui;

import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomCellRenderer
extends DefaultTableCellRenderer {
    private Border unselectedBorder = null;
    private Border selectedBorder = null;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (this.selectedBorder == null) {
            this.selectedBorder = BorderFactory.createMatteBorder(5, 5, 5, 5, table.getSelectionBackground());
        }
        if (this.unselectedBorder == null) {
            this.unselectedBorder = BorderFactory.createMatteBorder(5, 5, 5, 5, table.getBackground());
        }
        if (value instanceof byte[]) {
            byte[] imageData = (byte[])value;
            ImageIcon image = new ImageIcon(imageData);
            image = new ImageIcon(image.getImage().getScaledInstance(100, 100, 4));
            if (imageData != null) {
                table.setRowHeight(row, 120);
            }
            JLabel l = new JLabel(image);
            if (isSelected) {
                l.setBorder(this.selectedBorder);
            } else {
                l.setBorder(this.unselectedBorder);
            }
            return l;
        }
        if (value instanceof Color) {
            JLabel l = new JLabel();
            Color newColor = (Color)value;
            l.setOpaque(true);
            l.setBackground(newColor);
            if (isSelected) {
                l.setBorder(this.selectedBorder);
            } else {
                l.setBorder(this.unselectedBorder);
            }
            return l;
        }
        if (value instanceof Date) {
            String pattern = "MM/dd hh:mm a";
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            value = format.format((Date)value);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
        if (value instanceof String) {
            value = "<html>" + value + "</html>";
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}

