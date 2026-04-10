/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdom2.Document
 *  org.jdom2.Element
 *  org.jdom2.input.SAXBuilder
 */
package com.mercurypay.ws.sdk;

import com.floreantpos.PosLog;
import java.io.Reader;
import java.io.StringReader;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class MercuryResponse {
    private String cmdStatus;
    private Element responseRoot;

    public MercuryResponse(String responseXml) throws Exception {
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document document = jdomBuilder.build((Reader)new StringReader(responseXml));
        this.responseRoot = document.getRootElement();
        this.cmdStatus = this.responseRoot.getChild("CmdResponse").getChildText("CmdStatus");
    }

    public boolean isApproved() {
        return "Approved".equalsIgnoreCase(this.cmdStatus);
    }

    public String getCmdStatus() {
        return this.cmdStatus;
    }

    public static void main(String[] args) throws Exception {
        MercuryResponse r = new MercuryResponse("<?xml version=\"1.0\"?><RStream>   <CmdResponse>      <ResponseOrigin>Client</ResponseOrigin>      <DSIXReturnCode>009999</DSIXReturnCode>      <CmdStatus>Error</CmdStatus>      <TextResponse>Invalid Credentials CALL 800-846-4472</TextResponse>   </CmdResponse></RStream>");
        PosLog.info(MercuryResponse.class, r.cmdStatus);
    }

    public String getTransactionId() {
        Element tranResponseElement = this.responseRoot.getChild("TranResponse");
        if (tranResponseElement == null) {
            return null;
        }
        return tranResponseElement.getChildTextTrim("RecordNo");
    }

    public String getAuthCode() {
        Element tranResponseElement = this.responseRoot.getChild("TranResponse");
        if (tranResponseElement == null) {
            return null;
        }
        return tranResponseElement.getChildTextTrim("AuthCode");
    }

    public String getAcqRefData() {
        Element tranResponseElement = this.responseRoot.getChild("TranResponse");
        if (tranResponseElement == null) {
            return null;
        }
        return tranResponseElement.getChildTextTrim("AcqRefData");
    }
}

