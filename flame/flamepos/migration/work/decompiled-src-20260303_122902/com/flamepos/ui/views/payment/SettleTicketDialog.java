/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  javax.json.Json
 *  javax.json.JsonArray
 *  javax.json.JsonNumber
 *  javax.json.JsonObject
 *  javax.json.JsonReader
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.io.IOUtils
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
import com.floreantpos.model.TicketDiscount;
import com.floreantpos.model.UserPermission;
import com.floreantpos.report.ReceiptPrintService;
import com.floreantpos.services.PosTransactionService;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.DiscountSelectionDialog;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.TransactionCompletionDialog;
import com.floreantpos.ui.ticket.TicketViewerTable;
import com.floreantpos.ui.views.order.OrderController;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.payment.AuthorizationCodeDialog;
import com.floreantpos.ui.views.payment.CardInputListener;
import com.floreantpos.ui.views.payment.CardInputProcessor;
import com.floreantpos.ui.views.payment.CardProcessor;
import com.floreantpos.ui.views.payment.ConfirmPayDialog;
import com.floreantpos.ui.views.payment.CustomPaymentSelectionDialog;
import com.floreantpos.ui.views.payment.GiftCertDialog;
import com.floreantpos.ui.views.payment.GratuityInputDialog;
import com.floreantpos.ui.views.payment.ManualCardEntryDialog;
import com.floreantpos.ui.views.payment.PaymentProcessWaitDialog;
import com.floreantpos.ui.views.payment.PaymentView;
import com.floreantpos.ui.views.payment.PosPaymentWaitDialog;
import com.floreantpos.ui.views.payment.SwipeCardDialog;
import com.floreantpos.util.CurrencyUtil;
import com.floreantpos.util.DrawerUtil;
import com.floreantpos.util.NumberUtil;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class SettleTicketDialog
extends POSDialog
implements CardInputListener {
    public static final String LOYALTY_DISCOUNT_PERCENTAGE = "loyalty_discount_percentage";
    public static final String LOYALTY_POINT = "loyalty_point";
    public static final String LOYALTY_COUPON = "loyalty_coupon";
    public static final String LOYALTY_DISCOUNT = "loyalty_discount";
    public static final String LOYALTY_ID = "loyalty_id";
    private PaymentView paymentView;
    private TicketViewerTable ticketViewerTable;
    private JScrollPane ticketScrollPane;
    private Ticket ticket;
    private double tenderAmount;
    private PaymentType paymentType;
    private String cardName;
    private JTextField tfSubtotal;
    private JTextField tfDiscount;
    private JTextField tfDeliveryCharge;
    private JTextField tfTax;
    private JTextField tfTotal;
    private JTextField tfGratuity;
    public static PosPaymentWaitDialog waitDialog = new PosPaymentWaitDialog();

    public SettleTicketDialog() {
    }

    public SettleTicketDialog(Ticket ticket) {
        this.ticket = ticket;
        if (ticket.getOrderType().isConsolidateItemsInReceipt().booleanValue()) {
            ticket.consolidateTicketItems();
        }
        this.setTitle(Messages.getString("SettleTicketDialog.6"));
        this.getContentPane().setLayout(new BorderLayout());
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 0));
        this.ticketViewerTable = new TicketViewerTable(ticket);
        this.ticketScrollPane = new PosScrollPane(this.ticketViewerTable);
        centerPanel.add((Component)this.createTicketInfoPanel(), "North");
        centerPanel.add((Component)this.ticketScrollPane, "Center");
        centerPanel.add((Component)this.createTotalViewerPanel(), "South");
        this.paymentView = new PaymentView(this);
        this.paymentView.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.getContentPane().add((Component)centerPanel, "Center");
        this.getContentPane().add((Component)this.paymentView, "East");
        this.paymentView.updateView();
        this.paymentView.setDefaultFocus();
        this.updateView();
    }

    public void updateView() {
        if (this.ticket == null) {
            this.tfSubtotal.setText("");
            this.tfDiscount.setText("");
            this.tfDeliveryCharge.setText("");
            this.tfTax.setText("");
            this.tfTotal.setText("");
            this.tfGratuity.setText("");
            return;
        }
        this.tfSubtotal.setText(NumberUtil.formatNumber(this.ticket.getSubtotalAmount()));
        this.tfDiscount.setText(NumberUtil.formatNumber(this.ticket.getDiscountAmount()));
        this.tfDeliveryCharge.setText(NumberUtil.formatNumber(this.ticket.getDeliveryCharge()));
        if (Application.getInstance().isPriceIncludesTax()) {
            this.tfTax.setText(Messages.getString("TicketView.35"));
        } else {
            this.tfTax.setText(NumberUtil.formatNumber(this.ticket.getTaxAmount()));
        }
        if (this.ticket.getGratuity() != null) {
            this.tfGratuity.setText(NumberUtil.formatNumber(this.ticket.getGratuity().getAmount()));
        } else {
            this.tfGratuity.setText("0.00");
        }
        this.tfTotal.setText(NumberUtil.formatNumber(this.ticket.getTotalAmount()));
    }

    private JPanel createTicketInfoPanel() {
        JLabel lblTicket = new JLabel();
        lblTicket.setText(Messages.getString("SettleTicketDialog.0"));
        JLabel labelTicketNumber = new JLabel();
        labelTicketNumber.setText("[" + String.valueOf(this.ticket.getId()) + "]");
        JLabel lblTable = new JLabel();
        lblTable.setText(", " + Messages.getString("SettleTicketDialog.3"));
        JLabel labelTableNumber = new JLabel();
        labelTableNumber.setText("[" + this.getTableNumbers(this.ticket.getTableNumbers()) + "]");
        if (this.ticket.getTableNumbers().isEmpty()) {
            labelTableNumber.setVisible(false);
            lblTable.setVisible(false);
        }
        JLabel lblCustomer = new JLabel();
        lblCustomer.setText(", " + Messages.getString("SettleTicketDialog.10") + ": ");
        JLabel labelCustomer = new JLabel();
        labelCustomer.setText(this.ticket.getProperty("CUSTOMER_NAME"));
        if (this.ticket.getProperty("CUSTOMER_NAME") == null) {
            labelCustomer.setVisible(false);
            lblCustomer.setVisible(false);
        }
        TransparentPanel ticketInfoPanel = new TransparentPanel((LayoutManager)new MigLayout("hidemode 3,insets 0", "[]0[]0[]0[]0[]0[]", "[]"));
        ticketInfoPanel.add(lblTicket);
        ticketInfoPanel.add(labelTicketNumber);
        ticketInfoPanel.add(lblTable);
        ticketInfoPanel.add(labelTableNumber);
        ticketInfoPanel.add(lblCustomer);
        ticketInfoPanel.add(labelCustomer);
        return ticketInfoPanel;
    }

    private String getTableNumbers(List<Integer> numbers) {
        String tableNumbers = "";
        Iterator<Integer> iterator = numbers.iterator();
        while (iterator.hasNext()) {
            Integer n = iterator.next();
            tableNumbers = tableNumbers + n;
            if (!iterator.hasNext()) continue;
            tableNumbers = tableNumbers + ", ";
        }
        return tableNumbers;
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
        this.tfDiscount.setText(this.ticket.getDiscountAmount().toString());
        JLabel lblDeliveryCharge = new JLabel();
        lblDeliveryCharge.setHorizontalAlignment(4);
        lblDeliveryCharge.setText("Delivery Charge: " + CurrencyUtil.getCurrencySymbol());
        this.tfDeliveryCharge = new JTextField(10);
        this.tfDeliveryCharge.setHorizontalAlignment(11);
        this.tfDeliveryCharge.setEditable(false);
        JLabel lblTax = new JLabel();
        lblTax.setHorizontalAlignment(4);
        lblTax.setText(POSConstants.TAX + ":" + " " + CurrencyUtil.getCurrencySymbol());
        this.tfTax = new JTextField(10);
        this.tfTax.setEditable(false);
        this.tfTax.setHorizontalAlignment(11);
        JLabel lblGratuity = new JLabel();
        lblGratuity.setHorizontalAlignment(4);
        lblGratuity.setText(Messages.getString("SettleTicketDialog.5") + ":" + " " + CurrencyUtil.getCurrencySymbol());
        this.tfGratuity = new JTextField(10);
        this.tfGratuity.setEditable(false);
        this.tfGratuity.setHorizontalAlignment(11);
        JLabel lblTotal = new JLabel();
        lblTotal.setFont(lblTotal.getFont().deriveFont(1, PosUIManager.getFontSize(18)));
        lblTotal.setHorizontalAlignment(4);
        lblTotal.setText(POSConstants.TOTAL + ":" + " " + CurrencyUtil.getCurrencySymbol());
        this.tfTotal = new JTextField(10);
        this.tfTotal.setFont(this.tfTotal.getFont().deriveFont(1, PosUIManager.getFontSize(18)));
        this.tfTotal.setHorizontalAlignment(11);
        this.tfTotal.setEditable(false);
        TransparentPanel ticketAmountPanel = new TransparentPanel((LayoutManager)new MigLayout("hidemode 3,ins 2 2 3 2,alignx trailing,fill", "[grow]2[]", ""));
        ticketAmountPanel.add((Component)lblSubtotal, "growx,aligny center");
        ticketAmountPanel.add((Component)this.tfSubtotal, "growx,aligny center");
        ticketAmountPanel.add((Component)lblDiscount, "newline,growx,aligny center");
        ticketAmountPanel.add((Component)this.tfDiscount, "growx,aligny center");
        ticketAmountPanel.add((Component)lblTax, "newline,growx,aligny center");
        ticketAmountPanel.add((Component)this.tfTax, "growx,aligny center");
        if (this.ticket.getOrderType().isDelivery().booleanValue() && !this.ticket.isCustomerWillPickup().booleanValue()) {
            ticketAmountPanel.add((Component)lblDeliveryCharge, "newline,growx,aligny center");
            ticketAmountPanel.add((Component)this.tfDeliveryCharge, "growx,aligny center");
        }
        ticketAmountPanel.add((Component)lblGratuity, "newline,growx,aligny center");
        ticketAmountPanel.add((Component)this.tfGratuity, "growx,aligny center");
        ticketAmountPanel.add((Component)lblTotal, "newline,growx,aligny center");
        ticketAmountPanel.add((Component)this.tfTotal, "growx,aligny center");
        return ticketAmountPanel;
    }

    private void updateModel() {
        if (this.ticket == null) {
            return;
        }
        this.ticket.calculatePrice();
    }

    public void doApplyCoupon() {
        try {
            if (this.ticket == null) {
                return;
            }
            if (!Application.getCurrentUser().hasPermission(UserPermission.ADD_DISCOUNT)) {
                POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("SettleTicketDialog.7"));
                return;
            }
            DiscountSelectionDialog dialog = new DiscountSelectionDialog(this.ticket);
            dialog.open();
            if (dialog.isCanceled()) {
                return;
            }
            this.updateModel();
            this.ticketViewerTable.repaint();
            this.ticketViewerTable.updateView();
            this.updateView();
            OrderController.saveOrder(this.ticket);
            this.paymentView.updateView();
            OrderView.getInstance().setCurrentTicket(this.ticket);
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
        }
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

    public void doSettle(PaymentType paymentType) {
        try {
            if (this.ticket == null) {
                return;
            }
            this.paymentType = paymentType;
            this.tenderAmount = this.paymentView.getTenderedAmount();
            this.cardName = paymentType.getDisplayString();
            PosTransaction transaction = null;
            switch (paymentType) {
                case CASH: {
                    if (!this.confirmPayment()) {
                        return;
                    }
                    transaction = paymentType.createTransaction();
                    transaction.setTicket(this.ticket);
                    transaction.setCaptured(true);
                    this.setTransactionAmounts(transaction);
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
                    transaction.setTicket(this.ticket);
                    transaction.setCaptured(true);
                    this.setTransactionAmounts(transaction);
                    this.settleTicket(transaction);
                    break;
                }
                case CREDIT_CARD: 
                case CREDIT_VISA: 
                case CREDIT_MASTER_CARD: 
                case CREDIT_AMEX: 
                case CREDIT_DISCOVERY: {
                    this.payUsingCard(this.cardName, this.tenderAmount);
                    break;
                }
                case DEBIT_VISA: 
                case DEBIT_MASTER_CARD: {
                    this.payUsingCard(this.cardName, this.tenderAmount);
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
                    transaction.setTicket(this.ticket);
                    transaction.setCaptured(true);
                    this.setTransactionAmounts(transaction);
                    double giftCertFaceValue = giftCertDialog.getGiftCertFaceValue();
                    double giftCertCashBackAmount = 0.0;
                    transaction.setTenderAmount(giftCertFaceValue);
                    if (giftCertFaceValue >= this.ticket.getDueAmount()) {
                        transaction.setAmount(this.ticket.getDueAmount());
                        giftCertCashBackAmount = giftCertFaceValue - this.ticket.getDueAmount();
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
        confirmPayDialog.setAmount(this.tenderAmount);
        confirmPayDialog.open();
        return !confirmPayDialog.isCanceled();
    }

    public void doSettleBarTabTicket(Ticket ticket) {
        try {
            String msg = "Do you want to settle ticket?";
            int option1 = POSMessageDialog.showYesNoQuestionDialog(null, msg, Messages.getString("NewBarTabAction.4"));
            if (option1 != 0) {
                return;
            }
            for (PosTransaction barTabTransaction : ticket.getTransactions()) {
                barTabTransaction.setAmount(ticket.getDueAmount());
                barTabTransaction.setTenderAmount(ticket.getDueAmount());
                barTabTransaction.setAuthorizable(true);
                this.settleTicket(barTabTransaction);
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
        }
    }

    public void settleTicket(PosTransaction transaction) {
        try {
            double dueAmount = this.ticket.getDueAmount();
            this.confirmLoyaltyDiscount(this.ticket);
            PosTransactionService transactionService = PosTransactionService.getInstance();
            transactionService.settleTicket(this.ticket, transaction);
            SettleTicketDialog.printTicket(this.ticket, transaction);
            SettleTicketDialog.showTransactionCompleteMsg(dueAmount, transaction.getTenderAmount(), this.ticket, transaction);
            if (this.ticket.getDueAmount() > 0.0) {
                int option = JOptionPane.showConfirmDialog(Application.getPosWindow(), POSConstants.CONFIRM_PARTIAL_PAYMENT, POSConstants.MDS_POS, 0);
                if (option != 0) {
                    this.setCanceled(false);
                    this.dispose();
                }
                this.setTicket(this.ticket);
            } else {
                this.setCanceled(false);
                this.dispose();
            }
        }
        catch (UnknownHostException e) {
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("SettleTicketDialog.12"));
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
        }
    }

    public static void showTransactionCompleteMsg(double dueAmount, double tenderedAmount, Ticket ticket, PosTransaction transaction) {
        TransactionCompletionDialog dialog = new TransactionCompletionDialog(transaction);
        dialog.setCompletedTransaction(transaction);
        dialog.setTenderedAmount(tenderedAmount);
        dialog.setTotalAmount(dueAmount);
        dialog.setPaidAmount(transaction.getAmount());
        dialog.setDueAmount(ticket.getDueAmount());
        if (tenderedAmount > transaction.getAmount()) {
            dialog.setChangeAmount(tenderedAmount - transaction.getAmount());
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
            if (ticket.getOrderType().isShouldPrintToKitchen().booleanValue() && ticket.needsKitchenPrint()) {
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
                transaction.setTicket(this.ticket);
                if (!this.confirmPayment()) {
                    return;
                }
                transaction.setCaptured(false);
                transaction.setCardMerchantGateway(paymentGateway.getProductName());
                this.setTransactionAmounts(transaction);
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
            transaction.setTicket(this.ticket);
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
                this.setTransactionAmounts(transaction);
                if (this.ticket.getOrderType().isPreAuthCreditCard().booleanValue()) {
                    cardProcessor.preAuth(transaction);
                } else {
                    cardProcessor.chargeAmount(transaction);
                }
                this.settleTicket(transaction);
            } else if (inputter instanceof ManualCardEntryDialog) {
                ManualCardEntryDialog mDialog = (ManualCardEntryDialog)inputter;
                transaction.setCaptured(false);
                transaction.setCardMerchantGateway(paymentGateway.getProductName());
                transaction.setCardReader(CardReader.MANUAL.name());
                transaction.setCardNumber(mDialog.getCardNumber());
                transaction.setCardExpMonth(mDialog.getExpMonth());
                transaction.setCardExpYear(mDialog.getExpYear());
                this.setTransactionAmounts(transaction);
                if (this.ticket.getOrderType().isPreAuthCreditCard().booleanValue()) {
                    cardProcessor.preAuth(transaction);
                } else {
                    cardProcessor.chargeAmount(transaction);
                }
                this.settleTicket(transaction);
            } else if (inputter instanceof AuthorizationCodeDialog) {
                PosTransaction selectedTransaction = selectedPaymentType.createTransaction();
                selectedTransaction.setTicket(this.ticket);
                AuthorizationCodeDialog authDialog = (AuthorizationCodeDialog)inputter;
                String authorizationCode = authDialog.getAuthorizationCode();
                if (StringUtils.isEmpty((String)authorizationCode)) {
                    throw new PosException(Messages.getString("SettleTicketDialog.17"));
                }
                selectedTransaction.setCardType(selectedPaymentType.getDisplayString());
                selectedTransaction.setCaptured(true);
                selectedTransaction.setCardReader(CardReader.EXTERNAL_TERMINAL.name());
                selectedTransaction.setCardAuthCode(authorizationCode);
                this.setTransactionAmounts(selectedTransaction);
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

    private void setTransactionAmounts(PosTransaction transaction) {
        transaction.setTenderAmount(this.tenderAmount);
        if (this.tenderAmount >= this.ticket.getDueAmount()) {
            transaction.setAmount(this.ticket.getDueAmount());
        } else {
            transaction.setAmount(this.tenderAmount);
        }
    }

    public boolean hasMyKalaId() {
        if (this.ticket == null) {
            return false;
        }
        return this.ticket.hasProperty(LOYALTY_ID);
    }

    public void submitMyKalaDiscount() {
        if (this.ticket.hasProperty(LOYALTY_ID)) {
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("SettleTicketDialog.18"));
            return;
        }
        try {
            String loyaltyid = JOptionPane.showInputDialog(Messages.getString("SettleTicketDialog.19"));
            if (StringUtils.isEmpty((String)loyaltyid)) {
                return;
            }
            this.ticket.addProperty(LOYALTY_ID, loyaltyid);
            String transactionURL = this.buildLoyaltyApiURL(this.ticket, loyaltyid);
            String string = IOUtils.toString((InputStream)new URL(transactionURL).openStream());
            JsonReader reader = Json.createReader((Reader)new StringReader(string));
            JsonObject object = reader.readObject();
            JsonArray jsonArray = (JsonArray)object.get((Object)"discounts");
            for (int i = 0; i < jsonArray.size(); ++i) {
                JsonObject jsonObject = (JsonObject)jsonArray.get(i);
                this.addCoupon(this.ticket, jsonObject);
            }
            this.updateModel();
            OrderController.saveOrder(this.ticket);
            POSMessageDialog.showMessage(Application.getPosWindow(), Messages.getString("SettleTicketDialog.21"));
            this.paymentView.updateView();
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("SettleTicketDialog.22"), e);
        }
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

    private void addCoupon(Ticket ticket, JsonObject jsonObject) {
        Set keys = jsonObject.keySet();
        for (String key : keys) {
            JsonNumber jsonNumber = jsonObject.getJsonNumber(key);
            double doubleValue = jsonNumber.doubleValue();
            TicketDiscount coupon = new TicketDiscount();
            coupon.setName(key);
            coupon.setType(3);
            coupon.setValue(doubleValue);
            ticket.addTodiscounts(coupon);
        }
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        this.paymentView.updateView();
    }
}

