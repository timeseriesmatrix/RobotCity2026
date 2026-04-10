/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.CashDrawer;
import com.floreantpos.model.Currency;
import com.floreantpos.model.CurrencyBalance;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.dao.CashDrawerDAO;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.NumericKeypad;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Window;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;

public class MultiCurrencyAssignDrawerDialog
extends OkCancelOptionDialog {
    private List<Currency> currencyList;
    private double initialAmount;
    private double totalAmount;
    private List<CurrencyRow> currencyRows = new ArrayList<CurrencyRow>();
    private CashDrawer cashDrawer;

    public MultiCurrencyAssignDrawerDialog(double initialAmount, List<Currency> currencyList) {
        super((Window)Application.getPosWindow());
        this.initialAmount = initialAmount;
        this.currencyList = currencyList;
        this.init();
    }

    private void init() {
        JPanel contentPane = this.getContentPanel();
        this.setOkButtonText(POSConstants.SAVE_BUTTON_TEXT);
        this.setTitle("Enter drawer amount");
        this.setTitlePaneText("Enter drawer amount");
        this.setResizable(false);
        MigLayout layout = new MigLayout("inset 0", "[grow,fill]", "[grow,fill]");
        contentPane.setLayout((LayoutManager)layout);
        JPanel inputPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(0, 2, 10, 5);
        inputPanel.setLayout(gridLayout);
        JLabel lblCurrency = this.getJLabel("Currency", 1, 16, 0);
        JLabel lblAmount = this.getJLabel("Amount", 1, 16, 0);
        inputPanel.add(lblCurrency);
        inputPanel.add(lblAmount);
        for (Currency currency : this.currencyList) {
            JLabel currencyName = this.getJLabel(currency.getName(), 0, 16, 0);
            DoubleTextField tfTenderedAmount = this.getDoubleTextField("", 0, 16, 4);
            inputPanel.add(currencyName);
            inputPanel.add(tfTenderedAmount);
            CurrencyRow item = new CurrencyRow(currency, tfTenderedAmount);
            this.currencyRows.add(item);
        }
        contentPane.add((Component)inputPanel, "cell 0 0,alignx left,aligny top");
        NumericKeypad numericKeypad = new NumericKeypad();
        contentPane.add((Component)new JSeparator(), "gapbottom 5,gaptop 5,cell 0 1");
        contentPane.add((Component)numericKeypad, "cell 0 2");
    }

    private JLabel getJLabel(String text, int bold, int fontSize, int align) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(bold, PosUIManager.getSize(fontSize)));
        lbl.setHorizontalAlignment(align);
        return lbl;
    }

    private DoubleTextField getDoubleTextField(String text, int bold, int fontSize, int align) {
        DoubleTextField tf = new DoubleTextField();
        tf.setText(text);
        tf.setFont(tf.getFont().deriveFont(bold, PosUIManager.getSize(fontSize)));
        tf.setHorizontalAlignment(align);
        tf.setBackground(Color.WHITE);
        return tf;
    }

    @Override
    public void doOk() {
        Terminal terminal = Application.getInstance().getTerminal();
        this.cashDrawer = CashDrawerDAO.getInstance().findByTerminal(terminal);
        if (this.cashDrawer == null) {
            this.cashDrawer = new CashDrawer();
            this.cashDrawer.setTerminal(terminal);
            if (this.cashDrawer.getCurrencyBalanceList() == null) {
                this.cashDrawer.setCurrencyBalanceList(new HashSet<CurrencyBalance>());
            }
        }
        this.totalAmount = 0.0;
        for (CurrencyRow rowItem : this.currencyRows) {
            double amount;
            CurrencyBalance item = this.cashDrawer.getCurrencyBalance(rowItem.currency);
            if (item == null) {
                item = new CurrencyBalance();
                item.setCurrency(rowItem.currency);
                item.setCashDrawer(this.cashDrawer);
                this.cashDrawer.addTocurrencyBalanceList(item);
            }
            if (Double.isNaN(amount = rowItem.tfAmount.getDouble())) {
                amount = 0.0;
            }
            item.setBalance(amount);
            this.totalAmount += amount / rowItem.currency.getExchangeRate();
        }
        this.setCanceled(false);
        this.dispose();
    }

    public CashDrawer getCashDrawer() {
        return this.cashDrawer;
    }

    public double getTotalAmount() {
        return this.totalAmount;
    }

    private class CurrencyRow {
        Currency currency;
        DoubleTextField tfAmount;
        double initialAmount = 0.0;

        public CurrencyRow(Currency currency, DoubleTextField tfAmount) {
            this.currency = currency;
            this.tfAmount = tfAmount;
        }

        void setInitialAmount(double initialAmount) {
            this.initialAmount = initialAmount;
        }
    }
}

