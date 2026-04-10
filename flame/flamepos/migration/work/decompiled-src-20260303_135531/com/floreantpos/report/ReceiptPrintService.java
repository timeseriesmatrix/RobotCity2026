/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.sf.jasperreports.engine.JRDataSource
 *  net.sf.jasperreports.engine.JREmptyDataSource
 *  net.sf.jasperreports.engine.JRException
 *  net.sf.jasperreports.engine.JRExporterParameter
 *  net.sf.jasperreports.engine.JasperFillManager
 *  net.sf.jasperreports.engine.JasperPrint
 *  net.sf.jasperreports.engine.JasperReport
 *  net.sf.jasperreports.engine.data.JRTableModelDataSource
 *  net.sf.jasperreports.engine.export.JRPrintServiceExporter
 *  net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter
 *  org.apache.commons.lang.StringUtils
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  us.fatehi.magnetictrack.bankcard.BankCardMagneticTrack
 */
package com.floreantpos.report;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.CardConfig;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.CardReader;
import com.floreantpos.model.Currency;
import com.floreantpos.model.KitchenTicket;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.Printer;
import com.floreantpos.model.RefundTransaction;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.TerminalPrinters;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.KitchenTicketDAO;
import com.floreantpos.model.dao.RestaurantDAO;
import com.floreantpos.model.dao.TerminalPrintersDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.report.KitchenTicketDataSource;
import com.floreantpos.report.ReportUtil;
import com.floreantpos.report.TicketDataSource;
import com.floreantpos.report.TicketPrintProperties;
import com.floreantpos.util.CurrencyUtil;
import com.floreantpos.util.NumberUtil;
import com.floreantpos.util.PrintServiceUtil;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import us.fatehi.magnetictrack.bankcard.BankCardMagneticTrack;

public class ReceiptPrintService {
    private static final String TOTAL_TEXT = "totalAmountText";
    private static final String TIPS_TEXT = "tipsText";
    private static final String DELIVERY_CHARGE_TEXT = "deliveryChargeText";
    private static final String SERVICE_CHARGE_TEXT = "serviceChargeText";
    private static final String TAX_TEXT = "taxText";
    private static final String DISCOUNT_TEXT = "discountText";
    private static final String DATA = "data";
    private static final String TITLE = "title";
    private static final String ORDER_ = "ORDER-";
    public static final String PROP_PRINTER_NAME = "printerName";
    private static final String TIP_AMOUNT = "tipAmount";
    private static final String SERVICE_CHARGE = "serviceCharge";
    private static final String DELIVERY_CHARGE = "deliveryCharge";
    private static final String ADJUST_AMOUNT = "adjustAmount";
    private static final String TAX_AMOUNT = "taxAmount";
    private static final String DISCOUNT_AMOUNT = "discountAmount";
    private static final String HEADER_LINE5 = "headerLine5";
    private static final String HEADER_LINE4 = "headerLine4";
    private static final String HEADER_LINE3 = "headerLine3";
    private static final String HEADER_LINE2 = "headerLine2";
    private static final String HEADER_LINE1 = "headerLine1";
    private static final String REPORT_DATE = "reportDate";
    private static final String SERVER_NAME = "serverName";
    private static final String GUEST_COUNT = "guestCount";
    private static final String TABLE_NO = "tableNo";
    private static final String CHECK_NO = "checkNo";
    private static final String TERMINAL = "terminal";
    private static final String SHOW_FOOTER = "showFooter";
    private static final String SHOW_HEADER_SEPARATOR = "showHeaderSeparator";
    private static final String SHOW_SUBTOTAL = "showSubtotal";
    private static final String RECEIPT_TYPE = "receiptType";
    private static final String SUB_TOTAL_TEXT = "subTotalText";
    private static final String QUANTITY_TEXT = "quantityText";
    private static final String ITEM_TEXT = "itemText";
    private static final String CURRENCY_SYMBOL = "currencySymbol";
    private static Log logger = LogFactory.getLog(ReceiptPrintService.class);
    private static final SimpleDateFormat reportDateFormat = new SimpleDateFormat("M/d/yy, h:mm a");
    public static final String CUSTOMER_COPY = "Customer Copy";
    public static final String DRIVER_COPY = "Driver Copy";
    public static final String CENTER = "center";
    public static final String LEFT = "left";
    public static final String RIGHT = "right";

    public static void printGenericReport(String title, String data) throws Exception {
        HashMap<String, String> map = new HashMap<String, String>(2);
        map.put(TITLE, title);
        map.put(DATA, data);
        JasperPrint jasperPrint = ReceiptPrintService.createJasperPrint(ReportUtil.getReport("generic-receipt"), map, (JRDataSource)new JREmptyDataSource());
        jasperPrint.setProperty(PROP_PRINTER_NAME, Application.getPrinters().getReceiptPrinter());
        ReceiptPrintService.printQuitely(jasperPrint);
    }

    public static void testPrinter(String deviceName, String title, String data) throws Exception {
        HashMap<String, String> map = new HashMap<String, String>(2);
        map.put(TITLE, title);
        map.put(DATA, data);
        JasperPrint jasperPrint = ReceiptPrintService.createJasperPrint(ReportUtil.getReport("test-printer"), map, (JRDataSource)new JREmptyDataSource());
        jasperPrint.setProperty(PROP_PRINTER_NAME, deviceName);
        ReceiptPrintService.printQuitely(jasperPrint);
    }

    public static JasperPrint createJasperPrint(JasperReport report, Map<String, String> properties, JRDataSource dataSource) throws Exception {
        JasperPrint jasperPrint = JasperFillManager.fillReport((JasperReport)report, properties, (JRDataSource)dataSource);
        return jasperPrint;
    }

