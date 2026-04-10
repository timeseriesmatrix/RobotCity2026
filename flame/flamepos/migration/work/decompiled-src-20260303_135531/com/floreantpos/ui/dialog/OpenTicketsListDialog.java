/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.UserListDialog;
import com.floreantpos.ui.dialog.VoidTicketDialog;
import com.floreantpos.ui.views.order.CashierModeNextActionDialog;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.util.CurrencyUtil;
import com.floreantpos.util.NumberUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class OpenTicketsListDialog
extends POSDialog {
    private List<Ticket> openTickets;
    private OpenTicketListTableModel tableModel;
    private PosButton btnClose;
    private PosButton btnScrollDown;
    private PosButton btnScrollUp;
    private PosButton btnTransferServer;
    private PosButton btnVoid;
    private JScrollPane jScrollPane1;
    private JTable openTicketListTable;
    private TitlePanel titlePanel;
    private TransparentPanel transparentPanel1;
    private TransparentPanel transparentPanel2;
    private TransparentPanel transparentPanel3;
    private TransparentPanel transparentPanel4;
    private DefaultListSelectionModel selectionModel;

    public OpenTicketsListDialog() {
        this.init();
    }

    private void init() {
        this.initComponents();
        this.setTitle(POSConstants.ACTIVE_TICKETS);
        this.titlePanel.setTitle(POSConstants.ACTIVE_TICKETS_BEFORE_DRAWER_RESET);
        TicketDAO dao = new TicketDAO();
        this.openTickets = TerminalConfig.isCashierMode() ? dao.findOpenTicketsForUser(Application.getCurrentUser()) : dao.findOpenTickets();
        this.tableModel = new OpenTicketListTableModel();
        this.openTicketListTable.setModel(this.tableModel);
        this.openTicketListTable.setDefaultRenderer(Object.class, new TicketTableCellRenderer());
        this.openTicketListTable.setRowHeight(40);
        this.selectionModel = new DefaultListSelectionModel();
        this.selectionModel.setSelectionMode(0);
        this.openTicketListTable.setSelectionModel(this.selectionModel);
        if (TerminalConfig.isCashierMode()) {
            this.selectionModel.addListSelectionListener(new ListSelectionListener(){

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    Ticket ticket = (Ticket)OpenTicketsListDialog.this.openTickets.get(OpenTicketsListDialog.this.openTicketListTable.getSelectedRow());
                    ticket = TicketDAO.getInstance().loadFullTicket(ticket.getId());
                    OrderView.getInstance().setCurrentTicket(ticket);
                    OpenTicketsListDialog.this.dispose();
                }
            });
        }
    }

    private void initComponents() {
        this.titlePanel = new TitlePanel();
        this.transparentPanel1 = new TransparentPanel();
        this.transparentPanel3 = new TransparentPanel();
        this.btnClose = new PosButton();
        this.transparentPanel4 = new TransparentPanel();
        this.btnScrollUp = new PosButton();
        this.btnScrollDown = new PosButton();
        this.transparentPanel2 = new TransparentPanel();
        this.jScrollPane1 = new JScrollPane();
        this.openTicketListTable = new JTable();
        this.setDefaultCloseOperation(2);
        this.getContentPane().add((Component)this.titlePanel, "North");
        this.transparentPanel1.setLayout(new BorderLayout());
        this.transparentPanel3.setLayout(new FlowLayout(2));
        this.transparentPanel3.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 5));
        if (!TerminalConfig.isCashierMode()) {
            this.btnVoid = new PosButton();
            this.btnVoid.setText(POSConstants.VOID);
            this.btnVoid.setPreferredSize(new Dimension(100, 50));
            this.btnVoid.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent evt) {
                    OpenTicketsListDialog.this.doVoidTicket(evt);
                }
            });
            this.transparentPanel3.add(this.btnVoid);
            this.btnTransferServer = new PosButton();
            this.btnTransferServer.setText("<html><body><center>TRANSFER<br>SERVER</center></body></html>");
            this.btnTransferServer.setPreferredSize(new Dimension(100, 50));
            this.btnTransferServer.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent evt) {
                    OpenTicketsListDialog.this.doTransferServer(evt);
                }
            });
            this.transparentPanel3.add(this.btnTransferServer);
        }
        this.btnClose.setText(POSConstants.CLOSE);
        this.btnClose.setPreferredSize(new Dimension(100, 50));
        this.btnClose.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                OpenTicketsListDialog.this.doClose(evt);
            }
        });
        this.transparentPanel3.add(this.btnClose);
        this.transparentPanel1.add((Component)this.transparentPanel3, "Center");
        this.transparentPanel4.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
        this.btnScrollUp.setIcon(IconFactory.getIcon("/ui_icons/", "up.png"));
        this.btnScrollUp.setPreferredSize(new Dimension(80, 50));
        this.btnScrollUp.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                OpenTicketsListDialog.this.doScrollUp(evt);
            }
        });
        this.transparentPanel4.add(this.btnScrollUp);
        this.btnScrollDown.setIcon(IconFactory.getIcon("/ui_icons/", "down.png"));
        this.btnScrollDown.setPreferredSize(new Dimension(80, 50));
        this.btnScrollDown.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                OpenTicketsListDialog.this.doScrollDown(evt);
            }
        });
        this.transparentPanel4.add(this.btnScrollDown);
        this.transparentPanel1.add((Component)this.transparentPanel4, "West");
        this.getContentPane().add((Component)this.transparentPanel1, "South");
        this.transparentPanel2.setLayout(new BorderLayout(0, 5));
        this.transparentPanel2.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        this.openTicketListTable.setModel(new DefaultTableModel(new Object[0][], new String[0]));
        this.jScrollPane1.setViewportView(this.openTicketListTable);
        this.transparentPanel2.add((Component)this.jScrollPane1, "Center");
        this.getContentPane().add((Component)this.transparentPanel2, "Center");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((screenSize.width - 646) / 2, (screenSize.height - 435) / 2, 646, 435);
    }

    private void doScrollUp(ActionEvent evt) {
        int selectedRow = this.openTicketListTable.getSelectedRow();
        int rowCount = this.tableModel.getRowCount();
        selectedRow = selectedRow <= 0 ? rowCount - 1 : (selectedRow > rowCount - 1 ? rowCount - 1 : --selectedRow);
        this.openTicketListTable.transferFocus();
        this.selectionModel.setLeadSelectionIndex(selectedRow);
        Rectangle cellRect = this.openTicketListTable.getCellRect(selectedRow, 0, false);
        this.openTicketListTable.scrollRectToVisible(cellRect);
    }

    private void doScrollDown(ActionEvent evt) {
        int selectedRow = this.openTicketListTable.getSelectedRow();
        int rowCount = this.tableModel.getRowCount();
        selectedRow = selectedRow < 0 ? 0 : (selectedRow >= rowCount - 1 ? 0 : ++selectedRow);
        this.openTicketListTable.transferFocus();
        this.selectionModel.setLeadSelectionIndex(selectedRow);
        Rectangle cellRect = this.openTicketListTable.getCellRect(selectedRow, 0, false);
        this.openTicketListTable.scrollRectToVisible(cellRect);
    }

    private Ticket getSelectedTicket() {
        int row = this.openTicketListTable.getSelectedRow();
        if (row < 0) {
            POSMessageDialog.showError(this, POSConstants.SELECT_TICKET);
            return null;
        }
        return this.openTickets.get(row);
    }

    private void doClose(ActionEvent evt) {
        this.canceled = false;
        this.dispose();
        if (TerminalConfig.isCashierMode()) {
            String message = Messages.getString("OpenTicketsListDialog.0");
            CashierModeNextActionDialog dialog = new CashierModeNextActionDialog(message);
            dialog.open();
        }
    }

    private void doTransferServer(ActionEvent evt) {
        try {
            Ticket ticket = this.getSelectedTicket();
            if (ticket != null) {
                UserListDialog dialog = new UserListDialog();
                dialog.open();
                if (!dialog.isCanceled()) {
                    User selectedUser = dialog.getSelectedUser();
                    if (!ticket.getOwner().equals(selectedUser)) {
                        ticket.setOwner(selectedUser);
                        TicketDAO.getInstance().update(ticket);
                        this.openTicketListTable.repaint();
                    }
                }
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE);
        }
    }

    private void doPrintTicket(ActionEvent evt) {
        JOptionPane.showMessageDialog(this, Messages.getString("OpenTicketsListDialog.6"));
    }

    private void doVoidTicket(ActionEvent evt) {
        Ticket ticket = this.getSelectedTicket();
        if (ticket == null) {
            return;
        }
        Ticket ticketToVoid = TicketDAO.getInstance().loadFullTicket(ticket.getId());
        VoidTicketDialog dialog = new VoidTicketDialog();
        dialog.setTicket(ticketToVoid);
        dialog.open();
        if (!dialog.isCanceled()) {
            this.tableModel.removeTicket(ticketToVoid);
        }
    }

    class TicketTableCellRenderer
    extends DefaultTableCellRenderer {
        Font font = this.getFont().deriveFont(1, 12.0f);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd-yy hh:mm a");
        String currencySymbol = CurrencyUtil.getCurrencySymbol();

        TicketTableCellRenderer() {
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setFont(this.font);
            if (value instanceof Date) {
                label.setText(this.dateFormat.format(value));
                label.setHorizontalAlignment(0);
            } else if (value instanceof Double) {
                label.setText(this.currencySymbol + NumberUtil.formatNumber((double)((Double)value)));
                label.setHorizontalAlignment(4);
            } else {
                label.setHorizontalAlignment(2);
            }
            return label;
        }
    }

    class OpenTicketListTableModel
    extends AbstractTableModel {
        OpenTicketListTableModel() {
        }

        @Override
        public int getRowCount() {
            if (OpenTicketsListDialog.this.openTickets == null) {
                return 0;
            }
            return OpenTicketsListDialog.this.openTickets.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0: {
                    return POSConstants.TICKET_ID;
                }
                case 1: {
                    return POSConstants.SERVER;
                }
                case 2: {
                    return POSConstants.DATE_TIME;
                }
                case 3: {
                    return POSConstants.TOTAL;
                }
            }
            return null;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (OpenTicketsListDialog.this.openTickets == null) {
                return null;
            }
            Ticket ticket = (Ticket)OpenTicketsListDialog.this.openTickets.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return (int)ticket.getId();
                }
                case 1: {
                    return ticket.getOwner().toString();
                }
                case 2: {
                    return ticket.getCreateDate();
                }
                case 3: {
                    return (double)ticket.getTotalAmount();
                }
            }
            return null;
        }

        void removeTicket(Ticket ticket) {
            OpenTicketsListDialog.this.openTickets.remove(ticket);
            this.fireTableDataChanged();
        }
    }
}

