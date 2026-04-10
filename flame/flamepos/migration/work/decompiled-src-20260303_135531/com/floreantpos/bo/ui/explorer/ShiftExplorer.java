/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.Shift;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.ShiftDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.model.ShiftEntryDialog;
import com.floreantpos.util.ShiftUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ShiftExplorer
extends TransparentPanel {
    private JTable table;
    private ShiftTableModel tableModel;

    public ShiftExplorer() {
        List<Shift> shifts = new ShiftDAO().findAll();
        this.tableModel = new ShiftTableModel(shifts);
        this.table = new JTable(this.tableModel);
        this.table.setDefaultRenderer(Object.class, new PosTableRenderer());
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane(this.table));
        JButton addButton = new JButton(POSConstants.ADD);
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ShiftEntryDialog dialog = new ShiftEntryDialog();
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    Shift shift = dialog.getShift();
                    ShiftExplorer.this.tableModel.addItem(shift);
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
                    int index = ShiftExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    Shift shift = (Shift)ShiftExplorer.this.tableModel.getRowData(index);
                    ShiftEntryDialog dialog = new ShiftEntryDialog();
                    dialog.setShift(shift);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    ShiftExplorer.this.tableModel.updateItem(index);
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
                    int index = ShiftExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    if (ConfirmDeleteDialog.showMessage(ShiftExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) == 0) {
                        User user = (User)ShiftExplorer.this.tableModel.getRowData(index);
                        UserDAO.getInstance().delete(user);
                        ShiftExplorer.this.tableModel.deleteItem(index);
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
        this.add((Component)panel, "South");
    }

    class ShiftTableModel
    extends ListTableModel {
        ShiftTableModel(List list) {
            super(new String[]{POSConstants.ID, POSConstants.NAME, POSConstants.START_TIME, POSConstants.END_TIME}, list);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Shift shift = (Shift)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return String.valueOf(shift.getId());
                }
                case 1: {
                    return shift.getName();
                }
                case 2: {
                    return ShiftUtil.buildShiftTimeRepresentation(shift.getStartTime());
                }
                case 3: {
                    return ShiftUtil.buildShiftTimeRepresentation(shift.getEndTime());
                }
            }
            return null;
        }
    }
}

