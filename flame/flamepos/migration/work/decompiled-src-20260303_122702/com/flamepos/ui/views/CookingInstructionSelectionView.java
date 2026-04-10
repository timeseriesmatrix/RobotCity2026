/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang3.StringUtils
 */
package com.floreantpos.ui.views;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.CookingInstruction;
import com.floreantpos.model.TicketItemCookingInstruction;
import com.floreantpos.model.dao.CookingInstructionDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.QwertyKeyPad;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.Component;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

public class CookingInstructionSelectionView
extends OkCancelOptionDialog {
    private JTable table;
    private List<TicketItemCookingInstruction> ticketItemCookingInstructions;
    JTextField tfCookingInstruction = new JTextField();
    private List<CookingInstruction> cookingInstructions;

    public CookingInstructionSelectionView() {
        super((Frame)Application.getPosWindow(), true);
        this.createUI();
        this.updateView();
    }

    private void createUI() {
        this.setTitle(Messages.getString("CookingInstructionSelectionView.1"));
        this.setTitlePaneText(Messages.getString("CookingInstructionSelectionView.1"));
        this.getContentPanel().setBorder(new EmptyBorder(10, 20, 0, 20));
        JScrollPane scrollPane = new JScrollPane();
        this.table = new JTable();
        this.table.setRowHeight(35);
        scrollPane.setViewportView(this.table);
        this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int index = CookingInstructionSelectionView.this.table.getSelectedRow();
                if (index < 0) {
                    return;
                }
                CookingInstructionTableModel model = (CookingInstructionTableModel)CookingInstructionSelectionView.this.table.getModel();
                CookingInstruction cookingInstruction = (CookingInstruction)model.rowsList.get(index);
                CookingInstructionSelectionView.this.tfCookingInstruction.setText(cookingInstruction.getDescription());
            }
        });
        this.tfCookingInstruction.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {
                CookingInstructionSelectionView.this.doFilter();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                CookingInstructionSelectionView.this.doFilter();
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
        PosButton btnSave = new PosButton(IconFactory.getIcon("save.png"));
        btnSave.setText(POSConstants.SAVE.toUpperCase());
        btnSave.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String instruction = CookingInstructionSelectionView.this.tfCookingInstruction.getText();
                if (instruction == null || instruction.isEmpty()) {
                    POSMessageDialog.showMessage(Application.getPosWindow(), "Instruction cannot be empty.");
                    return;
                }
                CookingInstruction cookingInstruction = new CookingInstruction();
                cookingInstruction.setDescription(instruction);
                CookingInstructionDAO.getInstance().save(cookingInstruction);
                CookingInstructionSelectionView.this.updateView();
                CookingInstructionTableModel model = (CookingInstructionTableModel)CookingInstructionSelectionView.this.table.getModel();
                CookingInstructionSelectionView.this.table.getSelectionModel().addSelectionInterval(model.getRowCount() - 1, model.getRowCount() - 1);
            }
        });
        JPanel contentPanel = new JPanel((LayoutManager)new MigLayout("fill,wrap 1,inset 0"));
        contentPanel.add((Component)scrollPane, "grow");
        contentPanel.add((Component)this.tfCookingInstruction, "h 35!,split 2,grow");
        contentPanel.add((Component)btnSave, "h 35!,w 90!");
        QwertyKeyPad keyPad = new QwertyKeyPad();
        contentPanel.add((Component)((Object)keyPad), "grow");
        this.getContentPanel().add(contentPanel);
    }

    private void doFilter() {
        String text = this.tfCookingInstruction.getText().toLowerCase();
        ArrayList<CookingInstruction> filteredInstructions = new ArrayList<CookingInstruction>();
        for (CookingInstruction i : this.cookingInstructions) {
            String description = i.getDescription().toLowerCase();
            if (!description.contains(text)) continue;
            filteredInstructions.add(i);
        }
        CookingInstructionTableModel model = (CookingInstructionTableModel)this.table.getModel();
        model.rowsList = filteredInstructions;
        model.fireTableDataChanged();
    }

    @Override
    public void doOk() {
        int[] selectedRows = this.table.getSelectedRows();
        if (this.ticketItemCookingInstructions == null) {
            this.ticketItemCookingInstructions = new ArrayList<TicketItemCookingInstruction>(selectedRows.length);
        }
        CookingInstructionTableModel model = (CookingInstructionTableModel)this.table.getModel();
        String inputInstruction = this.tfCookingInstruction.getText();
        if (selectedRows.length == 1) {
            if (inputInstruction == null || inputInstruction.isEmpty()) {
                POSMessageDialog.showMessage(Application.getPosWindow(), "Instruction cannot be empty.");
                return;
            }
            CookingInstruction ci = (CookingInstruction)model.rowsList.get(selectedRows[0]);
            if (!inputInstruction.equals(ci.getDescription())) {
                TicketItemCookingInstruction cookingInstruction = new TicketItemCookingInstruction();
                cookingInstruction.setDescription(inputInstruction);
                this.ticketItemCookingInstructions.add(cookingInstruction);
            } else {
                TicketItemCookingInstruction cookingInstruction = new TicketItemCookingInstruction();
                cookingInstruction.setDescription(ci.getDescription());
                this.ticketItemCookingInstructions.add(cookingInstruction);
            }
        } else if (selectedRows.length == 0) {
            if (inputInstruction == null || inputInstruction.isEmpty()) {
                POSMessageDialog.showMessage(Application.getPosWindow(), "Instruction cannot be empty.");
                return;
            }
            TicketItemCookingInstruction cookingInstruction = new TicketItemCookingInstruction();
            cookingInstruction.setDescription(inputInstruction);
            this.ticketItemCookingInstructions.add(cookingInstruction);
        } else {
            boolean newInstruction = false;
            for (int i = 0; i < selectedRows.length; ++i) {
                CookingInstruction ci = (CookingInstruction)model.rowsList.get(selectedRows[i]);
                TicketItemCookingInstruction cookingInstruction = new TicketItemCookingInstruction();
                cookingInstruction.setDescription(ci.getDescription());
                if (StringUtils.isNotEmpty((CharSequence)inputInstruction) && !ci.getDescription().equals(inputInstruction)) {
                    newInstruction = true;
                }
                this.ticketItemCookingInstructions.add(cookingInstruction);
            }
            if (newInstruction) {
                TicketItemCookingInstruction cookingInstruction = new TicketItemCookingInstruction();
                cookingInstruction.setDescription(inputInstruction);
                this.ticketItemCookingInstructions.add(cookingInstruction);
            }
        }
        this.setCanceled(false);
        this.dispose();
    }

    protected void updateView() {
        this.cookingInstructions = CookingInstructionDAO.getInstance().findAll();
        this.table.setModel(new CookingInstructionTableModel(this.cookingInstructions));
    }

    public List<TicketItemCookingInstruction> getTicketItemCookingInstructions() {
        return this.ticketItemCookingInstructions;
    }

    class CookingInstructionTableModel
    extends AbstractTableModel {
        private final String[] columns = new String[]{Messages.getString("CookingInstructionSelectionView.2")};
        private List<CookingInstruction> rowsList;

        public CookingInstructionTableModel() {
        }

        public CookingInstructionTableModel(List<CookingInstruction> rows) {
            this.rowsList = rows;
        }

        @Override
        public int getRowCount() {
            if (this.rowsList == null) {
                return 0;
            }
            return this.rowsList.size();
        }

        @Override
        public int getColumnCount() {
            return this.columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return this.columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (this.rowsList == null) {
                return null;
            }
            CookingInstruction row = this.rowsList.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return row.getDescription();
                }
            }
            return null;
        }
    }
}

