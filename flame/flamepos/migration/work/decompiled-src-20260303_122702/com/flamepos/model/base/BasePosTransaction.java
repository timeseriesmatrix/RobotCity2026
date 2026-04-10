/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.PayoutReason;
import com.floreantpos.model.PayoutRecepient;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public abstract class BasePosTransaction
implements Comparable,
Serializable {
    public static String REF = "PosTransaction";
    public static String PROP_USER = "user";
    public static String PROP_CARD_A_I_D = "cardAID";
    public static String PROP_CUSTOM_PAYMENT_FIELD_NAME = "customPaymentFieldName";
    public static String PROP_RECEPIENT = "recepient";
    public static String PROP_GIFT_CERT_CASH_BACK_AMOUNT = "giftCertCashBackAmount";
    public static String PROP_CUSTOM_PAYMENT_REF = "customPaymentRef";
    public static String PROP_TRANSACTION_TYPE = "transactionType";
    public static String PROP_AUTHORIZABLE = "authorizable";
    public static String PROP_GIFT_CERT_NUMBER = "giftCertNumber";
    public static String PROP_CARD_READER = "cardReader";
    public static String PROP_TICKET = "ticket";
    public static String PROP_CARD_EXT_DATA = "cardExtData";
    public static String PROP_CARD_A_R_Q_C = "cardARQC";
    public static String PROP_CARD_HOLDER_NAME = "cardHolderName";
    public static String PROP_CARD_MERCHANT_GATEWAY = "cardMerchantGateway";
    public static String PROP_CARD_TYPE = "cardType";
    public static String PROP_DRAWER_RESETTED = "drawerResetted";
    public static String PROP_TRANSACTION_TIME = "transactionTime";
    public static String PROP_CARD_AUTH_CODE = "cardAuthCode";
    public static String PROP_REASON = "reason";
    public static String PROP_GIFT_CERT_FACE_VALUE = "giftCertFaceValue";
    public static String PROP_CARD_NUMBER = "cardNumber";
    public static String PROP_GLOBAL_ID = "globalId";
    public static String PROP_AMOUNT = "amount";
    public static String PROP_CAPTURED = "captured";
    public static String PROP_TERMINAL = "terminal";
    public static String PROP_NOTE = "note";
    public static String PROP_CUSTOM_PAYMENT_NAME = "customPaymentName";
    public static String PROP_TIPS_EXCEED_AMOUNT = "tipsExceedAmount";
    public static String PROP_PAYMENT_TYPE = "paymentType";
    public static String PROP_TIPS_AMOUNT = "tipsAmount";
    public static String PROP_TENDER_AMOUNT = "tenderAmount";
    public static String PROP_CARD_TRANSACTION_ID = "cardTransactionId";
    public static String PROP_ID = "id";
    public static String PROP_VOIDED = "voided";
    public static String PROP_GIFT_CERT_PAID_AMOUNT = "giftCertPaidAmount";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String globalId;
    protected Date transactionTime;
    protected Double amount;
    protected Double tipsAmount;
    protected Double tipsExceedAmount;
    protected Double tenderAmount;
    protected String transactionType;
    protected String customPaymentName;
    protected String customPaymentRef;
    protected String customPaymentFieldName;
    protected String paymentType;
    protected Boolean captured;
    protected Boolean voided;
    protected Boolean authorizable;
    protected String cardHolderName;
    protected String cardNumber;
    protected String cardAuthCode;
    protected String cardType;
    protected String cardTransactionId;
    protected String cardMerchantGateway;
    protected String cardReader;
    protected String cardAID;
    protected String cardARQC;
    protected String cardExtData;
    protected String giftCertNumber;
    protected Double giftCertFaceValue;
    protected Double giftCertPaidAmount;
    protected Double giftCertCashBackAmount;
    protected Boolean drawerResetted;
    protected String note;
    private Terminal terminal;
    private Ticket ticket;
    private User user;
    private PayoutReason reason;
    private PayoutRecepient recepient;
    private Map<String, String> properties;

    public BasePosTransaction() {
        this.initialize();
    }

    public BasePosTransaction(Integer id) {
        this.setId(id);
        this.initialize();
    }

    public BasePosTransaction(Integer id, String transactionType, String paymentType) {
        this.setId(id);
        this.setTransactionType(transactionType);
        this.setPaymentType(paymentType);
        this.initialize();
    }

    protected void initialize() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
        this.hashCode = Integer.MIN_VALUE;
    }

    public String getGlobalId() {
        return this.globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public Date getTransactionTime() {
        return this.transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    public Double getAmount() {
        return this.amount == null ? Double.valueOf(0.0) : this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTipsAmount() {
        return this.tipsAmount == null ? Double.valueOf(0.0) : this.tipsAmount;
    }

    public void setTipsAmount(Double tipsAmount) {
        this.tipsAmount = tipsAmount;
    }

    public Double getTipsExceedAmount() {
        return this.tipsExceedAmount == null ? Double.valueOf(0.0) : this.tipsExceedAmount;
    }

    public void setTipsExceedAmount(Double tipsExceedAmount) {
        this.tipsExceedAmount = tipsExceedAmount;
    }

    public Double getTenderAmount() {
        return this.tenderAmount == null ? Double.valueOf(0.0) : this.tenderAmount;
    }

    public void setTenderAmount(Double tenderAmount) {
        this.tenderAmount = tenderAmount;
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getCustomPaymentName() {
        return this.customPaymentName;
    }

    public void setCustomPaymentName(String customPaymentName) {
        this.customPaymentName = customPaymentName;
    }

    public String getCustomPaymentRef() {
        return this.customPaymentRef;
    }

    public void setCustomPaymentRef(String customPaymentRef) {
        this.customPaymentRef = customPaymentRef;
    }

    public String getCustomPaymentFieldName() {
        return this.customPaymentFieldName;
    }

    public void setCustomPaymentFieldName(String customPaymentFieldName) {
        this.customPaymentFieldName = customPaymentFieldName;
    }

    public String getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Boolean isCaptured() {
        return this.captured == null ? Boolean.FALSE : this.captured;
    }

    public void setCaptured(Boolean captured) {
        this.captured = captured;
    }

    public Boolean isVoided() {
        return this.voided == null ? Boolean.FALSE : this.voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public Boolean isAuthorizable() {
        return this.authorizable == null ? Boolean.FALSE : this.authorizable;
    }

    public void setAuthorizable(Boolean authorizable) {
        this.authorizable = authorizable;
    }

    public String getCardHolderName() {
        return this.cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardAuthCode() {
        return this.cardAuthCode;
    }

    public void setCardAuthCode(String cardAuthCode) {
        this.cardAuthCode = cardAuthCode;
    }

    public String getCardType() {
        return this.cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardTransactionId() {
        return this.cardTransactionId;
    }

    public void setCardTransactionId(String cardTransactionId) {
        this.cardTransactionId = cardTransactionId;
    }

    public String getCardMerchantGateway() {
        return this.cardMerchantGateway;
    }

    public void setCardMerchantGateway(String cardMerchantGateway) {
        this.cardMerchantGateway = cardMerchantGateway;
    }

    public String getCardReader() {
        return this.cardReader;
    }

    public void setCardReader(String cardReader) {
        this.cardReader = cardReader;
    }

    public String getCardAID() {
        return this.cardAID;
    }

    public void setCardAID(String cardAID) {
        this.cardAID = cardAID;
    }

    public String getCardARQC() {
        return this.cardARQC;
    }

    public void setCardARQC(String cardARQC) {
        this.cardARQC = cardARQC;
    }

    public String getCardExtData() {
        return this.cardExtData;
    }

    public void setCardExtData(String cardExtData) {
        this.cardExtData = cardExtData;
    }

    public String getGiftCertNumber() {
        return this.giftCertNumber;
    }

    public void setGiftCertNumber(String giftCertNumber) {
        this.giftCertNumber = giftCertNumber;
    }

    public Double getGiftCertFaceValue() {
        return this.giftCertFaceValue == null ? Double.valueOf(0.0) : this.giftCertFaceValue;
    }

    public void setGiftCertFaceValue(Double giftCertFaceValue) {
        this.giftCertFaceValue = giftCertFaceValue;
    }

    public Double getGiftCertPaidAmount() {
        return this.giftCertPaidAmount == null ? Double.valueOf(0.0) : this.giftCertPaidAmount;
    }

    public void setGiftCertPaidAmount(Double giftCertPaidAmount) {
        this.giftCertPaidAmount = giftCertPaidAmount;
    }

    public Double getGiftCertCashBackAmount() {
        return this.giftCertCashBackAmount == null ? Double.valueOf(0.0) : this.giftCertCashBackAmount;
    }

    public void setGiftCertCashBackAmount(Double giftCertCashBackAmount) {
        this.giftCertCashBackAmount = giftCertCashBackAmount;
    }

    public Boolean isDrawerResetted() {
        return this.drawerResetted == null ? Boolean.FALSE : this.drawerResetted;
    }

    public void setDrawerResetted(Boolean drawerResetted) {
        this.drawerResetted = drawerResetted;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PayoutReason getReason() {
        return this.reason;
    }

    public void setReason(PayoutReason reason) {
        this.reason = reason;
    }

    public PayoutRecepient getRecepient() {
        return this.recepient;
    }

    public void setRecepient(PayoutRecepient recepient) {
        this.recepient = recepient;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof PosTransaction)) {
            return false;
        }
        PosTransaction posTransaction = (PosTransaction)obj;
        if (null == this.getId() || null == posTransaction.getId()) {
            return false;
        }
        return this.getId().equals(posTransaction.getId());
    }

    public int hashCode() {
        if (Integer.MIN_VALUE == this.hashCode) {
            if (null == this.getId()) {
                return super.hashCode();
            }
            String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
            this.hashCode = hashStr.hashCode();
        }
        return this.hashCode;
    }

    public int compareTo(Object obj) {
        if (obj.hashCode() > this.hashCode()) {
            return 1;
        }
        if (obj.hashCode() < this.hashCode()) {
            return -1;
        }
        return 0;
    }

    public String toString() {
        return super.toString();
    }
}