    public static JasperPrint createPrint(Ticket ticket, Map<String, String> map, PosTransaction transaction) throws Exception {
        TicketDataSource dataSource = new TicketDataSource(ticket);
        return ReceiptPrintService.createJasperPrint(ReportUtil.getReport("ticket-receipt"), map, (JRDataSource)new JRTableModelDataSource((TableModel)dataSource));
    }

    public static void printTicket(Ticket ticket) {
        try {
            TicketPrintProperties printProperties = new TicketPrintProperties("*** ORDER " + ticket.getId() + " ***", false, true, true);
            printProperties.setPrintCookingInstructions(false);
            HashMap map = ReceiptPrintService.populateTicketProperties(ticket, printProperties, null);
            List<TerminalPrinters> terminalPrinters = TerminalPrintersDAO.getInstance().findTerminalPrinters();
            ArrayList<Printer> activeReceiptPrinters = new ArrayList<Printer>();
            for (TerminalPrinters terminalPrinters2 : terminalPrinters) {
                int printerType = terminalPrinters2.getVirtualPrinter().getType();
                if (printerType != 1) continue;
                Printer printer = new Printer(terminalPrinters2.getVirtualPrinter(), terminalPrinters2.getPrinterName());
                activeReceiptPrinters.add(printer);
            }
            if (activeReceiptPrinters == null || activeReceiptPrinters.isEmpty()) {
                JasperPrint jasperPrint = ReceiptPrintService.createPrint(ticket, map, null);
                jasperPrint.setName(ORDER_ + ticket.getId());
                jasperPrint.setProperty(PROP_PRINTER_NAME, Application.getPrinters().getReceiptPrinter());
                ReceiptPrintService.printQuitely(jasperPrint);
            } else {
                for (Printer activeReceiptPrinter : activeReceiptPrinters) {
                    JasperPrint jasperPrint = ReceiptPrintService.createPrint(ticket, map, null);
                    jasperPrint.setName(ORDER_ + ticket.getId() + activeReceiptPrinter.getDeviceName());
                    jasperPrint.setProperty(PROP_PRINTER_NAME, activeReceiptPrinter.getDeviceName());
                    ReceiptPrintService.printQuitely(jasperPrint);
                }
            }
        }
        catch (Exception e) {
            logger.error((Object)POSConstants.PRINT_ERROR, (Throwable)e);
        }
    }

    public static void printTicket(Ticket ticket, String copyType) {
        try {
            TicketPrintProperties printProperties = new TicketPrintProperties("*** ORDER " + ticket.getId() + " ***", false, true, true);
            printProperties.setPrintCookingInstructions(false);
            HashMap map = ReceiptPrintService.populateTicketProperties(ticket, printProperties, null);
            map.put("copyType", copyType);
            map.put("cardPayment", true);
            List<TerminalPrinters> terminalPrinters = TerminalPrintersDAO.getInstance().findTerminalPrinters();
            ArrayList<Printer> activeReceiptPrinters = new ArrayList<Printer>();
            for (TerminalPrinters terminalPrinters2 : terminalPrinters) {
                int printerType = terminalPrinters2.getVirtualPrinter().getType();
                if (printerType != 1) continue;
                Printer printer = new Printer(terminalPrinters2.getVirtualPrinter(), terminalPrinters2.getPrinterName());
                activeReceiptPrinters.add(printer);
            }
            if (activeReceiptPrinters == null || activeReceiptPrinters.isEmpty()) {
                JasperPrint jasperPrint = ReceiptPrintService.createPrint(ticket, map, null);
                jasperPrint.setName(ORDER_ + ticket.getId());
                jasperPrint.setProperty(PROP_PRINTER_NAME, Application.getPrinters().getReceiptPrinter());
                ReceiptPrintService.printQuitely(jasperPrint);
            } else {
                for (Printer activeReceiptPrinter : activeReceiptPrinters) {
                    JasperPrint jasperPrint = ReceiptPrintService.createPrint(ticket, map, null);
                    jasperPrint.setName(ORDER_ + ticket.getId() + activeReceiptPrinter.getDeviceName());
                    jasperPrint.setProperty(PROP_PRINTER_NAME, activeReceiptPrinter.getDeviceName());
                    ReceiptPrintService.printQuitely(jasperPrint);
                }
            }
        }
        catch (Exception e) {
            logger.error((Object)POSConstants.PRINT_ERROR, (Throwable)e);
        }
    }

    public static JasperPrint createRefundPrint(Ticket ticket, HashMap map) throws Exception {
        TicketDataSource dataSource = new TicketDataSource(ticket);
        return ReceiptPrintService.createJasperPrint(ReportUtil.getReport("RefundReceipt"), map, (JRDataSource)new JRTableModelDataSource((TableModel)dataSource));
    }

    public static void printRefundTicket(Ticket ticket, RefundTransaction posTransaction) {
        try {
            TicketPrintProperties printProperties = new TicketPrintProperties("*** REFUND RECEIPT ***", true, true, true);
            printProperties.setPrintCookingInstructions(false);
            HashMap map = ReceiptPrintService.populateTicketProperties(ticket, printProperties, posTransaction);
            map.put("refundAmountText", Messages.getString("ReceiptPrintService.1"));
            map.put("refundAmount", NumberUtil.formatNumber(posTransaction.getAmount()));
            map.put("cashRefundText", Messages.getString("ReceiptPrintService.2"));
            map.put("cashRefund", NumberUtil.formatNumber(posTransaction.getAmount()));
            JasperPrint jasperPrint = ReceiptPrintService.createRefundPrint(ticket, map);
            jasperPrint.setName("REFUND_" + ticket.getId());
            jasperPrint.setProperty(PROP_PRINTER_NAME, Application.getPrinters().getReceiptPrinter());
            ReceiptPrintService.printQuitely(jasperPrint);
        }
        catch (Exception e) {
            logger.error((Object)POSConstants.PRINT_ERROR, (Throwable)e);
        }
    }

