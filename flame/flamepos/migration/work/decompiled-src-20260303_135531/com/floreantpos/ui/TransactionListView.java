/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.ui;

import com.floreantpos.Messages;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.JXTable;

public class TransactionListView
extends JPanel {
    private JXTable table = new TransactionListTable();
    private TransactionListTableModel tableModel;

    public TransactionListView() {
        this.table.setSortable(true);
        this.tableModel = new TransactionListTableModel();
        this.table.setModel((TableModel)this.tableModel);
        this.table.setRowHeight(PosUIManager.getSize(40));
        this.table.setAutoResizeMode(3);
        this.table.setDefaultRenderer(Object.class, (TableCellRenderer)new PosTableRenderer());
        this.table.setGridColor(Color.LIGHT_GRAY);
        PosScrollPane scrollPane = new PosScrollPane((Component)this.table, 20, 31);
        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        scrollBar.setPreferredSize(PosUIManager.getSize(30, 60));
        this.setLayout(new BorderLayout());
        this.add(scrollPane);
    }

    public void setTransactions(List<PosTransaction> transactions) {
        this.tableModel.setRows(transactions);
    }

    public void addTransaction(PosTransaction transaction) {
        this.tableModel.addItem(transaction);
    }

    public PosTransaction getSelectedTransaction() {
        int selectedRow = this.table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return (PosTransaction)this.tableModel.getRowData(selectedRow);
    }

    public List<PosTransaction> getAllTransactions() {
        return this.tableModel.getRows();
    }

    public List<PosTransaction> getSelectedTransactions() {
        int[] selectedRows = this.table.getSelectedRows();
        ArrayList<PosTransaction> transactions = new ArrayList<PosTransaction>(selectedRows.length);
        for (int i = 0; i < selectedRows.length; ++i) {
            PosTransaction transaction = (PosTransaction)this.tableModel.getRowData(selectedRows[i]);
            transactions.add(transaction);
        }
        return transactions;
    }

    public PosTransaction getFirstSelectedTransaction() {
        List<PosTransaction> selectedTickets = this.getSelectedTransactions();
        if (selectedTickets.size() == 0 || selectedTickets.size() > 1) {
            POSMessageDialog.showMessage(Messages.getString("TransactionListView.7"));
            return null;
        }
        PosTransaction t = selectedTickets.get(0);
        return t;
    }

    public JXTable getTable() {
        return this.table;
    }

    private class TransactionListTableModel
    extends ListTableModel<PosTransaction> {
        public TransactionListTableModel() {
            super(new String[]{Messages.getString("TransactionListView.0"), Messages.getString("TransactionListView.8"), Messages.getString("TransactionListView.2"), Messages.getString("TransactionListView.4"), "TIP OVERAGE", Messages.getString("TransactionListView.5"), Messages.getString("TransactionListView.6")});
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            PosTransaction transaction = (PosTransaction)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return transaction.getCardTransactionId();
                }
                case 1: {
                    return transaction.getTransactionTime();
                }
                case 2: {
                    return transaction.getTicket().getOwner().getFirstName();
                }
                case 3: {
                    return transaction.getTipsAmount();
                }
                case 4: {
                    return transaction.getTipsExceedAmount();
                }
                case 5: {
                    return transaction.getAmount() - transaction.getTipsAmount();
                }
                case 6: {
                    return transaction.getAmount();
                }
            }
            return null;
        }
    }

    private class TransactionListTable
    extends JXTable {
        public TransactionListTable() {
            this.setColumnControlVisible(false);
        }

        public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
            ListSelectionModel selectionModel = this.getSelectionModel();
            boolean selected = selectionModel.isSelectedIndex(rowIndex);
            if (selected) {
                selectionModel.removeSelectionInterval(rowIndex, rowIndex);
            } else {
                selectionModel.addSelectionInterval(rowIndex, rowIndex);
            }
        }
    }
}

