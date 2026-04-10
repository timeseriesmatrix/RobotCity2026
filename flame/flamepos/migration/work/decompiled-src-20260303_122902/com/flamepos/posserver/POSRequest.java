/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.posserver;

import com.floreantpos.posserver.Ident;
import com.floreantpos.posserver.POSDefaultInfo;
import com.floreantpos.posserver.Payment;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="POSRequest")
public class POSRequest {
    Ident ident;
    POSDefaultInfo posDefaultInfo;
    Payment payment;

    @XmlElement(name="Ident")
    public Ident getIdent() {
        return this.ident;
    }

    public void setIdent(Ident ident) {
        this.ident = ident;
    }

    @XmlElement(name="POSDefaultInfo")
    public POSDefaultInfo getPosDefaultInfo() {
        return this.posDefaultInfo;
    }

    public void setPosDefaultInfo(POSDefaultInfo posDefaultInfo) {
        this.posDefaultInfo = posDefaultInfo;
    }

    @XmlElement(name="Payment")
    public Payment getPayment() {
        return this.payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}