    public static void printTransaction(PosTransaction transaction) {
        try {
            Ticket ticket = transaction.getTicket();
            TicketPrintProperties printProperties = new TicketPrintProperties(Messages.getString("ReceiptPrintService.3"), true, true, true);
            printProperties.setPrintCookingInstructions(false);
            HashMap map = ReceiptPrintService.populateTicketProperties(ticket, printProperties, transaction);
            if (transaction != null && transaction.isCard()) {
                CardReader cardReader = CardReader.fromString(transaction.getCardReader());
                if (cardReader == CardReader.EXTERNAL_TERMINAL) {
                    return;
                }
                map.put("cardPayment", true);
                map.put("copyType", Messages.getString("ReceiptPrintService.4"));
                JasperPrint jasperPrint = ReceiptPrintService.createPrint(ticket, map, transaction);
                jasperPrint.setName("Ticket-" + ticket.getId() + "-CustomerCopy");
                jasperPrint.setProperty(PROP_PRINTER_NAME, Application.getPrinters().getReceiptPrinter());
                ReceiptPrintService.printQuitely(jasperPrint);
                map.put("copyType", Messages.getString("ReceiptPrintService.5"));
                jasperPrint = ReceiptPrintService.createPrint(ticket, map, transaction);
                jasperPrint.setName("Ticket-" + ticket.getId() + "-MerchantCopy");
                jasperPrint.setProperty(PROP_PRINTER_NAME, Application.getPrinters().getReceiptPrinter());
                ReceiptPrintService.printQuitely(jasperPrint);
            } else {
                JasperPrint jasperPrint = ReceiptPrintService.createPrint(ticket, map, transaction);
                jasperPrint.setName("Ticket-" + ticket.getId());
                jasperPrint.setProperty(PROP_PRINTER_NAME, Application.getPrinters().getReceiptPrinter());
                ReceiptPrintService.printQuitely(jasperPrint);
            }
        }
        catch (Exception e) {
            logger.error((Object)POSConstants.PRINT_ERROR, (Throwable)e);
        }
    }

    public static void printTransaction(PosTransaction transaction, boolean printCustomerCopy) {
        try {
            Ticket ticket = transaction.getTicket();
            TicketPrintProperties printProperties = new TicketPrintProperties(Messages.getString("ReceiptPrintService.6"), true, true, true);
            printProperties.setPrintCookingInstructions(false);
            HashMap map = ReceiptPrintService.populateTicketProperties(ticket, printProperties, transaction);
            if (transaction != null && transaction.isCard()) {
                map.put("cardPayment", true);
                map.put("copyType", Messages.getString("ReceiptPrintService.7"));
                JasperPrint jasperPrint = ReceiptPrintService.createPrint(ticket, map, transaction);
                jasperPrint.setName("Ticket-" + ticket.getId() + "-MerchantCopy");
                jasperPrint.setProperty(PROP_PRINTER_NAME, Application.getPrinters().getReceiptPrinter());
                ReceiptPrintService.printQuitely(jasperPrint);
                if (printCustomerCopy) {
                    map.put("copyType", Messages.getString("ReceiptPrintService.8"));
                    jasperPrint = ReceiptPrintService.createPrint(ticket, map, transaction);
                    jasperPrint.setName("Ticket-" + ticket.getId() + "-CustomerCopy");
                    jasperPrint.setProperty(PROP_PRINTER_NAME, Application.getPrinters().getReceiptPrinter());
                    ReceiptPrintService.printQuitely(jasperPrint);
                }
            } else {
                JasperPrint jasperPrint = ReceiptPrintService.createPrint(ticket, map, transaction);
                jasperPrint.setName("Ticket-" + ticket.getId());
                jasperPrint.setProperty(PROP_PRINTER_NAME, Application.getPrinters().getReceiptPrinter());
                ReceiptPrintService.printQuitely(jasperPrint);
            }
        }
        catch (Exception e) {
            logger.error((Object)POSConstants.PRINT_ERROR, (Throwable)e);
        }
    }

    private static void beginRow(StringBuilder html) {
        html.append("<div>");
    }

    private static void endRow(StringBuilder html) {
        html.append("</div>");
    }

    private static void addColumn(StringBuilder html, String columnText) {
        html.append("<span>" + columnText + "</span>");
    }

