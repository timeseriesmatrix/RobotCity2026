/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.actions.ActionCommand;
import com.floreantpos.actions.CloseDialogAction;
import com.floreantpos.config.CardConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.dao.PosTransactionDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.TransactionListView;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.payment.AuthorizationDialog;
import com.floreantpos.ui.views.payment.CardProcessor;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;

public class AuthorizableTicketBrowser
extends POSDialog {
    private TransactionListView authClosedListView = new TransactionListView();
    private TransactionListView authWaitingListView = new TransactionListView();
    private JTabbedPane tabbedPane;

    public AuthorizableTicketBrowser(JDialog parent) {
        this.init();
    }

    public AuthorizableTicketBrowser(JFrame parent) {
        this.init();
    }

    private void init() {
        TitlePanel titlePanel = new TitlePanel();
        titlePanel.setTitle(Messages.getString("TicketAuthorizationDialog.0"));
        this.add((Component)titlePanel, "North");
        this.tabbedPane = new JTabbedPane();
        JPanel authWaitingTab = new JPanel(new BorderLayout());
        this.authWaitingListView.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.authClosedListView.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        authWaitingTab.add(this.authWaitingListView);
        JPanel buttonPanel = new JPanel((LayoutManager)new MigLayout("al center", "sg, fill", ""));
        ActionHandler actionHandler = new ActionHandler();
        buttonPanel.add((Component)new PosButton(ActionCommand.EDIT_TIPS, actionHandler), "grow");
        buttonPanel.add((Component)new PosButton(ActionCommand.AUTHORIZE, actionHandler), "grow");
        buttonPanel.add((Component)new PosButton(ActionCommand.AUTHORIZE_ALL, actionHandler), "grow");
        buttonPanel.add((Component)new PosButton(ActionCommand.VOID_TRANS, actionHandler), "grow");
        buttonPanel.add(new PosButton(new CloseDialogAction(this)));
        authWaitingTab.add((Component)buttonPanel, "South");
        JPanel authClosedTab = new JPanel(new BorderLayout());
        JPanel buttonPanel2 = new JPanel((LayoutManager)new MigLayout("al center", "sg, fill", ""));
        buttonPanel2.add((Component)new PosButton(ActionCommand.VOID_TRANS, actionHandler), "grow");
        buttonPanel2.add(new PosButton(new CloseDialogAction(this)));
        authClosedTab.add(this.authClosedListView);
        authClosedTab.add((Component)buttonPanel2, "South");
        this.tabbedPane.addTab("Auth Waiting", authWaitingTab);
        this.tabbedPane.addTab("Auth Closed", authClosedTab);
        this.add(this.tabbedPane);
        this.updateTransactiontList();
    }

    public void updateTransactiontList() {
        User owner = null;
        User currentUser = Application.getCurrentUser();
        if (!currentUser.hasPermission(UserPermission.VIEW_ALL_OPEN_TICKETS)) {
            owner = currentUser;
        }
        this.authWaitingListView.setTransactions(PosTransactionDAO.getInstance().findUnauthorizedTransactions(owner));
        this.authClosedListView.setTransactions(PosTransactionDAO.getInstance().findAuthorizedTransactions(owner));
    }

    private boolean confirmAuthorize(String message) {
        int option = JOptionPane.showConfirmDialog(this, message, Messages.getString("TicketAuthorizationDialog.1"), 2);
        return option == 0;
    }

    private void doAuthorize() {
        List<PosTransaction> transactions = this.authWaitingListView.getSelectedTransactions();
        if (transactions == null || transactions.size() == 0) {
            POSMessageDialog.showMessage(this, Messages.getString("TicketAuthorizationDialog.2"));
            return;
        }
        if (!this.confirmAuthorize(Messages.getString("TicketAuthorizationDialog.3"))) {
            return;
        }
        AuthorizationDialog authorizingDialog = new AuthorizationDialog(this, transactions);
        authorizingDialog.setVisible(true);
        this.updateTransactiontList();
    }

    public void doAuthorizeAll() {
        List<PosTransaction> transactions = this.authWaitingListView.getAllTransactions();
        if (transactions == null || transactions.size() == 0) {
            POSMessageDialog.showMessage(this, Messages.getString("TicketAuthorizationDialog.5"));
            return;
        }
        if (!this.confirmAuthorize(Messages.getString("TicketAuthorizationDialog.6"))) {
            return;
        }
        AuthorizationDialog authorizingDialog = new AuthorizationDialog(this, transactions);
        authorizingDialog.setVisible(true);
        this.updateTransactiontList();
    }

    private void doEditTips() {
        PosTransaction transaction = this.authWaitingListView.getFirstSelectedTransaction();
        if (transaction == null) {
            return;
        }
        Ticket ticket = TicketDAO.getInstance().loadFullTicket(transaction.getTicket().getId());
        Set<PosTransaction> transactions = ticket.getTransactions();
        for (PosTransaction posTransaction : transactions) {
            if (!transaction.getId().equals(posTransaction.getId())) continue;
            transaction = posTransaction;
            break;
        }
        double oldTipsAmount = transaction.getTipsAmount();
        double newTipsAmount = NumberSelectionDialog2.show(this, Messages.getString("TicketAuthorizationDialog.8"), oldTipsAmount);
        if (Double.isNaN(newTipsAmount)) {
            return;
        }
        transaction.setTipsAmount(newTipsAmount);
        transaction.setAmount(transaction.getAmount() - oldTipsAmount + newTipsAmount);
        if (ticket.hasGratuity()) {
            double ticketTipsAmount = ticket.getGratuity().getAmount();
            double ticketPaidAmount = ticket.getPaidAmount();
            double newTicketTipsAmount = ticketTipsAmount - oldTipsAmount + newTipsAmount;
            double newTicketPaidAmount = ticketPaidAmount - oldTipsAmount + newTipsAmount;
            ticket.setGratuityAmount(newTicketTipsAmount);
            ticket.setPaidAmount(newTicketPaidAmount);
        } else {
            ticket.setGratuityAmount(newTipsAmount);
            ticket.setPaidAmount(ticket.getPaidAmount() + newTipsAmount);
        }
        ticket.calculatePrice();
        TicketDAO.getInstance().saveOrUpdate(ticket);
        this.updateTransactiontList();
    }

    private void doVoidTransaction() {
        PosTransaction transaction = this.authWaitingListView.getSelectedTransaction();
        if (this.tabbedPane.getSelectedIndex() == 1) {
            transaction = this.authClosedListView.getSelectedTransaction();
        }
        if (transaction == null) {
            POSMessageDialog.showMessage(this, Messages.getString("TicketAuthorizationDialog.2"));
            return;
        }
        int option = POSMessageDialog.showYesNoQuestionDialog(this, "Selected transaction will be voided, proceed?", "Confirm");
        if (option != 0) {
            return;
        }
        CardProcessor cardProcessor = CardConfig.getPaymentGateway().getProcessor();
        try {
            cardProcessor.voidTransaction(transaction);
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
        this.updateTransactiontList();
    }

    class ActionHandler
    implements ActionListener {
        ActionHandler() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ActionCommand command = ActionCommand.valueOf(e.getActionCommand());
            try {
                switch (command) {
                    case EDIT_TIPS: {
                        AuthorizableTicketBrowser.this.doEditTips();
                        break;
                    }
                    case AUTHORIZE: {
                        AuthorizableTicketBrowser.this.doAuthorize();
                        break;
                    }
                    case AUTHORIZE_ALL: {
                        AuthorizableTicketBrowser.this.doAuthorizeAll();
                        break;
                    }
                    case VOID_TRANS: {
                        AuthorizableTicketBrowser.this.doVoidTransaction();
                        break;
                    }
                }
            }
            catch (Exception e2) {
                POSMessageDialog.showError(AuthorizableTicketBrowser.this, e2.getMessage(), e2);
            }
        }
    }
}

