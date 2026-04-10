/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.table;

import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.bo.ui.Command;
import com.floreantpos.bo.ui.ModelBrowser;
import com.floreantpos.table.ShopTableForm;
import com.floreantpos.ui.BeanEditor;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;

public class ShopTableModelBrowser<E>
extends ModelBrowser {
    private BeanEditor beanEditor;
    private JButton btnDuplicate = new JButton(Messages.getString("ShopTableModelBrowser.0"));
    private JButton btnDeleteAll = new JButton(Messages.getString("ShopTableModelBrowser.1"));

    public ShopTableModelBrowser(BeanEditor<E> beanEditor) {
        super(beanEditor);
        this.beanEditor = beanEditor;
    }

    @Override
    public void init(TableModel tableModel) {
        super.init(tableModel);
        this.buttonPanel.add(this.btnDuplicate);
        this.btnDuplicate.addActionListener(this);
        this.btnDuplicate.setEnabled(false);
        this.buttonPanel.add(this.btnDeleteAll);
        this.btnDeleteAll.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Command command = Command.fromString(e.getActionCommand());
        try {
            switch (command) {
                case NEW: {
                    this.beanEditor.createNew();
                    this.beanEditor.setFieldsEnable(true);
                    this.btnNew.setEnabled(false);
                    this.btnEdit.setEnabled(false);
                    this.btnSave.setEnabled(true);
                    this.btnDelete.setEnabled(false);
                    this.btnCancel.setEnabled(true);
                    this.browserTable.clearSelection();
                    this.btnDuplicate.setEnabled(false);
                    break;
                }
                case EDIT: {
                    this.beanEditor.edit();
                    this.beanEditor.setFieldsEnable(true);
                    this.btnNew.setEnabled(false);
                    this.btnEdit.setEnabled(false);
                    this.btnSave.setEnabled(true);
                    this.btnDelete.setEnabled(false);
                    this.btnCancel.setEnabled(true);
                    this.btnDuplicate.setEnabled(false);
                    break;
                }
                case CANCEL: {
                    this.doCancelEditing();
                    break;
                }
                case SAVE: {
                    if (!this.beanEditor.save()) break;
                    this.beanEditor.setFieldsEnable(false);
                    this.btnNew.setEnabled(true);
                    this.btnEdit.setEnabled(false);
                    this.btnSave.setEnabled(false);
                    this.btnDelete.setEnabled(false);
                    this.btnCancel.setEnabled(false);
                    this.refreshTable();
                    this.customSelectedRow();
                    break;
                }
                case DELETE: {
                    if (!this.beanEditor.delete()) break;
                    this.beanEditor.setBean(null);
                    this.beanEditor.setFieldsEnable(false);
                    this.btnNew.setEnabled(true);
                    this.btnEdit.setEnabled(false);
                    this.btnSave.setEnabled(false);
                    this.btnDelete.setEnabled(false);
                    this.btnCancel.setEnabled(false);
                    this.refreshTable();
                    this.btnDuplicate.setEnabled(false);
                    break;
                }
            }
            this.handleAdditionaButtonActionIfApplicable(e);
            ShopTableForm form = (ShopTableForm)this.beanEditor;
            if (e.getSource() == this.btnDuplicate) {
                form.setDuplicate(true);
                form.createNew();
                form.save();
                this.refreshTable();
                this.customSelectedRow();
                this.btnSave.setEnabled(false);
                this.btnCancel.setEnabled(false);
            } else if (e.getSource() == this.btnDeleteAll) {
                if (!form.deleteAllTables()) {
                    return;
                }
                this.refreshTable();
                this.btnNew.setEnabled(true);
                this.btnEdit.setEnabled(false);
                this.btnSave.setEnabled(false);
                this.btnDelete.setEnabled(false);
                this.btnCancel.setEnabled(false);
                this.btnDuplicate.setEnabled(false);
            }
        }
        catch (Exception e2) {
            PosLog.error(this.getClass(), e2);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        super.valueChanged(e);
        this.btnDuplicate.setEnabled(true);
        this.btnDeleteAll.setEnabled(true);
    }

    @Override
    public void doCancelEditing() {
        super.doCancelEditing();
        if (this.browserTable.getSelectedRow() != -1) {
            this.btnDuplicate.setEnabled(true);
        }
    }

    private void customSelectedRow() {
        ShopTableForm form = (ShopTableForm)this.beanEditor;
        int x = this.getRowByValue(this.browserTable.getModel(), form.getNewTable());
        this.browserTable.setRowSelectionInterval(x, x);
    }

    private int getRowByValue(TableModel model, Object value) {
        for (int i = 0; i <= model.getRowCount(); ++i) {
            if (!model.getValueAt(i, 0).equals(value)) continue;
            return i;
        }
        return -1;
    }
}

