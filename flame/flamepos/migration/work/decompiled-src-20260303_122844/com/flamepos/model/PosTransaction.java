/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.config.CardConfig;
import com.floreantpos.model.CreditCardTransaction;
import com.floreantpos.model.DebitCardTransaction;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TransactionType;
import com.floreantpos.model.base.BasePosTransaction;
import com.floreantpos.util.GlobalIdGenerator;
import com.floreantpos.util.POSUtil;
import java.util.HashMap;
import org.apache.commons.lang.StringUtils;

public class PosTransaction
extends BasePosTransaction {
    private static final long serialVersionUID = 1L;
    private String cardTrack;
    private String cardNo;
    private String cardExpYear;
    private String cardExpMonth;
    public static final String CASH = "CASH";
    public static final String GIFT_CERT = "GIFT_CERT";
    public static final String CREDIT_CARD = "CREDIT_CARD";
    public static final String DEBIT_CARD = "DEBIT_CARD";
    public static final String CASH_DROP = "CASH_DROP";
    public static final String REFUND = "REFUND";
    public static final String PAY_OUT = "PAY_OUT";
    public static final String VOID_TRANS = "VOID_TRANS";

    public PosTransaction() {
    }

    public PosTransaction(Integer id) {
        super(id);
    }

    public PosTransaction(Integer id, String transactionType, String paymentType) {
        super(id, transactionType, paymentType);
    }

    @Override
    protected void initialize() {
        this.setGlobalId(GlobalIdGenerator.generate());
    }

    @Override
    public String getTransactionType() {
        String type = super.getTransactionType();
        if (StringUtils.isEmpty((String)type)) {
            return TransactionType.CREDIT.name();
        }
        return type;
    }

    public void updateTerminalBalance() {
        Terminal terminal = this.getTerminal();
        if (terminal == null) {
            return;
        }
        Double amount = this.getAmount();
        if (amount == null || amount == 0.0) {
            return;
        }
        double terminalBalance = terminal.getCurrentBalance();
        TransactionType transactionType = TransactionType.valueOf(this.getTransactionType());
        switch (transactionType) {
            case CREDIT: {
                terminalBalance += amount.doubleValue();
                break;
            }
            case DEBIT: {
                terminalBalance -= amount.doubleValue();
            }
        }
        terminal.setCurrentBalance(terminalBalance);
    }

    public boolean isCard() {
        return this instanceof CreditCardTransaction || this instanceof DebitCardTransaction;
    }

    public void addProperty(String name, String value) {
        if (this.getProperties() == null) {
            this.setProperties(new HashMap<String, String>());
        }
        this.getProperties().put(name, value);
    }

    public boolean hasProperty(String key) {
        return this.getProperty(key) != null;
    }

    public String getProperty(String key) {
        if (this.getProperties() == null) {
            return null;
        }
        return this.getProperties().get(key);
    }

    public boolean isPropertyValueTrue(String propertyName) {
        String property = this.getProperty(propertyName);
        return POSUtil.getBoolean(property);
    }

    public Double calculateTotalAmount() {
        return this.getAmount() + this.getTipsAmount();
    }

    public Double calculateAuthorizeAmount() {
        double advanceTipsPercentage = CardConfig.getAdvanceTipsPercentage();
        return this.getTenderAmount() + this.getTenderAmount() * (advanceTipsPercentage / 100.0);
    }

    public String getCardTrack() {
        return this.cardTrack;
    }

    public void setCardTrack(String cardTrack) {
        this.cardTrack = cardTrack;
    }

    public String getCardNo() {
        return this.cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardExpYear() {
        return this.cardExpYear;
    }

    public void setCardExpYear(String expYear) {
        this.cardExpYear = expYear;
    }

    public String getCardExpMonth() {
        return this.cardExpMonth;
    }

    public void setCardExpMonth(String expMonth) {
        this.cardExpMonth = expMonth;
    }

    public String getTicketId() {
        Ticket ticket = this.getTicket();
        if (ticket == null) {
            return "";
        }
        return String.valueOf(ticket.getId());
    }
}

