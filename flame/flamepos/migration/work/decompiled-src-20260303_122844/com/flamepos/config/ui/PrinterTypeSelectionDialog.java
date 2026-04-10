/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.config.ui;

import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.model.VirtualPrinter;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;

public class PrinterTypeSelectionDialog
extends POSDialog
implements ActionListener {
    private JPanel buttonsPanel;
    private POSToggleButton btnReportPrinter;
    private POSToggleButton btnReceiptPrinter;
    private POSToggleButton btnKitchenPrinter;
    private POSToggleButton btnPackingPrinter;
    private POSToggleButton btnKitchenDisplayPrinter;
    private int selectedPrinterType;

    public PrinterTypeSelectionDialog() {
        super((Frame)POSUtil.getBackOfficeWindow(), true);
        this.initializeComponent();
        this.initializeData();
    }

    private void initializeComponent() {
        this.setTitle("Select Printer Type");
        this.setLayout(new BorderLayout());
        TitlePanel titlePanel = new TitlePanel();
        titlePanel.setTitle("Select Printer Type");
        this.add((Component)titlePanel, "North");
        JPanel buttonActionPanel = new JPanel((LayoutManager)new MigLayout("fill"));
        PosButton btnCancel = new PosButton(POSConstants.CANCEL.toUpperCase());
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                PrinterTypeSelectionDialog.this.selectedPrinterType = 0;
                PrinterTypeSelectionDialog.this.setCanceled(true);
                PrinterTypeSelectionDialog.this.dispose();
            }
        });
        buttonActionPanel.add((Component)btnCancel, "grow, span");
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        footerPanel.add((Component)new JSeparator(), "North");
        footerPanel.add(buttonActionPanel);
        this.add((Component)footerPanel, "South");
        this.buttonsPanel = new JPanel((LayoutManager)new MigLayout("fill", "sg, fill", ""));
        this.add((Component)this.buttonsPanel, "Center");
        this.setSize(500, 280);
    }

    private void initializeData() {
        try {
            ButtonGroup group = new ButtonGroup();
            this.btnReportPrinter = new POSToggleButton(VirtualPrinter.PRINTER_TYPE_NAMES[0]);
            this.btnReceiptPrinter = new POSToggleButton(VirtualPrinter.PRINTER_TYPE_NAMES[1]);
            this.btnKitchenPrinter = new POSToggleButton(VirtualPrinter.PRINTER_TYPE_NAMES[2]);
            this.btnPackingPrinter = new POSToggleButton(VirtualPrinter.PRINTER_TYPE_NAMES[3]);
            this.btnKitchenDisplayPrinter = new POSToggleButton(VirtualPrinter.PRINTER_TYPE_NAMES[4]);
            this.btnReportPrinter.addActionListener(this);
            this.btnReceiptPrinter.addActionListener(this);
            this.btnKitchenPrinter.addActionListener(this);
            this.btnPackingPrinter.addActionListener(this);
            this.btnKitchenDisplayPrinter.addActionListener(this);
            this.buttonsPanel.add((Component)this.btnReportPrinter, "growy");
            this.buttonsPanel.add((Component)this.btnReceiptPrinter, "growy");
            this.buttonsPanel.add((Component)this.btnKitchenPrinter, "growy");
            this.buttonsPanel.add((Component)this.btnPackingPrinter, "growy");
            this.buttonsPanel.add((Component)this.btnKitchenDisplayPrinter, "growy");
            group.add(this.btnReportPrinter);
            group.add(this.btnReceiptPrinter);
            group.add(this.btnKitchenPrinter);
            group.add(this.btnPackingPrinter);
            group.add(this.btnKitchenDisplayPrinter);
        }
        catch (PosException e) {
            POSMessageDialog.showError(this, e.getLocalizedMessage(), e);
        }
    }

    protected void doFinish() {
        this.setCanceled(false);
        this.dispose();
    }

    public int getSelectedPrinterType() {
        return this.selectedPrinterType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == this.btnReportPrinter) {
            this.selectedPrinterType = 0;
        } else if (source == this.btnReceiptPrinter) {
            this.selectedPrinterType = 1;
        } else if (source == this.btnKitchenPrinter) {
            this.selectedPrinterType = 2;
        } else if (source == this.btnPackingPrinter) {
            this.selectedPrinterType = 3;
        } else if (source == this.btnKitchenDisplayPrinter) {
            this.selectedPrinterType = 4;
        }
        this.doFinish();
    }
}

