/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.PayOutTransaction;
import com.floreantpos.model.PaymentType;
import com.floreantpos.model.PayoutReason;
import com.floreantpos.model.PayoutRecepient;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.TransactionType;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.PayOutTransactionDAO;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.PayOutView;
import com.floreantpos.util.NumberUtil;
import java.util.Date;

public class PayoutDialog
extends OkCancelOptionDialog {
    private PayOutView payOutView;

    public PayoutDialog() {
        this.setTitle(Application.getTitle() + POSConstants.PAYOUT_BUTTON_TEXT);
        this.initComponents();
        this.payOutView.initialize();
    }

    private void initComponents() {
        this.setTitlePaneText(POSConstants.PAYOUT_BUTTON_TEXT);
        this.setOkButtonText(POSConstants.FINISH);
        this.payOutView = new PayOutView();
        this.setDefaultCloseOperation(2);
        this.getContentPanel().add(this.payOutView);
        this.pack();
    }

    @Override
    public void doOk() {
        Application application = Application.getInstance();
        Terminal terminal = application.refreshAndGetTerminal();
        double payoutAmount = this.payOutView.getPayoutAmount();
        PayoutReason reason = this.payOutView.getReason();
        PayoutRecepient recepient = this.payOutView.getRecepient();
        String note = this.payOutView.getNote();
        terminal.setCurrentBalance(terminal.getCurrentBalance() - payoutAmount);
        PayOutTransaction payOutTransaction = new PayOutTransaction();
        payOutTransaction.setPaymentType(PaymentType.CASH.name());
        payOutTransaction.setTransactionType(TransactionType.DEBIT.name());
        payOutTransaction.setReason(reason);
        payOutTransaction.setRecepient(recepient);
        payOutTransaction.setNote(note);
        payOutTransaction.setAmount(payoutAmount);
        payOutTransaction.setUser(Application.getCurrentUser());
        payOutTransaction.setTransactionTime(new Date());
        payOutTransaction.setTerminal(terminal);
        try {
            PayOutTransactionDAO dao = new PayOutTransactionDAO();
            dao.saveTransaction(payOutTransaction, terminal);
            this.setCanceled(false);
            String actionMessage = "";
            actionMessage = actionMessage + Messages.getString("PayoutDialog.2") + ":" + NumberUtil.formatNumber(payoutAmount);
            ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), ActionHistory.PAY_OUT, actionMessage);
            this.dispose();
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }
}

