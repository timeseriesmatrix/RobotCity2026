/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.Messages;
import com.floreantpos.model.TipsCashoutReportData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TipsCashoutReport {
    private String server;
    private Date fromDate;
    private Date toDate;
    private Date reportTime;
    private int cashTipsCount;
    private double cashTipsAmount;
    private int chargedTipsCount;
    private double chargedTipsAmount;
    private double totalTips;
    private double averageTips;
    private double paidTips;
    private double tipsDue;
    private List<TipsCashoutReportData> datas;

    public Date getFromDate() {
        return this.fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getReportTime() {
        return this.reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Date getToDate() {
        return this.toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public void addReportData(TipsCashoutReportData data) {
        if (this.datas == null) {
            this.datas = new ArrayList<TipsCashoutReportData>();
        }
        this.datas.add(data);
    }

    public List<TipsCashoutReportData> getDatas() {
        return this.datas;
    }

    public void calculateOthers() {
        if (this.datas == null) {
            return;
        }
        for (TipsCashoutReportData data : this.datas) {
            if (Messages.getString("TipsCashoutReport.0").equals(data.getSaleType())) {
                ++this.cashTipsCount;
                this.cashTipsAmount += data.getTips().doubleValue();
            } else {
                ++this.chargedTipsCount;
                this.chargedTipsAmount += data.getTips().doubleValue();
            }
            this.totalTips += data.getTips().doubleValue();
            if (data.isPaid()) {
                this.paidTips += 1.0;
                continue;
            }
            this.tipsDue += data.getTips().doubleValue();
        }
        this.averageTips = this.totalTips / (double)this.datas.size();
    }

    public double getAverageTips() {
        return this.averageTips;
    }

    public void setAverageTips(double averageTips) {
        this.averageTips = averageTips;
    }

    public int getCashTipsCount() {
        return this.cashTipsCount;
    }

    public void setCashTipsCount(int cashTipsCount) {
        this.cashTipsCount = cashTipsCount;
    }

    public int getChargedTipsCount() {
        return this.chargedTipsCount;
    }

    public void setChargedTipsCount(int chargedTipsCount) {
        this.chargedTipsCount = chargedTipsCount;
    }

    public double getPaidTips() {
        return this.paidTips;
    }

    public void setPaidTips(double paidTips) {
        this.paidTips = paidTips;
    }

    public double getTotalTips() {
        return this.totalTips;
    }

    public void setTotalTips(double totalTips) {
        this.totalTips = totalTips;
    }

    public double getCashTipsAmount() {
        return this.cashTipsAmount;
    }

    public void setCashTipsAmount(double cashTipsAmount) {
        this.cashTipsAmount = cashTipsAmount;
    }

    public double getChargedTipsAmount() {
        return this.chargedTipsAmount;
    }

    public void setChargedTipsAmount(double chargedTipsAmount) {
        this.chargedTipsAmount = chargedTipsAmount;
    }

    public double getTipsDue() {
        return this.tipsDue;
    }

    public void setTipsDue(double tipsDue) {
        this.tipsDue = tipsDue;
    }
}

