/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.main.Application;
import com.floreantpos.model.PosPrinters;
import com.floreantpos.model.Printer;
import com.floreantpos.model.PrinterGroup;
import com.floreantpos.swing.CheckBoxList;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

public class AddPrinterGroupDialog
extends POSDialog {
    private FixedLengthTextField tfName = new FixedLengthTextField(60);
    private CheckBoxList<Printer> printerList;
    private JCheckBox chkDefault;
    private PrinterGroup printerGroup;
    private List<Printer> printers;

    public AddPrinterGroupDialog() throws HeadlessException {
        super((Frame)POSUtil.getBackOfficeWindow(), true);
        this.setTitle(Messages.getString("AddPrinterGroupDialog.0"));
        this.init();
        this.setDefaultCloseOperation(2);
        this.pack();
    }

    private void init() {
        JPanel contentPane = (JPanel)this.getContentPane();
        contentPane.setLayout((LayoutManager)new MigLayout("", "[][grow]", ""));
        this.add(new JLabel(Messages.getString("AddPrinterGroupDialog.4")));
        this.add((Component)this.tfName, "grow, wrap");
        this.chkDefault = new JCheckBox(Messages.getString("AddPrinterGroupDialog.1"));
        this.add((Component)new JLabel(), "grow");
        this.add((Component)this.chkDefault, "wrap");
        PosPrinters printersKitchen = PosPrinters.load();
        this.printers = printersKitchen.getKitchenPrinters();
        this.printerList = new CheckBoxList<Printer>(new Vector<Printer>(this.printers));
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(new TitledBorder(Messages.getString("AddPrinterGroupDialog.6")));
        listPanel.add(new JScrollPane(this.printerList));
        this.add((Component)listPanel, "newline, span 2, grow");
        JPanel panel = new JPanel();
        contentPane.add((Component)panel, "cell 0 4 3 1,grow");
        JButton btnOk = new JButton(Messages.getString("AddPrinterGroupDialog.9"));
        btnOk.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (StringUtils.isEmpty((String)AddPrinterGroupDialog.this.tfName.getText())) {
                    POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("AddPrinterGroupDialog.10"));
                    return;
                }
                List checkedValues = AddPrinterGroupDialog.this.printerList.getCheckedValues();
                if (checkedValues == null || checkedValues.size() == 0) {
                    POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("AddPrinterGroupDialog.11"));
                    return;
                }
                AddPrinterGroupDialog.this.setCanceled(false);
                AddPrinterGroupDialog.this.dispose();
            }
        });
        panel.add(btnOk);
        JButton btnCancel = new JButton(Messages.getString("AddPrinterGroupDialog.12"));
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                AddPrinterGroupDialog.this.setCanceled(true);
                AddPrinterGroupDialog.this.dispose();
            }
        });
        panel.add(btnCancel);
    }

    public PrinterGroup getPrinterGroup() {
        if (this.printerGroup == null) {
            this.printerGroup = new PrinterGroup();
        }
        this.printerGroup.setIsDefault(this.chkDefault.isSelected());
        this.printerGroup.setName(this.tfName.getText());
        List<Printer> checkedValues = this.printerList.getCheckedValues();
        if (checkedValues != null) {
            ArrayList<String> names = new ArrayList<String>();
            Iterator<Printer> i$ = checkedValues.iterator();
            while (i$.hasNext()) {
                Printer object;
                Printer p = object = i$.next();
                names.add(p.getVirtualPrinter().getName());
            }
            this.printerGroup.setPrinterNames(names);
        }
        return this.printerGroup;
    }

    public void setPrinterGroup(PrinterGroup group) {
        this.printerGroup = group;
        this.tfName.setText(group.getName());
        this.chkDefault.setSelected(group.isIsDefault());
        Vector<Printer> selectedPrinters = new Vector<Printer>();
        for (Printer printer : this.printers) {
            if (!this.printerGroup.getPrinterNames().contains(printer.getVirtualPrinter().getName())) continue;
            selectedPrinters.add(printer);
        }
        this.printerList.selectItems(selectedPrinters);
    }
}

