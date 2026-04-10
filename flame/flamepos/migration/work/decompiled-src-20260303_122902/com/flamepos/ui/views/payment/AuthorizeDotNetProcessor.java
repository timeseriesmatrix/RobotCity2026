/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.authorize.DeviceType
 *  net.authorize.Environment
 *  net.authorize.MarketType
 *  net.authorize.Merchant
 *  net.authorize.ResponseReasonCode
 *  net.authorize.Transaction
 *  net.authorize.TransactionType
 *  net.authorize.aim.Transaction
 *  net.authorize.aim.cardpresent.Result
 *  net.authorize.data.creditcard.CardType
 *  net.authorize.data.creditcard.CreditCard
 *  org.apache.commons.lang.StringUtils
 *  us.fatehi.magnetictrack.bankcard.BankCardMagneticTrack
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.config.CardConfig;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.ui.views.payment.CardProcessor;
import java.math.BigDecimal;
import net.authorize.DeviceType;
import net.authorize.Environment;
import net.authorize.MarketType;
import net.authorize.Merchant;
import net.authorize.ResponseReasonCode;
import net.authorize.TransactionType;
import net.authorize.aim.Transaction;
import net.authorize.aim.cardpresent.Result;
import net.authorize.data.creditcard.CardType;
import net.authorize.data.creditcard.CreditCard;
import org.apache.commons.lang.StringUtils;
import us.fatehi.magnetictrack.bankcard.BankCardMagneticTrack;

