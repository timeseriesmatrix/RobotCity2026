/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.POSConstants;
import com.floreantpos.model.CreditCardTransaction;
import com.floreantpos.model.DebitCardTransaction;
import com.floreantpos.swing.ListTableModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesDetailedReport {
    private Date fromDate;
    private Date toDate;
    private Date reportTime;
    int giftCertReturnCount;
    double giftCertReturnAmount;
    int giftCertChangeCount;
    double giftCertChangeAmount;
    int tipsCount;
    double chargedTips;
    double tipsPaid;
    double tipsDifferential;
    private List<DrawerPullData> drawerPullDatas = new ArrayList<DrawerPullData>();
    private Map<String, CreditCardData> creditCardDatas = new HashMap<String, CreditCardData>();

    public void addCreditCardData(CreditCardTransaction t) {
        CreditCardData data = this.creditCardDatas.get(t.getCardType());
        if (data == null) {
            data = new CreditCardData();
            data.setCardName(t.getCardType());
            this.creditCardDatas.put(t.getCardType(), data);
        }
        data.setSalesCount(data.getSalesCount() + 1);
        data.setSalesAmount(data.getSalesAmount() + t.getAmount());
        data.setNetSalesAmount(data.getNetSalesAmount() + t.getAmount());
    }

    public void addCreditCardData(DebitCardTransaction t) {
        CreditCardData data = this.creditCardDatas.get(t.getCardType());
        if (data == null) {
            data = new CreditCardData();
            data.setCardName(t.getCardType());
            this.creditCardDatas.put(t.getCardType(), data);
        }
        data.setSalesCount(data.getSalesCount() + 1);
        data.setSalesAmount(data.getSalesAmount() + t.getAmount());
        data.setNetSalesAmount(data.getNetSalesAmount() + t.getAmount());
    }

    public void addDrawerPullData(DrawerPullData data) {
        this.drawerPullDatas.add(data);
    }

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

    public Date getToDate() {
        return this.toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public DrawerPullDataTableModel getDrawerPullDataTableModel() {
        DrawerPullDataTableModel model = new DrawerPullDataTableModel();
        model.setRows(this.drawerPullDatas);
        return model;
    }

    public CreditCardDataTableModel getCreditCardDataTableModel() {
        CreditCardDataTableModel model = new CreditCardDataTableModel();
        ArrayList<CreditCardData> list = new ArrayList<CreditCardData>(this.creditCardDatas.values());
        model.setRows(list);
        return model;
    }

    public double getChargedTips() {
        return this.chargedTips;
    }

    public void setChargedTips(double chargedTips) {
        this.chargedTips = chargedTips;
    }

    public double getGiftCertChangeAmount() {
        return this.giftCertChangeAmount;
    }

    public void setGiftCertChangeAmount(double giftCertChangeAmount) {
        this.giftCertChangeAmount = giftCertChangeAmount;
    }

    public int getGiftCertChangeCount() {
        return this.giftCertChangeCount;
    }

    public void setGiftCertChangeCount(int giftCertChangeCount) {
        this.giftCertChangeCount = giftCertChangeCount;
    }

    public double getGiftCertReturnAmount() {
        return this.giftCertReturnAmount;
    }

    public void setGiftCertReturnAmount(double giftCertReturnAmount) {
        this.giftCertReturnAmount = giftCertReturnAmount;
    }

    public int getGiftCertReturnCount() {
        return this.giftCertReturnCount;
    }

    public void setGiftCertReturnCount(int giftCertReturnCount) {
        this.giftCertReturnCount = giftCertReturnCount;
    }

    public double getTipsDifferential() {
        return this.tipsDifferential;
    }

    public void setTipsDifferential(double tipsDifferential) {
        this.tipsDifferential = tipsDifferential;
    }

    public double getTipsPaid() {
        return this.tipsPaid;
    }

    public void setTipsPaid(double tipsPaid) {
        this.tipsPaid = tipsPaid;
    }

    public int getTipsCount() {
        return this.tipsCount;
    }

    public void setTipsCount(int tipsCount) {
        this.tipsCount = tipsCount;
    }

    public class CreditCardDataTableModel
    extends ListTableModel {
        public CreditCardDataTableModel() {
            this.setColumnNames(new String[]{"creditCard", "salesCount", "salesAmount", "returnCount", "returnAmount", "netAmount", "netTipsAmount", "percentage"});
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CreditCardData data = (CreditCardData)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return data.cardName;
                }
                case 1: {
                    return data.salesCount;
                }
                case 2: {
                    return data.salesAmount;
                }
                case 3: {
                    return data.returnCount;
                }
                case 4: {
                    return data.returnAmount;
                }
                case 5: {
                    return data.netSalesAmount;
                }
                case 6: {
                    return data.netTipsAmount;
                }
                case 7: {
                    return data.percentage;
                }
            }
            return null;
        }
    }

    public class DrawerPullDataTableModel
    extends ListTableModel {
        public DrawerPullDataTableModel() {
            this.setColumnNames(new String[]{"no", "count", "ideal", "actual", "variant"});
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DrawerPullData data = (DrawerPullData)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return POSConstants.DRAWER_PULL_ + data.drawerPullId;
                }
                case 1: {
                    return data.getTicketCount();
                }
                case 2: {
                    return data.idealAmount;
                }
                case 3: {
                    return data.actualAmount;
                }
                case 4: {
                    return data.getVarinceAmount();
                }
            }
            return null;
        }
    }

    public static class CreditCardData {
        String cardName;
        int salesCount;
        double salesAmount;
        int returnCount;
        double returnAmount;
        double netSalesAmount;
        double netTipsAmount;
        double percentage;

        public String getCardName() {
            return this.cardName;
        }

        public void setCardName(String cardName) {
            this.cardName = cardName;
        }

        public double getNetSalesAmount() {
            return this.netSalesAmount;
        }

        public void setNetSalesAmount(double netSalesAmount) {
            this.netSalesAmount = netSalesAmount;
        }

        public double getNetTipsAmount() {
            return this.netTipsAmount;
        }

        public void setNetTipsAmount(double netTipsAmount) {
            this.netTipsAmount = netTipsAmount;
        }

        public double getPercentage() {
            return this.percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }

        public double getReturnAmount() {
            return this.returnAmount;
        }

        public void setReturnAmount(double returnAmount) {
            this.returnAmount = returnAmount;
        }

        public int getReturnCount() {
            return this.returnCount;
        }

        public void setReturnCount(int returnCount) {
            this.returnCount = returnCount;
        }

        public double getSalesAmount() {
            return this.salesAmount;
        }

        public void setSalesAmount(double salesAmount) {
            this.salesAmount = salesAmount;
        }

        public int getSalesCount() {
            return this.salesCount;
        }

        public void setSalesCount(int salesCount) {
            this.salesCount = salesCount;
        }
    }

    public static class DrawerPullData {
        private Integer drawerPullId;
        private int ticketCount;
        private double idealAmount;
        private double actualAmount;
        private double varinceAmount;

        public double getActualAmount() {
            return this.actualAmount;
        }

        public void setActualAmount(double actualAmount) {
            this.actualAmount = actualAmount;
        }

        public Integer getDrawerPullId() {
            return this.drawerPullId;
        }

        public void setDrawerPullId(Integer drawerPullId) {
            this.drawerPullId = drawerPullId;
        }

        public double getIdealAmount() {
            return this.idealAmount;
        }

        public void setIdealAmount(double idealAmount) {
            this.idealAmount = idealAmount;
        }

        public int getTicketCount() {
            return this.ticketCount;
        }

        public void setTicketCount(int ticketCount) {
            this.ticketCount = ticketCount;
        }

        public double getVarinceAmount() {
            return this.varinceAmount;
        }

        public void setVarinceAmount(double varinceAmount) {
            this.varinceAmount = varinceAmount;
        }
    }
}

