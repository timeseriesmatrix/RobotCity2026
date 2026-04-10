/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.posserver;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Check")
public class Check {
    String tableNo;
    String tableName;
    String chkName;
    String chkNo;
    String amt;
    String tax;

    @XmlAttribute(name="tableno")
    public String getTableNo() {
        return this.tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    @XmlAttribute(name="tablename")
    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @XmlAttribute(name="chkname")
    public String getChkName() {
        return this.chkName;
    }

    public void setChkName(String chkName) {
        this.chkName = chkName;
    }

    @XmlAttribute(name="chkno")
    public String getChkNo() {
        return this.chkNo;
    }

    public void setChkNo(String chkNo) {
        this.chkNo = chkNo;
    }

    @XmlAttribute(name="amt")
    public String getAmt() {
        return this.amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    @XmlAttribute(name="tax")
    public String getTax() {
        return this.tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }
}

