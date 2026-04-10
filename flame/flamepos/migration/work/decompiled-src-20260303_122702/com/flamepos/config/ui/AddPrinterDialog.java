/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.config.ui.PrintServiceComboRenderer;
import com.floreantpos.config.ui.VirtualPrinterConfigDialog;
import com.floreantpos.model.Printer;
import com.floreantpos.model.VirtualPrinter;
import com.floreantpos.model.dao.VirtualPrinterDAO;
import com.floreantpos.swing.ComboBoxModel;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

public class AddPrinterDialog
extends POSDialog {
    private static final String DO_NOT_PRINT = "Do not print";
    private Printer printer;
    private VirtualPrinter virtualPrinter;
    private FixedLengthTextField tfName;
    private JComboBox cbVirtualPrinter;
    private JComboBox cbDevice;
    private JCheckBox chckbxDefault;
    TitlePanel titlePanel;

    public AddPrinterDialog() throws HeadlessException {
        super((Frame)POSUtil.getBackOfficeWindow(), true);
        this.setTitle(Messages.getString("AddPrinterDialog.0"));
        this.setMinimumSize(new Dimension(400, 200));
        this.setDefaultCloseOperation(2);
        this.pack();
    }

    @Override
    public void initUI() {
        this.setLayout(new BorderLayout(5, 5));
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout((LayoutManager)new MigLayout("", "[][grow][]", "[][][][][grow]"));
        this.titlePanel = new TitlePanel();
        this.titlePanel.setTitle("Printer Type:");
        this.add((Component)this.titlePanel, "North");
        JLabel lblName = new JLabel("Virtual Printer Name : ");
        centerPanel.add((Component)lblName, "cell 0 0,alignx trailing");
        this.tfName = new FixedLengthTextField(20);
        this.cbVirtualPrinter = new JComboBox();
        List<VirtualPrinter> virtualPrinters = VirtualPrinterDAO.getInstance().findAll();
        this.cbVirtualPrinter.setModel(new DefaultComboBoxModel<VirtualPrinter>(virtualPrinters.toArray(new VirtualPrinter[0])));
        centerPanel.add((Component)this.tfName, "cell 1 0,growx");
        JButton btnNew = new JButton(Messages.getString("AddPrinterDialog.7"));
        btnNew.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                AddPrinterDialog.this.doAddNewVirtualPrinter();
            }
        });
        JLabel lblDevice = new JLabel(Messages.getString("AddPrinterDialog.9"));
        centerPanel.add((Component)lblDevice, "cell 0 1,alignx trailing");
        this.cbDevice = new JComboBox();
        ArrayList<Object> printerServices = new ArrayList<Object>();
        printerServices.add(DO_NOT_PRINT);
        PrintService[] lookupPrintServices = PrintServiceLookup.lookupPrintServices(null, null);
        this.cbDevice.addItem(null);
        for (int i = 0; i < lookupPrintServices.length; ++i) {
            printerServices.add(lookupPrintServices[i]);
        }
        this.cbDevice.setModel(new ComboBoxModel(printerServices));
        this.cbDevice.setRenderer(new PrintServiceComboRenderer());
        centerPanel.add((Component)this.cbDevice, "cell 1 1,growx");
        this.chckbxDefault = new JCheckBox(Messages.getString("AddPrinterDialog.12"));
        centerPanel.add((Component)this.chckbxDefault, "cell 1 2");
        JSeparator separator = new JSeparator();
        centerPanel.add((Component)separator, "cell 0 3 3 1,growx,gapy 50px");
        this.add((Component)centerPanel, "Center");
        JPanel panel = new JPanel();
        JButton btnOk = new JButton(Messages.getString("AddPrinterDialog.16"));
        btnOk.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                AddPrinterDialog.this.doAddPrinter();
            }
        });
        panel.add(btnOk);
        JButton btnCancel = new JButton(Messages.getString("AddPrinterDialog.17"));
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                AddPrinterDialog.this.setCanceled(true);
                AddPrinterDialog.this.dispose();
            }
        });
        panel.add(btnCancel);
        this.add((Component)panel, "South");
    }

    protected void doAddNewVirtualPrinter() {
        VirtualPrinterConfigDialog dialog = new VirtualPrinterConfigDialog();
        dialog.open();
        if (dialog.isCanceled()) {
            return;
        }
        VirtualPrinter virtualPrinter = dialog.getPrinter();
        DefaultComboBoxModel model = (DefaultComboBoxModel)this.cbVirtualPrinter.getModel();
        model.addElement(virtualPrinter);
        this.cbVirtualPrinter.setSelectedItem(virtualPrinter);
    }

    protected void doAddPrinter() {
        String name = this.tfName.getText();
        if (StringUtils.isEmpty((String)name)) {
            POSMessageDialog.showMessage(this, Messages.getString("VirtualPrinterConfigDialog.11"));
            return;
        }
        if (this.virtualPrinter == null) {
            this.virtualPrinter = new VirtualPrinter();
        }
        this.virtualPrinter.setName(name);
        PrintService printService = null;
        Object selectedObject = this.cbDevice.getSelectedItem();
        if (selectedObject instanceof PrintService) {
            printService = (PrintService)selectedObject;
        }
        boolean defaultPrinter = this.chckbxDefault.isSelected();
        if (this.printer == null) {
            this.printer = new Printer();
        }
        this.printer.setVirtualPrinter(this.virtualPrinter);
        if (printService != null && printService.getName() != null) {
            this.printer.setDeviceName(printService.getName());
        } else {
            this.printer.setDeviceName(null);
        }
        this.printer.setDefaultPrinter(defaultPrinter);
        this.setCanceled(false);
        this.dispose();
    }

    public Printer getPrinter() {
        return this.printer;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
        this.virtualPrinter = printer.getVirtualPrinter();
        this.tfName.setText(printer.getVirtualPrinter().getName());
        if (printer != null) {
            this.cbVirtualPrinter.setSelectedItem(printer.getVirtualPrinter());
            this.chckbxDefault.setSelected(printer.isDefaultPrinter());
            if (printer.getDeviceName() == "No Print") {
                this.cbDevice.setSelectedItem(DO_NOT_PRINT);
                return;
            }
            ComboBoxModel deviceModel = (ComboBoxModel)this.cbDevice.getModel();
            for (int i = 0; i < deviceModel.getSize(); ++i) {
                PrintService printService;
                Object selectedObject = deviceModel.getElementAt(i);
                if (!(selectedObject instanceof PrintService) || (printService = (PrintService)selectedObject) == null || !printService.getName().equals(printer.getDeviceName())) continue;
                this.cbDevice.setSelectedIndex(i);
                break;
            }
        }
    }
}

