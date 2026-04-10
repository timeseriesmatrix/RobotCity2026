/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.swing.ListTableModel;
import com.floreantpos.util.NumberUtil;
import java.util.ArrayList;
import java.util.List;

public class SalesStatistics {
    private int capacity;
    private int guestCount;
    private double guestPerSeat;
    private double tableTurnOver;
    private double avgGuest;
    private int openChecks;
    private int voidChecks;
    private int oppdChecks;
    private int trngChecks;
    private int ropnChecks;
    private int ntaxChecks;
    private double ntaxAmount;
    private int mergeChecks;
    private double laborHour;
    private double laborCost;
    private double laborSale;
    private int tables;
    private int checkCount;
    private double guestPerCheck;
    private String turnOverTime;
    private double avgCheck;
    private double openAmount;
    private double voidAmount;
    private double paidChecks;
    private double trngAmount;
    private double ropnAmount;
    private double mergeAmount;
    private double labor;
    private double grossSale;
    private double discount;
    private double tax;
    private double netSale;
    private ArrayList<ShiftwiseSalesTableData> salesTableDataList;

    public void calculateOthers() {
        this.netSale = this.grossSale - this.discount;
        if (this.tables > 0) {
            this.tableTurnOver = this.checkCount / this.tables;
        }
        if (this.guestCount > 0) {
            this.avgGuest = this.netSale / (double)this.guestCount;
        }
        if (this.capacity > 0) {
            this.guestPerSeat = this.guestCount / this.capacity;
        }
        if (this.checkCount > 0) {
            this.guestPerCheck = this.guestCount / this.checkCount;
            this.avgCheck = this.grossSale / (double)this.checkCount;
        }
    }

    public double getNetSale() {
        return this.netSale;
    }

    public void setNetSale(double netSale) {
        this.netSale = netSale;
    }

    public double getAvgGuest() {
        return this.avgGuest;
    }

    public void setAvgGuest(double averageGuest) {
        this.avgGuest = averageGuest;
    }

    public double getAvgCheck() {
        return this.avgCheck;
    }

