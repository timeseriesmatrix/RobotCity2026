/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jdesktop.swingx.JXDatePicker
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;

public class TicketExplorer
extends TransparentPanel {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy, h:m a");
    private JXDatePicker fromDatePicker = UiUtil.getCurrentMonthStart();
    private JXDatePicker toDatePicker = UiUtil.getCurrentMonthEnd();
    private JButton btnGo = new JButton(POSConstants.GO);
    private JXTable table;
    private TicketExplorerTableModel tableModel;
    private List<Ticket> tickets;

    public TicketExplorer() {
        this.setLayout(new BorderLayout());
        this.table = new JXTable();
        this.table.setDefaultRenderer(Object.class, (TableCellRenderer)new PosTableRenderer());
        this.tableModel = new TicketExplorerTableModel();
        this.table.setModel((TableModel)this.tableModel);
        this.table.setAutoResizeMode(4);
        this.table.setRowHeight(25);
        this.addTopPanel();
        this.add((Component)new JScrollPane((Component)this.table), "Center");
        this.addButtonPanel();
        this.refresh();
    }

    private void addTopPanel() {
        JPanel topPanel = new JPanel((LayoutManager)new MigLayout());
        this.btnGo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    TicketExplorer.this.refresh();
                }
                catch (Exception e1) {
                    BOMessageDialog.showError(TicketExplorer.this, POSConstants.ERROR_MESSAGE, e1);
                }
            }
        });
        topPanel.add((Component)new JLabel(POSConstants.FROM), "grow");
        topPanel.add((Component)this.fromDatePicker, "gapright 10");
        topPanel.add((Component)new JLabel(POSConstants.TO), "grow");
        topPanel.add((Component)this.toDatePicker);
        topPanel.add((Component)this.btnGo, "width 60!");
        this.add((Component)topPanel, "North");
    }

    private void addButtonPanel() {
        JButton btnVoid = new JButton(POSConstants.DELETE);
        btnVoid.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = TicketExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        POSMessageDialog.showMessage(POSUtil.getBackOfficeWindow(), POSConstants.SELECT_ONE_TICKET_TO_VOID);
                        return;
                    }
                    index = TicketExplorer.this.table.convertRowIndexToModel(index);
                    ArrayList<Ticket> tickets = new ArrayList<Ticket>();
                    Ticket ticket = (Ticket)TicketExplorer.this.tableModel.getRows().get(index);
                    tickets.add(ticket);
                    if (POSMessageDialog.showYesNoQuestionDialog(TicketExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != 0) {
                        return;
                    }
                    TicketDAO.getInstance().deleteTickets(tickets);
                    TicketExplorer.this.tableModel.deleteItem(index);
                    TicketExplorer.this.table.repaint();
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        JButton btnVoidAll = new JButton(POSConstants.DELETE_ALL);
        btnVoidAll.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<Ticket> tickets = TicketExplorer.this.tableModel.getRows();
                    if (tickets.isEmpty()) {
                        return;
                    }
                    if (POSMessageDialog.showYesNoQuestionDialog(TicketExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE_ALL) != 0) {
                        return;
                    }
                    TicketDAO.getInstance().deleteTickets(tickets);
                    TicketExplorer.this.refresh();
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        TransparentPanel panel = new TransparentPanel();
        panel.add(btnVoid);
        panel.add(btnVoidAll);
        this.add((Component)panel, "South");
    }

    private void refresh() {
        if (this.tableModel.getRows() != null) {
            this.tableModel.getRows().clear();
        }
        Date fromDate = this.fromDatePicker.getDate();
        Date toDate = this.toDatePicker.getDate();
        fromDate = DateUtil.startOfDay(fromDate);
        toDate = DateUtil.endOfDay(toDate);
        TicketDAO dao = new TicketDAO();
        this.tickets = dao.findClosedTickets(fromDate, toDate);
        this.tableModel.setRows(this.tickets);
        this.table.repaint();
    }

    class TicketExplorerTableModel
    extends ListTableModel<Ticket> {
        String[] columnNames = new String[]{POSConstants.ID, POSConstants.CREATED_BY.toUpperCase(), POSConstants.CREATE_TIME.toUpperCase(), POSConstants.SETTLE_TIME.toUpperCase(), POSConstants.SUBTOTAL.toUpperCase(), POSConstants.DISCOUNT.toUpperCase(), POSConstants.TAX.toUpperCase(), POSConstants.TOTAL, POSConstants.PAID, POSConstants.VOID};

        TicketExplorerTableModel() {
        }

        @Override
        public String[] getColumnNames() {
            return this.columnNames;
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
        public Object getValueAt(int rowIndex, int columnIndex) {
            Ticket ticket = (Ticket)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return String.valueOf(ticket.getId());
                }
                case 1: {
                    return ticket.getOwner().toString();
                }
                case 2: {
                    return TicketExplorer.this.dateFormat.format(ticket.getCreateDate());
                }
                case 3: {
                    if (ticket.getClosingDate() != null) {
                        return TicketExplorer.this.dateFormat.format(ticket.getClosingDate());
                    }
                    return "";
                }
                case 4: {
                    return (double)ticket.getSubtotalAmount();
                }
                case 5: {
                    return (double)ticket.getDiscountAmount();
                }
                case 6: {
                    return (double)ticket.getTaxAmount();
                }
                case 7: {
                    return (double)ticket.getTotalAmount();
                }
                case 8: {
                    return (boolean)ticket.isPaid();
                }
                case 9: {
                    return (boolean)ticket.isVoided();
                }
            }
            return null;
        }
    }
}

