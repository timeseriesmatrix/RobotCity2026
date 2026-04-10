/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.Messages;
import com.floreantpos.PosException;
import com.floreantpos.PosLog;
import com.floreantpos.config.CardConfig;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.Ticket;
import com.floreantpos.ui.util.StreamUtils;
import com.floreantpos.ui.views.payment.CardProcessor;
import com.floreantpos.util.NumberUtil;
import com.mercurypay.ws.sdk.MercuryResponse;
import com.mercurypay.ws.sdk.MercuryWebRequest;
import java.io.IOException;

public class MercuryPayProcessor
implements CardProcessor {
    public static final String $merchantId = "$merchantId";
    public static final String $laneId = "$laneId";
    public static final String $invoiceNo = "$invoiceNo";
    public static final String $encryptedBlock = "$encryptedBlock";
    public static final String $encryptedKey = "$encryptedKey";
    public static final String $amount = "$amount";
    public static final String $authorizeAmount = "$authorizeAmount";
    public static final String $terminalName = "$terminalName";
    public static final String $shiftName = "$shiftName";
    public static final String $operatorId = "$operatorId";
    public static final String $tranCode = "$tranCode";
    public static final String $refNo = "$refNo";

    @Override
    public void preAuth(PosTransaction transaction) throws Exception {
        Ticket ticket = transaction.getTicket();
        if (ticket.getOrderType().name() == "BAR_TAB" && ticket.hasProperty("AcqRefData")) {
            this.captureAuthAmount(transaction);
            return;
        }
        String mpsResponse = this.doPreAuth(ticket, transaction.getCardTrack(), transaction.getAmount());
        MercuryResponse result = new MercuryResponse(mpsResponse);
        if (!result.isApproved()) {
            throw new PosException(Messages.getString("MercuryPayProcessor.13"));
        }
        PosLog.info(this.getClass(), mpsResponse);
        transaction.setCardTransactionId(result.getTransactionId());
        transaction.setCardAuthCode(result.getAuthCode());
        transaction.addProperty("AcqRefData", result.getAcqRefData());
    }

    private String doPreAuth(Ticket ticket, String cardTrack, double amount) throws IOException, Exception {
        String xml = StreamUtils.toString(MercuryPayProcessor.class.getResourceAsStream("/com/mercurypay/ws/sdk/mercuryAuth.xml"));
        String[] strings = cardTrack.split("\\|");
        String laneId = "01";
        String tranCode = "PreAuth";
        String invoiceNo = String.valueOf(ticket.getId());
        String amountStrng = NumberUtil.formatNumber(amount);
        String encryptedBlock = strings[3];
        String encryptedKey = strings[9];
        xml = xml.replace($merchantId, CardConfig.getMerchantAccount());
        xml = xml.replace($laneId, laneId);
        xml = xml.replace($tranCode, tranCode);
        xml = xml.replace($invoiceNo, invoiceNo);
        xml = xml.replace($refNo, invoiceNo);
        xml = xml.replace($encryptedBlock, encryptedBlock);
        xml = xml.replace($encryptedKey, encryptedKey);
        xml = xml.replace($amount, amountStrng);
        xml = xml.replace($authorizeAmount, amountStrng);
        PosLog.info(this.getClass(), xml);
        MercuryWebRequest mpswr = new MercuryWebRequest("https://w1.mercurydev.net/ws/ws.asmx");
        mpswr.addParameter("tran", xml);
        mpswr.addParameter("pw", CardConfig.getMerchantPass());
        mpswr.setWebMethodName("CreditTransaction");
        mpswr.setTimeout(10);
        String mpsResponse = mpswr.sendRequest();
        return mpsResponse;
    }

    @Override
    public void captureAuthAmount(PosTransaction transaction) throws Exception {
        String xml = StreamUtils.toString(MercuryPayProcessor.class.getResourceAsStream("/com/mercurypay/ws/sdk/mercuryPreAuthCapture.xml"));
        Ticket ticket = transaction.getTicket();
        String laneId = "01";
        String invoiceNo = String.valueOf(ticket.getId());
        String amount = String.valueOf(transaction.getAmount());
        xml = xml.replace($merchantId, CardConfig.getMerchantAccount());
        xml = xml.replace($laneId, laneId);
        xml = xml.replace($invoiceNo, invoiceNo);
        xml = xml.replace($refNo, invoiceNo);
        xml = xml.replace($amount, amount);
        xml = xml.replace($authorizeAmount, amount);
        xml = xml.replace("$gratuity", String.valueOf(transaction.getTipsAmount()));
        xml = xml.replace("$recordNo", transaction.getCardTransactionId());
        xml = xml.replace("$authCode", transaction.getCardAuthCode());
        xml = xml.replace("$AcqRefData", transaction.getProperty("AcqRefData"));
        MercuryWebRequest mpswr = new MercuryWebRequest("https://w1.mercurydev.net/ws/ws.asmx");
        mpswr.addParameter("tran", xml);
        mpswr.addParameter("pw", CardConfig.getMerchantPass());
        mpswr.setWebMethodName("CreditTransaction");
        mpswr.setTimeout(10);
        String mpsResponse = mpswr.sendRequest();
        MercuryResponse result = new MercuryResponse(mpsResponse);
        if (!result.isApproved()) {
            throw new PosException("Error authorizing transaction.");
        }
        transaction.setCardTransactionId(result.getTransactionId());
        transaction.setCardAuthCode(result.getAuthCode());
    }

    @Override
    public void chargeAmount(PosTransaction transaction) throws Exception {
    }

    @Override
    public void voidTransaction(PosTransaction transaction) throws Exception {
    }

    @Override
    public String getCardInformationForReceipt(PosTransaction transaction) {
        return null;
    }
}

