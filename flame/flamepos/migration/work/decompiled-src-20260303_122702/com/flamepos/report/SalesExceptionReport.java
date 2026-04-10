/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.model.Discount;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketDiscount;
import com.floreantpos.swing.ListTableModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesExceptionReport {
    private Date fromDate;
    private Date toDate;
    private Date reportTime;
    private List<VoidData> voidedTickets = new ArrayList<VoidData>();
    private Map<Integer, DiscountData> disountMap = new HashMap<Integer, DiscountData>();

    public void addVoidToVoidData(Ticket ticket) {
        double amount = ticket.getSubtotalAmount();
        String voidReason = ticket.getVoidReason();
        VoidData voidData = new VoidData();
        voidData.id = ticket.getId();
        voidData.setReasonCode(voidReason);
        voidData.setCount(1);
        voidData.setAmount(amount);
        voidData.wasted = ticket.isWasted();
        this.voidedTickets.add(voidData);
    }

    public void addDiscountData(Ticket ticket) {
        List<TicketDiscount> discounts = ticket.getDiscounts();
        if (discounts != null) {
            for (TicketDiscount discount : discounts) {
                String name = discount.getName();
                DiscountData discountData = this.disountMap.get(discount.getDiscountId());
                if (discountData == null) {
                    discountData = new DiscountData();
                    discountData.code = discount.getDiscountId();
                    discountData.name = name;
                    this.disountMap.put(discount.getDiscountId(), discountData);
                }
                discountData.totalCount = ++discountData.totalCount;
                discountData.totalDiscount = discountData.totalDiscount + discount.getValue();
                discountData.totalGuest = discountData.totalGuest + (double)ticket.getNumberOfGuests().intValue();
                discountData.totalNetSales = discountData.totalNetSales + ticket.getSubtotalAmount();
                discountData.partySize = discountData.totalGuest / (double)discountData.totalCount;
                discountData.checkSize = discountData.totalNetSales / (double)discountData.totalCount;
            }
        }
    }

    public void addEmptyDiscounts(List<Discount> discounts) {
        if (discounts != null) {
            for (Discount discount : discounts) {
                String name = discount.getName();
                DiscountData discountData = this.disountMap.get(discount.getId());
                if (discountData != null) continue;
                discountData = new DiscountData();
                discountData.code = discount.getId();
                discountData.name = name;
                this.disountMap.put(discount.getId(), discountData);
            }
        }
    }

    public static void main(String[] args) {
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

    public VoidTableModel getVoidTableModel() {
        VoidTableModel model = new VoidTableModel();
        model.setRows(this.voidedTickets);
        return model;
    }

    public DiscountTableModel getDiscountTableModel() {
        DiscountTableModel model = new DiscountTableModel();
        ArrayList<DiscountData> list = new ArrayList<DiscountData>(this.disountMap.values());
        model.setRows(list);
        return model;
    }

    public class DiscountTableModel
    extends ListTableModel {
        public DiscountTableModel() {
            this.setColumnNames(new String[]{"no", "name", "code", "totalCount", "totalDiscount", "totalNetSales", "totalGuests", "partySize", "checkSize", "countPercent", "ratioDnet"});
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DiscountData data = (DiscountData)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return data.code;
                }
                case 1: {
                    return data.name;
                }
                case 2: {
                    return data.code;
                }
                case 3: {
                    return data.totalCount;
                }
                case 4: {
                    return data.totalDiscount;
                }
                case 5: {
                    return data.totalNetSales;
                }
                case 6: {
                    return data.totalGuest;
                }
                case 7: {
                    return data.partySize;
                }
                case 8: {
                    return data.checkSize;
                }
                case 9: {
                    return data.countPercentage;
                }
                case 10: {
                    return data.ratioDNet;
                }
            }
            return null;
        }
    }

    public class VoidTableModel
    extends ListTableModel {
        public VoidTableModel() {
            this.setColumnNames(new String[]{"code", "reason", "wast", "qty", "amount"});
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            VoidData data = (VoidData)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return String.valueOf(data.id);
                }
                case 1: {
                    return data.getReasonCode();
                }
                case 2: {
                    return data.wasted ? "Y" : "N";
                }
                case 3: {
                    return String.valueOf(data.getCount());
                }
                case 4: {
                    return data.getAmount();
                }
            }
            return null;
        }
    }

    public static class DiscountData {
        private int code;
        private String name;
        private int totalCount;
        private double totalDiscount;
        private double totalNetSales;
        private double totalGuest;
        private double partySize;
        private double checkSize;
        private double countPercentage;
        private double ratioDNet;

        public double getCheckSize() {
            return this.checkSize;
        }

        public void setCheckSize(double checkSize) {
            this.checkSize = checkSize;
        }

        public int getCode() {
            return this.code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public double getCountPercentage() {
            return this.countPercentage;
        }

        public void setCountPercentage(double countPercentage) {
            this.countPercentage = countPercentage;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPartySize() {
            return this.partySize;
        }

        public void setPartySize(double partySize) {
            this.partySize = partySize;
        }

        public double getRatioDNet() {
            return this.ratioDNet;
        }

        public void setRatioDNet(double ratioDNet) {
            this.ratioDNet = ratioDNet;
        }

        public int getTotalCount() {
            return this.totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public double getTotalDiscount() {
            return this.totalDiscount;
        }

        public void setTotalDiscount(double totalDiscount) {
            this.totalDiscount = totalDiscount;
        }

        public double getTotalGuest() {
            return this.totalGuest;
        }

        public void setTotalGuest(double totalGuest) {
            this.totalGuest = totalGuest;
        }

        public double getTotalNetSales() {
            return this.totalNetSales;
        }

        public void setTotalNetSales(double totalNetSales) {
            this.totalNetSales = totalNetSales;
        }
    }

    public static class VoidData {
        Integer id;
        private String reasonCode;
        private int count;
        private double amount;
        boolean wasted;

        public double getAmount() {
            return this.amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public int getCount() {
            return this.count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getReasonCode() {
            return this.reasonCode;
        }

        public void setReasonCode(String reasonCode) {
            this.reasonCode = reasonCode;
        }
    }
}

