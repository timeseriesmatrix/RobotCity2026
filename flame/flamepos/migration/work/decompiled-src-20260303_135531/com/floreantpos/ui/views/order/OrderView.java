/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 *  org.hibernate.Session
 *  org.hibernate.StaleObjectStateException
 *  org.hibernate.Transaction
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.PosLog;
import com.floreantpos.customer.CustomerSelectorDialog;
import com.floreantpos.customer.CustomerSelectorFactory;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.OrderServiceExtension;
import com.floreantpos.extension.OrderServiceFactory;
import com.floreantpos.main.Application;
import com.floreantpos.model.Customer;
import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemCookingInstruction;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.dao.CustomerDAO;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.MiscTicketItemDialog;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.PasswordEntryDialog;
import com.floreantpos.ui.dialog.SeatSelectionDialog;
import com.floreantpos.ui.tableselection.TableSelectorDialog;
import com.floreantpos.ui.tableselection.TableSelectorFactory;
import com.floreantpos.ui.views.CookingInstructionSelectionView;
import com.floreantpos.ui.views.order.CategoryView;
import com.floreantpos.ui.views.order.GroupView;
import com.floreantpos.ui.views.order.MenuItemView;
import com.floreantpos.ui.views.order.OrderController;
import com.floreantpos.ui.views.order.TicketView;
import com.floreantpos.ui.views.order.ViewPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;

