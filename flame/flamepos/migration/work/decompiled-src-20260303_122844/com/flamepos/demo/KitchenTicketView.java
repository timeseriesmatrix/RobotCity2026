/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 */
package com.floreantpos.demo;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.demo.KitchenTicketStatusSelector;
import com.floreantpos.model.KitchenTicket;
import com.floreantpos.model.KitchenTicketItem;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.dao.KitchenTicketDAO;
import com.floreantpos.model.dao.KitchenTicketItemDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.ButtonColumn;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TimerWatch;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import net.miginfocom.swing.MigLayout;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class KitchenTicketView
extends JPanel {
    KitchenTicket kitchenTicket;
    JLabel ticketId = new JLabel();
    KitchenTicketTableModel tableModel;
    JTable table;
    KitchenTicketStatusSelector statusSelector;
    private TimerWatch timerWatch;
    private JScrollPane scrollPane;
    private JPanel headerPanel;
    private JLabel ticketInfo;
    private JLabel tableInfo;
    private JLabel serverInfo;

    public KitchenTicketView(KitchenTicket ticket) {
        this.kitchenTicket = ticket;
        this.setLayout(new BorderLayout(1, 1));
        this.createHeader(ticket);
        this.createTable(ticket);
        this.createButtonPanel();
        this.statusSelector = new KitchenTicketStatusSelector((Frame)SwingUtilities.getWindowAncestor(this), ticket);
        this.statusSelector.pack();
        this.setPreferredSize(PosUIManager.getSize(350, 240));
        this.timerWatch.start();
        this.addAncestorListener(new AncestorListener(){

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                KitchenTicketView.this.timerWatch.stop();
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }

            @Override
            public void ancestorAdded(AncestorEvent event) {
            }
        });
    }

    public void stopTimer() {
        this.timerWatch.stop();
    }

    private void createHeader(KitchenTicket ticket) {
        String printerName = ticket.getPrinters().toString();
        this.ticketInfo = new JLabel("Ticket# " + ticket.getTicketId() + "-" + ticket.getSequenceNumber() + " " + printerName + "");
        this.tableInfo = new JLabel();
        if (ticket.getTableNumbers() != null && ticket.getTableNumbers().size() > 0) {
            String tableNumbers = ticket.getTableNumbers().toString();
            tableNumbers = tableNumbers.replace("[", "").replace("]", "");
            this.tableInfo.setText("Table# " + tableNumbers);
        }
        this.serverInfo = new JLabel();
        if (ticket.getServerName() != null) {
            this.serverInfo.setText("Server: " + ticket.getServerName());
        }
        Font font = this.getFont().deriveFont(1, 13.0f);
        this.ticketInfo.setFont(font);
        this.tableInfo.setFont(font);
        this.serverInfo.setFont(font);
        this.timerWatch = new TimerWatch(ticket.getCreateDate());
        this.headerPanel = new JPanel((LayoutManager)new MigLayout("fill", "sg, fill", ""));
        this.headerPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
        this.headerPanel.add((Component)this.ticketInfo, "split 2");
        this.headerPanel.add((Component)this.timerWatch, "right,wrap, span");
        this.headerPanel.add((Component)this.tableInfo, "split 2, grow");
        this.headerPanel.add((Component)this.serverInfo, "right,span");
        this.add((Component)this.headerPanel, "North");
    }

    private void createTable(KitchenTicket ticket) {
        this.tableModel = new KitchenTicketTableModel(ticket.getTicketItems());
        this.table = new JTable(this.tableModel);
        this.table.setRowSelectionAllowed(false);
        this.table.setCellSelectionEnabled(false);
        this.table.setRowHeight(30);
        this.table.setTableHeader(null);
        this.table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                KitchenTicketItem ticketItem = (KitchenTicketItem)KitchenTicketView.this.tableModel.getRowData(row);
                if (ticketItem != null && ticketItem.getStatus() != null) {
                    if (ticketItem.getStatus().equalsIgnoreCase(KitchenTicket.KitchenTicketStatus.DONE.name())) {
                        rendererComponent.setBackground(Color.green);
                    } else if (ticketItem.getStatus().equalsIgnoreCase(KitchenTicket.KitchenTicketStatus.VOID.name())) {
                        rendererComponent.setBackground(new Color(128, 0, 128));
                    } else {
                        rendererComponent.setBackground(Color.white);
                    }
                }
                if (column == 1 && ticketItem.getQuantity() <= 0) {
                    return new JLabel();
                }
                KitchenTicketView.this.updateHeaderView();
                return rendererComponent;
            }
        });
        this.resizeTableColumns();
        AbstractAction action = new AbstractAction(){

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = Integer.parseInt(e.getActionCommand());
                KitchenTicketItem ticketItem = (KitchenTicketItem)KitchenTicketView.this.tableModel.getRowData(row);
                if (!ticketItem.isCookable().booleanValue()) {
                    return;
                }
                KitchenTicketView.this.statusSelector.setTicketItem(ticketItem);
                KitchenTicketView.this.statusSelector.setLocationRelativeTo(KitchenTicketView.this);
                KitchenTicketView.this.statusSelector.setVisible(true);
                KitchenTicketView.this.table.repaint();
            }
        };
        new ButtonColumn(this.table, action, 2){

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                KitchenTicketItem ticketItem = (KitchenTicketItem)KitchenTicketView.this.tableModel.getRowData(row);
                if (ticketItem.getQuantity() <= 0) {
                    return new JLabel();
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                KitchenTicketItem ticketItem = (KitchenTicketItem)KitchenTicketView.this.tableModel.getRowData(row);
                if (ticketItem.getQuantity() <= 0) {
                    return new JLabel();
                }
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }
        };
        this.scrollPane = new JScrollPane(this.table);
        this.add(this.scrollPane);
    }

    private void updateHeaderView() {
        this.headerPanel.setBackground(this.timerWatch.backColor);
        this.ticketInfo.setForeground(this.timerWatch.textColor);
        this.tableInfo.setForeground(this.timerWatch.textColor);
        this.serverInfo.setForeground(this.timerWatch.textColor);
    }

    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        PosButton btnVoid = new PosButton(Messages.getString("KitchenTicketView.12"));
        btnVoid.setPreferredSize(PosUIManager.getSize(100, 40));
        btnVoid.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                KitchenTicketView.this.closeTicket(KitchenTicket.KitchenTicketStatus.VOID);
            }
        });
        PosButton btnDone = new PosButton(POSConstants.BUMP);
        btnDone.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                KitchenTicketView.this.closeTicket(KitchenTicket.KitchenTicketStatus.DONE);
            }
        });
        btnDone.setPreferredSize(PosUIManager.getSize(100, 40));
        buttonPanel.add(btnDone);
        this.add((Component)buttonPanel, "South");
    }

    private void resizeTableColumns() {
        this.table.setAutoResizeMode(4);
        this.setColumnWidth(1, PosUIManager.getSize(40));
        this.setColumnWidth(2, PosUIManager.getSize(50));
    }

    private void setColumnWidth(int columnNumber, int width) {
        TableColumn column = this.table.getColumnModel().getColumn(columnNumber);
        column.setPreferredWidth(width);
        column.setMaxWidth(width);
        column.setMinWidth(width);
    }

    public KitchenTicket getTicket() {
        return this.kitchenTicket;
    }

    public void setTicket(KitchenTicket ticket) {
        this.kitchenTicket = ticket;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void closeTicket(KitchenTicket.KitchenTicketStatus status) {
        try {
            this.stopTimer();
            this.kitchenTicket.setStatus(status.name());
            this.kitchenTicket.setClosingDate(new Date());
            Ticket parentTicket = TicketDAO.getInstance().load(this.kitchenTicket.getTicketId());
            Transaction tx = null;
            try (Session session = null;){
                session = KitchenTicketItemDAO.getInstance().createNewSession();
                tx = session.beginTransaction();
                for (KitchenTicketItem kitchenTicketItem : this.kitchenTicket.getTicketItems()) {
                    kitchenTicketItem.setStatus(status.name());
                    int itemCount = kitchenTicketItem.getQuantity();
                    for (TicketItem item : parentTicket.getTicketItems()) {
                        if (kitchenTicketItem.getMenuItemCode() == null || !kitchenTicketItem.getMenuItemCode().equals(item.getItemCode()) || item.getStatus() != null && item.getStatus().equals("Ready")) continue;
                        if (itemCount == 0) break;
                        if (status.equals((Object)KitchenTicket.KitchenTicketStatus.DONE)) {
                            item.setStatus("Ready");
                        } else {
                            item.setStatus("Void");
                        }
                        itemCount -= item.getItemCount().intValue();
                    }
                    session.saveOrUpdate((Object)parentTicket);
                    session.saveOrUpdate((Object)kitchenTicketItem);
                }
                tx.commit();
            }
            KitchenTicketDAO.getInstance().saveOrUpdate(this.kitchenTicket);
            Container parent = this.getParent();
            parent.remove(this);
            parent.revalidate();
            parent.repaint();
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, e.getMessage(), e);
        }
    }

    class KitchenTicketTableModel
    extends ListTableModel<KitchenTicketItem> {
        KitchenTicketTableModel(List<KitchenTicketItem> list) {
            super(new String[]{Messages.getString("KitchenTicketView.13"), Messages.getString("KitchenTicketView.14"), ""}, list);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            KitchenTicketItem ticketItem = (KitchenTicketItem)this.getRowData(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return ticketItem.getMenuItemName();
                }
                case 1: {
                    if (ticketItem.isFractionalUnit().booleanValue()) {
                        double itemQuantity = ticketItem.getFractionalQuantity();
                        if (itemQuantity % 1.0 == 0.0) {
                            return String.valueOf((int)itemQuantity) + ticketItem.getUnitName();
                        }
                        return String.valueOf(itemQuantity) + ticketItem.getUnitName();
                    }
                    return String.valueOf(ticketItem.getQuantity());
                }
                case 2: {
                    return POSConstants.BUMP;
                }
            }
            return null;
        }
    }
}

