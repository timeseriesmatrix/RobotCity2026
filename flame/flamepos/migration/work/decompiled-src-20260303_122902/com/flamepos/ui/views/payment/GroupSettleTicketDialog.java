/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang.SerializationUtils
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.PosLog;
import com.floreantpos.config.CardConfig;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.extension.InginicoPlugin;
import com.floreantpos.extension.PaymentGatewayPlugin;
import com.floreantpos.main.Application;
import com.floreantpos.model.CardReader;
import com.floreantpos.model.CashTransaction;
import com.floreantpos.model.GiftCertificateTransaction;
import com.floreantpos.model.Gratuity;
import com.floreantpos.model.PaymentType;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.Ticket;
import com.floreantpos.report.ReceiptPrintService;
import com.floreantpos.services.PosTransactionService;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.TransactionCompletionDialog;
import com.floreantpos.ui.views.TicketDetailView;
import com.floreantpos.ui.views.order.OrderController;
import com.floreantpos.ui.views.payment.AuthorizationCodeDialog;
import com.floreantpos.ui.views.payment.CardInputListener;
import com.floreantpos.ui.views.payment.CardInputProcessor;
import com.floreantpos.ui.views.payment.CardProcessor;
import com.floreantpos.ui.views.payment.ConfirmPayDialog;
import com.floreantpos.ui.views.payment.CustomPaymentSelectionDialog;
import com.floreantpos.ui.views.payment.GiftCertDialog;
import com.floreantpos.ui.views.payment.GratuityInputDialog;
import com.floreantpos.ui.views.payment.GroupPaymentView;
import com.floreantpos.ui.views.payment.ManualCardEntryDialog;
import com.floreantpos.ui.views.payment.PaymentProcessWaitDialog;
import com.floreantpos.ui.views.payment.PosPaymentWaitDialog;
import com.floreantpos.ui.views.payment.SwipeCardDialog;
import com.floreantpos.util.CurrencyUtil;
import com.floreantpos.util.DrawerUtil;
import com.floreantpos.util.GlobalIdGenerator;
import com.floreantpos.util.NumberUtil;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;

