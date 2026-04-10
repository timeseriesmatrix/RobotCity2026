/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.actions;

import com.floreantpos.ITicketList;
import com.floreantpos.Messages;
import com.floreantpos.PosException;
import com.floreantpos.PosLog;
import com.floreantpos.config.CardConfig;
import com.floreantpos.extension.PaymentGatewayPlugin;
import com.floreantpos.main.Application;
import com.floreantpos.model.CardReader;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PaymentType;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.services.PosTransactionService;
import com.floreantpos.swing.PosOptionPane;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.PaymentTypeSelectionDialog;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.ui.views.payment.AuthorizationCodeDialog;
import com.floreantpos.ui.views.payment.CardInputListener;
import com.floreantpos.ui.views.payment.CardInputProcessor;
import com.floreantpos.ui.views.payment.CardProcessor;
import com.floreantpos.ui.views.payment.ManualCardEntryDialog;
import com.floreantpos.ui.views.payment.PaymentProcessWaitDialog;
import com.floreantpos.ui.views.payment.SwipeCardDialog;
import com.floreantpos.util.CurrencyUtil;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.List;
import javax.swing.AbstractAction;
import org.apache.commons.lang.StringUtils;

public class NewBarTabAction
extends AbstractAction
implements CardInputListener {
    private Component parentComponent;
    private PaymentType selectedPaymentType;
    private OrderType orderType;
    private List<ShopTable> selectedTables;

    public NewBarTabAction(OrderType orderType, List selectedTables, Component parentComponent) {
        this.orderType = orderType;
        this.selectedTables = selectedTables;
        this.parentComponent = parentComponent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PaymentTypeSelectionDialog paymentTypeSelectionDialog = new PaymentTypeSelectionDialog();
        paymentTypeSelectionDialog.setCashButtonVisible(false);
        paymentTypeSelectionDialog.pack();
        paymentTypeSelectionDialog.setLocationRelativeTo(this.parentComponent);
        paymentTypeSelectionDialog.setVisible(true);
        if (paymentTypeSelectionDialog.isCanceled()) {
            return;
        }
        this.selectedPaymentType = paymentTypeSelectionDialog.getSelectedPaymentType();
        String symbol = CurrencyUtil.getCurrencySymbol();
        String message = symbol + CardConfig.getBartabLimit() + Messages.getString("NewBarTabAction.3");
        int option = POSMessageDialog.showYesNoQuestionDialog(this.parentComponent, message, Messages.getString("NewBarTabAction.4"));
        if (option != 0) {
            return;
        }
        SwipeCardDialog dialog = new SwipeCardDialog(this);
        dialog.setTitle(Messages.getString("NewBarTabAction.0"));
        dialog.pack();
        dialog.open();
    }

    private Ticket createTicket() {
        Ticket ticket = new Ticket();
        ticket.setBarTab(true);
        if (this.selectedTables != null && !this.selectedTables.isEmpty()) {
            for (ShopTable shopTable : this.selectedTables) {
                shopTable.setServing(true);
                ticket.addTable(shopTable.getTableNumber());
            }
        } else {
            String customerTabName = PosOptionPane.showInputDialog("Enter bar tab name");
            ticket.addProperty("CUSTOMER_NAME", customerTabName);
        }
        Application application = Application.getInstance();
        ticket.setPriceIncludesTax(application.isPriceIncludesTax());
        ticket.setOrderType(this.orderType);
        ticket.setTerminal(application.getTerminal());
        ticket.setOwner(Application.getCurrentUser());
        ticket.setShift(application.getCurrentShift());
        Calendar currentTime = Calendar.getInstance();
        ticket.setCreateDate(currentTime.getTime());
        ticket.setCreationHour(currentTime.get(11));
        return ticket;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void cardInputted(CardInputProcessor inputter, PaymentType paymentType) {
        PaymentProcessWaitDialog waitDialog = new PaymentProcessWaitDialog(Application.getPosWindow());
        try {
            waitDialog.setVisible(true);
            PosTransaction transaction = this.selectedPaymentType.createTransaction();
            Ticket ticket = this.createTicket();
            transaction.setTicket(ticket);
            transaction.setAuthorizable(false);
            transaction.setTenderAmount(CardConfig.getBartabLimit());
            PaymentGatewayPlugin paymentGateway = CardConfig.getPaymentGateway();
            CardProcessor cardProcessor = paymentGateway.getProcessor();
            if (inputter instanceof SwipeCardDialog) {
                SwipeCardDialog swipeCardDialog = (SwipeCardDialog)inputter;
                String cardString = swipeCardDialog.getCardString();
                if (StringUtils.isEmpty((String)cardString) || cardString.length() < 16) {
                    throw new RuntimeException(Messages.getString("SettleTicketDialog.16"));
                }
                transaction.setCardType(paymentType.getDisplayString());
                transaction.setCardTrack(cardString);
                transaction.setCaptured(false);
                transaction.setCardMerchantGateway(paymentGateway.getProductName());
                transaction.setCardReader(CardReader.SWIPE.name());
                if (ticket.getOrderType().isPreAuthCreditCard().booleanValue()) {
                    cardProcessor.preAuth(transaction);
                } else {
                    cardProcessor.chargeAmount(transaction);
                }
                this.saveTicket(transaction);
            } else if (inputter instanceof ManualCardEntryDialog) {
                ManualCardEntryDialog mDialog = (ManualCardEntryDialog)inputter;
                transaction.setCaptured(false);
                transaction.setCardMerchantGateway(paymentGateway.getProductName());
                transaction.setCardReader(CardReader.MANUAL.name());
                transaction.setCardNumber(mDialog.getCardNumber());
                transaction.setCardExpMonth(mDialog.getExpMonth());
                transaction.setCardExpYear(mDialog.getExpYear());
                cardProcessor.preAuth(transaction);
                this.saveTicket(transaction);
            } else if (inputter instanceof AuthorizationCodeDialog) {
                PosTransaction selectedTransaction = this.selectedPaymentType.createTransaction();
                selectedTransaction.setTicket(ticket);
                AuthorizationCodeDialog authDialog = (AuthorizationCodeDialog)inputter;
                String authorizationCode = authDialog.getAuthorizationCode();
                if (StringUtils.isEmpty((String)authorizationCode)) {
                    throw new PosException(Messages.getString("SettleTicketDialog.17"));
                }
                selectedTransaction.setCardType(this.selectedPaymentType.getDisplayString());
                selectedTransaction.setCaptured(false);
                selectedTransaction.setCardReader(CardReader.EXTERNAL_TERMINAL.name());
                selectedTransaction.setCardAuthCode(authorizationCode);
                this.saveTicket(selectedTransaction);
            }
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage());
        }
        finally {
            waitDialog.setVisible(false);
        }
    }

    private void saveTicket(PosTransaction transaction) {
        try {
            PosTransactionService transactionService = PosTransactionService.getInstance();
            transactionService.settleBarTabTicket(transaction.getTicket(), transaction, false);
            ShopTableDAO.getInstance().occupyTables(transaction.getTicket());
            POSMessageDialog.showMessage(Messages.getString("NewBarTabAction.5") + transaction.getTicket().getId());
            if (this.parentComponent instanceof ITicketList) {
                ((ITicketList)((Object)this.parentComponent)).updateTicketList();
            }
            this.doEditTicket(transaction.getTicket());
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
    }

    private void doEditTicket(Ticket ticket) {
        Ticket ticketToEdit = TicketDAO.getInstance().loadFullTicket(ticket.getId());
        OrderView.getInstance().setCurrentTicket(ticketToEdit);
        RootView.getInstance().showView("ORDER_VIEW");
        OrderView.getInstance().getTicketView().getTxtSearchItem().requestFocus();
    }
}

