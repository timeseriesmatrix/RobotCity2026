/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemDiscount;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.ticket.TicketViewerTable;
import com.floreantpos.util.NumberUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class TicketForSplitView
extends TransparentPanel
implements TableModelListener {
    private Ticket ticket;
    public static final String VIEW_NAME = "TICKET_FOR_SPLIT_VIEW";
    private TicketForSplitView ticketView1;
    private TicketForSplitView ticketView2;
    private TicketForSplitView ticketView3;
    private int viewNumber = 1;
    private PosButton btnScrollDown;
    private PosButton btnScrollUp;
    private PosButton btnTransferToTicket1;
    private PosButton btnTransferToTicket2;
    private PosButton btnTransferToTicket3;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private TransparentPanel jPanel1;
    private TransparentPanel jPanel2;
    private TransparentPanel jPanel3;
    private TransparentPanel jPanel5;
    private JScrollPane jScrollPane1;
    private JSeparator jSeparator1;
    private JSeparator jSeparator2;
    private JSeparator jSeparator3;
    private JTextField tfDiscount;
    private JTextField tfSubtotal;
    private JTextField tfTax;
    private JTextField tfTotal;
    private TicketViewerTable ticketViewerTable;
    private GridBagConstraints gridBagConstraints_1;
    private GridBagConstraints gridBagConstraints_2;
    private GridBagConstraints gridBagConstraints_3;
    private GridBagConstraints gridBagConstraints_4;

    public TicketForSplitView() {
        this.initComponents();
        this.ticket = new Ticket();
        this.ticket.setPriceIncludesTax(Application.getInstance().isPriceIncludesTax());
        this.ticket.setTerminal(Application.getInstance().getTerminal());
        this.ticket.setOwner(Application.getCurrentUser());
        this.ticket.setShift(Application.getInstance().getCurrentShift());
        Calendar currentTime = Calendar.getInstance();
        this.ticket.setCreateDate(currentTime.getTime());
        this.ticket.setCreationHour(currentTime.get(11));
        this.setOpaque(true);
        this.setTicket(this.ticket);
    }

    private void initComponents() {
        this.jPanel1 = new TransparentPanel();
        this.jSeparator1 = new JSeparator();
        this.jPanel3 = new TransparentPanel();
        this.jLabel5 = new JLabel();
        this.jLabel6 = new JLabel();
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.jSeparator2 = new JSeparator();
        this.jSeparator3 = new JSeparator();
        this.tfSubtotal = new JTextField();
        this.tfSubtotal.setMinimumSize(new Dimension(90, 19));
        this.tfSubtotal.setHorizontalAlignment(11);
        this.tfSubtotal.setColumns(10);
        this.tfTax = new JTextField();
        this.tfTax.setMinimumSize(new Dimension(90, 19));
        this.tfTax.setHorizontalAlignment(11);
        this.tfTax.setColumns(10);
        this.tfDiscount = new JTextField();
        this.tfDiscount.setMinimumSize(new Dimension(90, 19));
        this.tfDiscount.setHorizontalAlignment(11);
        this.tfDiscount.setColumns(10);
        this.tfTotal = new JTextField();
        this.tfTotal.setMinimumSize(new Dimension(90, 19));
        this.tfTotal.setHorizontalAlignment(11);
        this.tfTotal.setColumns(10);
        this.jPanel5 = new TransparentPanel();
        this.btnScrollUp = new PosButton();
        this.btnScrollDown = new PosButton();
        this.btnTransferToTicket1 = new PosButton();
        this.btnTransferToTicket2 = new PosButton();
        this.btnTransferToTicket3 = new PosButton();
        this.jPanel2 = new TransparentPanel();
        this.jScrollPane1 = new JScrollPane();
        this.ticketViewerTable = new TicketViewerTable();
        this.setLayout(new BorderLayout(5, 5));
        this.setBorder(BorderFactory.createTitledBorder(null, POSConstants.TICKET, 2, 0));
        this.setPreferredSize(new Dimension(280, 463));
        this.jPanel1.setLayout(new BorderLayout(5, 5));
        this.jPanel1.add((Component)this.jSeparator1, "Center");
        this.jPanel3.setLayout(new GridBagLayout());
        this.jLabel5.setFont(new Font("Tahoma", 1, 12));
        this.jLabel5.setHorizontalAlignment(4);
        this.jLabel5.setText(POSConstants.SUBTOTAL + ":");
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(3, 5, 0, 0);
        this.jPanel3.add((Component)this.jLabel5, gridBagConstraints);
        this.jLabel6.setFont(new Font("Tahoma", 1, 12));
        this.jLabel6.setHorizontalAlignment(4);
        this.jLabel6.setText(POSConstants.TOTAL + ":");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(0, 5, 3, 0);
        this.jPanel3.add((Component)this.jLabel6, gridBagConstraints);
        this.jLabel1.setFont(new Font("Tahoma", 1, 12));
        this.jLabel1.setHorizontalAlignment(4);
        this.jLabel1.setText(POSConstants.DISCOUNT + ":");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        this.jPanel3.add((Component)this.jLabel1, gridBagConstraints);
        this.jLabel2.setFont(new Font("Tahoma", 1, 12));
        this.jLabel2.setHorizontalAlignment(4);
        this.jLabel2.setText(POSConstants.TAX + ":");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        this.jPanel3.add((Component)this.jLabel2, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        this.jPanel3.add((Component)this.jSeparator2, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        this.jPanel3.add((Component)this.jSeparator3, gridBagConstraints);
        this.tfSubtotal.setEditable(false);
        this.tfSubtotal.setFont(new Font("Tahoma", 1, 12));
        this.gridBagConstraints_1 = new GridBagConstraints();
        this.gridBagConstraints_1.anchor = 17;
        this.gridBagConstraints_1.gridwidth = 0;
        this.gridBagConstraints_1.insets = new Insets(3, 5, 0, 5);
        this.jPanel3.add((Component)this.tfSubtotal, this.gridBagConstraints_1);
        this.tfTax.setEditable(false);
        this.tfTax.setFont(new Font("Tahoma", 1, 12));
        this.gridBagConstraints_3 = new GridBagConstraints();
        this.gridBagConstraints_3.anchor = 17;
        this.gridBagConstraints_3.gridx = 1;
        this.gridBagConstraints_3.gridy = 3;
        this.gridBagConstraints_3.insets = new Insets(3, 5, 0, 5);
        this.jPanel3.add((Component)this.tfTax, this.gridBagConstraints_3);
        this.tfDiscount.setEditable(false);
        this.tfDiscount.setFont(new Font("Tahoma", 1, 12));
        this.gridBagConstraints_2 = new GridBagConstraints();
        this.gridBagConstraints_2.anchor = 17;
        this.gridBagConstraints_2.gridx = 1;
        this.gridBagConstraints_2.gridy = 2;
        this.gridBagConstraints_2.insets = new Insets(3, 5, 0, 5);
        this.jPanel3.add((Component)this.tfDiscount, this.gridBagConstraints_2);
        this.tfTotal.setEditable(false);
        this.tfTotal.setFont(new Font("Tahoma", 1, 12));
        this.gridBagConstraints_4 = new GridBagConstraints();
        this.gridBagConstraints_4.anchor = 17;
        this.gridBagConstraints_4.gridx = 1;
        this.gridBagConstraints_4.gridy = 4;
        this.gridBagConstraints_4.gridwidth = 0;
        this.gridBagConstraints_4.insets = new Insets(3, 5, 3, 5);
        this.jPanel3.add((Component)this.tfTotal, this.gridBagConstraints_4);
        this.jPanel1.add((Component)this.jPanel3, "North");
        this.jPanel5.setLayout(new GridBagLayout());
        this.btnScrollUp.setIcon(IconFactory.getIcon("/ui_icons/", "up.png"));
        this.btnScrollUp.setPreferredSize(new Dimension(50, 45));
        this.btnScrollUp.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                TicketForSplitView.this.doScrollUp(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 2, 0, 0);
        this.jPanel5.add((Component)this.btnScrollUp, gridBagConstraints);
        this.btnScrollDown.setIcon(IconFactory.getIcon("/ui_icons/", "down.png"));
        this.btnScrollDown.setPreferredSize(new Dimension(50, 45));
        this.btnScrollDown.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                TicketForSplitView.this.doScrollDown(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 0, 0);
        this.jPanel5.add((Component)this.btnScrollDown, gridBagConstraints);
        this.btnTransferToTicket1.setText("1");
        this.btnTransferToTicket1.setPreferredSize(new Dimension(60, 45));
        this.btnTransferToTicket1.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                TicketForSplitView.this.btnTransferToTicket1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 2);
        this.jPanel5.add((Component)this.btnTransferToTicket1, gridBagConstraints);
        this.btnTransferToTicket2.setText("2");
        this.btnTransferToTicket2.setPreferredSize(new Dimension(60, 45));
        this.btnTransferToTicket2.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                TicketForSplitView.this.btnTransferToTicket2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        this.jPanel5.add((Component)this.btnTransferToTicket2, gridBagConstraints);
        this.btnTransferToTicket3.setText("3");
        this.btnTransferToTicket3.setPreferredSize(new Dimension(60, 45));
        this.btnTransferToTicket3.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                TicketForSplitView.this.btnTransferToTicket3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 0, 0);
        this.jPanel5.add((Component)this.btnTransferToTicket3, gridBagConstraints);
        this.jPanel1.add((Component)this.jPanel5, "Center");
        this.add((Component)this.jPanel1, "South");
        this.jPanel2.setLayout(new BorderLayout());
        this.jScrollPane1.setHorizontalScrollBarPolicy(31);
        this.jScrollPane1.setVerticalScrollBarPolicy(21);
        this.jScrollPane1.setViewportView(this.ticketViewerTable);
        this.jPanel2.add((Component)this.jScrollPane1, "Center");
        this.add((Component)this.jPanel2, "Center");
    }

    private void btnTransferToTicket3ActionPerformed(ActionEvent evt) {
        int selectedRow;
        ITicketItem object;
        if (this.ticketView3 != null && this.ticketView3.isVisible() && (object = this.ticketViewerTable.get(selectedRow = this.ticketViewerTable.getSelectedRow())) instanceof TicketItem) {
            this.transferTicketItem((TicketItem)object, this.ticketView3);
        }
    }

    private void btnTransferToTicket2ActionPerformed(ActionEvent evt) {
        int selectedRow;
        ITicketItem object;
        if (this.ticketView2 != null && this.ticketView2.isVisible() && (object = this.ticketViewerTable.get(selectedRow = this.ticketViewerTable.getSelectedRow())) instanceof TicketItem) {
            this.transferTicketItem((TicketItem)object, this.ticketView2);
        }
    }

    private void btnTransferToTicket1ActionPerformed(ActionEvent evt) {
        int selectedRow;
        ITicketItem object;
        if (this.ticketView1 != null && this.ticketView1.isVisible() && (object = this.ticketViewerTable.get(selectedRow = this.ticketViewerTable.getSelectedRow())) instanceof TicketItem) {
            this.transferTicketItem((TicketItem)object, this.ticketView1);
        }
    }

    private void doScrollDown(ActionEvent evt) {
        this.ticketViewerTable.scrollDown();
    }

    private void doScrollUp(ActionEvent evt) {
        this.ticketViewerTable.scrollUp();
    }

    public void updateModel() {
        this.ticket.calculatePrice();
    }

    public void updateView() {
        if (this.ticket == null || this.ticket.getTicketItems() == null || this.ticket.getTicketItems().size() <= 0) {
            this.tfSubtotal.setText("");
            this.tfDiscount.setText("");
            this.tfTax.setText("");
            this.tfTotal.setText("");
            return;
        }
        this.ticket.calculatePrice();
        this.tfSubtotal.setText(NumberUtil.formatNumber(this.ticket.getSubtotalAmount()));
        this.tfDiscount.setText(NumberUtil.formatNumber(this.ticket.getDiscountAmount()));
        this.tfTax.setText(NumberUtil.formatNumber(this.ticket.getTaxAmount()));
        this.tfTotal.setText(NumberUtil.formatNumber(this.ticket.getTotalAmount()));
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket _ticket) {
        this.ticket = _ticket;
        this.ticketViewerTable.setTicket(_ticket);
        this.updateView();
    }

    public void transferTicketItem(TicketItem ticketItem, TicketForSplitView toTicketView) {
        this.transferTicketItem(ticketItem, toTicketView, false);
    }

    public void transferTicketItem(TicketItem ticketItem, TicketForSplitView toTicketView, boolean fullTicketItem) {
        List<TicketItemModifier> addOnsList;
        List<TicketItemModifier> ticketItemModifiers;
        TicketItem newTicketItem = new TicketItem();
        if (!fullTicketItem) {
            newTicketItem.setItemCount(1);
        } else {
            newTicketItem.setItemCount(ticketItem.getItemCount());
        }
        newTicketItem.setItemId(ticketItem.getItemId());
        newTicketItem.setHasModifiers(ticketItem.isHasModifiers());
        newTicketItem.setName(ticketItem.getName());
        newTicketItem.setGroupName(ticketItem.getGroupName());
        newTicketItem.setCategoryName(ticketItem.getCategoryName());
        newTicketItem.setUnitPrice(ticketItem.getUnitPrice());
        newTicketItem.setTreatAsSeat(ticketItem.isTreatAsSeat());
        newTicketItem.setSeatNumber(ticketItem.getSeatNumber());
        List<TicketItemDiscount> discounts = ticketItem.getDiscounts();
        if (discounts != null) {
            ArrayList<TicketItemDiscount> newDiscounts = new ArrayList<TicketItemDiscount>();
            for (TicketItemDiscount ticketItemDiscount : discounts) {
                TicketItemDiscount newDiscount = new TicketItemDiscount(ticketItemDiscount);
                newDiscount.setTicketItem(newTicketItem);
                newDiscounts.add(newDiscount);
            }
            newTicketItem.setDiscounts(newDiscounts);
        }
        if ((ticketItemModifiers = ticketItem.getTicketItemModifiers()) != null) {
            for (TicketItemModifier ticketItemModifier : ticketItemModifiers) {
                TicketItemModifier newModifier = new TicketItemModifier();
                newModifier.setModifierId(ticketItemModifier.getModifierId());
                newModifier.setMenuItemModifierGroupId(ticketItemModifier.getMenuItemModifierGroupId());
                newModifier.setItemCount(ticketItemModifier.getItemCount());
                newModifier.setName(ticketItemModifier.getName());
                newModifier.setUnitPrice(ticketItemModifier.getUnitPrice());
                newModifier.setTaxRate(ticketItemModifier.getTaxRate());
                newModifier.setModifierType(ticketItemModifier.getModifierType());
                newModifier.setPrintedToKitchen(ticketItemModifier.isPrintedToKitchen());
                newModifier.setShouldPrintToKitchen(ticketItemModifier.isShouldPrintToKitchen());
                newModifier.setTicketItem(newTicketItem);
            }
        }
        if ((addOnsList = ticketItem.getAddOns()) != null) {
            for (TicketItemModifier addOns : ticketItem.getAddOns()) {
                TicketItemModifier newAddOns = new TicketItemModifier();
                newAddOns.setModifierId(addOns.getModifierId());
                newAddOns.setMenuItemModifierGroupId(addOns.getMenuItemModifierGroupId());
                newAddOns.setItemCount(addOns.getItemCount());
                newAddOns.setName(addOns.getName());
                newAddOns.setUnitPrice(addOns.getUnitPrice());
                newAddOns.setTaxRate(addOns.getTaxRate());
                newAddOns.setModifierType(addOns.getModifierType());
                newAddOns.setPrintedToKitchen(addOns.isPrintedToKitchen());
                newAddOns.setShouldPrintToKitchen(addOns.isShouldPrintToKitchen());
                newAddOns.setTicketItem(newTicketItem);
                newTicketItem.addToaddOns(newAddOns);
            }
        }
        newTicketItem.setTaxRate(ticketItem.getTaxRate());
        newTicketItem.setBeverage(ticketItem.isBeverage());
        newTicketItem.setShouldPrintToKitchen(ticketItem.isShouldPrintToKitchen());
        newTicketItem.setPrinterGroup(ticketItem.getPrinterGroup());
        newTicketItem.setPrintedToKitchen(ticketItem.isPrintedToKitchen());
        int itemCount = ticketItem.getItemCount();
        toTicketView.ticketViewerTable.addTicketItem(newTicketItem);
        if (itemCount > 1 && !fullTicketItem && !ticketItem.isHasModifiers().booleanValue()) {
            ticketItem.setItemCount(--itemCount);
        } else {
            this.ticketViewerTable.delete(ticketItem.getTableRowNum());
        }
        this.repaint();
        toTicketView.repaint();
        this.updateView();
        toTicketView.updateView();
    }

    public void transferAllTicketItem(TicketItem ticketItem, TicketForSplitView toTicketView) {
        TicketItem newTicketItem = new TicketItem();
        newTicketItem.setItemId(ticketItem.getItemId());
        newTicketItem.setItemCount(ticketItem.getItemCount());
        newTicketItem.setHasModifiers(ticketItem.isHasModifiers());
        newTicketItem.setTicketItemModifiers(new ArrayList<TicketItemModifier>(ticketItem.getTicketItemModifiers()));
        newTicketItem.setName(ticketItem.getName());
        newTicketItem.setGroupName(ticketItem.getGroupName());
        newTicketItem.setCategoryName(ticketItem.getCategoryName());
        newTicketItem.setUnitPrice(ticketItem.getUnitPrice());
        List<TicketItemDiscount> discounts = ticketItem.getDiscounts();
        if (discounts != null) {
            ArrayList<TicketItemDiscount> newDiscounts = new ArrayList<TicketItemDiscount>();
            for (TicketItemDiscount ticketItemDiscount : discounts) {
                TicketItemDiscount newDiscount = new TicketItemDiscount(ticketItemDiscount);
                newDiscount.setTicketItem(newTicketItem);
                newDiscounts.add(newDiscount);
            }
            newTicketItem.setDiscounts(newDiscounts);
        }
        newTicketItem.setTaxRate(ticketItem.getTaxRate());
        newTicketItem.setBeverage(ticketItem.isBeverage());
        newTicketItem.setShouldPrintToKitchen(ticketItem.isShouldPrintToKitchen());
        newTicketItem.setPrinterGroup(ticketItem.getPrinterGroup());
        newTicketItem.setPrintedToKitchen(ticketItem.isPrintedToKitchen());
        toTicketView.ticketViewerTable.addAllTicketItem(newTicketItem);
        this.ticketViewerTable.delete(ticketItem.getTableRowNum());
        this.repaint();
        toTicketView.repaint();
        this.updateView();
        toTicketView.updateView();
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (this.ticket == null || this.ticket.getTicketItems() == null || this.ticket.getTicketItems().size() <= 0) {
            this.tfSubtotal.setText("");
            this.tfDiscount.setText("");
            this.tfTax.setText("");
            this.tfTotal.setText("");
            return;
        }
        this.ticket.calculatePrice();
        this.tfSubtotal.setText(NumberUtil.formatNumber(this.ticket.getSubtotalAmount()));
        this.tfDiscount.setText(NumberUtil.formatNumber(this.ticket.getDiscountAmount()));
        this.tfTax.setText(NumberUtil.formatNumber(this.ticket.getTaxAmount()));
        this.tfTotal.setText(NumberUtil.formatNumber(this.ticket.getTotalAmount()));
    }

    public TicketForSplitView getTicketView1() {
        return this.ticketView1;
    }

    public void setTicketView1(TicketForSplitView ticketView1) {
        this.ticketView1 = ticketView1;
    }

    public TicketForSplitView getTicketView2() {
        return this.ticketView2;
    }

    public void setTicketView2(TicketForSplitView ticketView2) {
        this.ticketView2 = ticketView2;
    }

    public TicketForSplitView getTicketView3() {
        return this.ticketView3;
    }

    public void setTicketView3(TicketForSplitView ticketView3) {
        this.ticketView3 = ticketView3;
    }

    public int getViewNumber() {
        return this.viewNumber;
    }

    public void setViewNumber(int viewNumber) {
        this.viewNumber = viewNumber;
        TitledBorder titledBorder = new TitledBorder(Messages.getString("TicketForSplitView.1") + viewNumber);
        titledBorder.setTitleJustification(2);
        this.setBorder(titledBorder);
        switch (viewNumber) {
            case 1: {
                this.btnTransferToTicket1.setIcon(IconFactory.getIcon("next.png"));
                this.btnTransferToTicket1.setText("2");
                this.btnTransferToTicket2.setIcon(IconFactory.getIcon("next.png"));
                this.btnTransferToTicket2.setText("3");
                this.btnTransferToTicket3.setIcon(IconFactory.getIcon("next.png"));
                this.btnTransferToTicket3.setText("4");
                break;
            }
            case 2: {
                this.btnTransferToTicket1.setIcon(IconFactory.getIcon("previous.png"));
                this.btnTransferToTicket1.setText("1");
                this.btnTransferToTicket2.setIcon(IconFactory.getIcon("next.png"));
                this.btnTransferToTicket2.setText("3");
                this.btnTransferToTicket3.setIcon(IconFactory.getIcon("next.png"));
                this.btnTransferToTicket3.setText("4");
                break;
            }
            case 3: {
                this.btnTransferToTicket1.setIcon(IconFactory.getIcon("previous.png"));
                this.btnTransferToTicket1.setText("1");
                this.btnTransferToTicket2.setIcon(IconFactory.getIcon("previous.png"));
                this.btnTransferToTicket2.setText("2");
                this.btnTransferToTicket3.setIcon(IconFactory.getIcon("next.png"));
                this.btnTransferToTicket3.setText("4");
                break;
            }
            case 4: {
                this.btnTransferToTicket1.setIcon(IconFactory.getIcon("previous.png"));
                this.btnTransferToTicket1.setText("1");
                this.btnTransferToTicket2.setIcon(IconFactory.getIcon("previous.png"));
                this.btnTransferToTicket2.setText("2");
                this.btnTransferToTicket3.setIcon(IconFactory.getIcon("previous.png"));
                this.btnTransferToTicket3.setText("3");
                break;
            }
            default: {
                throw new RuntimeException(Messages.getString("TicketForSplitView.2"));
            }
        }
    }
}

