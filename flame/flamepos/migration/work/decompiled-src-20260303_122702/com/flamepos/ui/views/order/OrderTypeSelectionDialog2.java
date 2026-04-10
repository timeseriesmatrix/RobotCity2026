/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views.order;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.model.Ticket;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.POSDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

public class OrderTypeSelectionDialog2
extends POSDialog {
    Ticket ticket;
    private String selectedOrderType;
    private PosButton btnForHere;
    private PosButton btnToGo;

    public OrderTypeSelectionDialog2(Ticket ticket) throws HeadlessException {
        this.ticket = ticket;
        this.initializeComponent();
    }

    private void initializeComponent() {
        this.setTitle(Messages.getString("OrderTypeSelectionDialog.0"));
        this.setResizable(false);
        this.setLayout(new BorderLayout(5, 5));
        JPanel orderTypePanel = new JPanel(new GridLayout(1, 0, 10, 10));
        orderTypePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.btnForHere = new PosButton("FOR HERE");
        this.btnForHere.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                OrderTypeSelectionDialog2.this.selectedOrderType = "FOR HERE";
                OrderTypeSelectionDialog2.this.setCanceled(false);
                OrderTypeSelectionDialog2.this.dispose();
            }
        });
        orderTypePanel.add(this.btnForHere);
        this.btnToGo = new PosButton("TO GO");
        this.btnToGo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                OrderTypeSelectionDialog2.this.selectedOrderType = "TO GO";
                OrderTypeSelectionDialog2.this.setCanceled(false);
                OrderTypeSelectionDialog2.this.dispose();
            }
        });
        orderTypePanel.add(this.btnToGo);
        PosButton btnCancel = new PosButton(POSConstants.CANCEL_BUTTON_TEXT);
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                OrderTypeSelectionDialog2.this.setCanceled(true);
                OrderTypeSelectionDialog2.this.dispose();
            }
        });
        JPanel actionPanel = new JPanel((LayoutManager)new MigLayout("fill"));
        actionPanel.add((Component)btnCancel, "growx, span");
        this.add(orderTypePanel);
        this.add((Component)actionPanel, "South");
        this.setSize(400, 250);
    }

    public String getSelectedOrderType() {
        return this.selectedOrderType;
    }
}

