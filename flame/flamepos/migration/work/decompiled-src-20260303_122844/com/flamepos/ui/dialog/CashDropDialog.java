/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import com.floreantpos.model.CashDropTransaction;
import com.floreantpos.model.PaymentType;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.dao.CashDropTransactionDAO;
import com.floreantpos.swing.POSLabel;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.CurrencyUtil;
import com.floreantpos.util.NumberUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;

public class CashDropDialog
extends POSDialog {
    private List<CashDropTransaction> cashDropList;
    private DefaultListSelectionModel selectionModel;
    private CashDropTableModel tableModel;
    private String currencySymbol;
    private PosButton btnClose;
    private PosButton btnDeleteSelected;
    private PosButton btnDown;
    private PosButton btnNewCashDrop;
    private PosButton btnUp;
    private JScrollPane jScrollPane1;
    private JSeparator jSeparator1;
    private POSLabel lblActiveCashDrop;
    private TransparentPanel midPanel;
    private JTable tableCashDrops;
    private TitlePanel titlePanel1;
    private TransparentPanel transparentPanel1;
    private TransparentPanel transparentPanel2;
    private TransparentPanel transparentPanel3;
    private Terminal terminal;

    public CashDropDialog() {
        super((Frame)Application.getPosWindow(), true);
        this.initComponents();
        this.terminal = Application.getInstance().getTerminal();
        this.currencySymbol = CurrencyUtil.getCurrencySymbol();
        this.lblActiveCashDrop.setText("");
        TitledBorder titledBorder = new TitledBorder(Messages.getString("CashDropDialog.1") + this.terminal.getName());
        titledBorder.setTitleJustification(2);
        this.midPanel.setBorder(titledBorder);
        this.selectionModel = new DefaultListSelectionModel();
        this.selectionModel.setSelectionMode(0);
        this.tableCashDrops.setSelectionModel(this.selectionModel);
        this.tableCashDrops.setTableHeader(null);
        this.tableCashDrops.setDefaultRenderer(Object.class, new TableRenderer());
        this.tableModel = new CashDropTableModel();
        this.tableCashDrops.setModel(this.tableModel);
        this.setTitle(Messages.getString("CashDropDialog.2"));
    }

    public void initDate() throws Exception {
        CashDropTransactionDAO dao = new CashDropTransactionDAO();
        this.cashDropList = dao.findUnsettled(this.terminal);
        this.tableModel.fireTableDataChanged();
    }

    private void initComponents() {
        this.titlePanel1 = new TitlePanel();
        this.transparentPanel1 = new TransparentPanel();
        this.jSeparator1 = new JSeparator();
        this.transparentPanel3 = new TransparentPanel((LayoutManager)new MigLayout("al center", "sg, fill", ""));
        this.btnNewCashDrop = new PosButton();
        this.btnDeleteSelected = new PosButton();
        this.btnClose = new PosButton();
        this.midPanel = new TransparentPanel();
        this.transparentPanel2 = new TransparentPanel();
        this.btnUp = new PosButton();
        this.btnDown = new PosButton();
        this.lblActiveCashDrop = new POSLabel();
        this.jScrollPane1 = new JScrollPane();
        this.tableCashDrops = new JTable();
        this.tableCashDrops.setRowHeight(PosUIManager.getSize(40));
        this.getContentPane().setLayout(new BorderLayout(5, 5));
        this.titlePanel1.setTitle(Messages.getString("CashDropDialog.3"));
        this.getContentPane().add((Component)this.titlePanel1, "North");
        this.transparentPanel1.setLayout(new BorderLayout(5, 5));
        this.transparentPanel1.add((Component)this.jSeparator1, "North");
        this.btnNewCashDrop.setText(Messages.getString("CashDropDialog.4"));
        this.btnNewCashDrop.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                CashDropDialog.this.btnNewCashDropActionPerformed(evt);
            }
        });
        this.transparentPanel3.add(this.btnNewCashDrop);
        this.btnDeleteSelected.setText(Messages.getString("CashDropDialog.5"));
        this.btnDeleteSelected.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                CashDropDialog.this.btnDeleteSelectedActionPerformed(evt);
            }
        });
        this.transparentPanel3.add(this.btnDeleteSelected);
        this.btnClose.setText(Messages.getString("CashDropDialog.6"));
        this.btnClose.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                CashDropDialog.this.btnCloseActionPerformed(evt);
            }
        });
        this.transparentPanel3.add(this.btnClose);
        this.transparentPanel1.add((Component)this.transparentPanel3, "Center");
        this.getContentPane().add((Component)this.transparentPanel1, "South");
        this.midPanel.setLayout(new BorderLayout(1, 5));
        this.transparentPanel2.setLayout(new GridLayout(0, 1, 2, 2));
        this.transparentPanel2.setBorder(BorderFactory.createEmptyBorder(5, 1, 10, 5));
        this.btnUp.setIcon(IconFactory.getIcon("/ui_icons/", "up.png"));
        this.btnUp.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                CashDropDialog.this.doScrollUp(evt);
            }
        });
        this.transparentPanel2.add(this.btnUp);
        this.btnDown.setIcon(IconFactory.getIcon("/ui_icons/", "down.png"));
        this.btnDown.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                CashDropDialog.this.doScrollDown(evt);
            }
        });
        this.transparentPanel2.add(this.btnDown);
        this.midPanel.add((Component)this.transparentPanel2, "East");
        this.lblActiveCashDrop.setHorizontalAlignment(0);
        this.lblActiveCashDrop.setText(Messages.getString("CashDropDialog.11"));
        this.midPanel.add((Component)this.lblActiveCashDrop, "North");
        this.tableCashDrops.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}}, new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}));
        this.jScrollPane1.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(5, 10, 10, 10), this.jScrollPane1.getBorder()));
        this.jScrollPane1.setViewportView(this.tableCashDrops);
        this.midPanel.add((Component)this.jScrollPane1, "Center");
        this.getContentPane().add((Component)this.midPanel, "Center");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = PosUIManager.getSize(606);
        int height = PosUIManager.getSize(472);
        this.setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
    }

    private void doScrollDown(ActionEvent evt) {
        if (this.cashDropList == null) {
            return;
        }
        int selectedRow = this.tableCashDrops.getSelectedRow();
        selectedRow = selectedRow < 0 ? 0 : (selectedRow >= this.cashDropList.size() - 1 ? 0 : ++selectedRow);
        this.selectionModel.setLeadSelectionIndex(selectedRow);
        Rectangle cellRect = this.tableCashDrops.getCellRect(selectedRow, 0, false);
        this.tableCashDrops.scrollRectToVisible(cellRect);
    }

    private void doScrollUp(ActionEvent evt) {
        if (this.cashDropList == null) {
            return;
        }
        int selectedRow = this.tableCashDrops.getSelectedRow();
        selectedRow = selectedRow <= 0 ? this.cashDropList.size() - 1 : (selectedRow > this.cashDropList.size() - 1 ? this.cashDropList.size() - 1 : --selectedRow);
        this.selectionModel.setLeadSelectionIndex(selectedRow);
        Rectangle cellRect = this.tableCashDrops.getCellRect(selectedRow, 0, false);
        this.tableCashDrops.scrollRectToVisible(cellRect);
    }

    private void btnCloseActionPerformed(ActionEvent evt) {
        this.dispose();
    }

    private void btnDeleteSelectedActionPerformed(ActionEvent evt) {
        try {
            int index = this.tableCashDrops.getSelectedRow();
            if (index >= 0) {
                CashDropTransaction transaction = this.cashDropList.get(index);
                CashDropTransactionDAO dao = new CashDropTransactionDAO();
                dao.deleteCashDrop(transaction, Application.getInstance().refreshAndGetTerminal());
                this.tableModel.removeCashDrop(transaction);
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("CashDropDialog.16"), e);
        }
    }

    private void btnNewCashDropActionPerformed(ActionEvent evt) {
        try {
            NumberSelectionDialog2 dialog = new NumberSelectionDialog2();
            dialog.setTitle(Messages.getString("CashDropDialog.17"));
            dialog.setFloatingPoint(true);
            dialog.pack();
            dialog.open();
            if (!dialog.isCanceled()) {
                double amount = dialog.getValue();
                CashDropTransaction transaction = new CashDropTransaction();
                transaction.setDrawerResetted(false);
                transaction.setTerminal(Application.getInstance().getTerminal());
                transaction.setUser(Application.getCurrentUser());
                transaction.setTransactionTime(new Date());
                transaction.setAmount(amount);
                transaction.setPaymentType(PaymentType.CASH.toString());
                CashDropTransactionDAO dao = new CashDropTransactionDAO();
                dao.saveNewCashDrop(transaction, Application.getInstance().getTerminal());
                this.tableModel.addCashDrop(transaction);
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("CashDropDialog.18"), e);
        }
    }

    class TableRenderer
    extends DefaultTableCellRenderer {
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
        Font font = this.getFont().deriveFont(1, 14.0f);
        private JCheckBox checkBox = new JCheckBox();

        public TableRenderer() {
            this.checkBox.setHorizontalAlignment(0);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String string;
            if (value instanceof Boolean) {
                this.checkBox.setSelected((Boolean)value);
                if (isSelected) {
                    this.checkBox.setBackground(table.getSelectionBackground());
                } else {
                    this.checkBox.setBackground(table.getBackground());
                }
                return this.checkBox;
            }
            JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setFont(this.font);
            if (value instanceof Date) {
                string = this.dateFormat.format(value);
                label.setText(string);
                label.setHorizontalAlignment(4);
            }
            if (value instanceof Double) {
                string = NumberUtil.formatNumber((double)((Double)value));
                label.setText(CashDropDialog.this.currencySymbol + string);
                label.setHorizontalAlignment(4);
            } else {
                label.setHorizontalAlignment(2);
            }
            return label;
        }
    }

    class CashDropTableModel
    extends AbstractTableModel {
        CashDropTableModel() {
        }

        @Override
        public int getRowCount() {
            if (CashDropDialog.this.cashDropList == null) {
                return 0;
            }
            int size = CashDropDialog.this.cashDropList.size();
            return size;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        public void addCashDrop(int index, CashDropTransaction t) {
            if (CashDropDialog.this.cashDropList == null) {
                return;
            }
            CashDropDialog.this.cashDropList.add(index, t);
            this.fireTableRowsInserted(index, index);
            Rectangle cellRect = CashDropDialog.this.tableCashDrops.getCellRect(index, 0, false);
            CashDropDialog.this.tableCashDrops.scrollRectToVisible(cellRect);
            CashDropDialog.this.selectionModel.setLeadSelectionIndex(index);
        }

        public void addCashDrop(CashDropTransaction t) {
            int size = CashDropDialog.this.cashDropList.size();
            CashDropDialog.this.cashDropList.add(t);
            this.fireTableRowsInserted(size, size);
            Rectangle cellRect = CashDropDialog.this.tableCashDrops.getCellRect(size, 0, false);
            CashDropDialog.this.tableCashDrops.scrollRectToVisible(cellRect);
            CashDropDialog.this.selectionModel.setLeadSelectionIndex(size);
        }

        public void removeCashDrop(CashDropTransaction t) {
            int index = CashDropDialog.this.cashDropList.indexOf(t);
            if (index >= 0) {
                CashDropDialog.this.cashDropList.remove(index);
                this.fireTableRowsDeleted(index, index);
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (CashDropDialog.this.cashDropList == null) {
                return "";
            }
            CashDropTransaction t = (CashDropTransaction)CashDropDialog.this.cashDropList.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return t.getTransactionTime();
                }
                case 1: {
                    return (double)t.getAmount();
                }
            }
            return "";
        }
    }
}