    public static HashMap populateTicketProperties(Ticket ticket, TicketPrintProperties printProperties, PosTransaction transaction) {
        Restaurant restaurant = RestaurantDAO.getRestaurant();
        double totalAmount = ticket.getTotalAmount();
        double tipAmount = 0.0;
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("IS_IGNORE_PAGINATION", false);
        String currencySymbol = CurrencyUtil.getCurrencySymbol();
        map.put(CURRENCY_SYMBOL, currencySymbol);
        map.put(ITEM_TEXT, POSConstants.RECEIPT_REPORT_ITEM_LABEL);
        map.put(QUANTITY_TEXT, POSConstants.RECEIPT_REPORT_QUANTITY_LABEL);
        map.put("subtotalHeaderText", Messages.getString("RECEIPT_REPORT_SUBTOTAL_HEADER"));
        map.put(SUB_TOTAL_TEXT, POSConstants.RECEIPT_REPORT_SUBTOTAL_LABEL + " " + currencySymbol);
        map.put(TOTAL_TEXT, POSConstants.RECEIPT_REPORT_TOTAL_AMOUNT_LABEL + " " + currencySymbol);
        map.put("tenderedAmountText", POSConstants.RECEIPT_REPORT_TENDERED_AMOUNT_LABEL + " " + currencySymbol);
        map.put(DISCOUNT_TEXT, POSConstants.RECEIPT_REPORT_DISCOUNT_LABEL + " " + currencySymbol);
        map.put(TAX_TEXT, POSConstants.RECEIPT_REPORT_TAX_LABEL + " " + currencySymbol);
        map.put(SERVICE_CHARGE_TEXT, POSConstants.RECEIPT_REPORT_SERVICE_CHARGE_LABEL + " " + currencySymbol);
        map.put(DELIVERY_CHARGE_TEXT, POSConstants.RECEIPT_REPORT_DELIVERY_CHARGE_LABEL + " " + currencySymbol);
        map.put(TIPS_TEXT, POSConstants.RECEIPT_REPORT_TIPS_LABEL + " " + currencySymbol);
        map.put("paidAmountText", POSConstants.RECEIPT_REPORT_PAIDAMOUNT_LABEL + " " + currencySymbol);
        map.put("dueAmountText", POSConstants.RECEIPT_REPORT_DUEAMOUNT_LABEL + " " + currencySymbol);
        map.put("changeAmountText", POSConstants.RECEIPT_REPORT_CHANGEAMOUNT_LABEL + " " + currencySymbol);
        map.put(RECEIPT_TYPE, printProperties.getReceiptTypeName());
        map.put(SHOW_SUBTOTAL, printProperties.isShowSubtotal());
        map.put(SHOW_HEADER_SEPARATOR, Boolean.TRUE);
        map.put(SHOW_FOOTER, printProperties.isShowFooter());
        map.put(TERMINAL, POSConstants.RECEIPT_REPORT_TERMINAL_LABEL + Application.getInstance().getTerminal().getId());
        map.put(CHECK_NO, POSConstants.RECEIPT_REPORT_TICKET_NO_LABEL + ticket.getId());
        map.put(TABLE_NO, POSConstants.RECEIPT_REPORT_TABLE_NO_LABEL + ticket.getTableNumbers());
        map.put(GUEST_COUNT, POSConstants.RECEIPT_REPORT_GUEST_NO_LABEL + ticket.getNumberOfGuests());
        map.put(SERVER_NAME, POSConstants.RECEIPT_REPORT_SERVER_LABEL + ticket.getOwner());
        map.put(REPORT_DATE, POSConstants.RECEIPT_REPORT_DATE_LABEL + Application.formatDate(new Date()));
        StringBuilder ticketHeaderBuilder = ReceiptPrintService.buildTicketHeader(ticket, printProperties);
        map.put("ticketHeader", ticketHeaderBuilder.toString());
        if (TerminalConfig.isShowBarcodeOnReceipt()) {
            map.put("barcode", String.valueOf(ticket.getId()));
        }
        if (printProperties.isShowHeader()) {
            map.put(HEADER_LINE1, restaurant.getName());
            map.put(HEADER_LINE2, restaurant.getAddressLine1());
            map.put(HEADER_LINE3, restaurant.getAddressLine2());
            map.put(HEADER_LINE4, restaurant.getAddressLine3());
            map.put(HEADER_LINE5, restaurant.getTelephone());
        }
        if (printProperties.isShowFooter()) {
            map.put("subtotalAmount", NumberUtil.formatNumber(ticket.getSubtotalAmount()));
            double toleranceAmount = ticket.calculateToleranceAmount();
            if (toleranceAmount > 0.0) {
                map.put(ADJUST_AMOUNT, NumberUtil.formatNumber(toleranceAmount));
            }
            if (ticket.getDiscountAmount() > 0.0) {
                map.put(DISCOUNT_AMOUNT, NumberUtil.formatNumber(ticket.getDiscountAmount()));
            }
            if (ticket.getTaxAmount() > 0.0) {
                map.put(TAX_AMOUNT, NumberUtil.formatNumber(ticket.getTaxAmount()));
            }
            if (ticket.getServiceCharge() > 0.0) {
                map.put(SERVICE_CHARGE, NumberUtil.formatNumber(ticket.getServiceCharge()));
            }
            if (ticket.getDeliveryCharge() > 0.0) {
                map.put(DELIVERY_CHARGE, NumberUtil.formatNumber(ticket.getDeliveryCharge()));
            }
            if (ticket.getGratuity() != null) {
                tipAmount = ticket.getGratuity().getAmount();
                map.put(TIP_AMOUNT, NumberUtil.formatNumber(tipAmount));
            }
            map.put("totalAmount", NumberUtil.formatNumber(totalAmount));
            if (transaction != null) {
                map.put("tenderedAmount", NumberUtil.formatNumber(transaction.getTenderAmount()));
            }
            map.put("paidAmount", NumberUtil.formatNumber(ticket.getPaidAmount()));
            map.put("dueAmount", NumberUtil.formatNumber(ticket.getDueAmount()));
            map.put("footerMessage", restaurant.getTicketFooterMessage());
            map.put("copyType", printProperties.getReceiptCopyType());
            if (ticket.isRefunded().booleanValue()) {
                ReceiptPrintService.populateRefundProperties(ticket.getTransactions(), map);
            }
            if (transaction != null) {
                double changedAmount = transaction.getTenderAmount() - transaction.getAmount();
                if (changedAmount > 0.0) {
                    map.put("changedAmount", NumberUtil.formatNumber(changedAmount));
                }
                if (transaction.isCard()) {
                    String cardInformationForReceipt;
                    map.put("cardPayment", true);
                    if (transaction.hasProperty("requireSignature")) {
                        Boolean requireSignature = Boolean.valueOf(transaction.getProperty("requireSignature"));
                        map.put("showSignatureField", requireSignature);
                    }
                    if (StringUtils.isEmpty((String)(cardInformationForReceipt = CardConfig.getPaymentGateway().getProcessor().getCardInformationForReceipt(transaction)))) {
                        cardInformationForReceipt = ReceiptPrintService.getCardInformation(transaction);
                    }
                    map.put("cardInformation", cardInformationForReceipt);
                }
                if (TerminalConfig.isEnabledMultiCurrency()) {
                    StringBuilder multiCurrencyBreakdownCashBack = ReceiptPrintService.buildMultiCurrency(ticket, printProperties);
                    if (multiCurrencyBreakdownCashBack != null) {
                        map.put("additionalProperties", multiCurrencyBreakdownCashBack.toString());
                    } else {
                        StringBuilder multiCurrencyTotalAmount = ReceiptPrintService.buildMultiCurrencyTotalAmount(ticket, printProperties);
                        if (multiCurrencyTotalAmount != null) {
                            map.put("additionalProperties", multiCurrencyTotalAmount.toString());
                        }
                    }
                }
            }
        }
        return map;
    }

