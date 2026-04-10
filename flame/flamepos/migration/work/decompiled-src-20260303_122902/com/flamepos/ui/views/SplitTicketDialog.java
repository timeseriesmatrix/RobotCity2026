/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 */
package com.floreantpos.ui.views;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.POSTitleLabel;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.order.TicketForSplitView;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SplitTicketDialog
extends POSDialog {
    private Ticket ticket;
    private POSToggleButton btnSplitBySeat;
    private PosButton btnCancel;
    private PosButton btnFinish;
    private POSToggleButton btnNumSplit2;
    private POSToggleButton btnNumSplit3;
    private POSToggleButton btnNumSplit4;
    private POSTitleLabel lblTicketId;
    private TicketForSplitView mainTicketView;
    private TicketForSplitView ticketView2;
    private TicketForSplitView ticketView3;
    private TicketForSplitView ticketView4;
    private TitlePanel titlePanel;
    private TransparentPanel actionButtonPanel;
    private TransparentPanel centerPanel;
    private TransparentPanel toolbarPanel;
    private TransparentPanel ticketPanel;

    public SplitTicketDialog() {
        this.initComponents();
        this.mainTicketView.setViewNumber(1);
        this.ticketView2.setViewNumber(2);
        this.ticketView3.setViewNumber(3);
        this.ticketView4.setViewNumber(4);
        this.mainTicketView.setTicketView1(this.ticketView2);
        this.mainTicketView.setTicketView2(this.ticketView3);
        this.mainTicketView.setTicketView3(this.ticketView4);
        this.ticketView2.setTicketView1(this.mainTicketView);
        this.ticketView2.setTicketView2(this.ticketView3);
        this.ticketView2.setTicketView3(this.ticketView4);
        this.ticketView3.setTicketView1(this.mainTicketView);
        this.ticketView3.setTicketView2(this.ticketView2);
        this.ticketView3.setTicketView3(this.ticketView4);
        this.ticketView4.setTicketView1(this.mainTicketView);
        this.ticketView4.setTicketView2(this.ticketView2);
        this.ticketView4.setTicketView3(this.ticketView3);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize);
        this.setDefaultCloseOperation(2);
        this.setResizable(true);
    }

    private void initComponents() {
        this.createTitlePanel();
        this.createActionButtonPanel();
        this.createToolbarPanel();
        this.createTicketViewPanel();
        this.centerPanel = new TransparentPanel(new BorderLayout());
        this.centerPanel.add((Component)this.toolbarPanel, "North");
        this.centerPanel.add((Component)this.ticketPanel, "Center");
        this.getContentPane().add((Component)this.centerPanel, "Center");
    }

    private void createToolbarPanel() {
        this.toolbarPanel = new TransparentPanel();
        ButtonGroup buttonGroup = new ButtonGroup();
        this.btnNumSplit2 = new POSToggleButton();
        this.btnNumSplit3 = new POSToggleButton();
        this.btnNumSplit4 = new POSToggleButton();
        this.btnSplitBySeat = new POSToggleButton();
        this.lblTicketId = new POSTitleLabel();
        this.lblTicketId.setText(Messages.getString("SplitTicketDialog.0"));
        this.toolbarPanel.add(this.lblTicketId);
        JSeparator separator = new JSeparator(1);
        separator.setPreferredSize(new Dimension(5, 20));
        this.toolbarPanel.add(separator);
        this.toolbarPanel.add(new JLabel(POSConstants.NUMBER_OF_SPLITS));
        buttonGroup.add(this.btnNumSplit2);
        this.btnNumSplit2.setSelected(true);
        this.btnNumSplit2.setText("2");
        this.btnNumSplit2.setPreferredSize(new Dimension(60, 40));
        this.btnNumSplit2.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                SplitTicketDialog.this.btnNumSplit2ActionPerformed(evt);
            }
        });
        this.toolbarPanel.add(this.btnNumSplit2);
        buttonGroup.add(this.btnNumSplit3);
        this.btnNumSplit3.setText("3");
        this.btnNumSplit3.setPreferredSize(new Dimension(60, 40));
        this.btnNumSplit3.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                SplitTicketDialog.this.btnNumSplit3ActionPerformed(evt);
            }
        });
        this.toolbarPanel.add(this.btnNumSplit3);
        buttonGroup.add(this.btnNumSplit4);
        this.btnNumSplit4.setText("4");
        this.btnNumSplit4.setPreferredSize(new Dimension(60, 40));
        this.btnNumSplit4.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                SplitTicketDialog.this.btnNumSplit4ActionPerformed(evt);
            }
        });
        this.toolbarPanel.add(this.btnNumSplit4);
        buttonGroup.add(this.btnSplitBySeat);
        this.btnSplitBySeat.setText("Split by seat number");
        this.btnSplitBySeat.setPreferredSize(new Dimension(120, 40));
        this.btnSplitBySeat.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                SplitTicketDialog.this.doSplitBySeatNumber(evt);
            }
        });
        this.toolbarPanel.add(this.btnSplitBySeat);
    }

    private void createTicketViewPanel() {
        this.ticketPanel = new TransparentPanel((LayoutManager)new MigLayout("fill, hidemode 3"));
        this.mainTicketView = new TicketForSplitView();
        this.ticketView2 = new TicketForSplitView();
        this.ticketView3 = new TicketForSplitView();
        this.ticketView4 = new TicketForSplitView();
        this.ticketView3.setVisible(false);
        this.ticketView4.setVisible(false);
        this.ticketPanel.add((Component)this.mainTicketView, "grow");
        this.ticketPanel.add((Component)this.ticketView2, "grow");
        this.ticketPanel.add((Component)this.ticketView3, "grow");
        this.ticketPanel.add((Component)this.ticketView4, "grow");
    }

    private void createActionButtonPanel() {
        this.actionButtonPanel = new TransparentPanel();
        this.btnFinish = new PosButton();
        this.btnFinish.setText(POSConstants.FINISH);
        this.btnFinish.setPreferredSize(new Dimension(140, 50));
        this.btnFinish.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                SplitTicketDialog.this.btnFinishActionPerformed(evt);
            }
        });
        this.actionButtonPanel.add(this.btnFinish);
        this.btnCancel = new PosButton();
        this.btnCancel.setText(POSConstants.CANCEL);
        this.btnCancel.setPreferredSize(new Dimension(140, 50));
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                SplitTicketDialog.this.btnCancelActionPerformed(evt);
            }
        });
        this.actionButtonPanel.add(this.btnCancel);
        this.getContentPane().add((Component)this.actionButtonPanel, "South");
    }

    private void createTitlePanel() {
        this.titlePanel = new TitlePanel();
        this.titlePanel.setTitle(POSConstants.SPLIT_TICKET);
        this.getContentPane().add((Component)this.titlePanel, "North");
    }

    private void btnCancelActionPerformed(ActionEvent evt) {
        this.setTicket(null);
        this.dispose();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void btnFinishActionPerformed(ActionEvent evt) {
        Session session = null;
        Transaction tx = null;
        try {
            TicketDAO dao = new TicketDAO();
            session = dao.createNewSession();
            tx = session.beginTransaction();
            this.saveTicket(this.mainTicketView, session);
            this.saveTicket(this.ticketView2, session);
            this.saveTicket(this.ticketView3, session);
            this.saveTicket(this.ticketView4, session);
            tx.commit();
            ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), ActionHistory.SPLIT_CHECK, POSConstants.RECEIPT_REPORT_TICKET_NO_LABEL + ":" + this.mainTicketView.getTicket().getId());
            this.dispose();
        }
        catch (Exception e) {
            try {
                tx.rollback();
            }
            catch (Exception exception) {
                // empty catch block
            }
            POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, e);
        }
        finally {
            try {
                session.close();
            }
            catch (Exception exception) {}
        }
    }

    private void doSplitBySeatNumber(ActionEvent evt) {
        List<TicketItem> ticketItems = this.ticket.getTicketItems();
        HashMap<Integer, ArrayList<TicketItem>> itemMap = new HashMap<Integer, ArrayList<TicketItem>>();
        for (TicketItem ticketItem : ticketItems) {
            ArrayList<TicketItem> ticketItemList = (ArrayList<TicketItem>)itemMap.get(ticketItem.getSeatNumber());
            if (ticketItemList == null) {
                ticketItemList = new ArrayList<TicketItem>();
                ticketItemList.add(ticketItem);
                itemMap.put(ticketItem.getSeatNumber(), ticketItemList);
                continue;
            }
            ticketItemList.add(ticketItem);
        }
        int splitViewNumber = 1;
        for (Integer seatNumber : itemMap.keySet()) {
            List splitTicketItems = (List)itemMap.get(seatNumber);
            this.transferTicketItemsToSplitView(splitTicketItems, splitViewNumber);
            ++splitViewNumber;
        }
    }

    private void transferTicketItemsToSplitView(List<TicketItem> splitTicketItems, int splitViewNumber) {
        block4: {
            block5: {
                block3: {
                    if (splitViewNumber != 2) break block3;
                    for (TicketItem ticketItem : splitTicketItems) {
                        this.mainTicketView.transferTicketItem(ticketItem, this.ticketView2, true);
                    }
                    break block4;
                }
                if (splitViewNumber != 3) break block5;
                this.ticketView3.setVisible(true);
                for (TicketItem ticketItem : splitTicketItems) {
                    this.mainTicketView.transferTicketItem(ticketItem, this.ticketView3, true);
                }
                break block4;
            }
            if (splitViewNumber != 4) break block4;
            this.ticketView4.setVisible(true);
            for (TicketItem ticketItem : splitTicketItems) {
                this.mainTicketView.transferTicketItem(ticketItem, this.ticketView4, true);
            }
        }
    }

    private void btnNumSplit4ActionPerformed(ActionEvent evt) {
        this.ticketView3.setVisible(true);
        this.ticketView4.setVisible(true);
    }

    private void btnNumSplit3ActionPerformed(ActionEvent evt) {
        this.ticketView3.setVisible(true);
        if (this.ticketView4.isVisible()) {
            Ticket ticket4 = this.ticketView4.getTicket();
            List<TicketItem> ticketItems = ticket4.getTicketItems();
            for (TicketItem item : ticketItems) {
                this.ticketView4.transferAllTicketItem(item, this.mainTicketView);
            }
            this.ticketView4.setVisible(false);
        }
    }

    private void btnNumSplit2ActionPerformed(ActionEvent evt) {
        List<TicketItem> ticketItems;
        if (this.ticketView3.isVisible()) {
            Ticket ticket3 = this.ticketView3.getTicket();
            ticketItems = new ArrayList<TicketItem>(ticket3.getTicketItems());
            for (TicketItem item : ticketItems) {
                this.ticketView3.transferAllTicketItem(item, this.mainTicketView);
            }
            this.ticketView3.setVisible(false);
        }
        if (this.ticketView4.isVisible()) {
            Ticket ticket4 = this.ticketView4.getTicket();
            ticketItems = ticket4.getTicketItems();
            for (TicketItem item : ticketItems) {
                this.ticketView4.transferAllTicketItem(item, this.mainTicketView);
            }
            this.ticketView4.setVisible(false);
        }
    }

    public void saveTicket(TicketForSplitView view, Session session) {
        if (!view.isVisible()) {
            return;
        }
        view.getTicket().setOrderType(this.mainTicketView.getTicket().getOrderType());
        view.updateModel();
        Ticket ticket = view.getTicket();
        if (ticket.getTicketItems().size() <= 0) {
            return;
        }
        List<ShopTable> tables = ShopTableDAO.getInstance().getTables(this.mainTicketView.getTicket());
        if (tables != null) {
            for (ShopTable shopTable : tables) {
                ticket.addTable(shopTable.getTableNumber());
            }
        }
        TicketDAO.getInstance().saveOrUpdate(ticket, session);
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        if (ticket != null) {
            this.lblTicketId.setText(POSConstants.ORIGINAL_TICKET_ID + ": " + ticket.getId());
            this.mainTicketView.setTicket(ticket);
            this.btnSplitBySeat.setVisible(ticket.getOrderType().isAllowSeatBasedOrder());
        }
    }
}