public class OrderView
extends ViewPanel {
    private HashMap<String, JComponent> views = new HashMap();
    public static final String VIEW_NAME = "ORDER_VIEW";
    private static OrderView instance;
    private Ticket currentTicket;
    private CategoryView categoryView = new CategoryView();
    private TransparentPanel midContainer = new TransparentPanel(new BorderLayout(5, 5));
    private TicketView ticketView = new TicketView();
    private GroupView groupView = new GroupView();
    private MenuItemView itemView = new MenuItemView();
    private OrderController orderController = new OrderController(this);
    private JPanel actionButtonPanel = new JPanel((LayoutManager)new MigLayout("fill, ins 2, hidemode 3", "sg, fill", ""));
    private PosButton btnHold = new PosButton(POSConstants.HOLD_BUTTON_TEXT);
    private PosButton btnDone = new PosButton(POSConstants.SAVE_BUTTON_TEXT);
    private PosButton btnSend = new PosButton(POSConstants.SEND_TO_KITCHEN);
    private PosButton btnCancel = new PosButton(POSConstants.CANCEL_BUTTON_TEXT);
    private PosButton btnGuestNo = new PosButton(POSConstants.GUEST_NO_BUTTON_TEXT);
    private PosButton btnSeatNo = new PosButton("SEAT:");
    private PosButton btnMisc = new PosButton(POSConstants.MISC_BUTTON_TEXT);
    private PosButton btnOrderType = new PosButton(POSConstants.ORDER_TYPE_BUTTON_TEXT);
    private PosButton btnTableNumber = new PosButton(POSConstants.TABLE_NO_BUTTON_TEXT);
    private PosButton btnCustomer = new PosButton(POSConstants.CUSTOMER_SELECTION_BUTTON_TEXT);
    private PosButton btnCookingInstruction = new PosButton(IconFactory.getIcon("/ui_icons/", "cooking-instruction.png"));
    private PosButton btnDiscount = new PosButton(Messages.getString("TicketView.43"));
    private PosButton btnDeliveryInfo = new PosButton("DELIVERY INFO");

    private OrderView() {
        this.initComponents();
    }

    public void addView(String viewName, JComponent view) {
        JComponent oldView = this.views.get(viewName);
        if (oldView != null) {
            return;
        }
        this.midContainer.add((Component)view, viewName);
    }

    private void initComponents() {
        this.setOpaque(false);
        this.setLayout(new BorderLayout(2, 1));
        this.midContainer.setOpaque(false);
        this.midContainer.setBorder(null);
        this.midContainer.add((Component)this.groupView, "North");
        this.midContainer.add(this.itemView);
        this.add((Component)this.categoryView, "East");
        this.add((Component)this.ticketView, "West");
        this.add((Component)this.midContainer, "Center");
        this.add((Component)this.actionButtonPanel, "South");
        this.addActionButtonPanel();
        this.btnOrderType.setVisible(false);
        this.showView("VIEW_EMPTY");
    }

    private void handleTicketItemSelection() {
        MenuItemDAO dao;
        MenuItem menuItem;
        ITicketItem selectedItem = this.ticketView.getTicketViewerTable().getSelected();
        TicketItem selectedTicketItem = null;
        OrderView orderView = OrderView.getInstance();
        if (selectedItem instanceof TicketItem && (menuItem = (dao = new MenuItemDAO()).get((selectedTicketItem = (TicketItem)selectedItem).getItemId())) != null) {
            MenuItemView itemView;
            MenuGroup menuGroup = menuItem.getParent();
            if (!menuGroup.equals((itemView = OrderView.getInstance().getItemView()).getMenuGroup())) {
                itemView.setMenuGroup(menuGroup);
            }
            orderView.showView("ITEM_VIEW");
            itemView.selectItem(menuItem);
            MenuCategory menuCategory = menuGroup.getParent();
            orderView.getCategoryView().setSelectedCategory(menuCategory);
        }
        if (selectedItem == null) {
            this.btnCookingInstruction.setEnabled(false);
            this.btnDiscount.setEnabled(false);
        } else {
            this.btnCookingInstruction.setEnabled(selectedItem.canAddCookingInstruction());
            this.btnDiscount.setEnabled(selectedItem.canAddDiscount());
        }
    }

    private void addActionButtonPanel() {
        this.ticketView.getTicketViewerTable().getSelectionModel().addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    OrderView.this.handleTicketItemSelection();
                }
            }
        });
        this.btnDone.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    OrderView.this.ticketView.doFinishOrder();
                }
                catch (StaleObjectStateException x) {
                    POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TicketView.22"));
                    return;
                }
                catch (PosException x) {
                    POSMessageDialog.showError(x.getMessage());
                }
                catch (Exception x) {
                    POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (OrderView.this.ticketView.isCancelable()) {
                    OrderView.this.ticketView.doCancelOrder();
                    return;
                }
                int result = POSMessageDialog.showYesNoQuestionDialog(null, "Items have been sent to kitchen, are you sure to cancel this ticket?", "Confirm");
                if (result != 0) {
                    return;
                }
                OrderView.this.ticketView.doCancelOrder();
                OrderView.this.ticketView.setAllowToLogOut(true);
            }
        });
        this.btnSend.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    OrderView.this.ticketView.sendTicketToKitchen();
                    OrderView.this.ticketView.updateView();
                    POSMessageDialog.showMessage("Items sent to kitchen");
                }
                catch (StaleObjectStateException x) {
                    POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TicketView.22"));
                    return;
                }
                catch (PosException x) {
                    POSMessageDialog.showError(x.getMessage());
                }
                catch (Exception x) {
                    POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        this.btnOrderType.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
            }
        });
        this.btnCustomer.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                OrderView.this.doAddEditCustomer();
            }
        });
        this.btnMisc.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                OrderView.this.doInsertMisc(evt);
            }
        });
        this.btnGuestNo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                OrderView.this.btnCustomerNumberActionPerformed();
            }
        });
        this.btnSeatNo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                OrderView.this.doAddSeatNumber();
            }
        });
        this.btnTableNumber.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                OrderView.this.updateTableNumber();
            }
        });
        this.btnCookingInstruction.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                OrderView.this.doAddCookingInstruction();
            }
        });
        this.btnHold.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                OrderType orderType = OrderView.this.currentTicket.getOrderType();
                if (orderType.isShowTableSelection().booleanValue() && orderType.isRequiredCustomerData().booleanValue() && !Application.getCurrentUser().hasPermission(UserPermission.HOLD_TICKET)) {
                    String password = PasswordEntryDialog.show(Application.getPosWindow(), "Please enter privileged password");
                    if (StringUtils.isEmpty((String)password)) {
                        return;
                    }
                    User user2 = UserDAO.getInstance().findUserBySecretKey(password);
                    if (user2 == null) {
                        POSMessageDialog.showError(Application.getPosWindow(), "No user found with that secret key");
                        return;
                    }
                    if (!user2.hasPermission(UserPermission.HOLD_TICKET)) {
                        POSMessageDialog.showError(Application.getPosWindow(), "No permission");
                        return;
                    }
                }
                if (!(OrderView.this.currentTicket.isBarTab().booleanValue() || OrderView.this.ticketView.getTicket().getTicketItems() != null && OrderView.this.ticketView.getTicket().getTicketItems().size() != 0)) {
                    POSMessageDialog.showError(POSConstants.TICKET_IS_EMPTY_);
                    return;
                }
                OrderView.this.ticketView.doHoldOrder();
                OrderView.this.ticketView.setAllowToLogOut(true);
            }
        });
        this.btnDiscount.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                OrderView.this.addDiscount();
            }
        });
        this.btnDeliveryInfo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                OrderView.this.doShowDeliveryDialog();
            }
        });
        this.actionButtonPanel.add(this.btnOrderType);
        this.actionButtonPanel.add(this.btnCustomer);
        this.actionButtonPanel.add(this.btnDeliveryInfo);
        this.actionButtonPanel.add(this.btnTableNumber);
        this.actionButtonPanel.add(this.btnGuestNo);
        this.actionButtonPanel.add(this.btnSeatNo);
        this.actionButtonPanel.add(this.btnCookingInstruction);
        this.actionButtonPanel.add(this.btnMisc);
        this.actionButtonPanel.add(this.btnHold);
        this.actionButtonPanel.add(this.btnSend);
        this.actionButtonPanel.add(this.btnCancel);
        this.actionButtonPanel.add(this.btnDone);
        this.btnCookingInstruction.setEnabled(false);
        this.btnDeliveryInfo.setVisible(false);
    }

    protected void doShowDeliveryDialog() {
        Customer customer = CustomerDAO.getInstance().findById(this.currentTicket.getCustomerId());
        OrderServiceFactory.getOrderService().showDeliveryInfo(this.currentTicket.getOrderType(), customer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateTableNumber() {
        Transaction transaction = null;
        try (Session session = null;){
            Ticket thisTicket = this.currentTicket;
            TableSelectorDialog dialog = TableSelectorFactory.createTableSelectorDialog(thisTicket.getOrderType());
            dialog.setCreateNewTicket(false);
            if (thisTicket != null) {
                dialog.setTicket(thisTicket);
            }
            dialog.openUndecoratedFullScreen();
            if (dialog.isCanceled()) {
                return;
            }
            List<ShopTable> tables = dialog.getSelectedTables();
            if (tables == null) {
                return;
            }
            session = TicketDAO.getInstance().createNewSession();
            transaction = session.beginTransaction();
            this.clearShopTable(session, thisTicket);
            session.saveOrUpdate((Object)thisTicket);
            for (ShopTable shopTable : tables) {
                shopTable.setServing(true);
                session.merge((Object)shopTable);
                thisTicket.addTable(shopTable.getTableNumber());
            }
            session.merge((Object)thisTicket);
            transaction.commit();
            this.actionUpdate();
        }
    }

    private void clearShopTable(Session session, Ticket thisTicket) {
        ShopTableDAO shopTableDao = ShopTableDAO.getInstance();
        List<ShopTable> tables2 = shopTableDao.getTables(thisTicket);
        if (tables2 == null) {
            return;
        }
        shopTableDao.releaseAndDeleteTicketTables(thisTicket);
        tables2.clear();
    }

    protected void btnCustomerNumberActionPerformed() {
        Ticket thisTicket = this.currentTicket;
        int guestNumber = thisTicket.getNumberOfGuests();
        NumberSelectionDialog2 dialog = new NumberSelectionDialog2();
        dialog.setTitle(POSConstants.NUMBER_OF_GUESTS);
        dialog.setValue(guestNumber);
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return;
        }
        guestNumber = (int)dialog.getValue();
        if (guestNumber == 0) {
            POSMessageDialog.showError(Application.getPosWindow(), POSConstants.GUEST_NUMBER_CANNOT_BE_0);
            return;
        }
        thisTicket.setNumberOfGuests(guestNumber);
        this.actionUpdate();
    }

    protected void doAddSeatNumber() {
        SeatSelectionDialog seatDialog = new SeatSelectionDialog(this.currentTicket.getTableNumbers(), this.getSeatNumbers());
        seatDialog.setTitle("Select Seat");
        seatDialog.pack();
        seatDialog.open();
        if (seatDialog.isCanceled()) {
            return;
        }
        int seatNumber = seatDialog.getSeatNumber();
        if (seatNumber == -1) {
            NumberSelectionDialog2 dialog = new NumberSelectionDialog2();
            dialog.setTitle("Enter seat number");
            dialog.pack();
            dialog.open();
            if (dialog.isCanceled()) {
                return;
            }
            seatNumber = (int)dialog.getValue();
        }
        this.btnSeatNo.setText("SEAT: " + seatNumber);
        this.btnSeatNo.putClientProperty("SEAT_NO", seatNumber);
        this.doAddSeatTreatTicketItem(seatNumber);
    }

    private void doAddSeatTreatTicketItem(Integer seatNumber) {
        TicketItem ticketItem = new TicketItem();
        if (seatNumber == 0) {
            ticketItem.setName("Seat** Shared");
        } else {
            ticketItem.setName("Seat** " + seatNumber);
        }
        ticketItem.setShouldPrintToKitchen(true);
        ticketItem.setTreatAsSeat(true);
        ticketItem.setSeatNumber(seatNumber);
        ticketItem.setTicket(this.currentTicket);
        this.ticketView.addTicketItem(ticketItem);
    }

    private int getLastSeatNumber() {
        int lastSeatNumber = 0;
        List<TicketItem> ticketItems = this.currentTicket.getTicketItems();
        if (ticketItems != null && !ticketItems.isEmpty()) {
            TicketItem lastTicketItem = ticketItems.get(ticketItems.size() - 1);
            lastSeatNumber = lastTicketItem.getSeatNumber();
        }
        return lastSeatNumber;
    }

    protected Integer getSelectedSeatNumber() {
        Object seatNumber = this.btnSeatNo.getClientProperty("SEAT_NO");
        if (seatNumber == null) {
            return 0;
        }
        Integer seatNo = (Integer)seatNumber;
        boolean sendToKitchen = false;
        for (TicketItem ticketItem : this.currentTicket.getTicketItems()) {
            int existingSeatNumber;
            if (!ticketItem.isTreatAsSeat().booleanValue() || (existingSeatNumber = ticketItem.getSeatNumber().intValue()) != seatNo) continue;
            sendToKitchen = ticketItem.isPrintedToKitchen();
        }
        if (sendToKitchen) {
            this.doAddSeatTreatTicketItem(seatNo);
        }
        return seatNo;
    }

    protected List<Integer> getSeatNumbers() {
        ArrayList<Integer> seatNumbers = new ArrayList<Integer>();
        for (TicketItem ticketItem : this.currentTicket.getTicketItems()) {
            if (!ticketItem.isTreatAsSeat().booleanValue() || seatNumbers.contains(ticketItem.getSeatNumber())) continue;
            seatNumbers.add(ticketItem.getSeatNumber());
        }
        return seatNumbers;
    }

    protected void doInsertMisc(ActionEvent evt) {
        MiscTicketItemDialog dialog = new MiscTicketItemDialog();
        dialog.pack();
        dialog.open();
        if (!dialog.isCanceled()) {
            TicketItem ticketItem = dialog.getTicketItem();
            ticketItem.setTicket(this.currentTicket);
            ticketItem.calculatePrice();
            this.ticketView.addTicketItem(ticketItem);
        }
    }

    protected void doAddEditCustomer() {
        CustomerSelectorDialog dialog = CustomerSelectorFactory.createCustomerSelectorDialog(this.currentTicket.getOrderType());
        dialog.setCreateNewTicket(false);
        if (this.currentTicket != null) {
            dialog.setTicket(this.currentTicket);
        }
        dialog.openUndecoratedFullScreen();
        if (!dialog.isCanceled()) {
            this.currentTicket.setCustomer(dialog.getSelectedCustomer());
        }
    }

    protected void addDiscount() {
        ITicketItem selectedObject = this.ticketView.getTicketViewerTable().getSelected();
        if (!(selectedObject instanceof TicketItem)) {
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TicketView.20"));
            return;
        }
    }

    protected void doAddCookingInstruction() {
        try {
            ITicketItem object = this.ticketView.getTicketViewerTable().getSelected();
            if (!(object instanceof TicketItem)) {
                POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TicketView.20"));
                return;
            }
            TicketItem ticketItem = (TicketItem)object;
            if (ticketItem.isPrintedToKitchen().booleanValue()) {
                POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TicketView.21"));
                return;
            }
            CookingInstructionSelectionView dialog = new CookingInstructionSelectionView();
            dialog.setSize(1000, 680);
            dialog.setLocationRelativeTo(Application.getPosWindow());
            dialog.setVisible(true);
            if (dialog.isCanceled()) {
                return;
            }
            List<TicketItemCookingInstruction> instructions = dialog.getTicketItemCookingInstructions();
            ticketItem.addCookingInstructions(instructions);
            this.ticketView.getTicketViewerTable().updateView();
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
            POSMessageDialog.showError(e.getMessage());
        }
    }

    public void actionUpdate() {
        if (this.currentTicket != null) {
            OrderType type = this.currentTicket.getOrderType();
            if (type.isPrepaid().booleanValue()) {
                this.btnDone.setVisible(false);
            } else {
                this.btnDone.setVisible(true);
            }
            if (!type.isShouldPrintToKitchen().booleanValue()) {
                this.btnSend.setEnabled(false);
            } else {
                this.btnSend.setEnabled(true);
            }
            if (!type.isAllowSeatBasedOrder().booleanValue()) {
                this.btnSeatNo.setVisible(false);
            } else {
                this.btnSeatNo.setVisible(true);
                int lastSeatNumber = this.getLastSeatNumber();
                this.btnSeatNo.putClientProperty("SEAT_NO", lastSeatNumber);
                if (lastSeatNumber > 0) {
                    this.btnSeatNo.setText("SEAT:" + lastSeatNumber);
                } else {
                    this.btnSeatNo.setText("SEAT:");
                }
            }
            if (!type.isShowTableSelection().booleanValue()) {
                this.btnGuestNo.setVisible(false);
                this.btnTableNumber.setVisible(false);
            } else {
                this.btnGuestNo.setVisible(true);
                this.btnTableNumber.setVisible(true);
                List<Integer> tableNumbers = this.currentTicket.getTableNumbers();
                if (tableNumbers != null) {
                    String tables = this.getTableNumbers(this.currentTicket.getTableNumbers());
                    this.btnTableNumber.setText("<html><center>TABLE: " + tables + "</center><html/>");
                } else {
                    this.btnTableNumber.setText("TABLE");
                }
                this.btnGuestNo.setText("GUEST: " + String.valueOf(this.currentTicket.getNumberOfGuests()));
            }
            OrderServiceExtension orderService = (OrderServiceExtension)ExtensionManager.getPlugin(OrderServiceExtension.class);
            if (orderService != null && type.isDelivery().booleanValue() && type.isRequiredCustomerData().booleanValue()) {
                this.btnDeliveryInfo.setVisible(true);
            }
        }
    }

    private String getTableNumbers(List<Integer> numbers) {
        String tableNumbers = "";
        if (numbers != null && !numbers.isEmpty()) {
            Iterator<Integer> iterator = numbers.iterator();
            while (iterator.hasNext()) {
                Integer n = iterator.next();
                tableNumbers = tableNumbers + n;
                if (!iterator.hasNext()) continue;
                tableNumbers = tableNumbers + ", ";
            }
            return tableNumbers;
        }
        return tableNumbers;
    }

    public void showView(String viewName) {
    }

    public CategoryView getCategoryView() {
        return this.categoryView;
    }

    public void setCategoryView(CategoryView categoryView) {
        this.categoryView = categoryView;
    }

    public GroupView getGroupView() {
        return this.groupView;
    }

    public void setGroupView(GroupView groupView) {
        this.groupView = groupView;
    }

    public MenuItemView getItemView() {
        return this.itemView;
    }

    public void setItemView(MenuItemView itemView) {
        this.itemView = itemView;
    }

    public TicketView getTicketView() {
        return this.ticketView;
    }

    public void setTicketView(TicketView ticketView) {
        this.ticketView = ticketView;
    }

    public OrderController getOrderController() {
        return this.orderController;
    }

    public Ticket getCurrentTicket() {
        return this.currentTicket;
    }

    public void setCurrentTicket(Ticket currentTicket) {
        this.currentTicket = currentTicket;
        this.ticketView.setTicket(currentTicket);
        this.actionUpdate();
        this.resetView();
    }

    public static synchronized OrderView getInstance() {
        if (instance == null) {
            instance = new OrderView();
        }
        return instance;
    }

    public void resetView() {
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag) {
            try {
                this.categoryView.initialize();
            }
            catch (Throwable t) {
                POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, t);
            }
        } else {
            this.categoryView.cleanup();
        }
        super.setVisible(aFlag);
    }

    @Override
    public String getViewName() {
        return VIEW_NAME;
    }
}