public class GroupSettleTicketDialog
extends POSDialog
implements CardInputListener {
    public static final String LOYALTY_DISCOUNT_PERCENTAGE = "loyalty_discount_percentage";
    public static final String LOYALTY_POINT = "loyalty_point";
    public static final String LOYALTY_COUPON = "loyalty_coupon";
    public static final String LOYALTY_DISCOUNT = "loyalty_discount";
    public static final String LOYALTY_ID = "loyalty_id";
    public static final String VIEW_NAME = "PAYMENT_VIEW";
    private GroupPaymentView paymentView;
    private List<Ticket> tickets;
    private TicketDetailView ticketDetailView;
    private JScrollPane ticketScrollPane;
    private Ticket ticket;
    private double totalTenderAmount;
    private PaymentType paymentType;
    private String cardName;
    private JTextField tfSubtotal;
    private JTextField tfDiscount;
    private JTextField tfDeliveryCharge;
    private JTextField tfTax;
    private JTextField tfTotal;
    private JTextField tfGratuity;
    private String ticketNumbers = "";
    private List<Integer> tableNumbers = new ArrayList<Integer>();
    private String customerName;
    private double totalDueAmount;
    private JLabel lblCustomer;
    private JLabel lblTable;
    private JLabel labelTicketNumber;
    private JLabel labelTableNumber;
    private JLabel labelCustomer;
    public static PosPaymentWaitDialog waitDialog = new PosPaymentWaitDialog();

    public GroupSettleTicketDialog(List<Ticket> tickets) {
        this.tickets = tickets;
        for (Ticket ticket : tickets) {
            if (!ticket.getOrderType().isConsolidateItemsInReceipt().booleanValue()) continue;
            ticket.consolidateTicketItems();
        }
        this.setTitle(Messages.getString("SettleTicketDialog.6"));
        this.getContentPane().setLayout(new BorderLayout());
        this.ticketDetailView = new TicketDetailView();
        this.ticketScrollPane = new PosScrollPane(this.ticketDetailView);
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 0));
        centerPanel.add((Component)this.createTicketInfoPanel(), "North");
        centerPanel.add((Component)this.ticketScrollPane, "Center");
        centerPanel.add((Component)this.createTotalViewerPanel(), "South");
        this.paymentView = new GroupPaymentView(this);
        this.paymentView.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.getContentPane().add((Component)centerPanel, "Center");
        this.getContentPane().add((Component)this.paymentView, "East");
        this.setSize(Application.getPosWindow().getSize());
        this.updateView();
        this.paymentView.updateView();
        this.paymentView.setDefaultFocus();
    }

    public void updateView() {
        if (this.tickets == null && !this.tickets.isEmpty()) {
            this.tfSubtotal.setText("");
            this.tfDiscount.setText("");
            this.tfDeliveryCharge.setText("");
            this.tfTax.setText("");
            this.tfTotal.setText("");
            this.tfGratuity.setText("");
            return;
        }
        double subtotalAmount = 0.0;
        double discountAmount = 0.0;
        double deliveryCharge = 0.0;
        double taxAmount = 0.0;
        double gratuityAmount = 0.0;
        double totalAmount = 0.0;
        for (Ticket ticket : this.tickets) {
            subtotalAmount += ticket.getSubtotalAmount().doubleValue();
            discountAmount += ticket.getDiscountAmount().doubleValue();
            deliveryCharge += ticket.getDeliveryCharge().doubleValue();
            taxAmount += ticket.getTaxAmount().doubleValue();
            if (ticket.getGratuity() != null) {
                gratuityAmount = ticket.getGratuity().getAmount();
            }
            totalAmount += ticket.getTotalAmount().doubleValue();
            this.totalDueAmount += ticket.getDueAmount().doubleValue();
            this.ticketNumbers = this.ticketNumbers + "[" + ticket.getId().toString() + "], ";
            for (Integer tableNumber : ticket.getTableNumbers()) {
                if (this.tableNumbers.contains(tableNumber)) continue;
                this.tableNumbers.add(tableNumber);
            }
            this.customerName = ticket.getProperty("CUSTOMER_NAME");
        }
        this.tfSubtotal.setText(NumberUtil.formatNumber(subtotalAmount));
        this.tfDiscount.setText(NumberUtil.formatNumber(discountAmount));
        this.tfDeliveryCharge.setText(NumberUtil.formatNumber(deliveryCharge));
        if (Application.getInstance().isPriceIncludesTax()) {
            this.tfTax.setText(Messages.getString("TicketView.35"));
        } else {
            this.tfTax.setText(NumberUtil.formatNumber(taxAmount));
        }
        if (gratuityAmount > 0.0) {
            this.tfGratuity.setText(NumberUtil.formatNumber(gratuityAmount));
        } else {
            this.tfGratuity.setText("0.00");
        }
        this.tfTotal.setText(NumberUtil.formatNumber(totalAmount));
        this.labelTicketNumber.setText(this.ticketNumbers.substring(0, this.ticketNumbers.length() - 2));
        this.labelTableNumber.setText(this.tableNumbers.toString());
        if (this.tableNumbers.isEmpty()) {
            this.labelTableNumber.setVisible(false);
            this.lblTable.setVisible(false);
        }
        this.labelCustomer.setText(this.customerName);
        if (this.customerName == null) {
            this.labelCustomer.setVisible(false);
            this.lblCustomer.setVisible(false);
        }
        this.ticketDetailView.setTickets(this.tickets);
    }

    private JPanel createTicketInfoPanel() {
        JLabel lblTicket = new JLabel();
        lblTicket.setText(Messages.getString("SettleTicketDialog.0"));
        this.labelTicketNumber = new JLabel();
        this.lblTable = new JLabel();
        this.lblTable.setText(POSConstants.TABLES);
        this.labelTableNumber = new JLabel();
        this.lblCustomer = new JLabel();
        this.lblCustomer.setText("Customer:");
        this.labelCustomer = new JLabel();
        TransparentPanel ticketInfoPanel = new TransparentPanel((LayoutManager)new MigLayout("wrap 2,fill, hidemode 3", "[][grow]", ""));
        ticketInfoPanel.add(lblTicket);
        ticketInfoPanel.add((Component)this.labelTicketNumber, "grow");
        ticketInfoPanel.add(this.lblTable);
        ticketInfoPanel.add((Component)this.labelTableNumber, "grow");
        ticketInfoPanel.add(this.lblCustomer);
        ticketInfoPanel.add((Component)this.labelCustomer, "grow");
        return ticketInfoPanel;
    }

    private JPanel createTotalViewerPanel() {
        JLabel lblSubtotal = new JLabel();
        lblSubtotal.setHorizontalAlignment(4);
        lblSubtotal.setText(POSConstants.SUBTOTAL + ":" + " " + CurrencyUtil.getCurrencySymbol());
        this.tfSubtotal = new JTextField(10);
        this.tfSubtotal.setHorizontalAlignment(11);
        this.tfSubtotal.setEditable(false);
        JLabel lblDiscount = new JLabel();
        lblDiscount.setHorizontalAlignment(4);
        lblDiscount.setText(Messages.getString("TicketView.9") + " " + CurrencyUtil.getCurrencySymbol());
        this.tfDiscount = new JTextField(10);
        this.tfDiscount.setHorizontalAlignment(11);
        this.tfDiscount.setEditable(false);
        JLabel lblDeliveryCharge = new JLabel();
        lblDeliveryCharge.setHorizontalAlignment(4);
        lblDeliveryCharge.setText("Delivery Charge: " + CurrencyUtil.getCurrencySymbol());
        this.tfDeliveryCharge = new JTextField(10);
        this.tfDeliveryCharge.setHorizontalAlignment(11);
        this.tfDeliveryCharge.setEditable(false);
        JLabel lblTax = new JLabel();
        lblTax.setHorizontalAlignment(4);
        lblTax.setText(POSConstants.TAX + ":" + " " + CurrencyUtil.getCurrencySymbol());
        this.tfTax = new JTextField();
        this.tfTax.setEditable(false);
        this.tfTax.setHorizontalAlignment(11);
        JLabel lblGratuity = new JLabel();
        lblGratuity.setHorizontalAlignment(4);
        lblGratuity.setText(Messages.getString("SettleTicketDialog.5") + ":" + " " + CurrencyUtil.getCurrencySymbol());
        this.tfGratuity = new JTextField();
        this.tfGratuity.setEditable(false);
        this.tfGratuity.setHorizontalAlignment(11);
        JLabel lblTotal = new JLabel();
        lblTotal.setFont(lblTotal.getFont().deriveFont(1, 18.0f));
        lblTotal.setHorizontalAlignment(4);
        lblTotal.setText(POSConstants.TOTAL + ":" + " " + CurrencyUtil.getCurrencySymbol());
        this.tfTotal = new JTextField(10);
        this.tfTotal.setFont(this.tfTotal.getFont().deriveFont(1, 18.0f));
        this.tfTotal.setHorizontalAlignment(11);
        this.tfTotal.setEditable(false);
        TransparentPanel ticketAmountPanel = new TransparentPanel((LayoutManager)new MigLayout("hidemode 3,ins 2 2 3 2,alignx trailing,fill", "[grow][]", ""));
        ticketAmountPanel.add((Component)lblSubtotal, "growx,aligny center");
        ticketAmountPanel.add((Component)this.tfSubtotal, "growx,aligny center");
        ticketAmountPanel.add((Component)lblDiscount, "newline,growx,aligny center");
        ticketAmountPanel.add((Component)this.tfDiscount, "growx,aligny center");
        ticketAmountPanel.add((Component)lblTax, "newline,growx,aligny center");
        ticketAmountPanel.add((Component)this.tfTax, "growx,aligny center");
        ticketAmountPanel.add((Component)lblDeliveryCharge, "newline,growx,aligny center");
        ticketAmountPanel.add((Component)this.tfDeliveryCharge, "growx,aligny center");
        ticketAmountPanel.add((Component)lblGratuity, "newline,growx,aligny center");
        ticketAmountPanel.add((Component)this.tfGratuity, "growx,aligny center");
        ticketAmountPanel.add((Component)lblTotal, "newline,growx,aligny center");
        ticketAmountPanel.add((Component)this.tfTotal, "growx,aligny center");
        return ticketAmountPanel;
    }

    public void doSetGratuity() {
        if (this.ticket == null) {
            return;
        }
        GratuityInputDialog d = new GratuityInputDialog();
        d.pack();
        d.setResizable(false);
        d.open();
        if (d.isCanceled()) {
            return;
        }
        double gratuityAmount = d.getGratuityAmount();
        Gratuity gratuity = this.ticket.createGratuity();
        gratuity.setAmount(gratuityAmount);
        this.ticket.setGratuity(gratuity);
        this.ticket.calculatePrice();
        OrderController.saveOrder(this.ticket);
        this.paymentView.updateView();
        this.updateView();
    }

    public void doGroupSettle(PaymentType paymentType) {
        try {
            if (this.tickets == null) {
                return;
            }
            this.paymentType = paymentType;
            this.totalTenderAmount = this.paymentView.getTenderedAmount();
            this.totalDueAmount = NumberUtil.roundToTwoDigit(this.totalDueAmount);
            if (this.totalTenderAmount < this.totalDueAmount) {
                POSMessageDialog.showMessage("Partial payment not allowed.");
                return;
            }
            this.cardName = paymentType.getDisplayString();
            PosTransaction transaction = null;
            switch (paymentType) {
                case CASH: {
                    if (!this.confirmPayment()) {
                        return;
                    }
                    transaction = paymentType.createTransaction();
                    transaction.setCaptured(true);
                    this.settleTicket(transaction);
                    break;
                }
                case CUSTOM_PAYMENT: {
                    CustomPaymentSelectionDialog customPaymentDialog = new CustomPaymentSelectionDialog();
                    customPaymentDialog.setTitle(Messages.getString("SettleTicketDialog.8"));
                    customPaymentDialog.pack();
                    customPaymentDialog.open();
                    if (customPaymentDialog.isCanceled()) {
                        return;
                    }
                    if (!this.confirmPayment()) {
                        return;
                    }
                    transaction = paymentType.createTransaction();
                    transaction.setCustomPaymentFieldName(customPaymentDialog.getPaymentFieldName());
                    transaction.setCustomPaymentName(customPaymentDialog.getPaymentName());
                    transaction.setCustomPaymentRef(customPaymentDialog.getPaymentRef());
                    transaction.setCaptured(true);
                    this.settleTicket(transaction);
                    break;
                }
                case CREDIT_CARD: 
                case CREDIT_VISA: 
                case CREDIT_MASTER_CARD: 
                case CREDIT_AMEX: 
                case CREDIT_DISCOVERY: {
                    this.payUsingCard(this.cardName, this.totalTenderAmount);
                    break;
                }
                case DEBIT_VISA: 
                case DEBIT_MASTER_CARD: {
                    this.payUsingCard(this.cardName, this.totalTenderAmount);
                    break;
                }
                case GIFT_CERTIFICATE: {
                    GiftCertDialog giftCertDialog = new GiftCertDialog(this);
                    giftCertDialog.pack();
                    giftCertDialog.open();
                    if (giftCertDialog.isCanceled()) {
                        return;
                    }
                    transaction = new GiftCertificateTransaction();
                    transaction.setPaymentType(PaymentType.GIFT_CERTIFICATE.name());
                    transaction.setCaptured(true);
                    double giftCertFaceValue = giftCertDialog.getGiftCertFaceValue();
                    double giftCertCashBackAmount = 0.0;
                    transaction.setTenderAmount(giftCertFaceValue);
                    if (giftCertFaceValue >= this.totalDueAmount) {
                        transaction.setAmount(this.totalDueAmount);
                        giftCertCashBackAmount = giftCertFaceValue - this.totalDueAmount;
                    } else {
                        transaction.setAmount(giftCertFaceValue);
                    }
                    transaction.setGiftCertNumber(giftCertDialog.getGiftCertNumber());
                    transaction.setGiftCertFaceValue(giftCertFaceValue);
                    transaction.setGiftCertPaidAmount(transaction.getAmount());
                    transaction.setGiftCertCashBackAmount(giftCertCashBackAmount);
                    this.settleTicket(transaction);
                    break;
                }
            }
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
    }

    private boolean confirmPayment() {
        if (!TerminalConfig.isUseSettlementPrompt()) {
            return true;
        }
        ConfirmPayDialog confirmPayDialog = new ConfirmPayDialog();
        confirmPayDialog.setAmount(this.totalTenderAmount);
        confirmPayDialog.open();
        return !confirmPayDialog.isCanceled();
    }

    public void settleTicket(PosTransaction posTransaction) {
        try {
            ArrayList<PosTransaction> transactionList = new ArrayList<PosTransaction>();
            this.totalTenderAmount = this.paymentView.getTenderedAmount();
            for (Ticket ticket : this.tickets) {
                PosTransaction transaction = null;
                if (this.totalTenderAmount <= 0.0) break;
                transaction = (PosTransaction)SerializationUtils.clone((Serializable)posTransaction);
                transaction.setGlobalId(GlobalIdGenerator.generate());
                transaction.setTicket(ticket);
                this.setTransactionAmounts(transaction);
                this.confirmLoyaltyDiscount(ticket);
                PosTransactionService transactionService = PosTransactionService.getInstance();
                transactionService.settleTicket(ticket, transaction);
                transactionList.add(transaction);
                GroupSettleTicketDialog.printTicket(ticket, transaction);
            }
            this.showTransactionCompleteMsg(this.totalDueAmount, this.totalTenderAmount, this.tickets, transactionList);
            this.setCanceled(false);
            this.dispose();
        }
        catch (UnknownHostException e) {
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("SettleTicketDialog.12"));
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
        }
    }

    public void showTransactionCompleteMsg(double dueAmount, double tenderedAmount, List<Ticket> ticket, List<PosTransaction> transactions) {
        TransactionCompletionDialog dialog = new TransactionCompletionDialog(transactions);
        double paidAmount = 0.0;
        double ticketsDueAmount = 0.0;
        for (PosTransaction transaction : transactions) {
            paidAmount += transaction.getAmount().doubleValue();
            dialog.setCard(transaction.isCard());
        }
        dialog.setTenderedAmount(tenderedAmount);
        dialog.setTotalAmount(dueAmount);
        dialog.setPaidAmount(paidAmount);
        for (Ticket tTicket : this.tickets) {
            ticketsDueAmount += tTicket.getDueAmount().doubleValue();
        }
        dialog.setDueAmount(ticketsDueAmount);
        if (tenderedAmount > paidAmount) {
            dialog.setChangeAmount(tenderedAmount - paidAmount);
        } else {
            dialog.setChangeAmount(0.0);
        }
        dialog.updateView();
        dialog.pack();
        dialog.open();
    }

    public void confirmLoyaltyDiscount(Ticket ticket) throws IOException, MalformedURLException {
        try {
            if (ticket.hasProperty(LOYALTY_ID)) {
                String url = this.buildLoyaltyApiURL(ticket, ticket.getProperty(LOYALTY_ID));
                url = url + "&paid=1";
                IOUtils.toString((InputStream)new URL(url).openStream());
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }

    public static void printTicket(Ticket ticket, PosTransaction transaction) {
        try {
            if (ticket.needsKitchenPrint()) {
                ReceiptPrintService.printToKitchen(ticket);
            }
            ReceiptPrintService.printTransaction(transaction);
            if (transaction instanceof CashTransaction) {
                DrawerUtil.kickDrawer();
            }
        }
        catch (Exception ee) {
            POSMessageDialog.showError(Application.getPosWindow(), POSConstants.PRINT_ERROR, ee);
        }
    }

    private void payUsingCard(String cardName, double tenderedAmount) throws Exception {
        try {
            PaymentGatewayPlugin paymentGateway = CardConfig.getPaymentGateway();
            if (paymentGateway instanceof InginicoPlugin) {
                waitDialog.setVisible(true);
                if (!waitDialog.isCanceled()) {
                    this.dispose();
                }
                return;
            }
            if (!paymentGateway.shouldShowCardInputProcessor()) {
                PosTransaction transaction = this.paymentType.createTransaction();
                if (!this.confirmPayment()) {
                    return;
                }
                transaction.setCaptured(false);
                transaction.setCardMerchantGateway(paymentGateway.getProductName());
                if (this.ticket.getOrderType().isPreAuthCreditCard().booleanValue()) {
                    paymentGateway.getProcessor().preAuth(transaction);
                } else {
                    paymentGateway.getProcessor().chargeAmount(transaction);
                }
                this.settleTicket(transaction);
                return;
            }
            CardReader cardReader = CardConfig.getCardReader();
            switch (cardReader) {
                case SWIPE: {
                    SwipeCardDialog swipeCardDialog = new SwipeCardDialog(this);
                    swipeCardDialog.pack();
                    swipeCardDialog.open();
                    break;
                }
                case MANUAL: {
                    ManualCardEntryDialog dialog = new ManualCardEntryDialog(this);
                    dialog.pack();
                    dialog.open();
                    break;
                }
                case EXTERNAL_TERMINAL: {
                    AuthorizationCodeDialog authorizationCodeDialog = new AuthorizationCodeDialog(this);
                    authorizationCodeDialog.pack();
                    authorizationCodeDialog.open();
                    break;
                }
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, e.getMessage(), e);
        }
    }

    @Override
    public void open() {
        super.open();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void cardInputted(CardInputProcessor inputter, PaymentType selectedPaymentType) {
        PaymentProcessWaitDialog waitDialog = new PaymentProcessWaitDialog(this);
        try {
            waitDialog.setVisible(true);
            PosTransaction transaction = this.paymentType.createTransaction();
            PaymentGatewayPlugin paymentGateway = CardConfig.getPaymentGateway();
            CardProcessor cardProcessor = paymentGateway.getProcessor();
            if (inputter instanceof SwipeCardDialog) {
                SwipeCardDialog swipeCardDialog = (SwipeCardDialog)inputter;
                String cardString = swipeCardDialog.getCardString();
                if (StringUtils.isEmpty((String)cardString) || cardString.length() < 16) {
                    throw new RuntimeException(Messages.getString("SettleTicketDialog.16"));
                }
                if (!this.confirmPayment()) {
                    return;
                }
                transaction.setCardType(this.paymentType.getDisplayString());
                transaction.setCardTrack(cardString);
                transaction.setCaptured(false);
                transaction.setCardMerchantGateway(paymentGateway.getProductName());
                transaction.setCardReader(CardReader.SWIPE.name());
                this.settleTicket(cardProcessor, transaction);
            } else if (inputter instanceof ManualCardEntryDialog) {
                ManualCardEntryDialog mDialog = (ManualCardEntryDialog)inputter;
                transaction.setCaptured(false);
                transaction.setCardMerchantGateway(paymentGateway.getProductName());
                transaction.setCardReader(CardReader.MANUAL.name());
                transaction.setCardNumber(mDialog.getCardNumber());
                transaction.setCardExpMonth(mDialog.getExpMonth());
                transaction.setCardExpYear(mDialog.getExpYear());
                this.settleTicket(cardProcessor, transaction);
            } else if (inputter instanceof AuthorizationCodeDialog) {
                PosTransaction selectedTransaction = selectedPaymentType.createTransaction();
                AuthorizationCodeDialog authDialog = (AuthorizationCodeDialog)inputter;
                String authorizationCode = authDialog.getAuthorizationCode();
                if (StringUtils.isEmpty((String)authorizationCode)) {
                    throw new PosException(Messages.getString("SettleTicketDialog.17"));
                }
                selectedTransaction.setCardType(selectedPaymentType.getDisplayString());
                selectedTransaction.setCaptured(false);
                selectedTransaction.setCardReader(CardReader.EXTERNAL_TERMINAL.name());
                selectedTransaction.setCardAuthCode(authorizationCode);
                this.settleTicket(selectedTransaction);
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

    private void settleTicket(CardProcessor cardProcessor, PosTransaction transaction) {
        try {
            ArrayList<PosTransaction> transactionList = new ArrayList<PosTransaction>();
            this.totalTenderAmount = this.paymentView.getTenderedAmount();
            for (Ticket ticket : this.tickets) {
                PosTransaction cardTransaction = new PosTransaction();
                if (this.totalTenderAmount <= 0.0) break;
                cardTransaction = (PosTransaction)SerializationUtils.clone((Serializable)transaction);
                cardTransaction.setId(null);
                cardTransaction.setGlobalId(GlobalIdGenerator.generate());
                cardTransaction.setTicket(ticket);
                this.setTransactionAmounts(cardTransaction);
                if (ticket.getOrderType().isPreAuthCreditCard().booleanValue()) {
                    cardProcessor.preAuth(transaction);
                } else {
                    cardProcessor.chargeAmount(transaction);
                }
                this.confirmLoyaltyDiscount(ticket);
                PosTransactionService transactionService = PosTransactionService.getInstance();
                transactionService.settleTicket(ticket, cardTransaction);
                transactionList.add(cardTransaction);
                GroupSettleTicketDialog.printTicket(ticket, cardTransaction);
            }
            this.showTransactionCompleteMsg(this.totalDueAmount, this.totalTenderAmount, this.tickets, transactionList);
            this.setCanceled(false);
            this.dispose();
        }
        catch (UnknownHostException e) {
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("SettleTicketDialog.12"));
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
        }
    }

    private void setTransactionAmounts(PosTransaction transaction) {
        if (this.tickets.get(this.tickets.size() - 1).getId() == transaction.getTicket().getId()) {
            if (this.totalTenderAmount > this.totalDueAmount) {
                transaction.setTenderAmount(this.totalTenderAmount - this.totalDueAmount + transaction.getTicket().getDueAmount());
                transaction.setAmount(transaction.getTicket().getDueAmount());
            } else {
                transaction.setTenderAmount(transaction.getTicket().getDueAmount());
                transaction.setAmount(transaction.getTicket().getDueAmount());
            }
            String ticketNumbers = "";
            for (Ticket ticket : this.tickets) {
                ticketNumbers = ticketNumbers + "[" + ticket.getId() + "]";
            }
            transaction.getTicket().addProperty("GROUP_SETTLE_TICKETS", "#CHK " + ticketNumbers);
        } else {
            transaction.setTenderAmount(transaction.getTicket().getDueAmount());
            transaction.setAmount(transaction.getTicket().getDueAmount());
        }
    }

    public boolean hasMyKalaId() {
        if (this.ticket == null) {
            return false;
        }
        return this.ticket.hasProperty(LOYALTY_ID);
    }

    public String buildLoyaltyApiURL(Ticket ticket, String loyaltyid) {
        Restaurant restaurant = Application.getInstance().getRestaurant();
        String transactionURL = "http://cloud.floreantpos.org/tri2/kala_api?";
        transactionURL = transactionURL + "kala_id=" + loyaltyid;
        transactionURL = transactionURL + "&store_id=" + restaurant.getUniqueId();
        transactionURL = transactionURL + "&store_name=" + POSUtil.encodeURLString(restaurant.getName());
        transactionURL = transactionURL + "&store_zip=" + restaurant.getZipCode();
        transactionURL = transactionURL + "&terminal=" + ticket.getTerminal().getId();
        transactionURL = transactionURL + "&server=" + POSUtil.encodeURLString(ticket.getOwner().getFirstName() + " " + ticket.getOwner().getLastName());
        transactionURL = transactionURL + "&" + ticket.toURLForm();
        return transactionURL;
    }

    public List<Ticket> getTickets() {
        return this.tickets;
    }

    public double getDueAmount() {
        return this.totalDueAmount;
    }
}