    public void setAvgCheck(double avgChecks) {
        this.avgCheck = avgChecks;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCheckCount() {
        return this.checkCount;
    }

    public void setCheckCount(int checkCount) {
        this.checkCount = checkCount;
    }

    public int getGuestCount() {
        return this.guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public double getGuestPerCheck() {
        return this.guestPerCheck;
    }

    public void setGuestPerCheck(double guestPerCheck) {
        this.guestPerCheck = guestPerCheck;
    }

    public double getGuestPerSeat() {
        return this.guestPerSeat;
    }

    public void setGuestPerSeat(double guestPerSeat) {
        this.guestPerSeat = guestPerSeat;
    }

    public double getLabor() {
        return this.labor;
    }

    public void setLabor(double labor) {
        this.labor = labor;
    }

    public double getLaborCost() {
        return this.laborCost;
    }

    public void setLaborCost(double laborCost) {
        this.laborCost = laborCost;
    }

    public double getLaborHour() {
        return this.laborHour;
    }

    public void setLaborHour(double laborHours) {
        this.laborHour = laborHours;
    }

    public double getLaborSale() {
        return this.laborSale;
    }

    public void setLaborSale(double laborSales) {
        this.laborSale = laborSales;
    }

    public double getMergeAmount() {
        return this.mergeAmount;
    }

    public void setMergeAmount(double mergeAmount) {
        this.mergeAmount = mergeAmount;
    }

    public int getMergeChecks() {
        return this.mergeChecks;
    }

    public void setMergeChecks(int mergeChecks) {
        this.mergeChecks = mergeChecks;
    }

    public double getNtaxAmount() {
        return this.ntaxAmount;
    }

    public void setNtaxAmount(double ntaxAmount) {
        this.ntaxAmount = ntaxAmount;
    }

    public int getNtaxChecks() {
        return this.ntaxChecks;
    }

    public void setNtaxChecks(int ntaxChecks) {
        this.ntaxChecks = ntaxChecks;
    }

    public double getOpenAmount() {
        return this.openAmount;
    }

    public void setOpenAmount(double openAmount) {
        this.openAmount = openAmount;
    }

    public int getOpenChecks() {
        return this.openChecks;
    }

    public void setOpenChecks(int openChecks) {
        this.openChecks = openChecks;
    }

    public int getOppdChecks() {
        return this.oppdChecks;
    }

    public void setOppdChecks(int oppdChecks) {
        this.oppdChecks = oppdChecks;
    }

    public double getPaidChecks() {
        return this.paidChecks;
    }

    public void setPaidChecks(double paidChecks) {
        this.paidChecks = paidChecks;
    }

    public double getRopnAmount() {
        return this.ropnAmount;
    }

    public void setRopnAmount(double ropnAmount) {
        this.ropnAmount = ropnAmount;
    }

    public int getRopnChecks() {
        return this.ropnChecks;
    }

    public void setRopnChecks(int ropnChecks) {
        this.ropnChecks = ropnChecks;
    }

    public int getTables() {
        return this.tables;
    }

    public void setTables(int tables) {
        this.tables = tables;
    }

    public double getTableTurnOver() {
        return this.tableTurnOver;
    }

    public void setTableTurnOver(double tableTurnOver) {
        this.tableTurnOver = tableTurnOver;
    }

    public double getTrngAmount() {
        return this.trngAmount;
    }

    public void setTrngAmount(double trngAmount) {
        this.trngAmount = trngAmount;
    }

    public int getTrngChecks() {
        return this.trngChecks;
    }

    public void setTrngChecks(int trngChecks) {
        this.trngChecks = trngChecks;
    }

    public String getTurnOverTime() {
        return this.turnOverTime;
    }

    public void setTurnOverTime(String turnOverTime) {
        this.turnOverTime = turnOverTime;
    }

    public double getVoidAmount() {
        return this.voidAmount;
    }

    public void setVoidAmount(double voidAmount) {
        this.voidAmount = voidAmount;
    }

    public int getVoidChecks() {
        return this.voidChecks;
    }

    public void setVoidChecks(int voidChecks) {
        this.voidChecks = voidChecks;
    }

    public ArrayList<ShiftwiseSalesTableData> getSalesTableDataList() {
        return this.salesTableDataList;
    }

    public void addSalesTableData(ShiftwiseSalesTableData data) {
        if (this.salesTableDataList == null) {
            this.salesTableDataList = new ArrayList();
        }
        this.salesTableDataList.add(data);
    }

    public double getGrossSale() {
        return this.grossSale;
    }

    public void setGrossSale(double grossSale) {
        this.grossSale = grossSale;
    }

    public double getDiscount() {
        return this.discount;
    }

    public void setDiscount(double totalDiscount) {
        this.discount = totalDiscount;
    }

    public double getTax() {
        return this.tax;
    }

    public void setTax(double totalTax) {
        this.tax = totalTax;
    }

    public static class ShiftwiseDataTableModel
    extends ListTableModel {
        public ShiftwiseDataTableModel(List<ShiftwiseSalesTableData> list) {
            super(new String[]{"DayPart", "profitCenter", "Check", "Guest", "Enter", "Sales", "AvgChk", "AvgGst", "Percentage"}, list);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ShiftwiseSalesTableData data = (ShiftwiseSalesTableData)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return data.getShiftName();
                }
                case 1: {
                    return data.getProfitCenter();
                }
                case 2: {
                    return String.valueOf(data.getCheckCount());
                }
                case 3: {
                    return String.valueOf(data.getGuestCount());
                }
                case 4: {
                    return " ";
                }
                case 5: {
                    return NumberUtil.formatNumber(data.getTotalSales());
                }
                case 6: {
                    return NumberUtil.formatNumber(data.getAvgChecks());
                }
                case 7: {
                    return NumberUtil.formatNumber(data.getAvgGuests());
                }
                case 8: {
                    return NumberUtil.formatNumber(data.getPercentage());
                }
            }
            return null;
        }
    }

    public static class ShiftwiseSalesTableData {
        private String shiftName;
        private String profitCenter;
        private int checkCount;
        private int guestCount;
        private int entre;
        private double totalSales;
        private double avgChecks;
        private double avgGuests;
        private double percentage;

        public double getAvgChecks() {
            return this.avgChecks;
        }

        public void setAvgChecks(double avgChecks) {
            this.avgChecks = avgChecks;
        }

        public double getAvgGuests() {
            return this.avgGuests;
        }

        public void setAvgGuests(double avgGuests) {
            this.avgGuests = avgGuests;
        }

        public int getCheckCount() {
            return this.checkCount;
        }

        public void setCheckCount(int checkCount) {
            this.checkCount = checkCount;
        }

        public int getEntre() {
            return this.entre;
        }

        public void setEntre(int entre) {
            this.entre = entre;
        }

        public int getGuestCount() {
            return this.guestCount;
        }

        public void setGuestCount(int guestCount) {
            this.guestCount = guestCount;
        }

        public double getPercentage() {
            return this.percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }

        public String getShiftName() {
            return this.shiftName;
        }

        public void setShiftName(String shiftName) {
            this.shiftName = shiftName;
        }

        public double getTotalSales() {
            return this.totalSales;
        }

        public void setTotalSales(double totalSales) {
            this.totalSales = totalSales;
        }

        public void calculateOthers() {
            if (this.totalSales > 0.0 && this.checkCount > 0) {
                this.avgChecks = this.totalSales / (double)this.checkCount;
            }
            if (this.totalSales > 0.0 && this.guestCount > 0) {
                this.avgGuests = this.totalSales / (double)this.guestCount;
            }
        }

        public String getProfitCenter() {
            return this.profitCenter;
        }

        public void setProfitCenter(String profitCenter) {
            this.profitCenter = profitCenter;
        }
    }
}

