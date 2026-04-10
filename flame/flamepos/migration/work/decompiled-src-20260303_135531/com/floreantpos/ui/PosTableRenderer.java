/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui;

import com.floreantpos.util.NumberUtil;
import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class PosTableRenderer
extends DefaultTableCellRenderer {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, h:m a");
    private JCheckBox checkBox = new JCheckBox();
    private JLabel lblColor = new JLabel();

    public PosTableRenderer() {
        this.checkBox.setHorizontalAlignment(0);
        this.lblColor.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Boolean) {
            this.checkBox.setSelected((Boolean)value);
            if (isSelected) {
                this.checkBox.setBackground(table.getSelectionBackground());
            } else {
                this.checkBox.setBackground(table.getBackground());
            }
            return this.checkBox;
        }
        if (value instanceof Color) {
            Color color = (Color)value;
            String rgb = Integer.toHexString(color.getRGB()).toUpperCase();
            rgb = rgb.substring(2, rgb.length());
            this.lblColor.setText(rgb);
            this.lblColor.setBackground(color);
            return this.lblColor;
        }
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
        return label;
    }

    @Override
    protected void setValue(Object value) {
        if (value == null) {
            this.setText("");
            return;
        }
        String text = value.toString();
        if (value instanceof Double || value instanceof Float) {
            text = NumberUtil.formatNumber(((Number)value).doubleValue());
            this.setHorizontalAlignment(4);
        } else if (value instanceof Integer) {
            this.setHorizontalAlignment(4);
        } else if (value instanceof Date) {
            text = dateFormat.format(value);
            this.setHorizontalAlignment(2);
        } else {
            this.setHorizontalAlignment(2);
        }
        this.setText(" " + text + " ");
    }
}

