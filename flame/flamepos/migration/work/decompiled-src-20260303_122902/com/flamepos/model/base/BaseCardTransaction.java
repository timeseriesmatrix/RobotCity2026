/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.CardTransaction;
import com.floreantpos.model.PosTransaction;
import java.io.Serializable;

public abstract class BaseCardTransaction
extends PosTransaction
implements Comparable,
Serializable {
    public static String REF = "CardTransaction";
    public static String PROP_CARD_NUMBER = "cardNumber";
    public static String PROP_AUTHORIZATION_CODE = "authorizationCode";
    public static String PROP_ID = "id";
    public static String PROP_CARD_TYPE = "cardType";
    private int hashCode = Integer.MIN_VALUE;
    protected String cardNumber;
    protected String authorizationCode;
    protected String cardType;

    public BaseCardTransaction() {
        this.initialize();
    }

    public BaseCardTransaction(Integer id) {
        super(id);
    }

    @Override
    public String getCardNumber() {
        return this.cardNumber;
    }

    @Override
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getAuthorizationCode() {
        return this.authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    @Override
    public String getCardType() {
        return this.cardType;
    }

    @Override
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof CardTransaction)) {
            return false;
        }
        CardTransaction cardTransaction = (CardTransaction)obj;
        if (null == this.getId() || null == cardTransaction.getId()) {
            return false;
        }
        return this.getId().equals(cardTransaction.getId());
    }

    @Override
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

    @Override
    public int compareTo(Object obj) {
        if (obj.hashCode() > this.hashCode()) {
            return 1;
        }
        if (obj.hashCode() < this.hashCode()) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

