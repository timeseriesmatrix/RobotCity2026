/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.logging.LogFactory
 *  org.jdesktop.swingx.JXCollapsiblePane
 */
package com.floreantpos.ui.views;

import com.floreantpos.ITicketList;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.PosLog;
import com.floreantpos.actions.NewBarTabAction;
import com.floreantpos.actions.RefundAction;
import com.floreantpos.actions.SettleTicketAction;
import com.floreantpos.actions.VoidTicketAction;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.FloorLayoutPlugin;
import com.floreantpos.extension.OrderServiceExtension;
import com.floreantpos.main.Application;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PaymentStatusFilter;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.UserType;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.services.TicketService;
import com.floreantpos.swing.OrderTypeButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.TicketListUpdateListener;
import com.floreantpos.ui.TicketListView;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.OrderInfoDialog;
import com.floreantpos.ui.views.OrderInfoView;
import com.floreantpos.ui.views.SplitTicketDialog;
import com.floreantpos.ui.views.order.DefaultOrderServiceExtension;
import com.floreantpos.ui.views.order.OrderController;
import com.floreantpos.ui.views.order.OrderTypeSelectionDialog;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.ui.views.order.TicketSelectionDialog;
import com.floreantpos.ui.views.order.ViewPanel;
import com.floreantpos.ui.views.payment.GroupSettleTicketDialog;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXCollapsiblePane;

