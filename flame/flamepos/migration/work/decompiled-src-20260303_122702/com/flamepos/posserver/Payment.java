/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.posserver;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Payment")
public class Payment {
    String pamt;
    String tamt;
    String cback;
    String schrg;
    String cardType;
    String acct;
    String exp;
    String track2;
    String edc;

    @XmlAttribute(name="pamt")
    public String getPamt() {
        return this.pamt;
    }

    public void setPamt(String pamt) {
        this.pamt = pamt;
    }

    @XmlAttribute(name="tamt")
    public String getTamt() {
        return this.tamt;
    }

    public void setTamt(String tamt) {
        this.tamt = tamt;
    }

    @XmlAttribute(name="cback")
    public String getCback() {
        return this.cback;
    }

    public void setCback(String cback) {
        this.cback = cback;
    }

    @XmlAttribute(name="schrg")
    public String getSchrg() {
        return this.schrg;
    }

    public void setSchrg(String schrg) {
        this.schrg = schrg;
    }

    @XmlAttribute(name="cardType")
    public String getCardType() {
        return this.cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @XmlAttribute(name="acct")
    public String getAcct() {
        return this.acct;
    }

    public void setAcct(String acct) {
        this.acct = acct;
    }

    @XmlAttribute(name="exp")
    public String getExp() {
        return this.exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    @XmlAttribute(name="track2")
    public String getTrack2() {
        return this.track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    @XmlAttribute(name="edc")
    public String getEdc() {
        return this.edc;
    }

    public void setEdc(String edc) {
        this.edc = edc;
    }
}

