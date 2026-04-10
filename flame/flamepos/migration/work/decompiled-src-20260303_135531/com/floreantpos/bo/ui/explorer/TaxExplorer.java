/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.Tax;
import com.floreantpos.model.dao.TaxDAO;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.model.TaxForm;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class TaxExplorer
extends TransparentPanel {
    private List<Tax> taxList = TaxDAO.getInstance().findAll();
    private JTable table;
    private TaxExplorerTableModel tableModel = new TaxExplorerTableModel();

    public TaxExplorer() {
        this.table = new JTable(this.tableModel);
        this.table.setDefaultRenderer(Object.class, new PosTableRenderer());
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane(this.table));
        JButton addButton = new JButton(POSConstants.ADD);
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    TaxForm editor = new TaxForm();
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    TaxExplorer.this.tableModel.addTax((Tax)editor.getBean());
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        JButton editButton = new JButton(POSConstants.EDIT);
        editButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = TaxExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    Tax tax = (Tax)TaxExplorer.this.taxList.get(index);
                    TaxForm taxForm = new TaxForm(tax);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)taxForm);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    TaxExplorer.this.table.repaint();
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        JButton deleteButton = new JButton(POSConstants.DELETE);
        deleteButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = TaxExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    if (ConfirmDeleteDialog.showMessage(TaxExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) == 0) {
                        Tax tax = (Tax)TaxExplorer.this.taxList.get(index);
                        TaxDAO.getInstance().delete(tax);
                        TaxExplorer.this.tableModel.deleteTax(tax, index);
                    }
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        TransparentPanel panel = new TransparentPanel();
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        this.add((Component)panel, "South");
    }

    class TaxExplorerTableModel
    extends AbstractTableModel {
        String[] columnNames = new String[]{POSConstants.ID, POSConstants.NAME, POSConstants.RATE};

        TaxExplorerTableModel() {
        }

        @Override
        public int getRowCount() {
            if (TaxExplorer.this.taxList == null) {
                return 0;
            }
            return TaxExplorer.this.taxList.size();
        }

        @Override
        public int getColumnCount() {
            return this.columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return this.columnNames[column];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (TaxExplorer.this.taxList == null) {
                return "";
            }
            Tax tax = (Tax)TaxExplorer.this.taxList.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return String.valueOf(tax.getId());
                }
                case 1: {
                    return tax.getName();
                }
                case 2: {
                    return tax.getRate();
                }
            }
            return null;
        }

        public void addTax(Tax tax) {
            int size = TaxExplorer.this.taxList.size();
            TaxExplorer.this.taxList.add(tax);
            this.fireTableRowsInserted(size, size);
        }

        public void deleteTax(Tax tax, int index) {
            TaxExplorer.this.taxList.remove(tax);
            this.fireTableRowsDeleted(index, index);
        }
    }
}