    private static void populateRefundProperties(Set<PosTransaction> transactions, HashMap map) {
        if (transactions == null) {
            return;
        }
        TicketPrintProperties printProperties = new TicketPrintProperties("*** REFUND RECEIPT ***", true, true, true);
        printProperties.setPrintCookingInstructions(false);
        double refundAmount = 0.0;
        for (PosTransaction transaction : transactions) {
            if (!(transaction instanceof RefundTransaction)) continue;
            refundAmount += transaction.getAmount().doubleValue();
        }
        map.put("additionalProperties", "<html><b>" + Messages.getString("ReceiptPrintService.1") + " " + CurrencyUtil.getCurrencySymbol() + "&nbsp;" + refundAmount + "</b></html>");
    }

    private static String getCardInformation(PosTransaction transaction) {
        String string = "<br/>CARD INFO: ------------------------";
        string = string + "<br/>PROCESS: " + transaction.getCardReader();
        string = string + "<br/> TYPE: " + transaction.getCardType();
        try {
            String cardNumber = transaction.getCardNumber();
            if (transaction.getCardNumber() != null) {
                string = string + "<br/> ACCT: **** **** **** " + cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
            }
            if (transaction.getCardHolderName() != null) {
                string = string + "<br/> CARDHOLDER: " + transaction.getCardHolderName();
            }
            if (transaction.getCardTransactionId() != null) {
                string = string + "<br/> TRANS ID: " + transaction.getCardTransactionId();
            }
            string = string + "<br/> APPROVAL: " + transaction.getCardAuthCode();
            if (transaction.getCardAID() != null) {
                string = string + "<br/> AID: " + transaction.getCardAID();
            }
            if (transaction.getCardARQC() != null) {
                string = string + "<br/> ARQC: " + transaction.getCardARQC();
            }
        }
        catch (Exception e) {
            logger.equals(e);
        }
        return string;
    }

