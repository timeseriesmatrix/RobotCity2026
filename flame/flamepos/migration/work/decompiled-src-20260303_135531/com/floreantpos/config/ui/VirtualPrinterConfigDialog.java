/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.model.VirtualPrinter;
import com.floreantpos.model.dao.VirtualPrinterDAO;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

public class VirtualPrinterConfigDialog
extends POSDialog {
    private VirtualPrinter printer;
    private FixedLengthTextField tfName;

    public VirtualPrinterConfigDialog() throws HeadlessException {
        super((Frame)POSUtil.getBackOfficeWindow(), true);
        this.setTitle(Messages.getString("VirtualPrinterConfigDialog.0"));
        this.init();
        this.pack();
        this.setResizable(false);
        this.setDefaultCloseOperation(2);
    }

    public void init() {
        this.getContentPane().setLayout((LayoutManager)new MigLayout("", "[][grow]", "[][][]"));
        JLabel lblName = new JLabel(Messages.getString("VirtualPrinterConfigDialog.4"));
        this.getContentPane().add((Component)lblName, "cell 0 0,alignx trailing");
        this.tfName = new FixedLengthTextField(60);
        this.getContentPane().add((Component)this.tfName, "cell 1 0,growx");
        JSeparator separator = new JSeparator();
        this.getContentPane().add((Component)separator, "cell 0 1 2 1,growx, gap top 50px");
        JPanel panel = new JPanel();
        this.getContentPane().add((Component)panel, "cell 0 4 2 1,grow");
        JButton btnOk = new JButton(Messages.getString("VirtualPrinterConfigDialog.9"));
        btnOk.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                VirtualPrinterConfigDialog.this.doAddPrinter();
            }
        });
        panel.add(btnOk);
        JButton btnCancel = new JButton(Messages.getString("VirtualPrinterConfigDialog.10"));
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                VirtualPrinterConfigDialog.this.setCanceled(true);
                VirtualPrinterConfigDialog.this.dispose();
            }
        });
        panel.add(btnCancel);
    }

    protected void doAddPrinter() {
        try {
            String name = this.tfName.getText();
            if (StringUtils.isEmpty((String)name)) {
                POSMessageDialog.showMessage(this, Messages.getString("VirtualPrinterConfigDialog.11"));
                return;
            }
            VirtualPrinterDAO printerDAO = VirtualPrinterDAO.getInstance();
            if (printerDAO.findPrinterByName(name) != null) {
                POSMessageDialog.showMessage(this, Messages.getString("VirtualPrinterConfigDialog.12"));
                return;
            }
            if (this.printer == null) {
                this.printer = new VirtualPrinter();
            }
            this.printer.setName(name);
            printerDAO.saveOrUpdate(this.printer);
            this.setCanceled(false);
            this.dispose();
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, e.getMessage(), e);
        }
    }

    public VirtualPrinter getPrinter() {
        return this.printer;
    }

    public void setPrinter(VirtualPrinter printer) {
        this.printer = printer;
        if (printer != null) {
            this.tfName.setText(printer.getName());
        }
    }
}

