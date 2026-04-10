/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.OrderType;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.POSDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class OrderTypeSelectionDialog
extends POSDialog {
    private OrderType selectedOrderType;

    public OrderTypeSelectionDialog() throws HeadlessException {
        this.setTitle(Messages.getString("OrderTypeSelectionDialog.0"));
        this.setResizable(false);
        this.setLayout(new BorderLayout(5, 5));
        JPanel orderTypePanel = new JPanel(new GridLayout(1, 0, 5, 5));
        orderTypePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        List<OrderType> values = Application.getInstance().getOrderTypes();
        for (final OrderType orderType : values) {
            if (!orderType.isBarTab().booleanValue()) continue;
            PosButton button = new PosButton(orderType.toString());
            button.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    OrderTypeSelectionDialog.this.selectedOrderType = orderType;
                    OrderTypeSelectionDialog.this.setCanceled(false);
                    OrderTypeSelectionDialog.this.dispose();
                }
            });
            orderTypePanel.add(button);
        }
        PosButton btnCancel = new PosButton(POSConstants.CANCEL_BUTTON_TEXT);
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                OrderTypeSelectionDialog.this.setCanceled(true);
                OrderTypeSelectionDialog.this.dispose();
            }
        });
        JPanel actionPanel = new JPanel();
        actionPanel.add(btnCancel);
        this.add(orderTypePanel);
        this.add((Component)actionPanel, "South");
        this.setSize(450, 300);
    }

    public OrderType getSelectedOrderType() {
        return this.selectedOrderType;
    }
}

