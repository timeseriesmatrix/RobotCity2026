/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Criteria
 *  org.hibernate.Session
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Restrictions
 */
package com.floreantpos.print;

import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.CashDrawer;
import com.floreantpos.model.CashDropTransaction;
import com.floreantpos.model.CashTransaction;
import com.floreantpos.model.CreditCardTransaction;
import com.floreantpos.model.CurrencyBalance;
import com.floreantpos.model.DebitCardTransaction;
import com.floreantpos.model.DrawerPullReport;
import com.floreantpos.model.GiftCertificateTransaction;
import com.floreantpos.model.Gratuity;
import com.floreantpos.model.PayOutTransaction;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.RefundTransaction;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketDiscount;
import com.floreantpos.model.dao.CashDropTransactionDAO;
import com.floreantpos.model.dao.GenericDAO;
import com.floreantpos.model.dao.PayOutTransactionDAO;
import com.floreantpos.model.dao.RefundTransactionDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.model.util.RefundSummary;
import com.floreantpos.model.util.TransactionSummary;
import com.floreantpos.util.NumberUtil;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class DrawerpullReportService {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static DrawerPullReport buildDrawerPullReport() throws Exception {
        try (Session session = null;){
            Terminal terminal = Application.getInstance().refreshAndGetTerminal();
            DrawerPullReport report = new DrawerPullReport();
            report.setReportTime(new Date());
            GenericDAO dao = new GenericDAO();
            session = dao.createNewSession();
            DrawerpullReportService.populateNetSales(session, terminal, report);
            DrawerpullReportService.populateReceiptDifferential(session, terminal, report);
            RefundSummary refundSummary = RefundTransactionDAO.getInstance().getTotalRefundForTerminal(terminal);
            report.setRefundReceiptCount(report.getRefundReceiptCount() + refundSummary.getCount());
            report.setRefundAmount(report.getRefundAmount() + refundSummary.getAmount());
            report.setTipsPaid(TicketDAO.getInstance().getPaidGratuityAmount(terminal));
            double totalPayout = 0.0;
            List<PayOutTransaction> payoutTransactions = new PayOutTransactionDAO().getUnsettled(terminal);
            for (PayOutTransaction transaction : payoutTransactions) {
                totalPayout += transaction.getAmount().doubleValue();
            }
            report.setPayOutCount(payoutTransactions.size());
            report.setPayOutAmount(totalPayout);
            double drawerBleedAmount = 0.0;
            List<CashDropTransaction> cashDrops = new CashDropTransactionDAO().findUnsettled(terminal);
            for (CashDropTransaction transaction : cashDrops) {
                drawerBleedAmount += transaction.getAmount().doubleValue();
            }
            report.setDrawerBleedCount(cashDrops.size());
            report.setDrawerBleedAmount(drawerBleedAmount);
            report.setBeginCash(terminal.getOpeningBalance());
            report.setCashToDeposit(terminal.getCurrentBalance());
            if (TerminalConfig.isEnabledMultiCurrency()) {
                DrawerpullReportService.populateCurrencyBalanceSection(session, terminal, report);
            }
            DrawerpullReportService.populateVoidSection(session, terminal, report);
            int totalDiscountCount = 0;
            double totalDiscountAmount = 0.0;
            double totalDiscountSales = 0.0;
            int totalDiscountGuest = 0;
            int totalDiscountPartySize = 0;
            int totalDiscountCheckSize = 0;
            double totalDiscountPercentage = 0.0;
            double totalDiscountRatio = 0.0;
            Criteria criteria = session.createCriteria(Ticket.class, "t");
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_DRAWER_RESETTED, (Object)Boolean.FALSE));
            criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
            List list = criteria.list();
            for (Ticket ticket : list) {
                if (ticket.getDiscounts() == null) continue;
                List<TicketDiscount> discounts = ticket.getDiscounts();
                for (TicketDiscount discount2 : discounts) {
                    ++totalDiscountCount;
                    totalDiscountAmount += discount2.getValue().doubleValue();
                    totalDiscountGuest += ticket.getNumberOfGuests().intValue();
                    totalDiscountSales += ticket.getTotalAmount().doubleValue();
                    ++totalDiscountCheckSize;
                }
            }
            totalDiscountPartySize = totalDiscountGuest;
            report.setTotalDiscountCount(totalDiscountCount);
            report.setTotalDiscountAmount(totalDiscountAmount);
            report.setTotalDiscountCheckSize(totalDiscountCheckSize);
            report.setTotalDiscountSales(totalDiscountSales);
            report.setTotalDiscountGuest(totalDiscountGuest);
            report.setTotalDiscountPartySize(totalDiscountPartySize);
            report.setTotalDiscountPercentage(totalDiscountPercentage);
            report.setTotalDiscountRatio(totalDiscountRatio);
            report.setTerminal(terminal);
            report.calculate();
            DrawerPullReport drawerPullReport = report;
            return drawerPullReport;
        }
    }

    private static void populateCurrencyBalanceSection(Session session, Terminal terminal, DrawerPullReport report) {
        Criteria criteria = session.createCriteria(CashDrawer.class);
        criteria.add((Criterion)Restrictions.eq((String)CashDrawer.PROP_TERMINAL, (Object)terminal));
        CashDrawer cashDrawer = (CashDrawer)criteria.uniqueResult();
        if (cashDrawer != null) {
            Set<CurrencyBalance> currencyBalance = cashDrawer.getCurrencyBalanceList();
            report.addCurrencyBalances(currencyBalance);
        }
    }

    private static void populateVoidSection(Session session, Terminal terminal, DrawerPullReport report) {
        Criteria criteria = session.createCriteria(Ticket.class, "t");
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.TRUE));
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_DRAWER_RESETTED, (Object)Boolean.FALSE));
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
        List list = criteria.list();
        double totalWaste = 0.0;
        double totalVoid = 0.0;
        for (Ticket ticket : list) {
            totalVoid += ticket.getSubtotalAmount().doubleValue();
            if (!ticket.isWasted().booleanValue()) continue;
            totalWaste += ticket.getSubtotalAmount().doubleValue();
        }
        report.setTotalVoid(totalVoid);
        report.setTotalVoidWst(totalWaste);
    }

    private static void populateNetSales(Session session, Terminal terminal, DrawerPullReport report) {
        Criteria criteria = session.createCriteria(Ticket.class);
        criteria.add((Criterion)Restrictions.gt((String)Ticket.PROP_PAID_AMOUNT, (Object)0.0));
        criteria.add((Criterion)Restrictions.or((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE), (Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.TRUE)));
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_DRAWER_RESETTED, (Object)Boolean.FALSE));
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
        List list = criteria.list();
        if (list == null || list.size() == 0) {
            return;
        }
        int ticketCount = 0;
        double subtotal = 0.0;
        double discount = 0.0;
        double salesTax = 0.0;
        double salesDeliveryCharge = 0.0;
        double tips = 0.0;
        for (Ticket ticket : list) {
            ++ticketCount;
            double refundAmount = 0.0;
            double refundTaxAmount = 0.0;
            Gratuity gratuity = ticket.getGratuity();
            if (gratuity != null) {
                if (gratuity.isRefunded().booleanValue()) {
                    refundAmount -= gratuity.getAmount().doubleValue();
                } else {
                    tips += gratuity.getAmount().doubleValue();
                }
            }
            if (ticket.isRefunded().booleanValue()) {
                if (ticket.getTransactions() != null) {
                    for (PosTransaction t : ticket.getTransactions()) {
                        if (!(t instanceof RefundTransaction)) continue;
                        refundAmount += NumberUtil.roundToTwoDigit(t.getAmount());
                    }
                }
                refundTaxAmount = ticket.getTaxAmount() * refundAmount / (ticket.getSubtotalAmount() + ticket.getTaxAmount());
                refundAmount = NumberUtil.roundToTwoDigit(refundAmount - refundTaxAmount);
            }
            subtotal += ticket.getSubtotalAmount() - refundAmount;
            discount += ticket.getDiscountAmount().doubleValue();
            salesTax += ticket.getTaxAmount() - refundTaxAmount;
            salesDeliveryCharge += ticket.getDeliveryCharge().doubleValue();
        }
        report.setTicketCount(ticketCount);
        report.setNetSales(subtotal - discount);
        report.setSalesTax(NumberUtil.roundToTwoDigit(salesTax));
        report.setSalesDeliveryCharge(salesDeliveryCharge);
        report.setChargedTips(tips);
    }

    private static void populateReceiptDifferential(Session session, Terminal terminal, DrawerPullReport report) {
        Criteria criteria = session.createCriteria(Ticket.class);
        criteria.add((Criterion)Restrictions.gt((String)Ticket.PROP_PAID_AMOUNT, (Object)0.0));
        criteria.add((Criterion)Restrictions.or((Criterion)Restrictions.eq((String)Ticket.PROP_VOIDED, (Object)Boolean.FALSE), (Criterion)Restrictions.eq((String)Ticket.PROP_REFUNDED, (Object)Boolean.TRUE)));
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_DRAWER_RESETTED, (Object)Boolean.FALSE));
        criteria.add((Criterion)Restrictions.eq((String)Ticket.PROP_TERMINAL, (Object)terminal));
        List list = criteria.list();
        if (list == null || list.size() == 0) {
            return;
        }
        TicketDAO dao = TicketDAO.getInstance();
        for (Ticket ticket : list) {
            ticket = dao.loadCouponsAndTransactions(ticket.getId());
            TransactionSummary ts = DrawerpullReportService.calculateTransactionSummary(ticket, CashTransaction.class);
            report.setCashReceiptCount(report.getCashReceiptCount() + ts.getCount());
            report.setCashReceiptAmount(report.getCashReceiptAmount() + ts.getAmount());
            ts = DrawerpullReportService.calculateTransactionSummary(ticket, CreditCardTransaction.class);
            report.setCreditCardReceiptCount(report.getCreditCardReceiptCount() + ts.getCount());
            report.setCreditCardReceiptAmount(report.getCreditCardReceiptAmount() + ts.getAmount());
            ts = DrawerpullReportService.calculateTransactionSummary(ticket, DebitCardTransaction.class);
            report.setDebitCardReceiptCount(report.getDebitCardReceiptCount() + ts.getCount());
            report.setDebitCardReceiptAmount(report.getDebitCardReceiptAmount() + ts.getAmount());
            ts = DrawerpullReportService.calculateTransactionSummary(ticket, GiftCertificateTransaction.class);
            report.setGiftCertReturnCount(report.getGiftCertReturnCount() + ts.getCount());
            report.setGiftCertReturnAmount(report.getGiftCertReturnAmount() + ts.getAmount());
            report.setGiftCertChangeAmount(report.getGiftCertChangeAmount() + ts.getChangeAmount());
            report.setCashBack(report.getCashBack() + ts.getChangeAmount());
        }
    }

    private static TransactionSummary calculateTransactionSummary(Ticket ticket, Class transactionClass) {
        int count = 0;
        double total = 0.0;
        double changeAmount = 0.0;
        TransactionSummary summary = new TransactionSummary();
        Set<PosTransaction> transactions = ticket.getTransactions();
        if (transactions == null) {
            return summary;
        }
        for (PosTransaction posTransaction : transactions) {
            if (!posTransaction.getClass().equals(transactionClass)) continue;
            ++count;
            total += posTransaction.getAmount().doubleValue();
            changeAmount += posTransaction.getGiftCertCashBackAmount().doubleValue();
        }
        summary.setCount(count);
        summary.setAmount(total);
        summary.setChangeAmount(changeAmount);
        return summary;
    }
}

