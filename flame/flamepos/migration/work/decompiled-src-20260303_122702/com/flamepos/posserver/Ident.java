/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.posserver;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Ident")
public class Ident {
    public static final String GET_TABLES = "45";
    public static final String APPLY_PAYMENT = "46";
    public static final String PRINT_CHECK = "47";
    String id;
    String ttype;
    String termserialno;

    @XmlAttribute(name="id")
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name="ttype")
    public String getTtype() {
        return this.ttype;
    }

    public void setTtype(String ttype) {
        this.ttype = ttype;
    }

    @XmlAttribute(name="termserialno")
    public String getTermserialno() {
        return this.termserialno;
    }

    public void setTermserialno(String termserialno) {
        this.termserialno = termserialno;
    }
}

