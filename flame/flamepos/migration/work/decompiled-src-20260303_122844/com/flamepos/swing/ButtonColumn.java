/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.swing.PosButton;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class ButtonColumn
extends AbstractCellEditor
implements TableCellRenderer,
TableCellEditor,
ActionListener,
MouseListener {
    private JTable table;
    private Action action;
    private int mnemonic;
    private Border originalBorder;
    private Border focusBorder;
    private PosButton renderButton;
    private PosButton editButton;
    private Object editorValue;
    private boolean isButtonColumnEditor;

    public ButtonColumn(JTable table, Action action, int column) {
        this.table = table;
        this.action = action;
        this.renderButton = new PosButton();
        this.editButton = new PosButton();
        this.editButton.setFocusPainted(false);
        this.editButton.addActionListener(this);
        this.originalBorder = this.editButton.getBorder();
        this.setFocusBorder(new LineBorder(Color.BLUE));
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer(this);
        columnModel.getColumn(column).setCellEditor(this);
        table.addMouseListener(this);
    }

    public Border getFocusBorder() {
        return this.focusBorder;
    }

    public void setFocusBorder(Border focusBorder) {
        this.focusBorder = focusBorder;
        this.editButton.setBorder(focusBorder);
    }

    public int getMnemonic() {
        return this.mnemonic;
    }

    public void setMnemonic(int mnemonic) {
        this.mnemonic = mnemonic;
        this.renderButton.setMnemonic(mnemonic);
        this.editButton.setMnemonic(mnemonic);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value == null) {
            this.editButton.setText("");
            this.editButton.setIcon(null);
        } else if (value instanceof Icon) {
            this.editButton.setText("");
            this.editButton.setIcon((Icon)value);
        } else {
            this.editButton.setText(value.toString());
            this.editButton.setIcon(null);
        }
        this.editorValue = value;
        return this.editButton;
    }

    @Override
    public Object getCellEditorValue() {
        return this.editorValue;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            this.renderButton.setForeground(table.getSelectionForeground());
            this.renderButton.setBackground(table.getSelectionBackground());
        } else {
            this.renderButton.setForeground(table.getForeground());
            this.renderButton.setBackground(UIManager.getColor("Button.background"));
        }
        if (hasFocus) {
            this.renderButton.setBorder(this.focusBorder);
        } else {
            this.renderButton.setBorder(this.originalBorder);
        }
        if (value == null) {
            this.renderButton.setText("");
            this.renderButton.setIcon(null);
        } else if (value instanceof Icon) {
            this.renderButton.setText("");
            this.renderButton.setIcon((Icon)value);
        } else {
            this.renderButton.setText(value.toString());
            this.renderButton.setIcon(null);
        }
        return this.renderButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int row = this.table.convertRowIndexToModel(this.table.getEditingRow());
        this.fireEditingStopped();
        ActionEvent event = new ActionEvent(this.table, 1001, "" + row);
        this.action.actionPerformed(event);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (this.table.isEditing() && this.table.getCellEditor() == this) {
            this.isButtonColumnEditor = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.isButtonColumnEditor && this.table.isEditing()) {
            this.table.getCellEditor().stopCellEditing();
        }
        this.isButtonColumnEditor = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}

