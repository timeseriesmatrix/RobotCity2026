/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.jidesoft.swing.JideScrollPane
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.ScrollableFlowPanel;
import com.floreantpos.swing.ShopTableButton;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import com.jidesoft.swing.JideScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;

public class TableSelectionView
extends JPanel
implements ActionListener {
    private DefaultListModel<ShopTableButton> addedTableListModel = new DefaultListModel();
    private Map<ShopTable, ShopTableButton> tableButtonMap = new HashMap<ShopTable, ShopTableButton>();
    private ScrollableFlowPanel buttonsPanel;
    private POSToggleButton btnGroup;
    private POSToggleButton btnUnGroup;
    private PosButton btnDone;
    private PosButton btnCancel;
    private PosButton btnRefresh;
    private ButtonGroup btnGroups;

    public TableSelectionView() {
        this.init();
    }

    private void init() {
        this.setLayout(new BorderLayout());
        this.buttonsPanel = new ScrollableFlowPanel(1);
        this.setLayout(new BorderLayout(10, 10));
        TitledBorder titledBorder1 = BorderFactory.createTitledBorder(null, POSConstants.TABLES, 2, 0);
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(new CompoundBorder(titledBorder1, new EmptyBorder(2, 2, 2, 2)));
        JideScrollPane scrollPane = new JideScrollPane((Component)this.buttonsPanel, 20, 31);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(60, 0));
        leftPanel.add((Component)scrollPane, "Center");
        this.add((Component)leftPanel, "Center");
        this.createButtonActionPanel();
    }

    private void createButtonActionPanel() {
        TitledBorder titledBorder2 = BorderFactory.createTitledBorder(null, "-", 2, 0);
        JPanel rightPanel = new JPanel(new BorderLayout(20, 20));
        rightPanel.setPreferredSize(new Dimension(120, 0));
        rightPanel.setBorder(new CompoundBorder(titledBorder2, new EmptyBorder(2, 2, 6, 2)));
        JPanel actionBtnPanel = new JPanel((LayoutManager)new MigLayout("ins 2 2 0 2, hidemode 3, flowy", "grow", ""));
        this.btnGroups = new ButtonGroup();
        this.btnGroup = new POSToggleButton(POSConstants.GROUP);
        this.btnUnGroup = new POSToggleButton(POSConstants.UNGROUP);
        this.btnDone = new PosButton(POSConstants.SAVE_BUTTON_TEXT);
        this.btnCancel = new PosButton(POSConstants.CANCEL);
        this.btnGroup.addActionListener(this);
        this.btnUnGroup.addActionListener(this);
        this.btnDone.addActionListener(this);
        this.btnCancel.addActionListener(this);
        this.btnDone.setVisible(false);
        this.btnCancel.setVisible(false);
        this.btnGroup.setIcon(new ImageIcon(this.getClass().getResource("/images/plus.png")));
        this.btnUnGroup.setIcon(new ImageIcon(this.getClass().getResource("/images/minus2.png")));
        this.btnGroups.add(this.btnGroup);
        this.btnGroups.add(this.btnUnGroup);
        actionBtnPanel.add((Component)this.btnGroup, "grow");
        actionBtnPanel.add((Component)this.btnUnGroup, "grow");
        actionBtnPanel.add((Component)this.btnDone, "grow");
        actionBtnPanel.add((Component)this.btnCancel, "grow");
        rightPanel.add(actionBtnPanel);
        this.btnRefresh = new PosButton(POSConstants.REFRESH);
        this.btnRefresh.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                TableSelectionView.this.redererTable();
                TableSelectionView.this.btnGroups.clearSelection();
            }
        });
        rightPanel.add((Component)this.btnRefresh, "South");
        this.add((Component)rightPanel, "East");
    }

    public synchronized void redererTable() {
        this.addedTableListModel.clear();
        this.buttonsPanel.getContentPane().removeAll();
        this.checkTables();
        List<ShopTable> tables = ShopTableDAO.getInstance().findAll();
        for (ShopTable shopTable : tables) {
            ShopTableButton tableButton = new ShopTableButton(shopTable);
            tableButton.setPreferredSize(new Dimension(157, 138));
            tableButton.setFont(new Font(tableButton.getFont().getName(), tableButton.getFont().getStyle(), 30));
            tableButton.setText(tableButton.getText());
            tableButton.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    TableSelectionView.this.addTable(e);
                }
            });
            tableButton.update();
            this.buttonsPanel.add(tableButton);
            this.tableButtonMap.put(shopTable, tableButton);
        }
        this.rendererTablesTicket();
    }

    private void rendererTablesTicket() {
        List<Ticket> openTickets = TicketDAO.getInstance().findOpenTickets();
        for (Ticket ticket : openTickets) {
            for (ShopTableButton shopTableButton : this.tableButtonMap.values()) {
                if (!ticket.getTableNumbers().contains(shopTableButton.getId())) continue;
                shopTableButton.setText("<html><center>" + shopTableButton.getText() + "<br><h4>" + ticket.getOwner().getFirstName() + "<br>Chk#" + ticket.getId() + "</h4></center></html>");
                shopTableButton.setTicket(ticket);
                shopTableButton.setUser(ticket.getOwner());
            }
        }
    }

    private boolean addTable(ActionEvent e) {
        ShopTableButton button = (ShopTableButton)e.getSource();
        int tableNumber = button.getId();
        ShopTable shopTable = ShopTableDAO.getInstance().getByNumber(tableNumber);
        if (shopTable == null) {
            POSMessageDialog.showError(this, Messages.getString("TableSelectionDialog.2") + e + Messages.getString("TableSelectionDialog.3"));
            return false;
        }
        if (this.btnGroup.isSelected()) {
            if (this.addedTableListModel.contains(button)) {
                return true;
            }
            if (button.getShopTable().isServing().booleanValue()) {
                return true;
            }
            button.getShopTable().setServing(true);
            button.setBackground(Color.green);
            button.setForeground(Color.black);
            this.addedTableListModel.addElement(button);
            return false;
        }
        if (this.btnUnGroup.isSelected()) {
            if (this.addedTableListModel.contains(button)) {
                return true;
            }
            Ticket ticket = button.getTicket();
            if (ticket == null) {
                return false;
            }
            int ticketId = ticket.getId();
            Enumeration<ShopTableButton> elements = this.addedTableListModel.elements();
            while (elements.hasMoreElements()) {
                ShopTableButton shopTableButton = elements.nextElement();
                if (shopTableButton.getTicket().getId() == ticketId) continue;
                return false;
            }
            if (this.addedTableListModel.size() >= ticket.getTableNumbers().size() - 1) {
                return false;
            }
            button.getShopTable().setServing(true);
            button.setBackground(Color.green);
            button.setForeground(Color.black);
            this.addedTableListModel.addElement(button);
            return false;
        }
        if (shopTable.isServing().booleanValue() && !this.btnGroup.isSelected()) {
            if (!button.hasUserAccess()) {
                return false;
            }
            this.editTicket(button.getTicket());
            return false;
        }
        if (!this.btnGroup.isSelected() && !this.btnGroup.isSelected()) {
            if (!this.addedTableListModel.contains(button)) {
                this.addedTableListModel.addElement(button);
            }
            this.doCreateNewTicket();
            this.clearSelection();
        }
        return true;
    }

    public List<ShopTable> getTables() {
        Enumeration<ShopTableButton> elements = this.addedTableListModel.elements();
        ArrayList<ShopTable> tables = new ArrayList<ShopTable>();
        while (elements.hasMoreElements()) {
            ShopTableButton shopTableButton = elements.nextElement();
            tables.add(shopTableButton.getShopTable());
        }
        return tables;
    }

    private void clearSelection() {
        this.redererTable();
        this.btnGroups.clearSelection();
        this.btnGroup.setVisible(true);
        this.btnUnGroup.setVisible(true);
        this.btnDone.setVisible(false);
        this.btnCancel.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object object = e.getSource();
        if (object == this.btnGroup) {
            this.addedTableListModel.clear();
            this.btnUnGroup.setVisible(false);
            this.btnDone.setVisible(true);
            this.btnCancel.setVisible(true);
        } else if (object == this.btnUnGroup) {
            this.addedTableListModel.clear();
            this.btnGroup.setVisible(false);
            this.btnDone.setVisible(true);
            this.btnCancel.setVisible(true);
        } else if (object == this.btnDone) {
            if (this.btnGroup.isSelected()) {
                this.doGroupAction();
                this.clearSelection();
            } else if (this.btnUnGroup.isSelected()) {
                this.doUnGroupAction();
                this.clearSelection();
            }
        } else if (object == this.btnCancel) {
            this.clearSelection();
        }
    }

    private void doCreateNewTicket() {
    }

    private boolean editTicket(Ticket ticket) {
        if (ticket == null) {
            return false;
        }
        Ticket ticketToEdit = TicketDAO.getInstance().loadFullTicket(ticket.getId());
        OrderView.getInstance().setCurrentTicket(ticketToEdit);
        RootView.getInstance().showView("ORDER_VIEW");
        OrderView.getInstance().getTicketView().getTxtSearchItem().requestFocus();
        return true;
    }

    private void doGroupAction() {
        this.doCreateNewTicket();
    }

    private void doUnGroupAction() {
        if (this.addedTableListModel == null || this.addedTableListModel.isEmpty()) {
            return;
        }
        Enumeration<ShopTableButton> elements = this.addedTableListModel.elements();
        if (!this.addedTableListModel.elementAt(0).hasUserAccess()) {
            return;
        }
        while (elements.hasMoreElements()) {
            ShopTableButton button = elements.nextElement();
            ShopTable shopTable = button.getShopTable();
            Ticket ticket = button.getTicket();
            if (ticket == null) continue;
            Iterator<Integer> iterator = ticket.getTableNumbers().iterator();
            while (iterator.hasNext()) {
                Integer id = iterator.next();
                if (button.getId() != id.intValue()) continue;
                iterator.remove();
            }
            shopTable.setServing(false);
            ShopTableDAO.getInstance().saveOrUpdate(shopTable);
            TicketDAO.getInstance().saveOrUpdate(ticket);
        }
    }

    private void checkTables() {
        List<ShopTable> allTables = ShopTableDAO.getInstance().findAll();
        if (allTables == null || allTables.isEmpty()) {
            int userInput = 0;
            int result = POSMessageDialog.showYesNoQuestionDialog(Application.getPosWindow(), Messages.getString("TableSelectionView.0"), Messages.getString("TableSelectionView.1"));
            if (result == 0) {
                userInput = NumberSelectionDialog2.takeIntInput(Messages.getString("TableSelectionView.2"));
                if (userInput == 0) {
                    POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TableSelectionView.3"));
                    return;
                }
                if (userInput != -1) {
                    ShopTableDAO.getInstance().createNewTables(userInput);
                }
            }
        }
    }
}

