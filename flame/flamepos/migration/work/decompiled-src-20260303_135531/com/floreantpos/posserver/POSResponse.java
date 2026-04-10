/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.posserver;

import com.floreantpos.posserver.Checks;
import com.floreantpos.posserver.Ident;
import com.floreantpos.posserver.POSDefaultInfo;
import com.floreantpos.posserver.PrintText;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="POSResponse")
public class POSResponse {
    Ident ident;
    POSDefaultInfo posDefaultInfo;
    Checks checks;
    List<PrintText> printTexts;

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

    @XmlElement(name="Checks")
    public Checks getChecks() {
        return this.checks;
    }

    public void setChecks(Checks checks) {
        this.checks = checks;
    }

    @XmlElement(name="PrintText")
    public List<PrintText> getPrintText() {
        return this.printTexts;
    }

    public void setPrintChecks(List<PrintText> printTexts) {
        this.printTexts = printTexts;
    }
}

