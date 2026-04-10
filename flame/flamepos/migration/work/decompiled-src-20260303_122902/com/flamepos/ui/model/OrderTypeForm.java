/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.model;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.extension.ExtensionManager;
import com.floreantpos.extension.OrderServiceExtension;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.dao.OrderTypeDAO;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;

public class OrderTypeForm
extends BeanEditor
implements ItemListener {
    private JLabel jLabel1;
    private FixedLengthTextField tfName;
    private JCheckBox chkEnabled;
    private JCheckBox chkShowTableSelection;
    private JCheckBox chkShowGuestSelection;
    private JCheckBox chkShouldPrintToKitchen;
    private JCheckBox chkCloseOnPaid;
    private JCheckBox chkPrepaid;
    private JCheckBox chkDelivery;
    private JCheckBox chkRequiredCustomerData;
    private JCheckBox chkShowItemBarcode;
    private JCheckBox chkShowInLoginScreen;
    private JCheckBox chkConsolidateItemsInReceipt;
    private JCheckBox chkAllowSeatBasedOrder;
    private JCheckBox chkHideItemWithEmptyInventory;
    private JCheckBox chkHasForHereAndToGo;
    private JCheckBox chkBarTab;
    private JCheckBox chkPreAuthCreditCard;
    private JCheckBox chkShowPriceOnButton;
    private JCheckBox chkShowStockCountOnButton;
    private JCheckBox chkShowUnitPriceInTicketGrid;
    OrderType orderType;
    JList<String> list;
    DefaultListModel<String> listModel;

    public OrderTypeForm() throws Exception {
        this(new OrderType());
        this.initHandler();
    }

    public OrderTypeForm(OrderType orderType) throws Exception {
        this.orderType = orderType;
        this.initComponents();
        this.setBean(orderType);
        this.initHandler();
    }

    @Override
    public String getDisplayText() {
        OrderType orderType = (OrderType)this.getBean();
        if (orderType.getId() == null) {
            return POSConstants.ORDER_TYPE;
        }
        return POSConstants.ORDER_TYPE;
    }

    private void initHandler() {
        this.chkRequiredCustomerData.addItemListener(this);
        this.chkDelivery.addItemListener(this);
    }

    private void initComponents() {
        TransparentPanel generalPanel = new TransparentPanel();
        this.jLabel1 = new JLabel(POSConstants.NAME + ":");
        this.tfName = new FixedLengthTextField();
        this.tfName.setLength(120);
        this.chkEnabled = new JCheckBox(POSConstants.ENABLED);
        this.chkShowTableSelection = new JCheckBox(Messages.getString("OrderTypeForm.1"));
        this.chkShowGuestSelection = new JCheckBox(Messages.getString("OrderTypeForm.2"));
        this.chkShouldPrintToKitchen = new JCheckBox(Messages.getString("OrderTypeForm.3"));
        this.chkCloseOnPaid = new JCheckBox(Messages.getString("OrderTypeForm.4"));
        this.chkPrepaid = new JCheckBox(Messages.getString("OrderTypeForm.5"));
        this.chkDelivery = new JCheckBox("Delivery");
        this.chkRequiredCustomerData = new JCheckBox(Messages.getString("OrderTypeForm.6"));
        this.chkShowItemBarcode = new JCheckBox(Messages.getString("OrderTypeForm.9"));
        this.chkShowInLoginScreen = new JCheckBox(Messages.getString("OrderTypeForm.10"));
        this.chkConsolidateItemsInReceipt = new JCheckBox(Messages.getString("OrderTypeForm.11"));
        this.chkAllowSeatBasedOrder = new JCheckBox("Allow seat based order");
        this.chkHideItemWithEmptyInventory = new JCheckBox(Messages.getString("OrderTypeForm.12"));
        this.chkHasForHereAndToGo = new JCheckBox(Messages.getString("OrderTypeForm.13"));
        this.chkBarTab = new JCheckBox(Messages.getString("OrderTypeForm.14"));
        this.chkPreAuthCreditCard = new JCheckBox(Messages.getString("OrderTypeForm.0"));
        this.chkShowPriceOnButton = new JCheckBox("Show price on button");
        this.chkShowStockCountOnButton = new JCheckBox("Show count on button");
        this.chkShowUnitPriceInTicketGrid = new JCheckBox("Show unit price in ticket grid");
        generalPanel.setLayout((LayoutManager)new MigLayout("", "[87px][327px,grow]", "[19px][][19px][][][21px][15px]"));
        generalPanel.add((Component)this.jLabel1, "cell 0 0,alignx left,aligny center");
        generalPanel.add((Component)this.tfName, "cell 1 0,growx,aligny top");
        generalPanel.add((Component)this.chkEnabled, "cell 1 1,alignx left,aligny top");
        generalPanel.add((Component)this.chkShowTableSelection, "cell 1 2,alignx left,aligny top");
        generalPanel.add((Component)this.chkShowGuestSelection, "cell 1 3,alignx left,aligny top");
        generalPanel.add((Component)this.chkShouldPrintToKitchen, "cell 1 4,alignx left,aligny top");
        generalPanel.add((Component)this.chkPrepaid, "cell 1 5,alignx left,aligny top");
        generalPanel.add((Component)this.chkCloseOnPaid, "cell 1 6,alignx left,aligny top");
        OrderServiceExtension orderServiceExtension = (OrderServiceExtension)ExtensionManager.getPlugin(OrderServiceExtension.class);
        generalPanel.add((Component)this.chkRequiredCustomerData, "cell 1 7,alignx left,aligny top");
        if (orderServiceExtension != null) {
            generalPanel.add((Component)this.chkDelivery, "cell 1 8,alignx left,aligny top");
        }
        generalPanel.add((Component)this.chkShowItemBarcode, "cell 1 10,alignx left,aligny top");
        generalPanel.add((Component)this.chkShowInLoginScreen, "cell 1 11,alignx left,aligny top");
        generalPanel.add((Component)this.chkConsolidateItemsInReceipt, "cell 1 12,alignx left,aligny top");
        generalPanel.add((Component)this.chkAllowSeatBasedOrder, "cell 1 13,alignx left,aligny top");
        generalPanel.add((Component)this.chkHideItemWithEmptyInventory, "cell 1 14,alignx left,aligny top");
        generalPanel.add((Component)this.chkHasForHereAndToGo, "cell 1 15,alignx left,aligny top");
        generalPanel.add((Component)this.chkBarTab, "cell 1 16,alignx left,aligny top");
        generalPanel.add((Component)this.chkPreAuthCreditCard, "cell 1 17,alignx left,aligny top");
        generalPanel.add((Component)this.chkShowPriceOnButton, "cell 1 18,alignx left,aligny top,wrap");
        generalPanel.add((Component)this.chkShowStockCountOnButton, "cell 1 19,alignx left,aligny top");
        this.add(new JScrollPane(generalPanel));
    }

    @Override
    protected void updateView() {
        OrderType ordersType = (OrderType)this.getBean();
        if (ordersType == null) {
            this.tfName.setText("");
            this.chkEnabled.setSelected(false);
            return;
        }
        this.tfName.setText(ordersType.getName());
        if (ordersType.getId() == null) {
            this.chkEnabled.setSelected(true);
        } else {
            this.chkEnabled.setSelected(ordersType.isEnabled());
            this.chkShowTableSelection.setSelected(ordersType.isShowTableSelection());
            this.chkShowGuestSelection.setSelected(ordersType.isShowGuestSelection());
            this.chkShouldPrintToKitchen.setSelected(ordersType.isShouldPrintToKitchen());
            this.chkPrepaid.setSelected(ordersType.isPrepaid());
            this.chkCloseOnPaid.setSelected(ordersType.isCloseOnPaid());
            this.chkDelivery.setSelected(ordersType.isDelivery());
            this.chkRequiredCustomerData.setSelected(ordersType.isRequiredCustomerData());
            this.chkShowItemBarcode.setSelected(ordersType.isShowItemBarcode());
            this.chkShowInLoginScreen.setSelected(ordersType.isShowInLoginScreen());
            this.chkConsolidateItemsInReceipt.setSelected(ordersType.isConsolidateItemsInReceipt());
            this.chkAllowSeatBasedOrder.setSelected(ordersType.isAllowSeatBasedOrder());
            this.chkHideItemWithEmptyInventory.setSelected(ordersType.isHideItemWithEmptyInventory());
            this.chkHasForHereAndToGo.setSelected(ordersType.isHasForHereAndToGo());
            this.chkBarTab.setSelected(ordersType.isBarTab());
            this.chkPreAuthCreditCard.setSelected(ordersType.isPreAuthCreditCard());
            this.chkShowPriceOnButton.setSelected(this.orderType.isShowPriceOnButton());
            this.chkShowStockCountOnButton.setSelected(this.orderType.isShowStockCountOnButton());
            this.chkShowUnitPriceInTicketGrid.setSelected(this.orderType.isShowUnitPriceInTicketGrid());
        }
    }

    @Override
    protected boolean updateModel() {
        OrderType ordersType = (OrderType)this.getBean();
        if (ordersType == null) {
            return false;
        }
        String categoryName = this.tfName.getText();
        if (POSUtil.isBlankOrNull(categoryName)) {
            MessageDialog.showError(Messages.getString("MenuCategoryForm.26"));
            return false;
        }
        ordersType.setName(categoryName);
        ordersType.setEnabled(this.chkEnabled.isSelected());
        ordersType.setShowTableSelection(this.chkShowTableSelection.isSelected());
        ordersType.setShowGuestSelection(this.chkShowGuestSelection.isSelected());
        ordersType.setShouldPrintToKitchen(this.chkShouldPrintToKitchen.isSelected());
        ordersType.setPrepaid(this.chkPrepaid.isSelected());
        ordersType.setCloseOnPaid(this.chkCloseOnPaid.isSelected());
        ordersType.setDelivery(this.chkDelivery.isSelected());
        ordersType.setRequiredCustomerData(this.chkRequiredCustomerData.isSelected());
        ordersType.setShowItemBarcode(this.chkShowItemBarcode.isSelected());
        ordersType.setShowInLoginScreen(this.chkShowInLoginScreen.isSelected());
        ordersType.setConsolidateItemsInReceipt(this.chkConsolidateItemsInReceipt.isSelected());
        ordersType.setAllowSeatBasedOrder(this.chkAllowSeatBasedOrder.isSelected());
        ordersType.setHideItemWithEmptyInventory(this.chkHideItemWithEmptyInventory.isSelected());
        ordersType.setHasForHereAndToGo(this.chkHasForHereAndToGo.isSelected());
        ordersType.setPreAuthCreditCard(this.chkPreAuthCreditCard.isSelected());
        ordersType.setShowPriceOnButton(this.chkShowPriceOnButton.isSelected());
        ordersType.setShowStockCountOnButton(this.chkShowStockCountOnButton.isSelected());
        ordersType.setShowUnitPriceInTicketGrid(this.chkShowUnitPriceInTicketGrid.isSelected());
        ordersType.setBarTab(this.chkBarTab.isSelected());
        return true;
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
            OrderType ordersType = (OrderType)this.getBean();
            OrderTypeDAO.getInstance().saveOrUpdate(ordersType);
            POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), Messages.getString("TerminalConfigurationView.40"));
            return true;
        }
        catch (Exception x) {
            MessageDialog.showError(x);
            return false;
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        JCheckBox chkBox = (JCheckBox)e.getItem();
        if (chkBox == this.chkDelivery) {
            if (this.chkDelivery.isSelected()) {
                this.chkRequiredCustomerData.setSelected(true);
                this.chkRequiredCustomerData.setEnabled(false);
            } else {
                this.chkRequiredCustomerData.setEnabled(true);
            }
        }
    }
}

