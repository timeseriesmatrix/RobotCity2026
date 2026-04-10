/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 */
package com.floreantpos.services;

import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.CashTransaction;
import com.floreantpos.model.GiftCertificateTransaction;
import com.floreantpos.model.Gratuity;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PaymentType;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.RefundTransaction;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TransactionType;
import com.floreantpos.model.User;
import com.floreantpos.model.VoidTransaction;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.GenericDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.report.ReceiptPrintService;
import com.floreantpos.util.NumberUtil;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PosTransactionService {
    private static PosTransactionService paymentService = new PosTransactionService();

    public void settleTicket(Ticket ticket, PosTransaction transaction) throws Exception {
        Application application = Application.getInstance();
        User currentUser = Application.getCurrentUser();
        Terminal terminal = application.refreshAndGetTerminal();
        Session session = null;
        Transaction tx = null;
        GenericDAO dao = new GenericDAO();
        try {
            Date currentDate = new Date();
            session = dao.createNewSession();
            tx = session.beginTransaction();
            ticket.setVoided(false);
            ticket.setDrawerResetted(false);
            ticket.setTerminal(terminal);
            ticket.setPaidAmount(ticket.getPaidAmount() + transaction.getAmount());
            ticket.calculatePrice();
            if (ticket.getDueAmount() == 0.0) {
                ticket.setPaid(true);
                this.closeTicketIfApplicable(ticket, currentDate);
            } else {
                ticket.setPaid(false);
                ticket.setClosed(false);
            }
            transaction.setTransactionType(TransactionType.CREDIT.name());
            transaction.setPaymentType(transaction.getPaymentType());
            transaction.setTerminal(terminal);
            transaction.setUser(currentUser);
            transaction.setTransactionTime(currentDate);
            ticket.addTotransactions(transaction);
            if (ticket.getOrderType().getName() == "BAR_TAB") {
                ticket.removeProperty("payment_method");
                ticket.removeProperty("card_name");
                ticket.removeProperty("card_transaction_id");
                ticket.removeProperty("card_tracks");
                ticket.removeProperty("card_reader");
                ticket.removeProperty("advance_payment");
                ticket.removeProperty("card_number");
                ticket.removeProperty("card_exp_year");
                ticket.removeProperty("card_exp_month");
                ticket.removeProperty("card_auth_code");
            }
            PosTransactionService.adjustTerminalBalance(transaction);
            session.update((Object)terminal);
            TicketDAO.getInstance().saveOrUpdate(ticket, session);
            tx.commit();
            dao.closeSession(session);
        }
        catch (Exception e) {
            try {
                try {
                    tx.rollback();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                throw e;
            }
            catch (Throwable throwable) {
                dao.closeSession(session);
                throw throwable;
            }
        }
        String actionMessage = POSConstants.RECEIPT_REPORT_TICKET_NO_LABEL + ":" + ticket.getId();
        actionMessage = actionMessage + ";" + POSConstants.TOTAL + ":" + NumberUtil.formatNumber(ticket.getTotalAmount());
        ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), ActionHistory.SETTLE_CHECK, actionMessage);
    }

    public void settleBarTabTicket(Ticket ticket, PosTransaction transaction, boolean closed) throws Exception {
        Application application = Application.getInstance();
        User currentUser = Application.getCurrentUser();
        Terminal terminal = application.refreshAndGetTerminal();
        Session session = null;
        Transaction tx = null;
        GenericDAO dao = new GenericDAO();
        try {
            Date currentDate = new Date();
            session = dao.createNewSession();
            tx = session.beginTransaction();
            ticket.setVoided(false);
            ticket.setDrawerResetted(false);
            ticket.setTerminal(terminal);
            ticket.setPaidAmount(ticket.getPaidAmount() + transaction.getAmount());
            ticket.calculatePrice();
            if (closed) {
                ticket.setPaid(true);
                this.closeTicketIfApplicable(ticket, currentDate);
            } else {
                ticket.setPaid(false);
                ticket.setClosed(false);
            }
            transaction.setTransactionType(TransactionType.CREDIT.name());
            transaction.setPaymentType(transaction.getPaymentType());
            transaction.setTerminal(terminal);
            transaction.setUser(currentUser);
            transaction.setTransactionTime(currentDate);
            ticket.addTotransactions(transaction);
            TicketDAO.getInstance().saveOrUpdate(ticket, session);
            tx.commit();
            dao.closeSession(session);
        }
        catch (Exception e) {
            try {
                e.printStackTrace();
                try {
                    tx.rollback();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                throw e;
            }
            catch (Throwable throwable) {
                dao.closeSession(session);
                throw throwable;
            }
        }
        String actionMessage = POSConstants.RECEIPT_REPORT_TICKET_NO_LABEL + ":" + ticket.getId();
        actionMessage = actionMessage + ";" + POSConstants.TOTAL + ":" + NumberUtil.formatNumber(ticket.getTotalAmount());
        ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), ActionHistory.SETTLE_CHECK, actionMessage);
    }

    public static void adjustTerminalBalance(PosTransaction transaction) {
        Terminal terminal = transaction.getTerminal();
        if (transaction instanceof CashTransaction) {
            double currentBalance = terminal.getCurrentBalance();
            double newBalance = currentBalance + transaction.getAmount();
            terminal.setCurrentBalance(newBalance);
        } else if (transaction instanceof GiftCertificateTransaction) {
            double currentBalance = terminal.getCurrentBalance();
            double newBalance = currentBalance - transaction.getGiftCertCashBackAmount();
            terminal.setCurrentBalance(newBalance);
        } else if (transaction instanceof VoidTransaction) {
            double currentBalance = terminal.getCurrentBalance();
            double newBalance = currentBalance - transaction.getAmount();
            terminal.setCurrentBalance(newBalance);
        }
    }

    private void closeTicketIfApplicable(Ticket ticket, Date currentDate) {
        OrderType ticketType = ticket.getOrderType();
        if (ticketType.isCloseOnPaid().booleanValue()) {
            ticket.setClosed(true);
            ticket.setClosingDate(currentDate);
        }
    }

    public void refundTicket(Ticket ticket, double refundAmount) throws Exception {
        User currentUser = Application.getCurrentUser();
        Terminal terminal = ticket.getTerminal();
        Session session = null;
        Transaction tx = null;
        GenericDAO dao = new GenericDAO();
        try {
            double diff;
            Double currentBalance = terminal.getCurrentBalance();
            double newBalance = currentBalance - refundAmount;
            terminal.setCurrentBalance(newBalance);
            Gratuity gratuity = ticket.getGratuity();
            if (gratuity != null && ((diff = ticket.getPaidAmount() - refundAmount) == 0.0 || diff > gratuity.getAmount())) {
                gratuity.setRefunded(true);
            }
            RefundTransaction posTransaction = new RefundTransaction();
            posTransaction.setTicket(ticket);
            posTransaction.setPaymentType(PaymentType.CASH.name());
            posTransaction.setTransactionType(TransactionType.DEBIT.name());
            posTransaction.setAmount(refundAmount);
            posTransaction.setTerminal(terminal);
            posTransaction.setUser(currentUser);
            posTransaction.setTransactionTime(new Date());
            ticket.setVoided(false);
            ticket.setRefunded(true);
            ticket.setClosed(true);
            ticket.setDrawerResetted(false);
            ticket.setClosingDate(new Date());
            ticket.addTotransactions(posTransaction);
            session = dao.createNewSession();
            tx = session.beginTransaction();
            dao.saveOrUpdate(ticket, session);
            dao.saveOrUpdate(terminal, session);
            tx.commit();
            ReceiptPrintService.printRefundTicket(ticket, posTransaction);
            dao.closeSession(session);
        }
        catch (Exception e) {
            try {
                try {
                    tx.rollback();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                throw e;
            }
            catch (Throwable throwable) {
                dao.closeSession(session);
                throw throwable;
            }
        }
    }

    public static PosTransactionService getInstance() {
        return paymentService;
    }
}

