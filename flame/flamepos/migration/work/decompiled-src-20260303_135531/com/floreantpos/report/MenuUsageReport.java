/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.swing.ListTableModel;
import com.floreantpos.util.NumberUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MenuUsageReport {
    private Date fromDate;
    private Date toDate;
    private Date reportTime;
    private List<MenuUsageReportData> reportDatas = new ArrayList<MenuUsageReportData>();
    private MenuUsageReportTableModel tableModel;

    public MenuUsageReportTableModel getTableModel() {
        if (this.tableModel == null) {
            this.tableModel = new MenuUsageReportTableModel(this.reportDatas);
        }
        return this.tableModel;
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

    public void addReportData(MenuUsageReportData data) {
        this.reportDatas.add(data);
    }

    public static class MenuUsageReportTableModel
    extends ListTableModel {
        public MenuUsageReportTableModel(List<MenuUsageReportData> datas) {
            super(new String[]{"category", "count", "grossSale", "discount", "netSale", "avgSale", "profit", "cost", "percentage"}, datas);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            MenuUsageReportData data = (MenuUsageReportData)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return data.getCategoryName();
                }
                case 1: {
                    return String.valueOf(data.getCount());
                }
                case 2: {
                    return NumberUtil.formatNumber(data.getGrossSales());
                }
                case 3: {
                    return NumberUtil.formatNumber(data.getDiscount());
                }
                case 4: {
                    return NumberUtil.formatNumber(data.getNetSales());
                }
                case 5: {
                    return " ";
                }
                case 6: {
                    return NumberUtil.formatNumber(data.getProfit());
                }
                case 7: {
                    return " ";
                }
                case 8: {
                    return " ";
                }
            }
            return null;
        }
    }

    public static class MenuUsageReportData {
        private int count;
        private String categoryName;
        private double grossSales;
        private double discount;
        private double netSales;
        private double avgSales;
        private double profit;
        private double costPercentage;
        private double percentage;

        public double getAvgSales() {
            return this.avgSales;
        }

        public void setAvgSales(double avgSales) {
            this.avgSales = avgSales;
        }

        public String getCategoryName() {
            return this.categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public double getCostPercentage() {
            return this.costPercentage;
        }

        public void setCostPercentage(double costPercentage) {
            this.costPercentage = costPercentage;
        }

        public int getCount() {
            return this.count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public double getDiscount() {
            return this.discount;
        }

        public void setDiscount(double discount) {
            this.discount = discount;
        }

        public double getGrossSales() {
            return this.grossSales;
        }

        public void setGrossSales(double grossSales) {
            this.grossSales = grossSales;
        }

        public double getNetSales() {
            return this.netSales;
        }

        public void setNetSales(double netSales) {
            this.netSales = netSales;
        }

        public double getPercentage() {
            return this.percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }

        public double getProfit() {
            return this.profit;
        }

        public void setProfit(double profit) {
            this.profit = profit;
        }

        public void calculate() {
            this.netSales = this.grossSales - this.discount;
        }
    }
}

