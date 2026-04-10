/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.CookingInstruction;
import com.floreantpos.model.dao.CookingInstructionDAO;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class CookingInstructionExplorer
extends TransparentPanel {
    private List<CookingInstruction> categoryList;
    private JTable table;
    private CookingInstructionTableModel tableModel;
    CookingInstructionDAO dao = new CookingInstructionDAO();

    public CookingInstructionExplorer() {
        this.categoryList = this.dao.findAll();
        this.tableModel = new CookingInstructionTableModel();
        this.table = new JTable(this.tableModel);
        this.table.setDefaultRenderer(Object.class, new PosTableRenderer());
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane(this.table));
        JButton addButton = new JButton(POSConstants.ADD);
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String instruction = JOptionPane.showInputDialog(POSUtil.getBackOfficeWindow(), (Object)POSConstants.ENTER_INSTRUCTION_DESCRIPTION);
                    if (instruction == null) {
                        BOMessageDialog.showError(POSUtil.getBackOfficeWindow(), POSConstants.INSTRUCTION_CANNOT_BE_EMPTY);
                        return;
                    }
                    if (instruction.length() > 60) {
                        BOMessageDialog.showError(POSUtil.getBackOfficeWindow(), POSConstants.LONG_INSTRUCTION_ERROR);
                        return;
                    }
                    CookingInstruction cookingInstruction = new CookingInstruction();
                    cookingInstruction.setDescription(instruction);
                    CookingInstructionExplorer.this.dao.save(cookingInstruction);
                    CookingInstructionExplorer.this.tableModel.add(cookingInstruction);
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
                    int index = CookingInstructionExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    CookingInstruction cookingInstruction = (CookingInstruction)CookingInstructionExplorer.this.categoryList.get(index);
                    String instruction = JOptionPane.showInputDialog(POSUtil.getBackOfficeWindow(), POSConstants.ENTER_INSTRUCTION_DESCRIPTION, cookingInstruction.getDescription());
                    if (instruction == null) {
                        BOMessageDialog.showError(POSUtil.getBackOfficeWindow(), POSConstants.INSTRUCTION_CANNOT_BE_EMPTY);
                        return;
                    }
                    if (instruction.length() > 60) {
                        BOMessageDialog.showError(POSUtil.getBackOfficeWindow(), POSConstants.LONG_INSTRUCTION_ERROR);
                        return;
                    }
                    cookingInstruction.setDescription(instruction);
                    CookingInstructionExplorer.this.dao.saveOrUpdate(cookingInstruction);
                    CookingInstructionExplorer.this.table.repaint();
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
                    int index = CookingInstructionExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    if (ConfirmDeleteDialog.showMessage(CookingInstructionExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) == 0) {
                        CookingInstruction cookingInstruction = (CookingInstruction)CookingInstructionExplorer.this.categoryList.get(index);
                        CookingInstructionExplorer.this.dao.delete(cookingInstruction);
                        CookingInstructionExplorer.this.tableModel.delete(cookingInstruction, index);
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

    class CookingInstructionTableModel
    extends AbstractTableModel {
        String[] columnNames = new String[]{POSConstants.ID, POSConstants.DESCRIPTION};

        CookingInstructionTableModel() {
        }

        @Override
        public int getRowCount() {
            if (CookingInstructionExplorer.this.categoryList == null) {
                return 0;
            }
            return CookingInstructionExplorer.this.categoryList.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
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
            if (CookingInstructionExplorer.this.categoryList == null) {
                return "";
            }
            CookingInstruction cookingInstruction = (CookingInstruction)CookingInstructionExplorer.this.categoryList.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return String.valueOf(cookingInstruction.getId());
                }
                case 1: {
                    return cookingInstruction.getDescription();
                }
            }
            return null;
        }

        public void add(CookingInstruction instruction) {
            int size = CookingInstructionExplorer.this.categoryList.size();
            CookingInstructionExplorer.this.categoryList.add(instruction);
            this.fireTableRowsInserted(size, size);
        }

        public void delete(CookingInstruction instruction, int index) {
            CookingInstructionExplorer.this.categoryList.remove(instruction);
            this.fireTableRowsDeleted(index, index);
        }
    }
}

