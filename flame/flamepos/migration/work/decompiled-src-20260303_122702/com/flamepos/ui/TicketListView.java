/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jdesktop.swingx.JXTable
 *  org.jdesktop.swingx.table.ColumnControlButton
 *  org.jdesktop.swingx.table.TableColumnExt
 *  org.jdesktop.swingx.table.TableColumnModelExt
 */
package com.floreantpos.ui;

import com.floreantpos.ITicketList;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.DataUpdateInfo;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.DataUpdateInfoDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PaginatedTableModel;
import com.floreantpos.swing.PosBlinkButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.OrderFilterPanel;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.TicketListUpdateListener;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.PosGuiUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;

public class TicketListView
extends JPanel
implements ITicketList {
    private OrderFilterPanel orderFiltersPanel;
    private JXTable table;
    private TicketListTableModel tableModel;
    private PosBlinkButton btnRefresh;
    private PosButton btnPrevious;
    private PosButton btnNext;
    private TableColumnModelExt columnModel;
    private ArrayList<TicketListUpdateListener> ticketUpdateListenerList = new ArrayList();
    private boolean isCustomerHistoryOpen;
    private Date lastUpdateTime;
    private Timer lastUpateCheckTimer = new Timer(5000, new TaskLastUpdateCheck());
    private Integer customerId;
    private POSToggleButton btnOrderFilters;

    public TicketListView() {
        this.setLayout(new BorderLayout());
        this.orderFiltersPanel = new OrderFilterPanel(this);
        this.add((Component)((Object)this.orderFiltersPanel), "North");
        this.createTicketTable();
        this.updateTicketList();
        this.updateButtonStatus();
    }

    public TicketListView(Integer customerId, boolean customerHistory) {
        this.isCustomerHistoryOpen = customerHistory;
        this.setLayout(new BorderLayout());
        this.createTicketTable();
        this.updateTicketList();
        this.updateButtonStatus();
    }

    private void createTicketTable() {
        this.table = new JXTable();
        this.table.setSortable(true);
        this.table.setSelectionMode(0);
        this.table.setColumnControlVisible(true);
        this.tableModel = new TicketListTableModel();
        this.table.setModel((TableModel)this.tableModel);
        this.tableModel.setPageSize(25);
        this.table.setRowHeight(PosUIManager.getSize(60));
        this.table.setAutoResizeMode(3);
        this.table.setDefaultRenderer(Object.class, (TableCellRenderer)new PosTableRenderer());
        this.table.setGridColor(Color.LIGHT_GRAY);
        this.table.getTableHeader().setPreferredSize(new Dimension(100, PosUIManager.getSize(40)));
        this.columnModel = (TableColumnModelExt)this.table.getColumnModel();
        this.columnModel.getColumn(0).setPreferredWidth(30);
        this.columnModel.getColumn(1).setPreferredWidth(20);
        this.columnModel.getColumn(2).setPreferredWidth(100);
        this.columnModel.getColumn(3).setPreferredWidth(100);
        if (this.isCustomerHistoryOpen) {
            this.columnModel.getColumnExt(1).setVisible(false);
            this.columnModel.getColumnExt(1).setVisible(false);
            this.columnModel.getColumnExt(5).setVisible(false);
            this.columnModel.getColumnExt(7).setVisible(false);
            this.createScrollPane();
            return;
        }
        this.restoreTableColumnsVisibility();
        this.addTableColumnListener();
        this.createScrollPane();
    }

    private void addTableColumnListener() {
        this.columnModel.addColumnModelListener(new TableColumnModelListener(){

            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
            }

            @Override
            public void columnRemoved(TableColumnModelEvent e) {
                TicketListView.this.saveHiddenColumns();
            }

            @Override
            public void columnMoved(TableColumnModelEvent e) {
            }

            @Override
            public void columnMarginChanged(ChangeEvent e) {
            }

            @Override
            public void columnAdded(TableColumnModelEvent e) {
                TicketListView.this.saveHiddenColumns();
            }
        });
    }

    private void restoreTableColumnsVisibility() {
        String recordedSelectedColumns = TerminalConfig.getTicketListViewHiddenColumns();
        TableColumnModelExt columnModel = (TableColumnModelExt)this.table.getColumnModel();
        if (recordedSelectedColumns.isEmpty()) {
            return;
        }
        String[] str = recordedSelectedColumns.split("\\*");
        for (int i = 0; i < str.length; ++i) {
            Integer columnIndex = Integer.parseInt(str[i]);
            columnModel.getColumnExt(columnIndex - i).setVisible(false);
        }
    }

    private void saveHiddenColumns() {
        List columns = this.columnModel.getColumns(true);
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (TableColumn tableColumn : columns) {
            TableColumnExt c = (TableColumnExt)tableColumn;
            if (c.isVisible()) continue;
            indices.add(c.getModelIndex());
        }
        this.saveTableColumnsVisibility(indices);
    }

    private void createScrollPane() {
        if (!this.isCustomerHistoryOpen) {
            this.btnOrderFilters = new POSToggleButton();
            this.btnOrderFilters.setText("<html>" + Messages.getString("SwitchboardView.2") + "</html>");
        }
        this.btnRefresh = new PosBlinkButton(Messages.getString("TicketListView.3"));
        this.btnPrevious = new PosButton(Messages.getString("TicketListView.4"));
        this.btnNext = new PosButton(Messages.getString("TicketListView.5"));
        this.createActionHandlers();
        PosScrollPane scrollPane = new PosScrollPane((Component)this.table, 20, 31);
        int height = PosUIManager.getSize(40);
        JPanel topButtonPanel = new JPanel((LayoutManager)new MigLayout("ins 0", "grow", ""));
        ColumnControlButton controlButton = new ColumnControlButton(this.table);
        if (!this.isCustomerHistoryOpen) {
            topButtonPanel.add((Component)controlButton, "h " + height + "!, grow, wrap");
        }
        topButtonPanel.add((Component)this.btnRefresh, "h " + height + "!, grow, wrap");
        topButtonPanel.add((Component)this.btnPrevious, "h " + height + "!, grow, wrap");
        JPanel downButtonPanel = new JPanel((LayoutManager)new MigLayout("ins 0", "grow", ""));
        downButtonPanel.add((Component)this.btnNext, "h " + height + "!, grow, wrap");
        if (!this.isCustomerHistoryOpen) {
            downButtonPanel.add((Component)this.btnOrderFilters, "h " + height + "!, grow, wrap");
        }
        JPanel tableButtonPanel = new JPanel(new BorderLayout());
        tableButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        tableButtonPanel.setPreferredSize(new Dimension(PosUIManager.getSize(80), 0));
        tableButtonPanel.add((Component)topButtonPanel, "North");
        tableButtonPanel.add((Component)downButtonPanel, "South");
        tableButtonPanel.add(scrollPane.getVerticalScrollBar());
        this.add(scrollPane);
        this.add((Component)tableButtonPanel, "East");
    }

    public void createActionHandlers() {
        this.btnPrevious.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (TicketListView.this.tableModel.hasPrevious()) {
                    TicketListView.this.tableModel.setCurrentRowIndex(TicketListView.this.tableModel.getPreviousRowIndex());
                    TicketDAO.getInstance().loadTickets(TicketListView.this.tableModel);
                }
                TicketListView.this.updateButtonStatus();
            }
        });
        this.btnNext.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (TicketListView.this.tableModel.hasNext()) {
                    TicketListView.this.tableModel.setCurrentRowIndex(TicketListView.this.tableModel.getNextRowIndex());
                    TicketDAO.getInstance().loadTickets(TicketListView.this.tableModel);
                }
                TicketListView.this.updateButtonStatus();
            }
        });
        this.btnRefresh.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                TicketListView.this.getTableModel().setCurrentRowIndex(0);
                if (TicketListView.this.customerId != null) {
                    TicketListView.this.updateCustomerTicketList(TicketListView.this.customerId);
                } else {
                    TicketListView.this.updateTicketList();
                }
                TicketListView.this.updateButtonStatus();
            }
        });
        if (!this.isCustomerHistoryOpen) {
            this.btnOrderFilters.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    TicketListView.this.orderFiltersPanel.setCollapsed(!TicketListView.this.orderFiltersPanel.isCollapsed());
                }
            });
        }
    }

    public void updateButtonStatus() {
        this.btnNext.setEnabled(this.tableModel.hasNext());
        this.btnPrevious.setEnabled(this.tableModel.hasPrevious());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void updateTicketList() {
        this.lastUpateCheckTimer.stop();
        try {
            Application.getPosWindow().setGlassPaneVisible(true);
            TicketListTableModel ticketListTableModel = this.getTableModel();
            ticketListTableModel.setCurrentRowIndex(0);
            ticketListTableModel.setNumRows(TicketDAO.getInstance().getNumTickets());
            TicketDAO.getInstance().loadTickets(ticketListTableModel);
            this.btnRefresh.setBlinking(false);
            this.updateButtonStatus();
            for (int i = 0; i < this.ticketUpdateListenerList.size(); ++i) {
                TicketListUpdateListener listener = this.ticketUpdateListenerList.get(i);
                listener.ticketListUpdated();
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, Messages.getString("SwitchboardView.19"), e);
        }
        finally {
            Application.getPosWindow().setGlassPaneVisible(false);
        }
        try {
            DataUpdateInfo lastUpdateInfo = DataUpdateInfoDAO.getLastUpdateInfo();
            if (lastUpdateInfo != null) {
                this.lastUpdateTime = new Date(lastUpdateInfo.getLastUpdateTime().getTime());
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, Messages.getString("SwitchboardView.20"), e);
        }
        this.lastUpateCheckTimer.restart();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void updateCustomerTicketList(Integer memberId) {
        this.lastUpateCheckTimer.stop();
        this.customerId = memberId;
        try {
            Application.getPosWindow().setGlassPaneVisible(true);
            TicketListTableModel ticketListTableModel = this.getTableModel();
            List<Ticket> tickets = TicketDAO.getInstance().findCustomerTickets(memberId, ticketListTableModel);
            this.setTickets(tickets);
            this.btnRefresh.setBlinking(false);
            for (int i = 0; i < this.ticketUpdateListenerList.size(); ++i) {
                TicketListUpdateListener listener = this.ticketUpdateListenerList.get(i);
                listener.ticketListUpdated();
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, Messages.getString("SwitchboardView.19"), e);
        }
        finally {
            Application.getPosWindow().setGlassPaneVisible(false);
        }
        try {
            DataUpdateInfo lastUpdateInfo = DataUpdateInfoDAO.getLastUpdateInfo();
            if (lastUpdateInfo != null) {
                this.lastUpdateTime = new Date(lastUpdateInfo.getLastUpdateTime().getTime());
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, Messages.getString("SwitchboardView.20"), e);
        }
        this.lastUpateCheckTimer.restart();
    }

    public void addTicketListUpateListener(TicketListUpdateListener l) {
        this.ticketUpdateListenerList.add(l);
    }

    public void setTickets(List<Ticket> tickets) {
        this.tableModel.setRows(tickets);
    }

    public void addTicket(Ticket ticket) {
        this.tableModel.addItem(ticket);
    }

    @Override
    public Ticket getSelectedTicket() {
        int selectedRow = this.table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return (Ticket)this.tableModel.getRowData(this.table.convertRowIndexToModel(selectedRow));
    }

    public List<Ticket> getSelectedTickets() {
        int[] selectedRows = this.table.getSelectedRows();
        ArrayList<Ticket> tickets = new ArrayList<Ticket>(selectedRows.length);
        for (int i = 0; i < selectedRows.length; ++i) {
            Ticket ticket = (Ticket)this.tableModel.getRowData(this.table.convertRowIndexToModel(selectedRows[i]));
            tickets.add(ticket);
        }
        return tickets;
    }

    public Ticket getFirstSelectedTicket() {
        List<Ticket> selectedTickets = this.getSelectedTickets();
        if (selectedTickets.size() == 0 || selectedTickets.size() > 1) {
            POSMessageDialog.showMessage(Messages.getString("TicketListView.14"));
            return null;
        }
        Ticket ticket = selectedTickets.get(0);
        return ticket;
    }

    public int getFirstSelectedTicketId() {
        Ticket ticket = this.getFirstSelectedTicket();
        if (ticket == null) {
            return -1;
        }
        return ticket.getId();
    }

    public JXTable getTable() {
        return this.table;
    }

    public TicketListTableModel getTableModel() {
        return this.tableModel;
    }

    public void setCurrentRowIndexZero() {
        this.getTableModel().setCurrentRowIndex(0);
    }

    public void setAutoUpdateCheck(boolean check) {
        if (check) {
            this.lastUpateCheckTimer.restart();
        } else {
            this.lastUpateCheckTimer.stop();
        }
    }

    private void saveTableColumnsVisibility(List indices) {
        String selectedColumns = "";
        Iterator iterator = indices.iterator();
        while (iterator.hasNext()) {
            String newSelectedColumn = String.valueOf(iterator.next());
            selectedColumns = selectedColumns + newSelectedColumn;
            if (!iterator.hasNext()) continue;
            selectedColumns = selectedColumns + "*";
        }
        TerminalConfig.setTicketListViewHiddenColumns(selectedColumns);
    }

    private class TicketListTableModel
    extends PaginatedTableModel {
        public TicketListTableModel() {
            super(new String[]{POSConstants.TICKET_LIST_COLUMN_ID, POSConstants.TICKET_LIST_COLUMN_TABLE, POSConstants.TICKET_LIST_COLUMN_SERVER, POSConstants.TICKET_LIST_COLUMN_CREATE_DATE, POSConstants.TICKET_LIST_COLUMN_CUSTOMER, POSConstants.TICKET_LIST_COLUMN_DELIVERY_ADDRESS, POSConstants.TICKET_LIST_COLUMN_DELIVERY_DATE, POSConstants.TICKET_LIST_COLUMN_TICKET_TYPE, POSConstants.TICKET_LIST_COLUMN_STATUS, POSConstants.TICKET_LIST_COLUMN_TOTAL, POSConstants.TICKET_LIST_COLUMN_DUE});
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Ticket ticket = (Ticket)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return (int)ticket.getId();
                }
                case 1: {
                    return ticket.getTableNumbers();
                }
                case 2: {
                    User owner = ticket.getOwner();
                    return owner.getFirstName();
                }
                case 3: {
                    return ticket.getCreateDate();
                }
                case 4: {
                    String customerName = ticket.getProperty("CUSTOMER_NAME");
                    if (customerName != null && !customerName.equals("")) {
                        return customerName;
                    }
                    String customerMobile = ticket.getProperty("CUSTOMER_MOBILE");
                    if (customerMobile != null) {
                        return customerMobile;
                    }
                    return Messages.getString("TicketListView.6");
                }
                case 5: {
                    return ticket.getDeliveryAddress();
                }
                case 6: {
                    return ticket.getDeliveryDate();
                }
                case 7: {
                    return ticket.getOrderType();
                }
                case 8: {
                    String status = "";
                    status = ticket.isPaid() != false ? Messages.getString("TicketListView.8") : Messages.getString("TicketListView.9");
                    if (ticket.isVoided().booleanValue()) {
                        status = Messages.getString("TicketListView.12");
                    } else if (ticket.isClosed().booleanValue()) {
                        status = status + Messages.getString("TicketListView.13");
                    }
                    return status;
                }
                case 9: {
                    return ticket.getTotalAmount();
                }
                case 10: {
                    return ticket.getDueAmount();
                }
            }
            return null;
        }
    }

    private class TaskLastUpdateCheck
    implements ActionListener {
        private TaskLastUpdateCheck() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (PosGuiUtil.isModalDialogShowing()) {
                    return;
                }
                TicketListView.this.lastUpateCheckTimer.stop();
                DataUpdateInfo lastUpdateInfo = DataUpdateInfoDAO.getLastUpdateInfo();
                if (lastUpdateInfo.getLastUpdateTime().after(TicketListView.this.lastUpdateTime)) {
                    TicketListView.this.btnRefresh.setBlinking(true);
                }
            }
            finally {
                TicketListView.this.lastUpateCheckTimer.restart();
            }
        }
    }
}