    private static StringBuilder buildTicketHeader(Ticket ticket, TicketPrintProperties printProperties) {
        StringBuilder ticketHeaderBuilder = new StringBuilder();
        ticketHeaderBuilder.append("<html>");
        ReceiptPrintService.beginRow(ticketHeaderBuilder);
        ReceiptPrintService.addColumn(ticketHeaderBuilder, "*" + ticket.getOrderType() + "*");
        ReceiptPrintService.endRow(ticketHeaderBuilder);
        ReceiptPrintService.beginRow(ticketHeaderBuilder);
        ReceiptPrintService.addColumn(ticketHeaderBuilder, POSConstants.RECEIPT_REPORT_TERMINAL_LABEL + Application.getInstance().getTerminal().getId());
        ReceiptPrintService.endRow(ticketHeaderBuilder);
        ReceiptPrintService.beginRow(ticketHeaderBuilder);
        ReceiptPrintService.addColumn(ticketHeaderBuilder, POSConstants.RECEIPT_REPORT_TICKET_NO_LABEL + ticket.getId());
        ReceiptPrintService.endRow(ticketHeaderBuilder);
        OrderType orderType = ticket.getOrderType();
        if (orderType.isShowTableSelection().booleanValue() || orderType.isShowGuestSelection().booleanValue()) {
            ReceiptPrintService.beginRow(ticketHeaderBuilder);
            ReceiptPrintService.addColumn(ticketHeaderBuilder, POSConstants.RECEIPT_REPORT_TABLE_NO_LABEL + ticket.getTableNumbers());
            ReceiptPrintService.endRow(ticketHeaderBuilder);
            ReceiptPrintService.beginRow(ticketHeaderBuilder);
            ReceiptPrintService.addColumn(ticketHeaderBuilder, POSConstants.RECEIPT_REPORT_GUEST_NO_LABEL + ticket.getNumberOfGuests());
            ReceiptPrintService.endRow(ticketHeaderBuilder);
        }
        ReceiptPrintService.beginRow(ticketHeaderBuilder);
        ReceiptPrintService.addColumn(ticketHeaderBuilder, POSConstants.RECEIPT_REPORT_SERVER_LABEL + ticket.getOwner());
        ReceiptPrintService.endRow(ticketHeaderBuilder);
        ReceiptPrintService.beginRow(ticketHeaderBuilder);
        ReceiptPrintService.addColumn(ticketHeaderBuilder, POSConstants.RECEIPT_REPORT_DATE_LABEL + reportDateFormat.format(new Date()));
        ReceiptPrintService.endRow(ticketHeaderBuilder);
        ReceiptPrintService.beginRow(ticketHeaderBuilder);
        ReceiptPrintService.addColumn(ticketHeaderBuilder, "");
        ReceiptPrintService.endRow(ticketHeaderBuilder);
        User driver = ticket.getAssignedDriver();
        if (driver != null) {
            ReceiptPrintService.beginRow(ticketHeaderBuilder);
            ReceiptPrintService.addColumn(ticketHeaderBuilder, "*Driver*");
            ReceiptPrintService.endRow(ticketHeaderBuilder);
            if (StringUtils.isNotEmpty((String)driver.getFullName())) {
                ReceiptPrintService.beginRow(ticketHeaderBuilder);
                ReceiptPrintService.addColumn(ticketHeaderBuilder, driver.getFullName());
                ReceiptPrintService.endRow(ticketHeaderBuilder);
            }
            ReceiptPrintService.beginRow(ticketHeaderBuilder);
            ReceiptPrintService.addColumn(ticketHeaderBuilder, "");
            ReceiptPrintService.endRow(ticketHeaderBuilder);
        }
        if (orderType.isRequiredCustomerData().booleanValue()) {
            String customerName = ticket.getProperty("CUSTOMER_NAME");
            String customerMobile = ticket.getProperty("CUSTOMER_MOBILE");
            if (StringUtils.isNotEmpty((String)customerName)) {
                ReceiptPrintService.beginRow(ticketHeaderBuilder);
                ReceiptPrintService.addColumn(ticketHeaderBuilder, Messages.getString("ReceiptPrintService.9"));
                ReceiptPrintService.endRow(ticketHeaderBuilder);
                if (StringUtils.isNotEmpty((String)customerName)) {
                    ReceiptPrintService.beginRow(ticketHeaderBuilder);
                    ReceiptPrintService.addColumn(ticketHeaderBuilder, customerName);
                    ReceiptPrintService.endRow(ticketHeaderBuilder);
                }
                if (StringUtils.isNotEmpty((String)ticket.getDeliveryAddress())) {
                    ReceiptPrintService.beginRow(ticketHeaderBuilder);
                    ReceiptPrintService.addColumn(ticketHeaderBuilder, ticket.getDeliveryAddress());
                    ReceiptPrintService.endRow(ticketHeaderBuilder);
                    if (StringUtils.isNotEmpty((String)ticket.getExtraDeliveryInfo())) {
                        ReceiptPrintService.beginRow(ticketHeaderBuilder);
                        ReceiptPrintService.addColumn(ticketHeaderBuilder, ticket.getExtraDeliveryInfo());
                        ReceiptPrintService.endRow(ticketHeaderBuilder);
                    }
                } else {
                    ReceiptPrintService.beginRow(ticketHeaderBuilder);
                    ReceiptPrintService.addColumn(ticketHeaderBuilder, Messages.getString("ReceiptPrintService.111"));
                    ReceiptPrintService.endRow(ticketHeaderBuilder);
                }
                if (StringUtils.isNotEmpty((String)customerMobile)) {
                    ReceiptPrintService.beginRow(ticketHeaderBuilder);
                    ReceiptPrintService.addColumn(ticketHeaderBuilder, "Tel: " + customerMobile);
                    ReceiptPrintService.endRow(ticketHeaderBuilder);
                }
                if (ticket.getDeliveryDate() != null) {
                    ReceiptPrintService.beginRow(ticketHeaderBuilder);
                    ReceiptPrintService.addColumn(ticketHeaderBuilder, "Delivery: " + reportDateFormat.format(ticket.getDeliveryDate()));
                    ReceiptPrintService.endRow(ticketHeaderBuilder);
                }
            }
        }
        ticketHeaderBuilder.append("</html>");
        return ticketHeaderBuilder;
    }

