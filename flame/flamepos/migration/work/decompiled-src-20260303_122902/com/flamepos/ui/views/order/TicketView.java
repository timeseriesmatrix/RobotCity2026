/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.report.ReceiptPrintService;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.AutomatedWeightInputDialog;
import com.floreantpos.ui.dialog.BasicWeightInputDialog;
import com.floreantpos.ui.dialog.ItemSearchDialog;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.SeatSelectionDialog;
import com.floreantpos.ui.ticket.TicketViewerTable;
import com.floreantpos.ui.views.order.OrderController;
import com.floreantpos.ui.views.order.OrderTypeSelectionDialog2;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.ui.views.order.actions.OrderListener;
import com.floreantpos.util.CurrencyUtil;
import com.floreantpos.util.DrawerUtil;
import com.floreantpos.util.NumberUtil;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.miginfocom.swing.MigLayout;

public class TicketView
extends JPanel {
    private Vector<OrderListener> orderListeners = new Vector();
    private Ticket ticket;
    private PosButton btnDecreaseAmount;
    private PosButton btnDelete = new PosButton();
    private PosButton btnIncreaseAmount = new PosButton();
    private PosButton btnEdit = new PosButton("...");
    private PosButton btnScrollDown;
    private PosButton btnScrollUp = new PosButton();
    private TransparentPanel ticketItemActionPanel;
    private JScrollPane ticketScrollPane;
    private PosButton btnTotal;
    private TicketViewerTable ticketViewerTable;
    private JPanel itemSearchPanel;
    private JTextField txtSearchItem;
    private TitledBorder titledBorder = new TitledBorder("");
    private Border border = new CompoundBorder(this.titledBorder, new EmptyBorder(2, 2, 2, 2));
    private boolean cancelable;
    private boolean allowToLogOut;
    public static final String VIEW_NAME = "TICKET_VIEW";

    public TicketView() {
        this.initComponents();
    }

    private void initComponents() {
        this.titledBorder.setTitleJustification(2);
        this.setBorder(this.border);
        this.setLayout(new BorderLayout(5, 5));
        this.itemSearchPanel = new JPanel();
        this.ticketItemActionPanel = new TransparentPanel();
        this.btnDecreaseAmount = new PosButton();
        this.btnScrollDown = new PosButton();
        this.ticketViewerTable = new TicketViewerTable();
        this.ticketScrollPane = new PosScrollPane(this.ticketViewerTable);
        this.ticketScrollPane.setHorizontalScrollBarPolicy(31);
        this.ticketScrollPane.setVerticalScrollBarPolicy(21);
        this.ticketScrollPane.setPreferredSize(PosUIManager.getSize(180, 200));
        this.btnEdit.setEnabled(false);
        this.createPayButton();
        this.createTicketItemControlPanel();
        this.createItemSearchPanel();
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(this.ticketScrollPane);
        this.add((Component)this.itemSearchPanel, "North");
        this.add(centerPanel);
        this.add((Component)this.ticketItemActionPanel, "East");
        this.ticketViewerTable.getRenderer().setInTicketScreen(true);
        this.ticketViewerTable.getSelectionModel().addListSelectionListener(new TicketItemSelectionListener());
        this.setPreferredSize(PosUIManager.getSize(360, 463));
    }

    private void createItemSearchPanel() {
        this.itemSearchPanel.setLayout((LayoutManager)new MigLayout("insets 0", "grow", ""));
        PosButton btnSearch = new PosButton("...");
        this.txtSearchItem = new JTextField();
        this.txtSearchItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (TicketView.this.txtSearchItem.getText().equals("")) {
                    POSMessageDialog.showMessage("Please enter item number or barcode ");
                    return;
                }
                if (!TicketView.this.addMenuItemByBarcode(TicketView.this.txtSearchItem.getText())) {
                    TicketView.this.addMenuItemByItemId(TicketView.this.txtSearchItem.getText());
                }
                TicketView.this.txtSearchItem.setText("");
            }
        });
        btnSearch.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ItemSearchDialog dialog = new ItemSearchDialog(Application.getPosWindow());
                dialog.setTitle("Search item");
                dialog.pack();
                dialog.open();
                if (dialog.isCanceled()) {
                    return;
                }
                TicketView.this.txtSearchItem.requestFocus();
                if (!TicketView.this.addMenuItemByBarcode(dialog.getValue()) && !TicketView.this.addMenuItemByItemId(dialog.getValue())) {
                    POSMessageDialog.showError(Application.getPosWindow(), "Item not found");
                }
            }
        });
        this.itemSearchPanel.add((Component)this.txtSearchItem, "split 2, grow,span");
        this.itemSearchPanel.add((Component)btnSearch, "grow, span, width " + PosUIManager.getSize(60) + "!, height " + PosUIManager.getSize(40) + "!");
    }

    private static boolean isParsable(String input) {
        boolean parsable = true;
        try {
            Integer.parseInt(input);
        }
        catch (NumberFormatException e) {
            parsable = false;
        }
        return parsable;
    }

    private boolean addMenuItemByItemId(String id) {
        if (!TicketView.isParsable(id)) {
            return false;
        }
        Integer itemId = Integer.parseInt(id);
        MenuItem menuItem = MenuItemDAO.getInstance().get(itemId);
        if (menuItem == null) {
            return false;
        }
        if (!this.filterByOrderType(menuItem)) {
            return false;
        }
        if (!this.filterByStockAmount(menuItem)) {
            return false;
        }
        OrderView.getInstance().getOrderController().itemSelected(menuItem);
        return true;
    }

    private boolean addMenuItemByBarcode(String barcode) {
        MenuItemDAO dao = new MenuItemDAO();
        MenuItem menuItem = dao.getMenuItemByBarcode(barcode);
        if (menuItem == null) {
            return false;
        }
        if (!this.filterByOrderType(menuItem)) {
            return false;
        }
        if (!this.filterByStockAmount(menuItem)) {
            return false;
        }
        OrderView.getInstance().getOrderController().itemSelected(menuItem);
        return true;
    }

    private boolean filterByOrderType(MenuItem menuItem) {
        List<OrderType> orderTypeList = menuItem.getOrderTypeList();
        if (orderTypeList == null || orderTypeList.size() == 0) {
            return true;
        }
        return orderTypeList.contains(this.ticket.getOrderType());
    }

    private boolean filterByStockAmount(MenuItem menuItem) {
        if (menuItem.isDisableWhenStockAmountIsZero().booleanValue() && menuItem.getStockAmount() <= 0.0) {
            POSMessageDialog.showError("Items are not available in stock");
            return false;
        }
        return true;
    }

    private void createPayButton() {
        this.btnTotal = new PosButton(POSConstants.TOTAL.toUpperCase());
        this.btnTotal.setFont(this.btnTotal.getFont().deriveFont(1));
        if (!Application.getInstance().getTerminal().isHasCashDrawer().booleanValue()) {
            this.btnTotal.setEnabled(false);
        }
        this.btnTotal.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (TicketView.this.ticket.getOrderType().isHasForHereAndToGo().booleanValue()) {
                    OrderTypeSelectionDialog2 dialog = new OrderTypeSelectionDialog2(TicketView.this.ticket);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    String orderType = dialog.getSelectedOrderType();
                    if (orderType != null) {
                        TicketView.this.ticket.updateTicketItemPriceByOrderType(orderType);
                        TicketView.this.updateModel();
                        TicketView.this.updateView();
                    }
                }
                TicketView.this.doPayNow();
            }
        });
        this.add((Component)this.btnTotal, "South");
    }

    private void createTicketItemControlPanel() {
        GridLayout gridLayout = new GridLayout(0, 1, 1, 3);
        this.ticketItemActionPanel.setLayout(gridLayout);
        Dimension size = PosUIManager.getSize(40, 40);
        this.btnScrollUp.setIcon(IconFactory.getIcon("/ui_icons/", "up.png", size));
        this.btnScrollUp.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                TicketView.this.doScrollUp();
            }
        });
        this.btnIncreaseAmount.setIcon(IconFactory.getIcon("/ui_icons/", "add_user.png", size));
        this.btnIncreaseAmount.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                ITicketItem selectedTicketItem = TicketView.this.ticketViewerTable.getSelected();
                if (selectedTicketItem == null) {
                    return;
                }
                if (TicketView.this.isFractionalUnit()) {
                    TicketView.this.doIncreaseFractionalUnit();
                } else {
                    TicketView.this.doIncreaseAmount();
                }
            }
        });
        this.btnDecreaseAmount.setIcon(IconFactory.getIcon("/ui_icons/", "minus.png", size));
        this.btnDecreaseAmount.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                TicketView.this.doDecreaseAmount();
            }
        });
        this.btnScrollDown.setIcon(IconFactory.getIcon("/ui_icons/", "down.png", size));
        this.btnScrollDown.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                TicketView.this.doScrollDown();
            }
        });
        this.btnDelete.setIcon(IconFactory.getIcon("/ui_icons/", "delete.png", size));
        this.btnDelete.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                TicketView.this.doDeleteSelection();
            }
        });
        this.btnEdit.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                TicketView.this.doEditSelection();
            }
        });
        this.ticketItemActionPanel.add(this.btnScrollUp);
        this.ticketItemActionPanel.add(this.btnIncreaseAmount);
        this.ticketItemActionPanel.add(this.btnDecreaseAmount);
        this.ticketItemActionPanel.add(this.btnDelete);
        this.ticketItemActionPanel.add(this.btnEdit);
        this.ticketItemActionPanel.add(this.btnScrollDown);
        this.ticketItemActionPanel.setPreferredSize(PosUIManager.getSize(60, 270));
    }

    public synchronized void doFinishOrder() {
        this.sendTicketToKitchen();
        this.closeView(false);
    }

    public synchronized void sendTicketToKitchen() {
        this.saveTicketIfNeeded();
        if (this.ticket.getOrderType().isShouldPrintToKitchen().booleanValue() && this.ticket.needsKitchenPrint()) {
            ReceiptPrintService.printToKitchen(this.ticket);
            TicketDAO.getInstance().refresh(this.ticket);
            this.setCancelable(false);
            this.setAllowToLogOut(false);
        }
        OrderController.saveOrder(this.ticket);
    }

    public synchronized void doHoldOrder() {
        this.updateModel();
        TicketDAO ticketDAO = TicketDAO.getInstance();
        OrderController.saveOrder(this.ticket);
        ticketDAO.refresh(this.ticket);
        this.closeView(false);
    }

    public void saveTicketIfNeeded() {
        this.updateModel();
        TicketDAO ticketDAO = TicketDAO.getInstance();
        OrderController.saveOrder(this.ticket);
        ticketDAO.refresh(this.ticket);
    }

    private void closeView(boolean orderCanceled) {
        this.ticketViewerTable.setRowHeight(PosUIManager.getSize(60));
        if (TerminalConfig.isCashierMode()) {
            RootView.getInstance().showView("csbv");
        } else {
            RootView.getInstance().showDefaultView();
        }
    }

    public void doCancelOrder() {
        this.closeView(true);
    }

    private synchronized void updateModel() {
        if (!(this.ticket.isBarTab().booleanValue() || this.ticket.getTicketItems() != null && this.ticket.getTicketItems().size() != 0)) {
            throw new PosException(POSConstants.TICKET_IS_EMPTY_);
        }
        this.ticket.calculatePrice();
    }

    private void doPayNow() {
        try {
            if (!POSUtil.checkDrawerAssignment()) {
                return;
            }
            this.updateModel();
            OrderController.saveOrder(this.ticket);
            this.firePayOrderSelected();
        }
        catch (PosException e) {
            POSMessageDialog.showError(e.getMessage());
        }
    }

    private void doDeleteSelection() {
        this.ticketViewerTable.deleteSelectedItem();
        this.updateView();
    }

    private void doEditSelection() {
        ITicketItem object = this.ticketViewerTable.getSelected();
        if (object == null) {
            return;
        }
        if (object instanceof TicketItem && ((TicketItem)object).isTreatAsSeat().booleanValue()) {
            TicketItem ticketItem = (TicketItem)object;
            SeatSelectionDialog seatDialog = new SeatSelectionDialog(this.ticket.getTableNumbers(), this.getSeatNumbers());
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
                dialog.setValue(ticketItem.getSeatNumber().intValue());
                dialog.pack();
                dialog.open();
                if (dialog.isCanceled()) {
                    return;
                }
                seatNumber = (int)dialog.getValue();
            }
            ticketItem.setName("Seat** " + seatNumber);
            ticketItem.setSeatNumber(seatNumber);
            this.updateTicketItemsSeatNumber(ticketItem);
        } else {
            OrderController.openModifierDialog(object);
        }
        this.updateView();
    }

    protected List<Integer> getSeatNumbers() {
        ArrayList<Integer> seatNumbers = new ArrayList<Integer>();
        for (TicketItem ticketItem : this.ticket.getTicketItems()) {
            if (!ticketItem.isTreatAsSeat().booleanValue() || seatNumbers.contains(ticketItem.getSeatNumber())) continue;
            seatNumbers.add(ticketItem.getSeatNumber());
        }
        return seatNumbers;
    }

    private void updateTicketItemsSeatNumber(TicketItem ticketItem) {
        boolean updateSeatNumber = false;
        for (TicketItem item : this.ticket.getTicketItems()) {
            if (item == ticketItem) {
                updateSeatNumber = true;
                continue;
            }
            if (!updateSeatNumber) continue;
            if (item.isTreatAsSeat().booleanValue()) break;
            item.setSeatNumber(ticketItem.getSeatNumber());
        }
    }

    private void doIncreaseAmount() {
        if (!this.checkStock(-1.0)) {
            POSMessageDialog.showError("Items are not available in stock");
            return;
        }
        if (this.ticketViewerTable.increaseItemAmount()) {
            this.updateView();
        }
    }

    private void doIncreaseFractionalUnit() {
        double selectedQuantity = this.getNewItemQuantity();
        if (selectedQuantity == -1.0) {
            return;
        }
        if (!this.checkStock(selectedQuantity)) {
            POSMessageDialog.showError("Items are not available in stock");
            return;
        }
        if (this.ticketViewerTable.increaseFractionalUnit(selectedQuantity)) {
            this.updateView();
        }
    }

    private void doDecreaseAmount() {
        if (this.ticketViewerTable.decreaseItemAmount()) {
            this.updateView();
        }
    }

    private void doScrollDown() {
        this.ticketViewerTable.scrollDown();
    }

    private void doScrollUp() {
        this.ticketViewerTable.scrollUp();
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket _ticket) {
        this.ticket = _ticket;
        this.ticketViewerTable.setTicket(_ticket);
        this.updateView();
        this.setCancelable(true);
        this.setAllowToLogOut(true);
    }

    public void addTicketItem(TicketItem ticketItem) {
        this.ticketViewerTable.addTicketItem(ticketItem);
        this.updateView();
    }

    public void removeModifier(TicketItem parent, TicketItemModifier modifier) {
        modifier.setItemCount(0);
        this.ticketViewerTable.removeModifier(parent, modifier);
    }

    public void selectRow(int rowIndex) {
        this.ticketViewerTable.selectRow(rowIndex);
    }

    public void updateView() {
        if (this.ticket == null) {
            this.btnTotal.setText(POSConstants.TOTAL.toUpperCase() + " " + CurrencyUtil.getCurrencySymbol() + "0.00");
            this.titledBorder.setTitle(this.ticket.getTicketType().toString() + "[New Ticket]");
            return;
        }
        this.ticket.calculatePrice();
        ITicketItem selectedItem = this.ticketViewerTable.getSelected();
        if (selectedItem != null && TerminalConfig.isActiveCustomerDisplay()) {
            String sendMessageToCustomerDisplay = this.getDisplayMessage(selectedItem, this.ticket.getTotalAmount().toString());
            DrawerUtil.setItemDisplay(TerminalConfig.getCustomerDisplayPort(), sendMessageToCustomerDisplay);
        }
        this.btnTotal.setText(POSConstants.TOTAL.toUpperCase() + " " + CurrencyUtil.getCurrencySymbol() + NumberUtil.formatNumber(this.ticket.getTotalAmount()));
        if (this.ticket.getId() == null) {
            this.titledBorder.setTitle(this.ticket.getTicketType() + " [New Ticket]");
        } else {
            this.titledBorder.setTitle(this.ticket.getTicketType() + " " + Messages.getString("TicketView.37") + this.ticket.getId() + " Table# " + this.getTableNumbers(this.ticket.getTableNumbers()));
        }
        this.ticketViewerTable.updateView();
    }

    public void addOrderListener(OrderListener listenre) {
        this.orderListeners.add(listenre);
    }

    public void removeOrderListener(OrderListener listenre) {
        this.orderListeners.remove(listenre);
    }

    public void firePayOrderSelected() {
        for (OrderListener listener : this.orderListeners) {
            listener.payOrderSelected(this.getTicket());
        }
    }

    public void setControlsVisible(boolean visible) {
        if (visible) {
            this.btnIncreaseAmount.setEnabled(true);
            this.btnDecreaseAmount.setEnabled(true);
            this.btnDelete.setEnabled(true);
        } else {
            this.btnIncreaseAmount.setEnabled(false);
            this.btnDecreaseAmount.setEnabled(false);
            this.btnDelete.setEnabled(false);
        }
    }

    public TicketViewerTable getTicketViewerTable() {
        return this.ticketViewerTable;
    }

    public JTextField getTxtSearchItem() {
        return this.txtSearchItem;
    }

    private String getDisplayMessage(ITicketItem item, String totalPrice) {
        int currentItemLenth = item.getNameDisplay().length();
        String ticketItems = currentItemLenth > 12 ? item.getNameDisplay().substring(0, 12) : item.getNameDisplay();
        String quantity = item.getItemQuantityDisplay();
        double itemPrice = item.getUnitPriceDisplay();
        String line = String.format("%-1s %-12s %4s", quantity, ticketItems, itemPrice);
        String total = "TOTAL" + CurrencyUtil.getCurrencySymbol();
        String line2 = String.format("%-6s %13s", total, totalPrice);
        return line + line2;
    }

    public boolean isCancelable() {
        return this.cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public boolean isAllowToLogOut() {
        return this.allowToLogOut;
    }

    public void setAllowToLogOut(boolean allowToLogOut) {
        this.allowToLogOut = allowToLogOut;
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

    private double getNewItemQuantity() {
        ITicketItem selectedTicketItem = this.ticketViewerTable.getSelected();
        double selectedQuantity = 0.0;
        selectedQuantity = TerminalConfig.getScaleActivationValue().equals("cas10") ? AutomatedWeightInputDialog.takeDoubleInput(selectedTicketItem.getNameDisplay(), 1.0) : BasicWeightInputDialog.takeDoubleInput("Please enter item weight or quantity.", 1.0);
        if (selectedQuantity <= -1.0) {
            return -1.0;
        }
        if (selectedQuantity == 0.0) {
            POSMessageDialog.showError("Unit can not be zero");
            return -1.0;
        }
        return selectedQuantity;
    }

    private boolean isFractionalUnit() {
        ITicketItem object = this.ticketViewerTable.getSelected();
        if (object instanceof TicketItem) {
            TicketItem ticketItem = (TicketItem)object;
            return ticketItem.isFractionalUnit();
        }
        return false;
    }

    private boolean checkStock(double selectedItemQuantity) {
        TicketItem selectedTicketItem = (TicketItem)this.ticketViewerTable.getSelected();
        MenuItemDAO dao = new MenuItemDAO();
        MenuItem menuItem = dao.get(selectedTicketItem.getItemId());
        return this.isStockAvailable(menuItem, selectedTicketItem, selectedItemQuantity);
    }

    public boolean isStockAvailable(MenuItem menuItem, TicketItem selectedTicketItem, double selectedItemQuantity) {
        if (!menuItem.isDisableWhenStockAmountIsZero().booleanValue()) {
            return true;
        }
        List<TicketItem> ticketItems = this.ticketViewerTable.getTicketItems();
        if (menuItem.isFractionalUnit().booleanValue()) {
            if (ticketItems == null || ticketItems.isEmpty()) {
                return !(menuItem.getStockAmount() < selectedTicketItem.getItemQuantity());
            }
            double totalItemQuantity = 0.0;
            for (TicketItem tItem : ticketItems) {
                if (!menuItem.getName().equals(tItem.getName())) continue;
                totalItemQuantity += tItem.getItemQuantity().doubleValue();
                if (!(menuItem.getStockAmount() < totalItemQuantity)) continue;
                return false;
            }
            if (selectedItemQuantity != -1.0) {
                totalItemQuantity -= selectedTicketItem.getItemQuantity().doubleValue();
                totalItemQuantity += selectedItemQuantity;
            } else {
                totalItemQuantity += selectedTicketItem.getItemQuantity().doubleValue();
            }
            return !(menuItem.getStockAmount() < totalItemQuantity);
        }
        if (ticketItems == null || ticketItems.isEmpty()) {
            return !(menuItem.getStockAmount() < (double)selectedTicketItem.getItemCount().intValue());
        }
        int totalItemCount = 0;
        for (TicketItem tItem : ticketItems) {
            if (!tItem.getName().equals(menuItem.getName())) continue;
            totalItemCount += tItem.getItemCount().intValue();
            if (!(menuItem.getStockAmount() <= (double)totalItemCount)) continue;
            return false;
        }
        return true;
    }

    private class TicketItemSelectionListener
    implements ListSelectionListener {
        private TicketItemSelectionListener() {
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            ITicketItem selected = TicketView.this.ticketViewerTable.getSelected();
            if (!(selected instanceof ITicketItem)) {
                return;
            }
            ITicketItem iTicketItem = selected;
            if (iTicketItem.isPrintedToKitchen().booleanValue()) {
                TicketView.this.btnIncreaseAmount.setEnabled(false);
                TicketView.this.btnDecreaseAmount.setEnabled(false);
            }
            if (selected instanceof TicketItemModifier) {
                TicketView.this.btnIncreaseAmount.setEnabled(false);
                TicketView.this.btnDecreaseAmount.setEnabled(false);
                TicketView.this.btnEdit.setEnabled(true);
                TicketView.this.btnDelete.setEnabled(false);
            } else {
                TicketView.this.btnIncreaseAmount.setEnabled(true);
                TicketView.this.btnDecreaseAmount.setEnabled(true);
                TicketView.this.btnDelete.setEnabled(true);
                TicketView.this.btnEdit.setEnabled(false);
                if (selected instanceof TicketItem) {
                    TicketItem ticketItem = (TicketItem)selected;
                    if (ticketItem.isPrintedToKitchen().booleanValue()) {
                        TicketView.this.btnIncreaseAmount.setEnabled(false);
                        TicketView.this.btnDecreaseAmount.setEnabled(false);
                        if (TerminalConfig.isAllowedToDeletePrintedTicketItem()) {
                            TicketView.this.btnDelete.setEnabled(true);
                        } else {
                            TicketView.this.btnDelete.setEnabled(false);
                        }
                    }
                    if (ticketItem.isTreatAsSeat().booleanValue()) {
                        TicketView.this.btnEdit.setEnabled(ticketItem.isPrintedToKitchen() == false);
                    } else if (ticketItem.isHasModifiers().booleanValue()) {
                        TicketView.this.btnIncreaseAmount.setEnabled(false);
                        TicketView.this.btnDecreaseAmount.setEnabled(false);
                        TicketView.this.btnEdit.setEnabled(true);
                    } else if (ticketItem.isFractionalUnit().booleanValue()) {
                        TicketView.this.btnIncreaseAmount.setEnabled(true);
                        TicketView.this.btnDecreaseAmount.setEnabled(false);
                        TicketView.this.btnDelete.setEnabled(true);
                    }
                }
            }
        }
    }
}

