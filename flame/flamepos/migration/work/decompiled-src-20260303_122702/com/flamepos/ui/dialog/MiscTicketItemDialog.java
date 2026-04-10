/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.PrinterGroup;
import com.floreantpos.model.Tax;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.dao.PrinterGroupDAO;
import com.floreantpos.model.dao.TaxDAO;
import com.floreantpos.swing.ComboBoxModel;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.PosComboRenderer;
import com.floreantpos.swing.QwertyKeyPad;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.Component;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

public class MiscTicketItemDialog
extends OkCancelOptionDialog {
    private TicketItem ticketItem;
    private JComboBox cbTax;
    private FixedLengthTextField tfItemName;
    private DoubleTextField tfItemPrice;
    private JComboBox cbPrinterGroup;
    private JLabel lblTax;

    public MiscTicketItemDialog() {
        super((Frame)Application.getPosWindow(), true);
        this.setTitle(Messages.getString("MiscTicketItemDialog.0"));
        this.initComponents();
    }

    private void initComponents() {
        JPanel contentPane = new JPanel((LayoutManager)new MigLayout("inset 0, fillx", "", ""));
        this.setTitle(Messages.getString("MiscTicketItemDialog.4"));
        this.setTitlePaneText(Messages.getString("MiscTicketItemDialog.4"));
        JLabel lblName = new JLabel(Messages.getString("MiscTicketItemDialog.6"));
        contentPane.add((Component)lblName, "newline,alignx trailing");
        this.tfItemName = new FixedLengthTextField();
        this.tfItemName.setLength(120);
        contentPane.add((Component)this.tfItemName, "grow, span, h 40");
        JLabel lblPrice = new JLabel(Messages.getString("MiscTicketItemDialog.9"));
        contentPane.add((Component)lblPrice, "newline,alignx trailing");
        this.tfItemPrice = new DoubleTextField();
        contentPane.add((Component)this.tfItemPrice, "grow, w 120, h 40");
        this.lblTax = new JLabel(Messages.getString("MiscTicketItemDialog.12"));
        contentPane.add((Component)this.lblTax, "alignx trailing");
        PosComboRenderer comboRenderer = new PosComboRenderer();
        comboRenderer.setEnableDefaultValueShowing(false);
        this.cbTax = new JComboBox();
        this.cbTax.setRenderer(comboRenderer);
        contentPane.add((Component)this.cbTax, "w 200!, h 40");
        contentPane.add((Component)new JLabel(Messages.getString("MiscTicketItemDialog.15")), "alignx trailing");
        this.cbPrinterGroup = new JComboBox();
        this.cbPrinterGroup.setRenderer(comboRenderer);
        contentPane.add((Component)this.cbPrinterGroup, "w 200!, h 40");
        QwertyKeyPad keyPad = new QwertyKeyPad();
        contentPane.add((Component)((Object)keyPad), "newline, grow, span, gaptop 10");
        this.getContentPanel().add(contentPane);
        this.initData();
    }

    private void initData() {
        List<Tax> taxes = TaxDAO.getInstance().findAll();
        this.cbTax.addItem("Select Tax");
        for (Tax tax : taxes) {
            this.cbTax.addItem(tax);
        }
        int defaultTaxId = TerminalConfig.getMiscItemDefaultTaxId();
        if (defaultTaxId != -1) {
            for (int i = 0; i < taxes.size(); ++i) {
                Tax tax = taxes.get(i);
                if (tax.getId() != defaultTaxId) continue;
                this.cbTax.setSelectedIndex(i);
                break;
            }
        }
        List<PrinterGroup> printerGroups = PrinterGroupDAO.getInstance().findAll();
        this.cbPrinterGroup.setModel(new ComboBoxModel(printerGroups));
    }

    @Override
    public void doCancel() {
        this.setCanceled(true);
        this.ticketItem = null;
        this.dispose();
    }

    @Override
    public void doOk() {
        PrinterGroup printerGroup;
        Tax tax;
        double amount = this.tfItemPrice.getDouble();
        String itemName = this.tfItemName.getText();
        if (StringUtils.isEmpty((String)itemName)) {
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("MiscTicketItemDialog.1"));
            return;
        }
        if (Double.isNaN(amount)) {
            amount = 0.0;
        }
        this.setCanceled(false);
        this.ticketItem = new TicketItem();
        this.ticketItem.setItemCount(1);
        this.ticketItem.setUnitPrice(amount);
        this.ticketItem.setName(itemName);
        this.ticketItem.setCategoryName(POSConstants.MISC_BUTTON_TEXT);
        this.ticketItem.setGroupName(POSConstants.MISC_BUTTON_TEXT);
        this.ticketItem.setShouldPrintToKitchen(true);
        Object selectedObject = this.cbTax.getSelectedItem();
        if (selectedObject instanceof Tax && (tax = (Tax)selectedObject) != null) {
            this.ticketItem.setTaxRate(tax.getRate());
            TerminalConfig.setMiscItemDefaultTaxId(tax.getId());
        }
        if ((printerGroup = (PrinterGroup)this.cbPrinterGroup.getSelectedItem()) != null) {
            this.ticketItem.setPrinterGroup(printerGroup);
        }
        this.dispose();
    }

    public TicketItem getTicketItem() {
        return this.ticketItem;
    }
}

