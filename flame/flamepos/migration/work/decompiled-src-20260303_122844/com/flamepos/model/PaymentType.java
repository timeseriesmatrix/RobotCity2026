/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.config.CardConfig;
import com.floreantpos.model.CashTransaction;
import com.floreantpos.model.CreditCardTransaction;
import com.floreantpos.model.CustomPaymentTransaction;
import com.floreantpos.model.DebitCardTransaction;
import com.floreantpos.model.GiftCertificateTransaction;
import com.floreantpos.model.PosTransaction;

public enum PaymentType {
    CUSTOM_PAYMENT("CUSTOM PAYMENT"),
    CASH("CASH"),
    CREDIT_CARD("CREDIT CARD"),
    DEBIT_CARD("DEBIT CARD"),
    DEBIT_VISA("VISA", "visa_card.png"),
    DEBIT_MASTER_CARD("MASTER CARD", "master_card.png"),
    CREDIT_VISA("VISA", "visa_card.png"),
    CREDIT_MASTER_CARD("MASTER CARD", "master_card.png"),
    CREDIT_AMEX("AMEX", "am_ex_card.png"),
    CREDIT_DISCOVERY("DISCOVER", "discover_card.png"),
    GIFT_CERTIFICATE("GIFT CERTIFICATE");

    private String displayString;
    private String imageFile;

    private PaymentType(String display) {
        this.displayString = display;
    }

    private PaymentType(String display, String image) {
        this.displayString = display;
        this.imageFile = image;
    }

    public String toString() {
        return this.displayString;
    }

    public String getDisplayString() {
        return this.displayString;
    }

    public void setDisplayString(String displayString) {
        this.displayString = displayString;
    }

    public String getImageFile() {
        return this.imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public boolean isSupported() {
        switch (this) {
            case CASH: {
                return true;
            }
        }
        return CardConfig.isSwipeCardSupported() || CardConfig.isManualEntrySupported() || CardConfig.isExtTerminalSupported();
    }

    public PosTransaction createTransaction() {
        PosTransaction transaction = null;
        switch (this) {
            case CREDIT_CARD: 
            case CREDIT_VISA: 
            case CREDIT_AMEX: 
            case CREDIT_DISCOVERY: 
            case CREDIT_MASTER_CARD: {
                transaction = new CreditCardTransaction();
                transaction.setAuthorizable(true);
                break;
            }
            case DEBIT_MASTER_CARD: 
            case DEBIT_VISA: {
                transaction = new DebitCardTransaction();
                transaction.setAuthorizable(true);
                break;
            }
            case GIFT_CERTIFICATE: {
                transaction = new GiftCertificateTransaction();
                break;
            }
            case CUSTOM_PAYMENT: {
                transaction = new CustomPaymentTransaction();
                break;
            }
            default: {
                transaction = new CashTransaction();
            }
        }
        transaction.setPaymentType(this.getDisplayString());
        return transaction;
    }
}

