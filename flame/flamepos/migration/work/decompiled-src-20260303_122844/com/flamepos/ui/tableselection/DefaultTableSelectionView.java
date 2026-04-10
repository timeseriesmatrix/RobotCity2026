/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.jidesoft.swing.JideScrollPane
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.tableselection;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosLog;
import com.floreantpos.actions.NewBarTabAction;
import com.floreantpos.bo.ui.explorer.QuickMaintenanceExplorer;
import com.floreantpos.extension.OrderServiceFactory;
import com.floreantpos.main.Application;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.ScrollableFlowPanel;
import com.floreantpos.swing.ShopTableButton;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.tableselection.BarTabSelectionView;
import com.floreantpos.ui.tableselection.TableSelector;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.util.TicketAlreadyExistsException;
import com.jidesoft.swing.JideScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Window;
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
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;

public class DefaultTableSelectionView
extends TableSelector
implements ActionListener {
    private DefaultListModel<ShopTableButton> addedTableListModel = new DefaultListModel();
    private DefaultListModel<ShopTableButton> removeTableListModel = new DefaultListModel();
    private Map<ShopTable, ShopTableButton> tableButtonMap = new HashMap<ShopTable, ShopTableButton>();
    private ScrollableFlowPanel buttonsPanel;
    private BarTabSelectionView barTab;
    private POSToggleButton btnGroup;
    private POSToggleButton btnUnGroup;
    private static PosButton btnCancelDialog;
    private PosButton btnDone;
    private PosButton btnCancel;
    private PosButton btnRefresh;
    private PosButton btnNewBarTab;
    private ButtonGroup btnGroups;
    private JTabbedPane tabbedPane;

    public DefaultTableSelectionView() {
        this.init();
    }

    private void init() {
        this.setLayout(new BorderLayout());
        this.buttonsPanel = new ScrollableFlowPanel(1);
        this.barTab = new BarTabSelectionView();
        this.setLayout(new BorderLayout(10, 10));
        TitledBorder titledBorder1 = BorderFactory.createTitledBorder(null, POSConstants.TABLES, 2, 0);
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(new CompoundBorder(titledBorder1, new EmptyBorder(2, 2, 2, 2)));
        JideScrollPane scrollPane = new JideScrollPane((Component)this.buttonsPanel, 20, 31);
        scrollPane.getVerticalScrollBar().setPreferredSize(PosUIManager.getSize(60, 0));
        this.tabbedPane = new JTabbedPane();
        leftPanel.add((Component)scrollPane, "Center");
        this.tabbedPane.addTab("Dining Room", leftPanel);
        this.add((Component)this.tabbedPane, "Center");
        this.createButtonActionPanel();
    }

    private void createButtonActionPanel() {
        TitledBorder titledBorder2 = BorderFactory.createTitledBorder(null, "-", 2, 0);
        JPanel rightPanel = new JPanel(new BorderLayout(20, 20));
        rightPanel.setPreferredSize(PosUIManager.getSize(120, 0));
        rightPanel.setBorder(new CompoundBorder(titledBorder2, new EmptyBorder(2, 2, 6, 2)));
        JPanel actionBtnPanel = new JPanel((LayoutManager)new MigLayout("ins 2 2 0 2, hidemode 3, flowy", "sg fill, grow", ""));
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
        this.btnNewBarTab = new PosButton("New Tab");
        this.btnNewBarTab.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                List<ShopTable> selectedTables = DefaultTableSelectionView.this.getSelectedTables();
                new NewBarTabAction(DefaultTableSelectionView.this.orderType, selectedTables, Application.getPosWindow()).actionPerformed(e);
            }
        });
        actionBtnPanel.add((Component)this.btnGroup, "grow");
        actionBtnPanel.add((Component)this.btnUnGroup, "grow");
        actionBtnPanel.add((Component)this.btnDone, "grow");
        actionBtnPanel.add((Component)this.btnCancel, "grow");
        actionBtnPanel.add((Component)this.btnNewBarTab, "grow");
        rightPanel.add(actionBtnPanel);
        JPanel southbuttonPanel = new JPanel((LayoutManager)new MigLayout("ins 2 2 0 2, hidemode 3, flowy", "grow", ""));
        this.btnRefresh = new PosButton(POSConstants.REFRESH);
        this.btnRefresh.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableSelectionView.this.redererTables();
            }
        });
        southbuttonPanel.add((Component)this.btnRefresh, "grow");
        btnCancelDialog = new PosButton(POSConstants.CANCEL);
        btnCancelDialog.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableSelectionView.this.closeDialog(true);
            }
        });
        southbuttonPanel.add((Component)btnCancelDialog, "grow");
        rightPanel.add((Component)southbuttonPanel, "South");
        this.add((Component)rightPanel, "East");
    }

    @Override
    public synchronized void redererTables() {
        this.clearSelection();
        this.buttonsPanel.getContentPane().removeAll();
        this.checkTables();
        ArrayList<ShopTable> tables = new ArrayList<ShopTable>();
        tables.addAll(ShopTableDAO.getInstance().findAll());
        if (RootView.getInstance().isMaintenanceMode()) {
            tables.add(new ShopTable(null, 0, 0, null));
        }
        for (ShopTable shopTable : tables) {
            ShopTableButton tableButton = new ShopTableButton(shopTable);
            tableButton.setPreferredSize(PosUIManager.getSize(157, 138));
            tableButton.setFont(new Font(tableButton.getFont().getName(), 1, PosUIManager.getTableNumberFontSize()));
            if (shopTable.getId() == null) {
                tableButton.setIcon(IconFactory.getIcon("/ui_icons/", "add+user.png"));
            } else {
                tableButton.setText(tableButton.getText());
            }
            tableButton.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (RootView.getInstance().isMaintenanceMode()) {
                        ShopTableButton button = (ShopTableButton)e.getSource();
                        QuickMaintenanceExplorer.quickMaintain(button.getShopTable());
                        return;
                    }
                    DefaultTableSelectionView.this.addTable(e);
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
                if (shopTableButton.getShopTable().getId() == null || !ticket.getTableNumbers().contains(shopTableButton.getId())) continue;
                shopTableButton.setText("<html><center>" + shopTableButton.getText() + "<br><h4>" + ticket.getOwner().getFirstName() + "<br>Chk#" + ticket.getId() + "</h4></center></html>");
                if (!ticket.getOwner().getUserId().toString().equals(Application.getCurrentUser().getUserId().toString())) {
                    shopTableButton.setBackground(new Color(139, 0, 139));
                }
                if (this.addedTableListModel.contains(shopTableButton)) {
                    shopTableButton.setBackground(Color.GREEN);
                }
                shopTableButton.setTicket(ticket);
                shopTableButton.setUser(ticket.getOwner());
            }
        }
        this.barTab.updateView(this.orderType);
        this.buttonsPanel.getContentPane().revalidate();
        this.buttonsPanel.getContentPane().repaint();
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
            if (this.removeTableListModel.contains(button)) {
                return true;
            }
            Ticket ticket = button.getTicket();
            if (ticket == null) {
                return false;
            }
            int ticketId = ticket.getId();
            Enumeration<ShopTableButton> elements = this.removeTableListModel.elements();
            while (elements.hasMoreElements()) {
                ShopTableButton shopTableButton = elements.nextElement();
                if (shopTableButton.getTicket().getId() == ticketId) continue;
                return false;
            }
            if (this.removeTableListModel.size() >= ticket.getTableNumbers().size() - 1) {
                return false;
            }
            button.getShopTable().setServing(true);
            button.setBackground(Color.white);
            button.setForeground(Color.black);
            this.removeTableListModel.addElement(button);
            return false;
        }
        if (shopTable.isServing().booleanValue() && !this.btnGroup.isSelected()) {
            if (!button.hasUserAccess()) {
                return false;
            }
            if (this.isCreateNewTicket()) {
                this.editTicket(button.getTicket());
                this.closeDialog(false);
            }
            return false;
        }
        if (!this.btnGroup.isSelected() && !this.btnGroup.isSelected()) {
            this.addedTableListModel.clear();
            if (!this.addedTableListModel.contains(button)) {
                this.addedTableListModel.addElement(button);
            }
            if (this.isCreateNewTicket()) {
                this.doCreateNewTicket();
            }
            this.closeDialog(false);
        }
        return true;
    }

    private void closeDialog(boolean canceled) {
        Window windowAncestor = SwingUtilities.getWindowAncestor(this);
        if (windowAncestor instanceof POSDialog) {
            ((POSDialog)windowAncestor).setCanceled(false);
            windowAncestor.dispose();
        }
    }

    @Override
    public List<ShopTable> getSelectedTables() {
        Enumeration<ShopTableButton> elements = this.addedTableListModel.elements();
        ArrayList<ShopTable> tables = new ArrayList<ShopTable>();
        while (elements.hasMoreElements()) {
            ShopTableButton shopTableButton = elements.nextElement();
            tables.add(shopTableButton.getShopTable());
        }
        return tables;
    }

    private void clearSelection() {
        if (this.isCreateNewTicket()) {
            this.addedTableListModel.clear();
        }
        this.removeTableListModel.clear();
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
            if (this.isCreateNewTicket()) {
                this.addedTableListModel.clear();
            }
            this.btnUnGroup.setVisible(false);
            this.btnDone.setVisible(true);
            this.btnCancel.setVisible(true);
        } else if (object == this.btnUnGroup) {
            this.removeTableListModel.clear();
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
            this.redererTables();
        }
    }

    private void doCreateNewTicket() {
        try {
            List<ShopTable> selectedTables = this.getSelectedTables();
            if (selectedTables.isEmpty()) {
                this.clearSelection();
                return;
            }
            OrderServiceFactory.getOrderService().createNewTicket(this.getOrderType(), selectedTables, null);
            this.clearSelection();
        }
        catch (TicketAlreadyExistsException e) {
            PosLog.error(this.getClass(), e);
        }
    }

    private boolean editTicket(Ticket ticket) {
        if (ticket == null) {
            return false;
        }
        this.closeDialog(false);
        Ticket ticketToEdit = TicketDAO.getInstance().loadFullTicket(ticket.getId());
        OrderView.getInstance().setCurrentTicket(ticketToEdit);
        RootView.getInstance().showView("ORDER_VIEW");
        OrderView.getInstance().getTicketView().getTxtSearchItem().requestFocus();
        return true;
    }

    private void doGroupAction() {
        if (this.isCreateNewTicket()) {
            this.doCreateNewTicket();
        }
        this.closeDialog(false);
    }

    private void doUnGroupAction() {
        if (this.removeTableListModel == null || this.removeTableListModel.isEmpty()) {
            return;
        }
        Enumeration<ShopTableButton> elements = this.removeTableListModel.elements();
        if (!this.removeTableListModel.elementAt(0).hasUserAccess()) {
            return;
        }
        while (elements.hasMoreElements()) {
            ShopTableButton button = elements.nextElement();
            if (this.addedTableListModel.contains(button)) {
                this.addedTableListModel.removeElement(button);
            }
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
        this.redererTables();
        if (!this.isCreateNewTicket()) {
            this.closeDialog(false);
        }
    }

    @Override
    public void setTicket(Ticket ticket) {
        if (ticket == null) {
            return;
        }
        this.ticket = ticket;
        List<ShopTable> tables = ShopTableDAO.getInstance().getTables(ticket);
        if (tables == null) {
            return;
        }
        this.addedTableListModel.clear();
        for (ShopTable shopTable : tables) {
            ShopTableButton shopTableButton = this.tableButtonMap.get(shopTable);
            if (shopTableButton != null) {
                shopTableButton.getShopTable().setServing(false);
            }
            this.addedTableListModel.addElement(shopTableButton);
        }
        this.redererTables();
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

    @Override
    public void setOrderType(OrderType orderType) {
        super.setOrderType(orderType);
        this.btnNewBarTab.setVisible(orderType.isBarTab());
        if (orderType.isBarTab().booleanValue()) {
            this.tabbedPane.addTab("Bar Tab", this.barTab);
        }
    }

    @Override
    public void updateView(boolean update) {
        btnCancelDialog.setVisible(update);
    }
}