public class AuthorizeDotNetProcessor
implements CardProcessor {
    @Override
    public void preAuth(PosTransaction transaction) throws Exception {
        Environment environment = this.createEnvironment();
        Merchant merchant = this.createMerchant(environment);
        CreditCard creditCard = this.createCard(transaction);
        Transaction authCaptureTransaction = merchant.createAIMTransaction(TransactionType.AUTH_ONLY, new BigDecimal(transaction.calculateAuthorizeAmount()));
        authCaptureTransaction.setCreditCard(creditCard);
        Result result = (Result)merchant.postTransaction((net.authorize.Transaction)authCaptureTransaction);
        if (!result.isApproved()) {
            if (result.isDeclined()) {
                throw new Exception(Messages.getString("AuthorizeDotNetProcessor.2") + ((ResponseReasonCode)result.getResponseReasonCodes().get(0)).getReasonText());
            }
            throw new Exception(Messages.getString("AuthorizeDotNetProcessor.3") + ((ResponseReasonCode)result.getResponseReasonCodes().get(0)).getReasonText());
        }
        transaction.setCaptured(false);
        transaction.setAuthorizable(true);
        this.populateResult((Result<Transaction>)result, transaction);
    }

    @Override
    public void chargeAmount(PosTransaction transaction) throws Exception {
        Environment environment = this.createEnvironment();
        Merchant merchant = this.createMerchant(environment);
        CreditCard creditCard = this.createCard(transaction);
        Transaction authCaptureTransaction = merchant.createAIMTransaction(TransactionType.AUTH_CAPTURE, new BigDecimal(transaction.getAmount()));
        authCaptureTransaction.setCreditCard(creditCard);
        Result result = (Result)merchant.postTransaction((net.authorize.Transaction)authCaptureTransaction);
        if (!result.isApproved()) {
            if (result.isDeclined()) {
                throw new Exception(Messages.getString("AuthorizeDotNetProcessor.2") + ((ResponseReasonCode)result.getResponseReasonCodes().get(0)).getReasonText());
            }
            throw new Exception(Messages.getString("AuthorizeDotNetProcessor.3") + ((ResponseReasonCode)result.getResponseReasonCodes().get(0)).getReasonText());
        }
        transaction.setCaptured(true);
        transaction.setAuthorizable(false);
        this.populateResult((Result<Transaction>)result, transaction);
    }

    private void populateResult(Result<Transaction> result, PosTransaction transaction) {
        transaction.setCardTransactionId(result.getTransId());
        transaction.setCardAuthCode(result.getAuthCode());
        CreditCard card = ((Transaction)result.getTarget()).getCreditCard();
        transaction.setCardType(card.getCardType().name());
        transaction.setCardNumber(card.getCreditCardNumber());
        String rawTrackData = "%" + card.getTrack1() + "?;" + card.getTrack2() + "?";
        BankCardMagneticTrack track = BankCardMagneticTrack.from((String)rawTrackData);
        transaction.setCardHolderName(track.getTrack1().getName().toString());
        transaction.setCardExpYear(card.getExpirationYear());
        transaction.setCardExpMonth(card.getExpirationMonth());
    }

    @Override
    public void captureAuthAmount(PosTransaction transaction) throws Exception {
        Environment environment = this.createEnvironment();
        Merchant merchant = this.createMerchant(environment);
        Transaction authCaptureTransaction = merchant.createAIMTransaction(TransactionType.PRIOR_AUTH_CAPTURE, new BigDecimal(transaction.getAmount()));
        authCaptureTransaction.setTransactionId(transaction.getCardTransactionId());
        Result result = (Result)merchant.postTransaction((net.authorize.Transaction)authCaptureTransaction);
        if (!result.isApproved()) {
            if (result.isDeclined()) {
                throw new Exception(Messages.getString("AuthorizeDotNetProcessor.6") + ((ResponseReasonCode)result.getResponseReasonCodes().get(0)).getReasonText());
            }
            throw new Exception(Messages.getString("AuthorizeDotNetProcessor.7") + ((ResponseReasonCode)result.getResponseReasonCodes().get(0)).getReasonText());
        }
        transaction.setCaptured(true);
        transaction.setAuthorizable(false);
    }

    public void captureNewAmount(PosTransaction transaction) throws Exception {
        Environment environment = this.createEnvironment();
        Merchant merchant = this.createMerchant(environment);
        CreditCard creditCard = this.createCard(transaction);
        Transaction authCaptureTransaction = merchant.createAIMTransaction(TransactionType.AUTH_CAPTURE, new BigDecimal(transaction.getAmount()));
        authCaptureTransaction.setCreditCard(creditCard);
        Result result = (Result)merchant.postTransaction((net.authorize.Transaction)authCaptureTransaction);
        if (!result.isApproved()) {
            if (result.isDeclined()) {
                throw new Exception(Messages.getString("AuthorizeDotNetProcessor.8") + ((ResponseReasonCode)result.getResponseReasonCodes().get(0)).getReasonText());
            }
            throw new Exception(Messages.getString("AuthorizeDotNetProcessor.9") + ((ResponseReasonCode)result.getResponseReasonCodes().get(0)).getReasonText());
        }
        transaction.setCaptured(true);
        transaction.setAuthorizable(false);
        this.populateResult((Result<Transaction>)result, transaction);
    }

    public void voidAmount(PosTransaction transaction) throws Exception {
        Environment environment = this.createEnvironment();
        Merchant merchant = this.createMerchant(environment);
        Transaction authCaptureTransaction = merchant.createAIMTransaction(TransactionType.VOID, new BigDecimal(transaction.getAmount()));
        authCaptureTransaction.setTransactionId(transaction.getCardTransactionId());
        Result result = (Result)merchant.postTransaction((net.authorize.Transaction)authCaptureTransaction);
        if (!result.isApproved()) {
            if (result.isDeclined()) {
                throw new Exception(Messages.getString("AuthorizeDotNetProcessor.10") + ((ResponseReasonCode)result.getResponseReasonCodes().get(0)).getReasonText());
            }
            throw new Exception(Messages.getString("AuthorizeDotNetProcessor.11") + ((ResponseReasonCode)result.getResponseReasonCodes().get(0)).getReasonText());
        }
        transaction.setCaptured(true);
        transaction.setAuthorizable(false);
        this.populateResult((Result<Transaction>)result, transaction);
    }

    private CreditCard createCard(PosTransaction transaction) {
        CreditCard creditCard = CreditCard.createCreditCard();
        creditCard.setCardType(CardType.findByValue((String)transaction.getCardType()));
        if (StringUtils.isNotEmpty((String)transaction.getCardTrack())) {
            return this.createCard(transaction.getCardTrack(), transaction.getCardType());
        }
        return this.createCard(transaction.getCardNumber(), transaction.getCardExpMonth(), transaction.getCardExpYear(), transaction.getCardType());
    }

    private CreditCard createCard(String cardTrack, String cardType) {
        CreditCard creditCard = CreditCard.createCreditCard();
        creditCard.setCardType(CardType.findByValue((String)cardType));
        String[] tracks = cardTrack.split(";");
        creditCard.setTrack1(tracks[0]);
        if (tracks.length > 1) {
            creditCard.setTrack2(";" + tracks[1]);
        }
        return creditCard;
    }

    private CreditCard createCard(String cardNumber, String expMonth, String expYear, String cardType) {
        CreditCard creditCard = CreditCard.createCreditCard();
        creditCard.setCardType(CardType.findByValue((String)cardType));
        creditCard.setExpirationYear(expYear);
        creditCard.setExpirationMonth(expMonth);
        creditCard.setCreditCardNumber(cardNumber);
        return creditCard;
    }

    private Merchant createMerchant(Environment environment) throws Exception {
        String apiLoginID = CardConfig.getMerchantAccount();
        String transactionKey = CardConfig.getMerchantPass();
        Merchant merchant = Merchant.createMerchant((Environment)environment, (String)apiLoginID, (String)transactionKey);
        merchant.setDeviceType(DeviceType.VIRTUAL_TERMINAL);
        merchant.setMarketType(MarketType.RETAIL);
        return merchant;
    }

    private Environment createEnvironment() {
        Environment environment = Environment.PRODUCTION;
        if (CardConfig.isSandboxMode()) {
            environment = Environment.SANDBOX;
        }
        return environment;
    }

    public static void main(String[] args) throws Exception {
        BankCardMagneticTrack track = BankCardMagneticTrack.from((String)"%B4111111111111111^SHAH/RIAR^1803101000000000020000831000000?;4111111111111111=1803101000020000831?");
        PosLog.info(AuthorizeDotNetProcessor.class, "" + track.getTrack1());
    }

    @Override
    public void voidTransaction(PosTransaction transaction) throws Exception {
    }

    @Override
    public String getCardInformationForReceipt(PosTransaction transaction) {
        return null;
    }
}

