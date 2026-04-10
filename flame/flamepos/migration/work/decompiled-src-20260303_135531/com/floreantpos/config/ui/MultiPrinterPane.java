/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosLog;
import com.floreantpos.config.AppConfig;
import com.floreantpos.config.ui.AddPrinterDialog;
import com.floreantpos.config.ui.PrinterTypeSelectionDialog;
import com.floreantpos.main.Application;
import com.floreantpos.model.Printer;
import com.floreantpos.model.TerminalPrinters;
import com.floreantpos.model.VirtualPrinter;
import com.floreantpos.model.dao.TerminalPrintersDAO;
import com.floreantpos.model.dao.VirtualPrinterDAO;
import com.floreantpos.report.ReceiptPrintService;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jdesktop.swingx.JXTable;

public class MultiPrinterPane
extends JPanel {
    private JList<Printer> list;
    private List<Printer> printers = new ArrayList<Printer>();
    private DefaultListModel<Printer> listModel;
    private JXTable table;
    private BeanTableModel<Printer> tableModel;
    private int selectedPrinterType;

    public MultiPrinterPane() {
    }

    public MultiPrinterPane(String title, List<Printer> allPrinters) {
        this.setBorder(BorderFactory.createTitledBorder(title));
        this.setLayout(new BorderLayout(10, 10));
        JPanel panel = new JPanel();
        this.add((Component)panel, "South");
        JButton btnAdd = new JButton(Messages.getString("MultiPrinterPane.0"));
        btnAdd.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PrinterTypeSelectionDialog dialog = new PrinterTypeSelectionDialog();
                dialog.open();
                if (dialog.isCanceled()) {
                    return;
                }
                MultiPrinterPane.this.selectedPrinterType = dialog.getSelectedPrinterType();
                MultiPrinterPane.this.doAddPrinter();
            }
        });
        panel.add(btnAdd);
        JButton btnEdit = new JButton(Messages.getString("MultiPrinterPane.1"));
        btnEdit.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MultiPrinterPane.this.doEditPrinter();
            }
        });
        panel.add(btnEdit);
        JButton btnDelete = new JButton(POSConstants.DELETE.toUpperCase());
        btnDelete.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MultiPrinterPane.this.doDeletePrinter();
            }
        });
        panel.add(btnDelete);
        JButton btnTest = new JButton("TEST");
        btnTest.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MultiPrinterPane.this.testPrinter();
            }
        });
        panel.add(btnTest);
        this.listModel = new DefaultListModel();
        this.list = new JList<Printer>(this.listModel);
        if (this.printers != null) {
            for (Printer printer : this.printers) {
                this.listModel.addElement(printer);
            }
        }
        this.tableModel = new BeanTableModel(Printer.class);
        this.tableModel.addColumn("Name", "virtualPrinter");
        this.tableModel.addColumn("Printer", "deviceName");
        this.tableModel.addColumn("Type", "type");
        List<VirtualPrinter> virtualPrinters = VirtualPrinterDAO.getInstance().findAll();
        if (virtualPrinters != null && !virtualPrinters.isEmpty()) {
            for (VirtualPrinter virtualPrinter : virtualPrinters) {
                Printer printer = new Printer();
                printer.setVirtualPrinter(virtualPrinter);
                printer.setDeviceName("");
                TerminalPrinters terminalPrinter = TerminalPrintersDAO.getInstance().findPrinters(printer.getVirtualPrinter());
                if (terminalPrinter != null && virtualPrinter.getName().equals(terminalPrinter.getVirtualPrinter().getName())) {
                    printer.setDeviceName(terminalPrinter.getPrinterName());
                }
                this.printers.add(printer);
            }
        }
        this.tableModel.addRows(this.printers);
        this.table = new JXTable(this.tableModel);
        this.add((Component)new JScrollPane((Component)this.table), "Center");
    }

    protected void doEditPrinter() {
        int index = this.table.getSelectedRow();
        if (index < 0) {
            return;
        }
        index = this.table.convertRowIndexToModel(index);
        Printer customPrinter = this.tableModel.getRow(index);
        AddPrinterDialog dialog = new AddPrinterDialog();
        dialog.setPrinter(customPrinter);
        dialog.open();
        if (dialog.isCanceled()) {
            return;
        }
        Printer p = dialog.getPrinter();
        if (p.isDefaultPrinter()) {
            for (Printer printer2 : this.printers) {
                printer2.setDefaultPrinter(false);
            }
        }
        VirtualPrinter virtualPrinter = p.getVirtualPrinter();
        VirtualPrinterDAO.getInstance().saveOrUpdate(virtualPrinter);
        TerminalPrinters terminalPrinter = TerminalPrintersDAO.getInstance().findPrinters(virtualPrinter);
        if (terminalPrinter == null) {
            terminalPrinter = new TerminalPrinters();
        }
        terminalPrinter.setTerminal(Application.getInstance().getTerminal());
        terminalPrinter.setPrinterName(p.getDeviceName());
        terminalPrinter.setVirtualPrinter(p.getVirtualPrinter());
        TerminalPrintersDAO.getInstance().saveOrUpdate(terminalPrinter);
        this.refresh();
    }

    protected void doDeletePrinter() {
        int index = this.table.getSelectedRow();
        if (index < 0) {
            return;
        }
        index = this.table.convertRowIndexToModel(index);
        Printer customPrinter = this.tableModel.getRow(index);
        List<TerminalPrinters> terminalPrinters = TerminalPrintersDAO.getInstance().findAll();
        for (TerminalPrinters terminalPrinter : terminalPrinters) {
            if (!terminalPrinter.getVirtualPrinter().equals(customPrinter.getVirtualPrinter())) continue;
            TerminalPrintersDAO.getInstance().delete(terminalPrinter);
        }
        if (customPrinter.getVirtualPrinter() != null) {
            VirtualPrinterDAO.getInstance().delete(customPrinter.getVirtualPrinter());
        }
        this.refresh();
    }

    protected void doAddPrinter() {
        AddPrinterDialog dialog = new AddPrinterDialog();
        dialog.titlePanel.setTitle(VirtualPrinter.PRINTER_TYPE_NAMES[this.selectedPrinterType] + " - Printer");
        dialog.open();
        if (dialog.isCanceled()) {
            return;
        }
        Printer p = dialog.getPrinter();
        VirtualPrinterDAO printerDAO = VirtualPrinterDAO.getInstance();
        if (printerDAO.findPrinterByName(p.getVirtualPrinter().getName()) != null) {
            POSMessageDialog.showMessage(this, Messages.getString("VirtualPrinterConfigDialog.12"));
            return;
        }
        if (p.isDefaultPrinter()) {
            for (Printer printer : this.printers) {
                printer.setDefaultPrinter(false);
            }
        }
        VirtualPrinter virtualPrinter = p.getVirtualPrinter();
        for (Printer printer : this.printers) {
            if (!virtualPrinter.equals(printer.getVirtualPrinter())) continue;
            POSMessageDialog.showError(this.getParent(), Messages.getString("MultiPrinterPane.2"));
            return;
        }
        virtualPrinter.setType(this.selectedPrinterType);
        VirtualPrinterDAO.getInstance().saveOrUpdate(virtualPrinter);
        TerminalPrinters terminalPrinters = new TerminalPrinters();
        terminalPrinters.setTerminal(Application.getInstance().getTerminal());
        terminalPrinters.setPrinterName(p.getDeviceName());
        terminalPrinters.setVirtualPrinter(p.getVirtualPrinter());
        TerminalPrintersDAO.getInstance().saveOrUpdate(terminalPrinters);
        this.printers.add(p);
        this.listModel.addElement(p);
        this.refresh();
    }

    private void refresh() {
        this.printers.clear();
        List<VirtualPrinter> virtualPrinters = VirtualPrinterDAO.getInstance().findAll();
        if (virtualPrinters != null && !virtualPrinters.isEmpty()) {
            for (VirtualPrinter virtualPrinter : virtualPrinters) {
                Printer printer = new Printer();
                printer.setVirtualPrinter(virtualPrinter);
                printer.setDeviceName("");
                TerminalPrinters terminalPrinter = TerminalPrintersDAO.getInstance().findPrinters(printer.getVirtualPrinter());
                if (terminalPrinter != null && virtualPrinter.getName().equals(terminalPrinter.getVirtualPrinter().getName())) {
                    printer.setDeviceName(terminalPrinter.getPrinterName());
                }
                this.printers.add(printer);
            }
        }
        this.tableModel.removeAll();
        this.tableModel.addRows(this.printers);
        this.table.validate();
        this.table.repaint();
    }

    private void testPrinter() {
        int index = this.table.getSelectedRow();
        if (index < 0) {
            return;
        }
        Printer printer = this.tableModel.getRow(index = this.table.convertRowIndexToModel(index));
        if (printer.getDeviceName() == null) {
            PosLog.info(this.getClass(), "No print selected for " + printer.getType());
            return;
        }
        try {
            String title = "System Information";
            String data = printer.getDeviceName() + "-" + printer.getVirtualPrinter().getName();
            data = data + "\n Terminal : " + Application.getInstance().getTerminal().getName();
            data = data + "\n Current User : " + Application.getCurrentUser().getFirstName();
            data = data + "\n Floreant Version : " + Application.VERSION;
            data = data + "\n Database Name : " + AppConfig.getDatabaseName() + AppConfig.getDatabaseHost() + AppConfig.getDatabasePort();
            ReceiptPrintService.testPrinter(printer.getDeviceName(), title, data);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

