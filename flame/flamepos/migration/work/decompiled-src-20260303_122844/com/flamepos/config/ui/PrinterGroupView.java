/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.AppConfig;
import com.floreantpos.config.ui.AddPrinterGroupDialog;
import com.floreantpos.main.Application;
import com.floreantpos.model.PrinterGroup;
import com.floreantpos.model.TerminalPrinters;
import com.floreantpos.model.dao.PrinterGroupDAO;
import com.floreantpos.model.dao.TerminalPrintersDAO;
import com.floreantpos.report.ReceiptPrintService;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PrinterGroupView
extends JPanel {
    private JList<PrinterGroup> list;
    private DefaultListModel<PrinterGroup> listModel;

    public PrinterGroupView() {
    }

    public PrinterGroupView(String title) {
        this.setBorder(BorderFactory.createTitledBorder(title));
        this.setLayout(new BorderLayout(10, 10));
        JPanel panel = new JPanel();
        this.add((Component)panel, "South");
        JButton btnAdd = new JButton(POSConstants.ADD.toUpperCase());
        btnAdd.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PrinterGroupView.this.doAddPrinter();
            }
        });
        panel.add(btnAdd);
        JButton btnEdit = new JButton(POSConstants.EDIT.toUpperCase());
        btnEdit.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PrinterGroupView.this.doEditPrinter();
            }
        });
        panel.add(btnEdit);
        JButton btnDelete = new JButton(POSConstants.DELETE.toUpperCase());
        btnDelete.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PrinterGroupView.this.doDeletePrinterGroup();
            }
        });
        panel.add(btnDelete);
        JButton btnTest = new JButton("TEST");
        btnTest.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PrinterGroupView.this.testPrinter();
            }
        });
        panel.add(btnTest);
        JScrollPane scrollPane = new JScrollPane();
        this.add((Component)scrollPane, "Center");
        this.listModel = new DefaultListModel();
        List<PrinterGroup> all = PrinterGroupDAO.getInstance().findAll();
        for (PrinterGroup printerGroup : all) {
            this.listModel.addElement(printerGroup);
        }
        this.list = new JList<PrinterGroup>(this.listModel);
        scrollPane.setViewportView(this.list);
    }

    private void doDeletePrinterGroup() {
        PrinterGroup pGroup = this.list.getSelectedValue();
        if (pGroup == null) {
            return;
        }
        try {
            PrinterGroupDAO.getInstance().delete(pGroup.getId());
            this.listModel.removeElement(pGroup);
        }
        catch (Exception e) {
            POSMessageDialog.showError(Messages.getString("PrinterGroupView.0"));
        }
        this.refresh();
    }

    protected void doEditPrinter() {
        PrinterGroup pGroup = this.list.getSelectedValue();
        if (pGroup == null) {
            return;
        }
        AddPrinterGroupDialog dialog = new AddPrinterGroupDialog();
        dialog.setPrinterGroup(pGroup);
        dialog.open();
        if (dialog.isCanceled()) {
            return;
        }
        PrinterGroup printerGroup = dialog.getPrinterGroup();
        PrinterGroupDAO.getInstance().saveOrUpdate(printerGroup);
        this.refresh();
    }

    protected void doAddPrinter() {
        AddPrinterGroupDialog dialog = new AddPrinterGroupDialog();
        dialog.open();
        if (dialog.isCanceled()) {
            return;
        }
        PrinterGroup printerGroup = dialog.getPrinterGroup();
        PrinterGroupDAO.getInstance().saveOrUpdate(printerGroup);
        this.listModel.addElement(printerGroup);
        this.refresh();
    }

    private void refresh() {
        this.listModel.clear();
        List<PrinterGroup> all = PrinterGroupDAO.getInstance().findAll();
        for (PrinterGroup printersG : all) {
            this.listModel.addElement(printersG);
        }
    }

    private void testPrinter() {
        PrinterGroup printerGroup = this.list.getSelectedValue();
        if (printerGroup == null) {
            return;
        }
        List<String> printerList = printerGroup.getPrinterNames();
        List<TerminalPrinters> terminalPrinters = TerminalPrintersDAO.getInstance().findTerminalPrinters();
        for (TerminalPrinters terminalPrinter : terminalPrinters) {
            if (!printerList.contains(terminalPrinter.getVirtualPrinter().getName())) continue;
            try {
                String title = "System Information";
                String data = terminalPrinter.getPrinterName() + "-" + terminalPrinter.getVirtualPrinter().getName();
                data = data + "\n Terminal : " + Application.getInstance().getTerminal().getName();
                data = data + "\n Current User : " + Application.getCurrentUser().getFirstName();
                data = data + "\n Floreant Version : " + Application.VERSION;
                data = data + "\n Database Name : " + AppConfig.getDatabaseName() + AppConfig.getDatabaseHost() + AppConfig.getDatabasePort();
                ReceiptPrintService.testPrinter(terminalPrinter.getPrinterName(), title, data);
            }
            catch (Exception exception) {}
        }
    }
}

