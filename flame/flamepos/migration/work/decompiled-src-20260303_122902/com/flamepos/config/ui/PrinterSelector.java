/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.config.ui;

import com.floreantpos.POSConstants;
import com.floreantpos.config.ui.ConfigurationView;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;

public class PrinterSelector
extends ConfigurationView {
    private JComboBox cbPrinters;

    public PrinterSelector() {
        this.initComponents();
    }

    private void initComponents() {
        this.cbPrinters = new JComboBox();
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.cbPrinters, 0, 376, Short.MAX_VALUE).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.cbPrinters, -2, -1, -2).addContainerGap(-1, Short.MAX_VALUE)));
    }

    @Override
    public String getName() {
        return POSConstants.SELECT_PRINTER;
    }

    @Override
    public void initialize() throws Exception {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        this.cbPrinters.setModel(new DefaultComboBoxModel<PrintService>(printServices));
    }

    @Override
    public boolean save() throws Exception {
        return false;
    }
}

