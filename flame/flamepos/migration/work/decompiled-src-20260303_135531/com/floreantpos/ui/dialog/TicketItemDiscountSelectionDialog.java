/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.model.Discount;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.ScrollableFlowPanel;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;

public class TicketItemDiscountSelectionDialog
extends OkCancelOptionDialog {
    private ScrollableFlowPanel buttonsPanel;
    private Ticket ticket;
    private Discount discount;
    private List<TicketItem> addedTicketItems = new ArrayList<TicketItem>();

    public TicketItemDiscountSelectionDialog(Ticket ticket, Discount discount) {
        super(POSUtil.getFocusedWindow(), POSConstants.SELECT_ITEMS);
        this.ticket = ticket;
        this.discount = discount;
        this.initComponent();
        this.rendererTicketItems();
    }

    private void initComponent() {
        this.setOkButtonText(POSConstants.SAVE_BUTTON_TEXT);
        this.buttonsPanel = new ScrollableFlowPanel(3);
        PosScrollPane scrollPane = new PosScrollPane(this.buttonsPanel, 20, 31);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), scrollPane.getBorder()));
        this.getContentPanel().add((Component)scrollPane, "Center");
        this.setSize(600, 500);
    }

    private void rendererTicketItems() {
        this.buttonsPanel.getContentPane().removeAll();
        List<TicketItem> ticketItems = this.ticket.getTicketItems();
        try {
            Dimension size = PosUIManager.getSize(115, 80);
            for (TicketItem ticketItem : ticketItems) {
                Integer itemId = Integer.parseInt(ticketItem.getItemId().toString());
                MenuItem menuItem = MenuItemDAO.getInstance().get(itemId);
                List<MenuItem> menuItems = this.discount.getMenuItems();
                if (menuItem == null || !this.discount.isApplyToAll().booleanValue() && !menuItems.contains(menuItem)) continue;
                TicketItemButton ticketItemButton = new TicketItemButton(ticketItem);
                ticketItemButton.setPreferredSize(size);
                this.buttonsPanel.add(ticketItemButton);
            }
            this.buttonsPanel.repaint();
            this.buttonsPanel.revalidate();
        }
        catch (PosException e) {
            POSMessageDialog.showError(this, e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void doOk() {
        if (this.addedTicketItems.isEmpty()) {
            POSMessageDialog.showMessage("Please select one or more item.");
            return;
        }
        this.setCanceled(false);
        this.dispose();
    }

    @Override
    public void doCancel() {
        this.addedTicketItems.clear();
        this.setCanceled(true);
        this.dispose();
    }

    public List<TicketItem> getSelectedTicketItems() {
        return this.addedTicketItems;
    }

    private class TicketItemButton
    extends POSToggleButton
    implements ActionListener {
        TicketItem ticketItem;

        TicketItemButton(TicketItem ticketItem) {
            this.ticketItem = ticketItem;
            this.setText("<html><body><center>" + ticketItem.getName() + "</center></body></html>");
            this.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (this.isSelected()) {
                TicketItemDiscountSelectionDialog.this.addedTicketItems.add(this.ticketItem);
            } else {
                TicketItemDiscountSelectionDialog.this.addedTicketItems.remove(this.ticketItem);
            }
        }
    }
}

