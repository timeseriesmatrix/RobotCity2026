/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.posserver;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="POSDefaultInfo")
public class POSDefaultInfo {
    String server;
    String table;
    String track2;
    String check;
    String res;
    String rText;

    @XmlAttribute(name="server")
    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @XmlAttribute(name="table")
    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @XmlAttribute(name="track2")
    public String getTrack2() {
        return this.track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    @XmlAttribute(name="check")
    public String getCheck() {
        return this.check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    @XmlAttribute(name="res")
    public String getRes() {
        return this.res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    @XmlAttribute(name="rtext")
    public String getrText() {
        return this.rText;
    }

    public void setrText(String rText) {
        this.rText = rText;
    }
}