public class SwitchboardView
extends ViewPanel
implements ActionListener,
ITicketList,
TicketListUpdateListener {
    public static final String VIEW_NAME = POSConstants.ORDERS;
    private OrderServiceExtension orderServiceExtension;
    private static SwitchboardView instance;
    private JPanel orderPanel;
    private PosButton btnEditTicket = new PosButton(POSConstants.EDIT_TICKET_BUTTON_TEXT);
    private PosButton btnGroupSettle = new PosButton(POSConstants.GROUP_SETTLE_BUTTON_TEXT);
    private PosButton btnOrderInfo = new PosButton(POSConstants.ORDER_INFO_BUTTON_TEXT);
    private PosButton btnReopenTicket = new PosButton(POSConstants.REOPEN_TICKET_BUTTON_TEXT);
    private PosButton btnSettleTicket = new PosButton(POSConstants.SETTLE_TICKET_BUTTON_TEXT);
    private PosButton btnSplitTicket = new PosButton(POSConstants.SPLIT_TICKET_BUTTON_TEXT);
    private PosButton btnVoidTicket = new PosButton(POSConstants.VOID_TICKET_BUTTON_TEXT);
    private PosButton btnRefundTicket = new PosButton(POSConstants.REFUND_BUTTON_TEXT, new RefundAction(this));
    private PosButton btnAssignDriver = new PosButton(POSConstants.ASSIGN_DRIVER_BUTTON_TEXT);
    private PosButton btnCloseOrder = new PosButton(POSConstants.CLOSE_ORDER_BUTTON_TEXT);
    private TicketListView ticketList = new TicketListView();
    private TitledBorder ticketsListPanelBorder;

    private SwitchboardView() {
        this.initComponents();
        this.ticketList.addTicketListUpateListener(this);
        this.btnEditTicket.addActionListener(this);
        this.btnGroupSettle.addActionListener(this);
        this.btnOrderInfo.addActionListener(this);
        this.btnReopenTicket.addActionListener(this);
        this.btnSettleTicket.addActionListener(this);
        this.btnSplitTicket.addActionListener(this);
        this.btnVoidTicket.setAction(new VoidTicketAction(this));
        this.orderServiceExtension = (OrderServiceExtension)ExtensionManager.getPlugin(OrderServiceExtension.class);
        if (this.orderServiceExtension == null) {
            this.btnAssignDriver.setEnabled(false);
            this.orderServiceExtension = new DefaultOrderServiceExtension();
        }
        this.applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
    }

    public static SwitchboardView getInstance() {
        if (instance == null) {
            instance = new SwitchboardView();
        }
        return instance;
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        JPanel ticketsAndActivityPanel = new JPanel(new BorderLayout(5, 5));
        this.ticketsListPanelBorder = BorderFactory.createTitledBorder(null, POSConstants.OPEN_TICKETS_AND_ACTIVITY, 2, 0);
        ticketsAndActivityPanel.setBorder(new CompoundBorder(this.ticketsListPanelBorder, new EmptyBorder(2, 2, 2, 2)));
        ticketsAndActivityPanel.add((Component)this.ticketList, "Center");
        JPanel activityPanel = this.createActivityPanel();
        ticketsAndActivityPanel.add((Component)activityPanel, "South");
        this.btnAssignDriver.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SwitchboardView.this.doAssignDriver();
            }
        });
        this.btnCloseOrder.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SwitchboardView.this.doCloseOrder();
            }
        });
        centerPanel.add((Component)ticketsAndActivityPanel, "Center");
        JPanel rightPanel = new JPanel(new BorderLayout(20, 20));
        TitledBorder titledBorder2 = BorderFactory.createTitledBorder(null, "-", 2, 0);
        rightPanel.setBorder(new CompoundBorder(titledBorder2, new EmptyBorder(2, 2, 6, 2)));
        this.orderPanel = new JPanel((LayoutManager)new MigLayout("ins 2 2 0 2, fill, hidemode 3, flowy", "fill, grow", ""));
        this.rendererOrderPanel();
        rightPanel.add(this.orderPanel);
        rightPanel.setMinimumSize(PosUIManager.getSize(120, 0));
        centerPanel.add((Component)rightPanel, "East");
        this.add((Component)centerPanel, "Center");
    }

    public void rendererOrderPanel() {
        this.orderPanel.removeAll();
        ArrayList<OrderType> orderTypes = new ArrayList<OrderType>();
        orderTypes.addAll(Application.getInstance().getOrderTypes());
        if (RootView.getInstance().isMaintenanceMode()) {
            OrderType newOrderType = new OrderType();
            newOrderType.setName("");
            newOrderType.setShowInLoginScreen(true);
            newOrderType.setEnabled(true);
            orderTypes.add(newOrderType);
        }
        for (OrderType orderType : orderTypes) {
            this.orderPanel.add((Component)new OrderTypeButton(orderType), "grow");
        }
        FloorLayoutPlugin floorLayoutPlugin = (FloorLayoutPlugin)ExtensionManager.getPlugin(FloorLayoutPlugin.class);
        if (floorLayoutPlugin != null) {
            this.orderPanel.add(this.createBarTabButton(orderTypes), "grow");
        }
        this.orderPanel.repaint();
    }

    private Component createBarTabButton(List<OrderType> orderTypes) {
        PosButton btnNewBarTab = new PosButton("NEW BAR TAB");
        ArrayList<OrderType> barTabOrders = new ArrayList<OrderType>();
        for (OrderType orderType : orderTypes) {
            if (!orderType.isBarTab().booleanValue()) continue;
            barTabOrders.add(orderType);
        }
        if (barTabOrders.isEmpty()) {
            btnNewBarTab.setEnabled(false);
        }
        btnNewBarTab.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                List<OrderType> orderTypes = Application.getInstance().getOrderTypes();
                ArrayList<OrderType> barTabOrders = new ArrayList<OrderType>();
                for (OrderType orderType : orderTypes) {
                    if (!orderType.isBarTab().booleanValue()) continue;
                    barTabOrders.add(orderType);
                }
                OrderType orderType = null;
                if (barTabOrders.size() > 1) {
                    OrderTypeSelectionDialog dialog = new OrderTypeSelectionDialog();
                    dialog.setTitle("SELECT ORDER TYPE");
                    dialog.setSize(400, 200);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    orderType = dialog.getSelectedOrderType();
                } else {
                    orderType = (OrderType)barTabOrders.get(0);
                }
                if (!orderType.isBarTab().booleanValue()) {
                    POSMessageDialog.showMessage("Selected order type is not bar.");
                    return;
                }
                new NewBarTabAction(orderType, null, Application.getPosWindow()).actionPerformed(e);
            }
        });
        return btnNewBarTab;
    }

    private JPanel createActivityPanel() {
        JPanel activityPanel = new JPanel(new BorderLayout(5, 5));
        JPanel innerActivityPanel = new JPanel((LayoutManager)new MigLayout("hidemode 3, fill, ins 0", "fill, grow", ""));
        JPanel firstRowButtonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        final JXCollapsiblePane secondRowButtonPanel = new JXCollapsiblePane();
        secondRowButtonPanel.setAnimated(false);
        secondRowButtonPanel.setCollapsed(true);
        secondRowButtonPanel.setVisible(false);
        secondRowButtonPanel.getContentPane().setLayout(new GridLayout(1, 0, 5, 5));
        if (Application.getInstance().getTerminal().isHasCashDrawer().booleanValue()) {
            firstRowButtonPanel.add(this.btnOrderInfo);
            firstRowButtonPanel.add(this.btnEditTicket);
            firstRowButtonPanel.add(this.btnSettleTicket);
            firstRowButtonPanel.add(this.btnGroupSettle);
            firstRowButtonPanel.add(this.btnCloseOrder);
            secondRowButtonPanel.getContentPane().add(this.btnSplitTicket);
            secondRowButtonPanel.getContentPane().add(this.btnReopenTicket);
            secondRowButtonPanel.getContentPane().add(this.btnVoidTicket);
            secondRowButtonPanel.getContentPane().add(this.btnRefundTicket);
            secondRowButtonPanel.getContentPane().add(this.btnAssignDriver);
        } else {
            firstRowButtonPanel.add(this.btnOrderInfo);
            firstRowButtonPanel.add(this.btnEditTicket);
            firstRowButtonPanel.add(this.btnCloseOrder);
            firstRowButtonPanel.add(this.btnSplitTicket);
            secondRowButtonPanel.getContentPane().add(this.btnReopenTicket);
            secondRowButtonPanel.getContentPane().add(this.btnVoidTicket);
            secondRowButtonPanel.getContentPane().add(this.btnRefundTicket);
            secondRowButtonPanel.getContentPane().add(this.btnAssignDriver);
        }
        innerActivityPanel.add(firstRowButtonPanel);
        innerActivityPanel.add((Component)secondRowButtonPanel, "newline");
        final PosButton btnMore = new PosButton(POSConstants.MORE_ACTIVITY_BUTTON_TEXT);
        btnMore.setPreferredSize(new Dimension(PosUIManager.getSize(78), 0));
        btnMore.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean collapsed = secondRowButtonPanel.isCollapsed();
                secondRowButtonPanel.setVisible(collapsed);
                secondRowButtonPanel.setCollapsed(!collapsed);
                if (collapsed) {
                    btnMore.setText(POSConstants.LESS_ACTIVITY_BUTTON_TEXT);
                } else {
                    btnMore.setText(POSConstants.MORE_ACTIVITY_BUTTON_TEXT);
                }
            }
        });
        activityPanel.add(innerActivityPanel);
        activityPanel.add((Component)btnMore, "East");
        return activityPanel;
    }

    protected void doCloseOrder() {
        Ticket ticket = this.getFirstSelectedTicket();
        if (ticket == null) {
            return;
        }
        ticket = TicketDAO.getInstance().loadFullTicket(ticket.getId());
        int due = (int)POSUtil.getDouble(ticket.getDueAmount());
        if (due != 0) {
            POSMessageDialog.showError(this, Messages.getString("SwitchboardView.5"));
            return;
        }
        int option = JOptionPane.showOptionDialog(Application.getPosWindow(), Messages.getString("SwitchboardView.6") + ticket.getId() + Messages.getString("SwitchboardView.7"), POSConstants.CONFIRM, 2, 1, null, null, null);
        if (option != 0) {
            return;
        }
        OrderController.closeOrder(ticket);
        this.updateTicketList();
    }

    protected void doAssignDriver() {
        try {
            int option;
            Ticket ticket = this.getFirstSelectedTicket();
            if (ticket == null) {
                return;
            }
            if (!ticket.getOrderType().isDelivery().booleanValue()) {
                POSMessageDialog.showError(this, Messages.getString("SwitchboardView.8"));
                return;
            }
            User assignedDriver = ticket.getAssignedDriver();
            if (assignedDriver != null && (option = JOptionPane.showOptionDialog(Application.getPosWindow(), Messages.getString("SwitchboardView.9"), POSConstants.CONFIRM, 0, 3, null, null, null)) != 0) {
                return;
            }
            this.orderServiceExtension.assignDriver(ticket.getId());
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
            POSMessageDialog.showError(this, e.getMessage());
            LogFactory.getLog(SwitchboardView.class).error((Object)e);
        }
    }

    private void doReopenTicket() {
        try {
            int ticketId = NumberSelectionDialog2.takeIntInput(Messages.getString("SwitchboardView.10"));
            if (ticketId == -1) {
                return;
            }
            Ticket ticket = TicketDAO.getInstance().loadFullTicket(ticketId);
            if (ticket == null) {
                throw new PosException(POSConstants.NO_TICKET_WITH_ID + " " + ticketId + " " + POSConstants.FOUND);
            }
            if (!ticket.isClosed().booleanValue()) {
                throw new PosException(POSConstants.TICKET_IS_NOT_CLOSED);
            }
            if (ticket.isVoided().booleanValue()) {
                throw new PosException(Messages.getString("SwitchboardView.11"));
            }
            ticket.setClosed(false);
            ticket.setClosingDate(null);
            ticket.setReOpened(true);
            TicketDAO.getInstance().saveOrUpdate(ticket);
            OrderInfoView view = new OrderInfoView(Arrays.asList(ticket));
            OrderInfoDialog dialog = new OrderInfoDialog(view);
            dialog.setSize(PosUIManager.getSize(400), PosUIManager.getSize(600));
            dialog.setDefaultCloseOperation(2);
            dialog.setLocationRelativeTo(Application.getPosWindow());
            dialog.setVisible(true);
            this.updateTicketList();
        }
        catch (PosException e) {
            POSMessageDialog.showError(this, e.getLocalizedMessage());
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
        }
    }

    private void doSettleTicket() {
        try {
            if (!POSUtil.checkDrawerAssignment()) {
                return;
            }
            Ticket ticket = null;
            List<Ticket> selectedTickets = this.ticketList.getSelectedTickets();
            if (selectedTickets.size() > 0) {
                ticket = selectedTickets.get(0);
            } else {
                int ticketId = NumberSelectionDialog2.takeIntInput(Messages.getString("SwitchboardView.12"));
                if (ticketId == -1) {
                    return;
                }
                ticket = TicketService.getTicket(ticketId);
            }
            new SettleTicketAction(ticket.getId()).execute();
            this.updateTicketList();
        }
        catch (PosException e) {
            POSMessageDialog.showError(this, e.getMessage());
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
            POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
        }
    }

    private void doShowOrderInfo() {
        this.doShowOrderInfo(this.ticketList.getSelectedTickets());
    }

    private void doShowOrderInfo(List<Ticket> tickets) {
        try {
            if (tickets.size() == 0) {
                int ticketId = NumberSelectionDialog2.takeIntInput(Messages.getString("SwitchboardView.0"));
                if (ticketId == -1) {
                    return;
                }
                Ticket ticket = TicketService.getTicket(ticketId);
                tickets.add(ticket);
            }
            ArrayList<Ticket> ticketsToShow = new ArrayList<Ticket>();
            for (int i = 0; i < tickets.size(); ++i) {
                Ticket ticket = tickets.get(i);
                ticketsToShow.add(TicketDAO.getInstance().loadFullTicket(ticket.getId()));
            }
            OrderInfoView view = new OrderInfoView(ticketsToShow);
            OrderInfoDialog dialog = new OrderInfoDialog(view);
            dialog.setSize(PosUIManager.getSize(400), PosUIManager.getSize(600));
            dialog.setDefaultCloseOperation(2);
            dialog.setLocationRelativeTo(Application.getPosWindow());
            dialog.setVisible(true);
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
        }
    }

    private void doSplitTicket() {
        try {
            Ticket selectedTicket = this.getFirstSelectedTicket();
            if (selectedTicket == null) {
                return;
            }
            Ticket ticket = TicketDAO.getInstance().loadFullTicket(selectedTicket.getId());
            SplitTicketDialog dialog = new SplitTicketDialog();
            dialog.setTicket(ticket);
            dialog.open();
            this.updateView();
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
        }
    }

    private void doEditTicket() {
        try {
            Ticket ticket = null;
            List<Ticket> selectedTickets = this.ticketList.getSelectedTickets();
            if (selectedTickets.size() > 0) {
                ticket = selectedTickets.get(0);
            } else {
                int ticketId = NumberSelectionDialog2.takeIntInput(Messages.getString("SwitchboardView.12"));
                if (ticketId == -1) {
                    return;
                }
                ticket = TicketService.getTicket(ticketId);
            }
            this.editTicket(ticket);
        }
        catch (PosException e) {
            POSMessageDialog.showError(this, e.getMessage());
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, e.getMessage(), e);
        }
    }

    private void editTicket(Ticket ticket) {
        if (ticket.isPaid().booleanValue()) {
            POSMessageDialog.showMessage(this, Messages.getString("SwitchboardView.14"));
            return;
        }
        Ticket ticketToEdit = TicketDAO.getInstance().loadFullTicket(ticket.getId());
        OrderView.getInstance().setCurrentTicket(ticketToEdit);
        RootView.getInstance().showView("ORDER_VIEW");
        OrderView.getInstance().getTicketView().getTxtSearchItem().requestFocus();
    }

    public void doHomeDelivery(OrderType ticketType) {
    }

    private void doGroupSettle() {
        if (!POSUtil.checkDrawerAssignment()) {
            return;
        }
        TicketSelectionDialog ticketSelectionDialog = new TicketSelectionDialog();
        ticketSelectionDialog.open();
        if (ticketSelectionDialog.isCanceled()) {
            return;
        }
        List<Ticket> selectedTickets = ticketSelectionDialog.getSelectedTickets();
        if (selectedTickets == null) {
            return;
        }
        ArrayList<Ticket> tickets = new ArrayList<Ticket>();
        for (int i = 0; i < selectedTickets.size(); ++i) {
            Ticket ticket = selectedTickets.get(i);
            Ticket fullTicket = TicketDAO.getInstance().loadFullTicket(ticket.getId());
            if (fullTicket.getOrderType().isBarTab().booleanValue()) continue;
            tickets.add(fullTicket);
        }
        GroupSettleTicketDialog posDialog = new GroupSettleTicketDialog(tickets);
        posDialog.setSize(Application.getPosWindow().getSize());
        posDialog.setDefaultCloseOperation(2);
        posDialog.openUndecoratedFullScreen();
        this.updateTicketList();
    }

    public void updateView() {
        Set<UserPermission> permissions;
        User user = Application.getCurrentUser();
        UserType userType = user.getType();
        if (userType != null && (permissions = userType.getPermissions()) != null) {
            this.btnEditTicket.setEnabled(false);
            this.btnGroupSettle.setEnabled(false);
            this.btnReopenTicket.setEnabled(false);
            this.btnSettleTicket.setEnabled(false);
            this.btnSplitTicket.setEnabled(false);
            for (UserPermission permission : permissions) {
                if (permission.equals(UserPermission.VOID_TICKET)) {
                    this.btnVoidTicket.setEnabled(true);
                    continue;
                }
                if (permission.equals(UserPermission.SETTLE_TICKET)) {
                    this.btnSettleTicket.setEnabled(true);
                    this.btnGroupSettle.setEnabled(true);
                    continue;
                }
                if (permission.equals(UserPermission.REOPEN_TICKET)) {
                    this.btnReopenTicket.setEnabled(true);
                    continue;
                }
                if (permission.equals(UserPermission.SPLIT_TICKET)) {
                    this.btnSplitTicket.setEnabled(true);
                    continue;
                }
                if (!permission.equals(UserPermission.CREATE_TICKET)) continue;
                this.btnEditTicket.setEnabled(true);
            }
        }
        this.updateTicketList();
    }

    @Override
    public synchronized void updateTicketList() {
        this.ticketList.updateTicketList();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            this.updateView();
            this.ticketList.setAutoUpdateCheck(true);
        } else {
            this.ticketList.setAutoUpdateCheck(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == this.btnEditTicket) {
            this.doEditTicket();
        } else if (source == this.btnGroupSettle) {
            this.doGroupSettle();
        } else if (source == this.btnOrderInfo) {
            this.doShowOrderInfo();
        } else if (source == this.btnReopenTicket) {
            this.doReopenTicket();
        } else if (source == this.btnSettleTicket) {
            this.doSettleTicket();
        } else if (source == this.btnSplitTicket) {
            this.doSplitTicket();
        }
    }

    public Ticket getFirstSelectedTicket() {
        List<Ticket> selectedTickets = this.ticketList.getSelectedTickets();
        if (selectedTickets.size() == 0 || selectedTickets.size() > 1) {
            POSMessageDialog.showMessage(this, Messages.getString("SwitchboardView.22"));
            return null;
        }
        Ticket ticket = selectedTickets.get(0);
        return ticket;
    }

    @Override
    public Ticket getSelectedTicket() {
        List<Ticket> selectedTickets = this.ticketList.getSelectedTickets();
        if (selectedTickets.size() == 0 || selectedTickets.size() > 1) {
            return null;
        }
        Ticket ticket = selectedTickets.get(0);
        return ticket;
    }

    @Override
    public String getViewName() {
        return VIEW_NAME;
    }

    @Override
    public void ticketListUpdated() {
        PaymentStatusFilter paymentStatusFilter = TerminalConfig.getPaymentStatusFilter();
        String orderTypeFilter = TerminalConfig.getOrderTypeFilter();
        String title = POSConstants.OPEN_TICKETS_AND_ACTIVITY + " [ FILTERS: " + (Object)((Object)paymentStatusFilter) + ", " + orderTypeFilter + " ]";
        this.ticketsListPanelBorder.setTitle(title);
    }

    @Override
    public void updateCustomerTicketList(Integer customerId) {
    }
}

