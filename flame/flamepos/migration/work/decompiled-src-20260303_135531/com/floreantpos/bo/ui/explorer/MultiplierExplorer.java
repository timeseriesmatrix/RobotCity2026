/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.bo.ui.CustomCellRenderer;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.dao.MultiplierDAO;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.model.MultiplierForm;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

public class MultiplierExplorer
extends TransparentPanel {
    private List<Multiplier> multiplierList = MultiplierDAO.getInstance().findAll();
    private JTable table;
    private MultiplierExplorerTableModel tableModel = new MultiplierExplorerTableModel();
    private JButton editButton;
    private JButton deleteButton;

    public MultiplierExplorer() {
        this.table = new JTable(this.tableModel);
        this.table.setDefaultRenderer(Object.class, new CustomCellRenderer());
        this.table.getColumnModel().getColumn(6).setCellRenderer(new PosTableRenderer());
        this.table.setSelectionMode(0);
        this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int index = MultiplierExplorer.this.table.getSelectedRow();
                if (index < 0) {
                    return;
                }
                Multiplier multiplier = (Multiplier)MultiplierExplorer.this.multiplierList.get(index);
                MultiplierExplorer.this.editButton.setEnabled(multiplier.isMain() == false);
                MultiplierExplorer.this.deleteButton.setEnabled(multiplier.isMain() == false);
            }
        });
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane(this.table));
        JButton addButton = new JButton(POSConstants.ADD);
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MultiplierForm editor = new MultiplierForm();
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    MultiplierExplorer.this.tableModel.addMultiplier((Multiplier)editor.getBean());
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        this.editButton = new JButton(POSConstants.EDIT);
        this.editButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = MultiplierExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    Multiplier multiplier = (Multiplier)MultiplierExplorer.this.multiplierList.get(index);
                    if (multiplier.isMain().booleanValue()) {
                        return;
                    }
                    MultiplierForm multiplierForm = new MultiplierForm(multiplier);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)multiplierForm);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    MultiplierExplorer.this.table.repaint();
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        this.deleteButton = new JButton(POSConstants.DELETE);
        this.deleteButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = MultiplierExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    Multiplier multiplier = (Multiplier)MultiplierExplorer.this.multiplierList.get(index);
                    if (multiplier.isMain().booleanValue()) {
                        return;
                    }
                    if (ConfirmDeleteDialog.showMessage(MultiplierExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) == 0) {
                        MultiplierDAO.getInstance().delete(multiplier);
                        MultiplierExplorer.this.tableModel.deleteMultiplier(multiplier, index);
                    }
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        JButton btnSetAsDefault = new JButton("Set default");
        btnSetAsDefault.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = MultiplierExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    Multiplier selectedMultiplier = (Multiplier)MultiplierExplorer.this.multiplierList.get(index);
                    for (Multiplier item : MultiplierExplorer.this.multiplierList) {
                        if (selectedMultiplier.getName().equals(item.getName())) {
                            item.setDefaultMultiplier(true);
                            continue;
                        }
                        item.setDefaultMultiplier(false);
                    }
                    MultiplierDAO.getInstance().saveOrUpdateMultipliers(MultiplierExplorer.this.multiplierList);
                    MultiplierExplorer.this.tableModel.fireTableDataChanged();
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        TransparentPanel panel = new TransparentPanel();
        panel.add(addButton);
        panel.add(this.editButton);
        panel.add(this.deleteButton);
        panel.add(btnSetAsDefault);
        this.add((Component)panel, "South");
    }

    class MultiplierExplorerTableModel
    extends AbstractTableModel {
        String[] columnNames = new String[]{POSConstants.NAME, "Prefix", POSConstants.RATE, POSConstants.SORT_ORDER, POSConstants.BUTTON_COLOR, POSConstants.TEXT_COLOR, "Default"};

        MultiplierExplorerTableModel() {
        }

        @Override
        public int getRowCount() {
            if (MultiplierExplorer.this.multiplierList == null) {
                return 0;
            }
            return MultiplierExplorer.this.multiplierList.size();
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
            if (MultiplierExplorer.this.multiplierList == null) {
                return "";
            }
            Multiplier multiplier = (Multiplier)MultiplierExplorer.this.multiplierList.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return multiplier.getName();
                }
                case 1: {
                    return multiplier.getTicketPrefix();
                }
                case 2: {
                    return multiplier.getRate();
                }
                case 3: {
                    return multiplier.getSortOrder();
                }
                case 4: {
                    if (multiplier.getButtonColor() != null) {
                        return new Color(multiplier.getButtonColor());
                    }
                    return null;
                }
                case 5: {
                    if (multiplier.getTextColor() != null) {
                        return new Color(multiplier.getTextColor());
                    }
                    return null;
                }
                case 6: {
                    return multiplier.isDefaultMultiplier();
                }
            }
            return null;
        }

        public void addMultiplier(Multiplier multiplier) {
            int size = MultiplierExplorer.this.multiplierList.size();
            MultiplierExplorer.this.multiplierList.add(multiplier);
            this.fireTableRowsInserted(size, size);
        }

        public void deleteMultiplier(Multiplier multiplier, int index) {
            MultiplierExplorer.this.multiplierList.remove(multiplier);
            this.fireTableRowsDeleted(index, index);
        }
    }
}

