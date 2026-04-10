/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views;

import com.floreantpos.POSConstants;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.views.order.ViewPanel;
import com.floreantpos.util.PosGuiUtil;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class CashierSwitchBoardView
extends ViewPanel
implements ActionListener {
    public static final String VIEW_NAME = "csbv";
    private PosButton btnNewOrder = new PosButton(POSConstants.NEW_ORDER_BUTTON_TEXT);
    private PosButton btnEditOrder = new PosButton(POSConstants.EDIT_TICKET_BUTTON_TEXT);
    private PosButton btnSettleOrder = new PosButton(POSConstants.SETTLE_TICKET_BUTTON_TEXT);

    public CashierSwitchBoardView() {
        this.setLayout((LayoutManager)new MigLayout("align 50% 50%"));
        JPanel orderPanel = new JPanel((LayoutManager)new MigLayout());
        orderPanel.setBorder(PosGuiUtil.createTitledBorder(POSConstants.CashierSwitchBoardView_LABEL_ORDER));
        orderPanel.add((Component)this.btnNewOrder, "w 160!, h 160!");
        orderPanel.add((Component)this.btnEditOrder, "w 160!, h 160!");
        orderPanel.add((Component)this.btnSettleOrder, "w 160!, h 160!");
        this.btnNewOrder.addActionListener(this);
        this.btnEditOrder.addActionListener(this);
        this.btnSettleOrder.addActionListener(this);
        this.add(orderPanel);
    }

    @Override
    public String getViewName() {
        return VIEW_NAME;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == this.btnNewOrder || source == this.btnEditOrder || source == this.btnSettleOrder) {
            // empty if block
        }
    }
}

