/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.demo;

import com.floreantpos.POSConstants;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;

public class KitchenFilterDialog
extends POSDialog {
    public KitchenFilterDialog() {
        this.initializeComponent();
        this.setResizable(true);
    }

    private void initializeComponent() {
        this.setTitle("Select Printer and Order Type");
        this.setLayout(new BorderLayout());
        TitlePanel titlePanel = new TitlePanel();
        titlePanel.setTitle("Select Printer and Order Type");
        this.add((Component)titlePanel, "North");
        JPanel buttonActionPanel = new JPanel((LayoutManager)new MigLayout("fill"));
        PosButton btnCancel = new PosButton(POSConstants.CANCEL.toUpperCase());
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                KitchenFilterDialog.this.setCanceled(true);
                KitchenFilterDialog.this.dispose();
            }
        });
        buttonActionPanel.add((Component)btnCancel, "grow, span");
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        footerPanel.add((Component)new JSeparator(), "North");
        footerPanel.add(buttonActionPanel);
        this.add((Component)footerPanel, "South");
        this.setSize(550, 450);
    }
}

