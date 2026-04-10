/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.CollectionUtils
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.Discount;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketDiscount;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemDiscount;
import com.floreantpos.model.dao.DiscountDAO;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.ScrollableFlowPanel;
import com.floreantpos.ui.dialog.ItemSearchDialog;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.TicketItemDiscountSelectionDialog;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.commons.collections.CollectionUtils;

public class DiscountSelectionDialog
extends OkCancelOptionDialog
implements ActionListener {
    private ScrollableFlowPanel buttonsPanel;
    private HashMap<Integer, TicketDiscount> addedTicketDiscounts = new HashMap();
    private List<Integer> clearTicketItemDiscounts = new ArrayList<Integer>();
    private HashMap<Integer, DiscountButton> buttonMap = new HashMap();
    private Ticket ticket;
    private JPanel itemSearchPanel;
    private JTextField txtSearchItem;

    public DiscountSelectionDialog(Ticket ticket) {
        super(POSUtil.getFocusedWindow(), Messages.getString("DiscountSelectionDialog.0"));
        this.ticket = ticket;
        this.initComponent();
        if (ticket.getDiscounts() != null) {
            for (TicketDiscount ticketDiscount : ticket.getDiscounts()) {
                this.addedTicketDiscounts.put(ticketDiscount.getDiscountId(), ticketDiscount);
            }
        }
    }

    private void initComponent() {
        this.setOkButtonText(POSConstants.SAVE_BUTTON_TEXT);
        this.createCouponSearchPanel();
        this.getContentPanel().add((Component)this.itemSearchPanel, "North");
        this.buttonsPanel = new ScrollableFlowPanel(3);
        PosScrollPane scrollPane = new PosScrollPane(this.buttonsPanel, 20, 31);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(80, 0));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), scrollPane.getBorder()));
        this.getContentPanel().add((Component)scrollPane, "Center");
        this.rendererDiscounts();
        this.setSize(1024, 720);
    }

    private void createCouponSearchPanel() {
        this.itemSearchPanel = new JPanel(new BorderLayout(5, 5));
        PosButton btnSearch = new PosButton("...");
        btnSearch.setPreferredSize(new Dimension(60, 40));
        JLabel lblCoupon = new JLabel("Enter Coupon Number");
        this.txtSearchItem = new JTextField();
        this.txtSearchItem.addFocusListener(new FocusListener(){

            @Override
            public void focusLost(FocusEvent e) {
                DiscountSelectionDialog.this.txtSearchItem.setText("Scan barcode");
                DiscountSelectionDialog.this.txtSearchItem.setForeground(Color.gray);
            }

            @Override
            public void focusGained(FocusEvent e) {
                DiscountSelectionDialog.this.txtSearchItem.setForeground(Color.black);
                DiscountSelectionDialog.this.txtSearchItem.setText("");
            }
        });
        this.txtSearchItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (DiscountSelectionDialog.this.txtSearchItem.getText().equals("")) {
                    POSMessageDialog.showMessage("Please enter coupon number or barcode ");
                    return;
                }
                if (!DiscountSelectionDialog.this.addCouponByBarcode(DiscountSelectionDialog.this.txtSearchItem.getText())) {
                    DiscountSelectionDialog.this.addCouponById(DiscountSelectionDialog.this.txtSearchItem.getText());
                }
                DiscountSelectionDialog.this.txtSearchItem.setText("");
            }
        });
        btnSearch.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ItemSearchDialog dialog = new ItemSearchDialog(Application.getPosWindow());
                dialog.setTitle("Search Coupon");
                dialog.pack();
                dialog.open();
                if (dialog.isCanceled()) {
                    return;
                }
                DiscountSelectionDialog.this.txtSearchItem.requestFocus();
                if (!DiscountSelectionDialog.this.addCouponByBarcode(dialog.getValue()) && !DiscountSelectionDialog.this.addCouponById(dialog.getValue())) {
                    POSMessageDialog.showError(Application.getPosWindow(), "Coupon not found");
                }
            }
        });
        this.itemSearchPanel.add((Component)lblCoupon, "West");
        this.itemSearchPanel.add(this.txtSearchItem);
        this.itemSearchPanel.add((Component)btnSearch, "East");
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

    private boolean addCouponById(String id) {
        if (!DiscountSelectionDialog.isParsable(id)) {
            return false;
        }
        Integer itemId = Integer.parseInt(id);
        Discount discount = DiscountDAO.getInstance().get(itemId);
        if (discount == null) {
            return false;
        }
        if (discount.getQualificationType() == 0) {
            DiscountButton discountButton = this.buttonMap.get(discount.getId());
            this.applyDiscountToTicketItems(discountButton);
        } else {
            if (discount.isModifiable().booleanValue()) {
                double newValue = this.getModifiedValue(discount);
                if (newValue <= 0.0) {
                    newValue = discount.getValue();
                }
                discount.setValue(newValue);
            }
            this.addedTicketDiscounts.put(discount.getId(), Ticket.convertToTicketDiscount(discount, this.ticket));
        }
        DiscountButton button = this.buttonMap.get(discount.getId());
        if (button != null) {
            button.setSelected(true);
        }
        return true;
    }

    private boolean addCouponByBarcode(String barcode) {
        Discount discount = DiscountDAO.getInstance().getDiscountByBarcode(barcode);
        if (discount == null) {
            return false;
        }
        if (discount.getQualificationType() == 0) {
            DiscountButton discountButton = this.buttonMap.get(discount.getId());
            this.applyDiscountToTicketItems(discountButton);
        } else {
            if (discount.isModifiable().booleanValue()) {
                double newValue = this.getModifiedValue(discount);
                if (newValue <= 0.0) {
                    newValue = discount.getValue();
                }
                discount.setValue(newValue);
            }
            this.addedTicketDiscounts.put(discount.getId(), Ticket.convertToTicketDiscount(discount, this.ticket));
        }
        DiscountButton button = this.buttonMap.get(discount.getId());
        button.setSelected(true);
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.rendererDiscounts();
    }

    private void rendererDiscounts() {
        this.buttonMap.clear();
        this.buttonsPanel.getContentPane().removeAll();
        List<Discount> discounts = DiscountDAO.getInstance().findAllValidCoupons();
        Dimension size = PosUIManager.getSize(115, 80);
        for (Discount discount : discounts) {
            DiscountButton btnDiscount = new DiscountButton(discount);
            btnDiscount.setSelected(false);
            btnDiscount.setPreferredSize(size);
            this.buttonsPanel.add(btnDiscount);
            this.buttonMap.put(discount.getId(), btnDiscount);
        }
        if (this.ticket.getDiscounts() != null) {
            for (TicketDiscount ticketCouponAndDiscount : this.ticket.getDiscounts()) {
                DiscountButton ticketDiscountButton = this.buttonMap.get(ticketCouponAndDiscount.getDiscountId());
                if (ticketDiscountButton == null) continue;
                ticketDiscountButton.setSelected(true);
            }
        }
        for (TicketItem ticketItem : this.ticket.getTicketItems()) {
            for (TicketItemDiscount ticketItemDiscount : ticketItem.getDiscounts()) {
                DiscountButton ticketDiscountButton = this.buttonMap.get(ticketItemDiscount.getDiscountId());
                if (ticketDiscountButton == null) continue;
                ticketDiscountButton.setSelected(true);
            }
        }
        this.buttonsPanel.repaint();
        this.buttonsPanel.revalidate();
    }

    @Override
    public void doOk() {
        List<TicketDiscount> couponAndDiscounts = this.ticket.getDiscounts();
        if (couponAndDiscounts == null) {
            couponAndDiscounts = new ArrayList<TicketDiscount>();
        }
        if (!CollectionUtils.isEqualCollection(couponAndDiscounts, this.addedTicketDiscounts.values())) {
            couponAndDiscounts.clear();
            for (TicketDiscount ticketDiscount : this.addedTicketDiscounts.values()) {
                this.ticket.addTodiscounts(ticketDiscount);
            }
        }
        for (TicketItem ticketItem : this.ticket.getTicketItems()) {
            Iterator<TicketItemDiscount> iterator2 = ticketItem.getDiscounts().iterator();
            while (iterator2.hasNext()) {
                TicketItemDiscount ticketItemDiscount = iterator2.next();
                if (!this.clearTicketItemDiscounts.contains(ticketItemDiscount.getDiscountId())) continue;
                iterator2.remove();
            }
        }
        for (TicketItem ticketItem : this.ticket.getTicketItems()) {
            for (DiscountButton discountButton : this.buttonMap.values()) {
                if (!discountButton.ticketItems.contains(ticketItem)) continue;
                ticketItem.getDiscounts().add(MenuItem.convertToTicketItemDiscount(discountButton.discount, ticketItem));
            }
        }
        this.setCanceled(false);
        this.dispose();
    }

    @Override
    public void doCancel() {
        this.addedTicketDiscounts.clear();
        this.buttonMap.clear();
        this.setCanceled(true);
        this.dispose();
    }

    private double getModifiedValue(Discount discount) {
        Double newValue = NumberSelectionDialog2.takeDoubleInput("Enter Amount", "Enter Amount", discount.getValue());
        if (newValue > 0.0) {
            return newValue;
        }
        return 0.0;
    }

    private void applyDiscountToTicketItems(DiscountButton discountButton) {
        TicketItemDiscountSelectionDialog dialog = new TicketItemDiscountSelectionDialog(this.ticket, discountButton.discount);
        dialog.open();
        if (!dialog.isCanceled()) {
            discountButton.ticketItems = dialog.getSelectedTicketItems();
            discountButton.setSelected(true);
        } else {
            discountButton.setSelected(false);
        }
    }

    private class DiscountButton
    extends POSToggleButton
    implements ActionListener {
        Discount discount;
        List<TicketItem> ticketItems;

        DiscountButton(Discount discount) {
            this.discount = discount;
            this.ticketItems = new ArrayList<TicketItem>();
            this.setFont(this.getFont().deriveFont(1, PosUIManager.getFontSize(18)));
            this.setText("<html><body><center>" + discount.getName() + "<br></center></body></html>");
            if (discount.getQualificationType() == 0) {
                this.setBackground(Color.CYAN);
            } else {
                this.setBackground(Color.MAGENTA);
            }
            this.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (this.isSelected()) {
                if (this.discount.getQualificationType() == 0) {
                    DiscountSelectionDialog.this.applyDiscountToTicketItems(this);
                } else {
                    if (this.discount.isModifiable().booleanValue()) {
                        double newValue = DiscountSelectionDialog.this.getModifiedValue(this.discount);
                        if (newValue <= 0.0) {
                            newValue = this.discount.getValue();
                        }
                        this.discount.setValue(newValue);
                    }
                    DiscountSelectionDialog.this.addedTicketDiscounts.put(this.discount.getId(), Ticket.convertToTicketDiscount(this.discount, DiscountSelectionDialog.this.ticket));
                }
            } else if (this.discount.getQualificationType() == 0) {
                DiscountSelectionDialog.this.clearTicketItemDiscounts.add(this.discount.getId());
            } else {
                DiscountSelectionDialog.this.addedTicketDiscounts.remove(this.discount.getId());
            }
        }
    }
}

