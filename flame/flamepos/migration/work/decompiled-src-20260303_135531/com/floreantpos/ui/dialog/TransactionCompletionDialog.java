/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.report.ReceiptPrintService;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.NumberUtil;
import java.awt.Component;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;

public class TransactionCompletionDialog
extends POSDialog {
    private double tenderedAmount;
    private double totalAmount;
    private double paidAmount;
    private double dueAmount;
    private double gratuityAmount;
    private double changeAmount;
    private JLabel lblTenderedAmount;
    private JLabel lblTotalAmount;
    private JLabel lblPaidAmount;
    private JLabel lblDueAmount;
    private JLabel lblChangeDue;
    private JLabel lblGratuityAmount;
    private PosTransaction completedTransaction;
    private List<PosTransaction> completedTransactions;
    private boolean isCard;

    public TransactionCompletionDialog(PosTransaction transaction) {
        this.completedTransaction = transaction;
        this.isCard = this.completedTransaction.isCard();
        this.initializeComponents();
    }

    public TransactionCompletionDialog(List<PosTransaction> transaction) {
        this.completedTransactions = transaction;
        this.initializeComponents();
    }

    private void initializeComponents() {
        this.setTitle(POSConstants.TRANSACTION_COMPLETED);
        this.setLayout((LayoutManager)new MigLayout("align 50% 0%, ins 20", "[]20[]", ""));
        this.add((Component)this.createLabel(Messages.getString("TransactionCompletionDialog.3") + ":", 2), "grow");
        this.lblTotalAmount = this.createLabel("0.0", 4);
        this.add((Component)this.lblTotalAmount, "span, grow");
        this.add((Component)this.createLabel(Messages.getString("TransactionCompletionDialog.8") + ":", 2), "newline,grow");
        this.lblTenderedAmount = this.createLabel("0.0", 4);
        this.add((Component)this.lblTenderedAmount, "span, grow");
        this.add((Component)new JSeparator(), "newline,span, grow");
        this.add((Component)this.createLabel(Messages.getString("TransactionCompletionDialog.14") + ":", 2), "newline,grow");
        this.lblPaidAmount = this.createLabel("0.0", 4);
        this.add((Component)this.lblPaidAmount, "span, grow");
        this.add((Component)this.createLabel(Messages.getString("TransactionCompletionDialog.19") + ":", 2), "newline,grow");
        this.lblDueAmount = this.createLabel("0.0", 4);
        this.add((Component)this.lblDueAmount, "span, grow");
        this.add((Component)new JSeparator(), "newline,span, grow");
        this.add((Component)this.createLabel(Messages.getString("TransactionCompletionDialog.25") + ":", 2), "newline,grow");
        this.lblGratuityAmount = this.createLabel("0.0", 4);
        this.add((Component)this.lblGratuityAmount, "span, grow");
        this.add((Component)new JSeparator(), "newline,span, grow");
        this.add((Component)this.createLabel(Messages.getString("TransactionCompletionDialog.31") + ":", 2), "grow");
        this.lblChangeDue = this.createLabel("0.0", 4);
        this.add((Component)this.lblChangeDue, "span, grow");
        this.add((Component)new JSeparator(), "sg mygroup,newline,span,grow");
        PosButton btnClose = new PosButton(Messages.getString("TransactionCompletionDialog.37"));
        btnClose.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                TransactionCompletionDialog.this.dispose();
            }
        });
        PosButton btnPrintStoreCopy = new PosButton(Messages.getString("TransactionCompletionDialog.38"));
        btnPrintStoreCopy.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ReceiptPrintService.printTransaction(TransactionCompletionDialog.this.completedTransaction, false);
                }
                catch (Exception ee) {
                    POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TransactionCompletionDialog.39"), ee);
                }
                TransactionCompletionDialog.this.dispose();
            }
        });
        PosButton btnPrintAllCopy = new PosButton(Messages.getString("TransactionCompletionDialog.40"));
        btnPrintAllCopy.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ReceiptPrintService.printTransaction(TransactionCompletionDialog.this.completedTransaction, true);
                }
                catch (Exception ee) {
                    POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TransactionCompletionDialog.41"), ee);
                }
                TransactionCompletionDialog.this.dispose();
            }
        });
        JPanel p = new JPanel();
        if (this.isCard) {
            p.add((Component)btnPrintAllCopy, "newline,skip, h 50");
            p.add((Component)btnPrintStoreCopy, "skip, h 50");
            p.add((Component)btnClose, "skip, h 50");
        } else {
            btnPrintStoreCopy.setText(Messages.getString("TransactionCompletionDialog.0"));
            p.add((Component)btnPrintStoreCopy, "skip, h 50");
            p.add((Component)btnClose, "skip, h 50");
        }
        this.add((Component)p, "newline, span 2, grow, gaptop 15px");
    }

    protected JLabel createLabel(String text, int alignment) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Tahoma", 1, 24));
        label.setHorizontalAlignment(alignment);
        label.setText(text);
        return label;
    }

    public double getTenderedAmount() {
        return this.tenderedAmount;
    }

    public void setTenderedAmount(double amountTendered) {
        this.tenderedAmount = amountTendered;
    }

    public void updateView() {
        this.lblTotalAmount.setText(NumberUtil.formatNumber(this.totalAmount));
        this.lblTenderedAmount.setText(NumberUtil.formatNumber(this.tenderedAmount));
        this.lblPaidAmount.setText(NumberUtil.formatNumber(this.paidAmount));
        this.lblDueAmount.setText(NumberUtil.formatNumber(this.dueAmount));
        this.lblGratuityAmount.setText(NumberUtil.formatNumber(this.gratuityAmount));
        this.lblChangeDue.setText(NumberUtil.formatNumber(this.changeAmount));
    }

    public double getDueAmount() {
        return this.dueAmount;
    }

    public void setDueAmount(double dueAmount) {
        this.dueAmount = dueAmount;
    }

    public double getPaidAmount() {
        return this.paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getGratuityAmount() {
        return this.gratuityAmount;
    }

    public void setGratuityAmount(double gratuityAmount) {
        this.gratuityAmount = gratuityAmount;
    }

    public double getChangeAmount() {
        return this.changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public void setCompletedTransaction(PosTransaction completedTransaction) {
        this.completedTransaction = completedTransaction;
    }

    public boolean isCard() {
        return this.isCard;
    }

    public void setCard(boolean isCard) {
        this.isCard = isCard;
    }
}