    private static StringBuilder buildMultiCurrencyTotalAmount(Ticket ticket, TicketPrintProperties printProperties) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        StringBuilder currencyAmountBuilder = new StringBuilder();
        currencyAmountBuilder.append("<html><table>");
        String sep = "------------------------------------";
        ReceiptPrintService.beginRow(currencyAmountBuilder);
        ReceiptPrintService.addColumn(currencyAmountBuilder, "&nbsp;");
        ReceiptPrintService.addColumn(currencyAmountBuilder, "&nbsp;");
        ReceiptPrintService.addColumn(currencyAmountBuilder, "&nbsp;");
        ReceiptPrintService.endRow(currencyAmountBuilder);
        ReceiptPrintService.beginRow(currencyAmountBuilder);
        ReceiptPrintService.addColumn(currencyAmountBuilder, "<b>Currency breakdown</b>");
        ReceiptPrintService.endRow(currencyAmountBuilder);
        ReceiptPrintService.beginRow(currencyAmountBuilder);
        ReceiptPrintService.addColumn(currencyAmountBuilder, sep);
        ReceiptPrintService.endRow(currencyAmountBuilder);
        ReceiptPrintService.beginRow(currencyAmountBuilder);
        ReceiptPrintService.addColumn(currencyAmountBuilder, ReceiptPrintService.getHtmlText("", 10, CENTER));
        ReceiptPrintService.addColumn(currencyAmountBuilder, ReceiptPrintService.getHtmlText("Net Amount", 10, CENTER));
        ReceiptPrintService.addColumn(currencyAmountBuilder, ReceiptPrintService.getHtmlText("Due", 10, CENTER));
        ReceiptPrintService.endRow(currencyAmountBuilder);
        ReceiptPrintService.beginRow(currencyAmountBuilder);
        ReceiptPrintService.addColumn(currencyAmountBuilder, sep);
        ReceiptPrintService.endRow(currencyAmountBuilder);
        int rowCount = 0;
        for (Currency currency : CurrencyUtil.getAllCurrency()) {
            if (currency == null) continue;
            String key = currency.getName();
            double rate = currency.getExchangeRate();
            ReceiptPrintService.beginRow(currencyAmountBuilder);
            ReceiptPrintService.addColumn(currencyAmountBuilder, ReceiptPrintService.getHtmlText(key, 10, LEFT));
            ReceiptPrintService.addColumn(currencyAmountBuilder, ReceiptPrintService.getHtmlText(decimalFormat.format(ticket.getTotalAmount() * rate), 10, RIGHT));
            ReceiptPrintService.addColumn(currencyAmountBuilder, ReceiptPrintService.getHtmlText(decimalFormat.format(ticket.getDueAmount() * rate), 10, RIGHT));
            ReceiptPrintService.endRow(currencyAmountBuilder);
            ++rowCount;
        }
        if (rowCount == 0) {
            return null;
        }
        currencyAmountBuilder.append("</table></html>");
        return currencyAmountBuilder;
    }

    private static StringBuilder buildMultiCurrency(Ticket ticket, TicketPrintProperties printProperties) {
        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        StringBuilder currencyAmountBuilder = new StringBuilder();
        currencyAmountBuilder.append("<html><table>");
        String sep = "------------------------------------";
        ReceiptPrintService.beginRow(currencyAmountBuilder);
        ReceiptPrintService.addColumn(currencyAmountBuilder, "&nbsp;");
        ReceiptPrintService.addColumn(currencyAmountBuilder, "&nbsp;");
        ReceiptPrintService.addColumn(currencyAmountBuilder, "&nbsp;");
        ReceiptPrintService.endRow(currencyAmountBuilder);
        String groupSettleTickets = ticket.getProperty("GROUP_SETTLE_TICKETS");
        if (groupSettleTickets == null) {
            groupSettleTickets = "";
        }
        ReceiptPrintService.beginRow(currencyAmountBuilder);
        ReceiptPrintService.addColumn(currencyAmountBuilder, groupSettleTickets + "<b>Currency breakdown</b>");
        ReceiptPrintService.endRow(currencyAmountBuilder);
        ReceiptPrintService.beginRow(currencyAmountBuilder);
        ReceiptPrintService.addColumn(currencyAmountBuilder, sep);
        ReceiptPrintService.endRow(currencyAmountBuilder);
        ReceiptPrintService.beginRow(currencyAmountBuilder);
        ReceiptPrintService.addColumn(currencyAmountBuilder, ReceiptPrintService.getHtmlText("", 10, CENTER));
        ReceiptPrintService.addColumn(currencyAmountBuilder, ReceiptPrintService.getHtmlText("Paid", 10, CENTER));
        ReceiptPrintService.addColumn(currencyAmountBuilder, ReceiptPrintService.getHtmlText("Cashback", 10, CENTER));
        ReceiptPrintService.endRow(currencyAmountBuilder);
        ReceiptPrintService.beginRow(currencyAmountBuilder);
        ReceiptPrintService.addColumn(currencyAmountBuilder, sep);
        ReceiptPrintService.endRow(currencyAmountBuilder);
        int rowCount = 0;
        List<Currency> allCurrency = CurrencyUtil.getAllCurrency();
        if (allCurrency != null) {
            for (Currency currency : allCurrency) {
                if (currency == null) continue;
                String key = currency.getName();
                String paidAmount = ticket.getProperty(key);
                String cashBackAmount = ticket.getProperty(key + "_CASH_BACK");
                if (paidAmount == null) {
                    paidAmount = "0";
                }
                if (cashBackAmount == null) {
                    cashBackAmount = "0";
                }
                Double paid = Double.valueOf(paidAmount);
                Double changeDue = Double.valueOf(cashBackAmount);
                if (paid == 0.0 && changeDue == 0.0) continue;
                ReceiptPrintService.beginRow(currencyAmountBuilder);
                ReceiptPrintService.addColumn(currencyAmountBuilder, ReceiptPrintService.getHtmlText(key, 10, LEFT));
                ReceiptPrintService.addColumn(currencyAmountBuilder, ReceiptPrintService.getHtmlText(decimalFormat.format(paid), 10, RIGHT));
                ReceiptPrintService.addColumn(currencyAmountBuilder, ReceiptPrintService.getHtmlText(decimalFormat.format(changeDue), 10, RIGHT));
                ReceiptPrintService.endRow(currencyAmountBuilder);
                ++rowCount;
            }
        }
        if (rowCount == 0) {
            return null;
        }
        currencyAmountBuilder.append("</table></html>");
        return currencyAmountBuilder;
    }

    public static String getHtmlText(String txt, int length, String align) {
        block5: {
            block6: {
                block4: {
                    if (txt.length() > 30) {
                        txt = txt.substring(0, 30);
                    }
                    if (!align.equals(CENTER)) break block4;
                    int space = (length - txt.length()) / 2;
                    for (int i = 1; i < space; ++i) {
                        txt = "&nbsp;" + txt + "&nbsp;";
                    }
                    break block5;
                }
                if (!align.equals(RIGHT)) break block6;
                int space = length - txt.length();
                for (int i = 1; i < space; ++i) {
                    txt = "&nbsp;" + txt;
                }
                break block5;
            }
            if (!align.equals(LEFT)) break block5;
            int space = length - txt.length();
            for (int i = 1; i < space; ++i) {
                txt = txt + "&nbsp;";
            }
        }
        return txt;
    }

    public static JasperPrint createKitchenPrint(KitchenTicket ticket) throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(HEADER_LINE1, Application.getInstance().getRestaurant().getName());
        map.put(HEADER_LINE2, Messages.getString("ReceiptPrintService.115"));
        map.put("cardPayment", (String)((Object)Boolean.valueOf(true)));
        map.put(SHOW_HEADER_SEPARATOR, (String)((Object)Boolean.TRUE));
        map.put(SHOW_HEADER_SEPARATOR, (String)((Object)Boolean.TRUE));
        map.put(CHECK_NO, POSConstants.RECEIPT_REPORT_TICKET_NO_LABEL + ticket.getTicketId());
        if (ticket.getTableNumbers() != null && ticket.getTableNumbers().size() > 0) {
            map.put(TABLE_NO, POSConstants.RECEIPT_REPORT_TABLE_NO_LABEL + ticket.getTableNumbers());
        }
        if (StringUtils.isNotEmpty((String)ticket.getCustomerName())) {
            map.put("customer", Messages.getString("ReceiptPrintService.0") + ticket.getCustomerName());
        }
        map.put(SERVER_NAME, POSConstants.RECEIPT_REPORT_SERVER_LABEL + ticket.getServerName());
        map.put(REPORT_DATE, Messages.getString("ReceiptPrintService.119") + reportDateFormat.format(new Date()));
        map.put("ticketHeader", Messages.getString("ReceiptPrintService.10"));
        String ticketType = ticket.getTicketType();
        if (StringUtils.isNotEmpty((String)ticketType)) {
            ticketType = ticketType.replaceAll("_", " ");
        }
        map.put("orderType", "* " + ticketType + " *");
        KitchenTicketDataSource dataSource = new KitchenTicketDataSource(ticket);
        return ReceiptPrintService.createJasperPrint(ReportUtil.getReport("kitchen-receipt"), map, (JRDataSource)new JRTableModelDataSource((TableModel)dataSource));
    }

    public static JasperPrint createKitchenPrint(String virtualPrinterName, KitchenTicket ticket, String deviceName) throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(HEADER_LINE1, Application.getInstance().getRestaurant().getName());
        map.put(HEADER_LINE2, Messages.getString("ReceiptPrintService.115"));
        map.put("cardPayment", (String)((Object)Boolean.valueOf(true)));
        map.put(SHOW_HEADER_SEPARATOR, (String)((Object)Boolean.TRUE));
        map.put(SHOW_HEADER_SEPARATOR, (String)((Object)Boolean.TRUE));
        map.put(CHECK_NO, POSConstants.RECEIPT_REPORT_TICKET_NO_LABEL + ticket.getTicketId() + "-" + ticket.getSequenceNumber());
        if (ticket.getTableNumbers() != null && ticket.getTableNumbers().size() > 0) {
            map.put(TABLE_NO, POSConstants.RECEIPT_REPORT_TABLE_NO_LABEL + ticket.getTableNumbers());
        }
        if (StringUtils.isNotEmpty((String)ticket.getCustomerName())) {
            map.put("customer", Messages.getString("ReceiptPrintService.0") + ticket.getCustomerName());
        }
        map.put(SERVER_NAME, POSConstants.RECEIPT_REPORT_SERVER_LABEL + ticket.getServerName());
        map.put(REPORT_DATE, Messages.getString("ReceiptPrintService.119") + DateUtil.getReportDate());
        map.put("ticketHeader", Messages.getString("ReceiptPrintService.10"));
        String ticketType = ticket.getTicketType();
        if (StringUtils.isNotEmpty((String)ticketType)) {
            ticketType = ticketType.replaceAll("_", " ");
        }
        map.put("orderType", "* " + ticketType + " *");
        map.put(PROP_PRINTER_NAME, "Printer Name : " + virtualPrinterName);
        KitchenTicketDataSource dataSource = new KitchenTicketDataSource(ticket);
        String reportName = "kitchen-receipt";
        if (TerminalConfig.isGroupKitchenReceiptItems()) {
            reportName = "kitchen-receipt-grouped-by-categories";
        }
        return ReceiptPrintService.createJasperPrint(ReportUtil.getReport(reportName), map, (JRDataSource)new JRTableModelDataSource((TableModel)dataSource));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void printToKitchen(Ticket ticket) {
        Transaction transaction = null;
        try (Session session = null;){
            session = KitchenTicketDAO.getInstance().createNewSession();
            transaction = session.beginTransaction();
            List<KitchenTicket> kitchenTickets = KitchenTicket.fromTicket(ticket);
            for (KitchenTicket kitchenTicket : kitchenTickets) {
                Printer printer = kitchenTicket.getPrinter();
                String deviceName = printer.getDeviceName();
                JasperPrint jasperPrint = ReceiptPrintService.createKitchenPrint(printer.getVirtualPrinter().getName(), kitchenTicket, deviceName);
                jasperPrint.setName("FP_KitchenReceipt_" + ticket.getId() + "_" + kitchenTicket.getSequenceNumber());
                jasperPrint.setProperty(PROP_PRINTER_NAME, deviceName);
                ReceiptPrintService.printQuitely(jasperPrint);
                session.saveOrUpdate((Object)kitchenTicket);
            }
            transaction.commit();
            TicketDAO.getInstance().saveOrUpdate(ticket);
        }
    }

    public static void printQuitely(JasperPrint jasperPrint) throws JRException {
        block2: {
            try {
                JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, (Object)jasperPrint);
                exporter.setParameter((JRExporterParameter)JRPrintServiceExporterParameter.PRINT_SERVICE, (Object)PrintServiceUtil.getPrintServiceForPrinter(jasperPrint.getProperty(PROP_PRINTER_NAME)));
                exporter.exportReport();
            }
            catch (Exception x) {
                String msg = "No print selected\n";
                String message = x.getMessage();
                if (message != null && message.contains("PrinterAbortException")) break block2;
                logger.error((Object)(msg + x));
            }
        }
    }

    private static String getCardNumber(BankCardMagneticTrack track) {
        String no = "";
        try {
            if (track.getTrack1().hasPrimaryAccountNumber()) {
                no = track.getTrack1().getPrimaryAccountNumber().getAccountNumber();
                no = "************" + no.substring(12);
            } else if (track.getTrack2().hasPrimaryAccountNumber()) {
                no = track.getTrack2().getPrimaryAccountNumber().getAccountNumber();
                no = "************" + no.substring(12);
            }
        }
        catch (Exception e) {
            logger.error((Object)e);
        }
        return no;
    }
}

