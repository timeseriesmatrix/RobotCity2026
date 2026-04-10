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
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketDiscount;
import com.floreantpos.model.dao.CashDrawerDAO;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.NumericKeypad;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.CurrencyUtil;
import com.floreantpos.util.NumberUtil;
import com.floreantpos.util.POSUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;

public class MultiCurrencyTenderDialog
extends OkCancelOptionDialog {
    private List<Currency> currencyList;
    private double dueAmount;
    private double totalTenderedAmount;
    private double totalCashBackAmount;
    private List<CurrencyRow> currencyRows = new ArrayList<CurrencyRow>();
    private CashDrawer cashDrawer;
    private Ticket ticket;

    public MultiCurrencyTenderDialog(Ticket ticket, List<Currency> currencyList) {
        this.ticket = ticket;
        this.currencyList = currencyList;
        this.dueAmount = ticket.getDueAmount();
        this.init();
    }

    public MultiCurrencyTenderDialog(List<Ticket> tickets, List<Currency> currencyList) {
        this.currencyList = currencyList;
        this.ticket = tickets.get(tickets.size() - 1);
        this.dueAmount = 0.0;
        for (Ticket ticket : tickets) {
            this.dueAmount += ticket.getDueAmount().doubleValue();
        }
        this.init();
    }

    private void init() {
        JPanel contentPane = this.getContentPanel();
        this.setOkButtonText(POSConstants.SAVE_BUTTON_TEXT);
        this.setTitle("Enter tender amount");
        this.setTitlePaneText("Due amount: " + CurrencyUtil.getCurrencySymbol() + this.dueAmount);
        this.setResizable(true);
        MigLayout layout = new MigLayout("inset 0", "[grow,fill]", "[grow,fill]");
        contentPane.setLayout((LayoutManager)layout);
        JPanel inputPanel = new JPanel((LayoutManager)new MigLayout("fill,ins 0", "[fill][right]30px[120px,grow,fill,right][120px,grow,fill,right][][]", ""));
        inputPanel.add(this.getJLabel("Currency", 1, 16, 10));
        inputPanel.add((Component)this.getJLabel("Remaining", 1, 16, 0), "gapleft 20");
        inputPanel.add((Component)this.getJLabel("Tender", 1, 16, 11), "center");
        inputPanel.add((Component)this.getJLabel("Cash Back", 1, 16, 11), "center");
        for (Currency currency : this.currencyList) {
            CurrencyRow item = new CurrencyRow(currency, this.dueAmount);
            inputPanel.add((Component)item.currencyName, "newline");
            inputPanel.add(item.lblRemainingBalance);
            inputPanel.add((Component)item.tfTenderdAmount, "grow");
            inputPanel.add((Component)item.tfCashBackAmount, "grow");
            this.currencyRows.add(item);
        }
        contentPane.add((Component)inputPanel, "cell 0 2,alignx left,aligny top");
        NumericKeypad numericKeypad = new NumericKeypad();
        contentPane.add((Component)new JSeparator(), "newline, gapbottom 5,gaptop 10");
        contentPane.add((Component)numericKeypad, "newline");
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
        this.updateView();
        if (!this.isValidAmount()) {
            POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), "Invalid Amount");
            return;
        }
        Terminal terminal = Application.getInstance().getTerminal();
        this.cashDrawer = CashDrawerDAO.getInstance().findByTerminal(terminal);
        if (this.cashDrawer == null) {
            this.cashDrawer = new CashDrawer();
            this.cashDrawer.setTerminal(terminal);
            if (this.cashDrawer.getCurrencyBalanceList() == null) {
                this.cashDrawer.setCurrencyBalanceList(new HashSet<CurrencyBalance>());
            }
        }
        for (CurrencyRow rowItem : this.currencyRows) {
            CurrencyBalance item = this.cashDrawer.getCurrencyBalance(rowItem.currency);
            if (item == null) {
                item = new CurrencyBalance();
                item.setCurrency(rowItem.currency);
                item.setCashDrawer(this.cashDrawer);
                this.cashDrawer.addTocurrencyBalanceList(item);
            }
            double tenderAmount = rowItem.getTenderAmount();
            double cashBackAmount = rowItem.getCashBackAmount();
            if (tenderAmount > 0.0) {
                this.ticket.addProperty(rowItem.currency.getName(), String.valueOf(tenderAmount));
            }
            if (cashBackAmount > 0.0) {
                this.ticket.addProperty(rowItem.currency.getName() + "_CASH_BACK", String.valueOf(cashBackAmount));
            }
            item.setBalance(NumberUtil.roundToThreeDigit(item.getBalance() + tenderAmount - cashBackAmount));
        }
        this.setCanceled(false);
        this.dispose();
    }

    private boolean isValidAmount() {
        double remainingBalance = this.dueAmount - (this.totalTenderedAmount - this.totalCashBackAmount);
        if (this.totalTenderedAmount <= 0.0 || remainingBalance < 0.0) {
            return false;
        }
        double toleranceAmount = CurrencyUtil.getMainCurrency().getTolerance();
        if (remainingBalance > toleranceAmount) {
            return true;
        }
        return this.isTolerable();
    }

    private boolean isTolerable() {
        double diff = this.dueAmount - (this.totalTenderedAmount - this.totalCashBackAmount);
        double tolerance = CurrencyUtil.getMainCurrency().getTolerance();
        if (diff <= tolerance) {
            if (diff > 0.0) {
                this.doAddToleranceToTicketDiscount(diff);
            }
            return true;
        }
        return false;
    }

    private void doAddToleranceToTicketDiscount(double discountAmount) {
        TicketDiscount ticketDiscount = new TicketDiscount();
        ticketDiscount.setName("Tolerance");
        ticketDiscount.setValue(NumberUtil.roundToThreeDigit(discountAmount));
        ticketDiscount.setAutoApply(true);
        ticketDiscount.setTicket(this.ticket);
        this.ticket.addTodiscounts(ticketDiscount);
        this.ticket.calculatePrice();
    }

    public CashDrawer getCashDrawer() {
        return this.cashDrawer;
    }

    private void updateView() {
        this.totalTenderedAmount = 0.0;
        this.totalCashBackAmount = 0.0;
        for (CurrencyRow rowItem : this.currencyRows) {
            double tenderAmount = rowItem.getTenderAmount();
            double cashBackAmount = rowItem.getCashBackAmount();
            this.totalTenderedAmount += tenderAmount / rowItem.currency.getExchangeRate();
            this.totalCashBackAmount += cashBackAmount / rowItem.currency.getExchangeRate();
        }
        for (CurrencyRow currInput : this.currencyRows) {
            double remainingBalance = (this.dueAmount - (this.totalTenderedAmount - this.totalCashBackAmount)) * currInput.currency.getExchangeRate();
            currInput.setRemainingBalance(remainingBalance);
        }
    }

    public double getTenderedAmount() {
        return this.totalTenderedAmount;
    }

    public double getChangeDueAmount() {
        return this.totalTenderedAmount - this.dueAmount;
    }

    class CurrencyRow
    implements ActionListener,
    FocusListener {
        Currency currency;
        DoubleTextField tfTenderdAmount;
        DoubleTextField tfCashBackAmount;
        double remainingBalance;
        JLabel lblRemainingBalance;
        JLabel currencyName;
        PosButton btnExact = new PosButton("EXACT");
        PosButton btnRound = new PosButton("ROUND");

        public CurrencyRow(Currency currency, double dueAmountInMainCurrency) {
            this.currency = currency;
            this.remainingBalance = currency.getExchangeRate() * dueAmountInMainCurrency;
            this.lblRemainingBalance = MultiCurrencyTenderDialog.this.getJLabel(NumberUtil.format3DigitNumber(this.remainingBalance), 0, 16, 4);
            this.currencyName = MultiCurrencyTenderDialog.this.getJLabel(currency.getName(), 0, 16, 10);
            this.tfTenderdAmount = MultiCurrencyTenderDialog.this.getDoubleTextField("", 0, 16, 4);
            this.tfCashBackAmount = MultiCurrencyTenderDialog.this.getDoubleTextField("", 0, 16, 4);
            this.tfTenderdAmount.addFocusListener(this);
            this.tfCashBackAmount.addFocusListener(this);
            this.btnExact.addActionListener(this);
            this.btnRound.addActionListener(this);
        }

        double getTenderAmount() {
            double tenderAmount = this.tfTenderdAmount.getDouble();
            if (Double.isNaN(tenderAmount)) {
                return 0.0;
            }
            return tenderAmount;
        }

        double getCashBackAmount() {
            double cashBackAmount = this.tfCashBackAmount.getDouble();
            if (Double.isNaN(cashBackAmount)) {
                return 0.0;
            }
            return cashBackAmount;
        }

        void setRemainingBalance(double remainingBalance) {
            this.remainingBalance = remainingBalance;
            this.lblRemainingBalance.setText(NumberUtil.format3DigitNumber(remainingBalance));
        }

        double getRemainingBalance() {
            return this.remainingBalance;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == this.btnExact) {
                this.tfTenderdAmount.setText(String.valueOf(NumberUtil.roundToThreeDigit(this.remainingBalance)));
            } else {
                this.tfTenderdAmount.setText(String.valueOf(NumberUtil.roundToThreeDigit(Math.ceil(this.remainingBalance))));
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
        }

        @Override
        public void focusLost(FocusEvent e) {
            MultiCurrencyTenderDialog.this.updateView();
        }
    }
}

