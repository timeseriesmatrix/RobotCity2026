/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.swing.ListTableModel;
import com.floreantpos.util.NumberUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServerProductivityReport {
    private Date fromDate;
    private Date toDate;
    private Date reportTime;
    private List<ServerProductivityReportData> reportDatas = new ArrayList<ServerProductivityReportData>();
    private ServerProductivityReportTableModel tableModel;

    public ServerProductivityReportTableModel getTableModel() {
        if (this.tableModel == null) {
            this.tableModel = new ServerProductivityReportTableModel(this.reportDatas);
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

    public void addReportData(ServerProductivityReportData data) {
        this.reportDatas.add(data);
    }

    public static class ServerProductivityReportTableModel
    extends ListTableModel {
        String[] columnNames = new String[]{"serverName", "categoryName", "totalCheckCount", "totalGuestCount", "totalSales", "netSales", "averageNetSales", "totalAllocation", "grossSales", "salesDiscount", "averageCheck", "averageGuest", "allocation", "checkCount"};

        public ServerProductivityReportTableModel(List<ServerProductivityReportData> datas) {
            this.setColumnNames(this.columnNames);
            this.setRows(datas);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ServerProductivityReportData data = (ServerProductivityReportData)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return data.getServerName();
                }
                case 1: {
                    return data.getCategoryName();
                }
                case 2: {
                    return String.valueOf(data.getTotalCheckCount());
                }
                case 3: {
                    return String.valueOf(data.getTotalGuestCount());
                }
                case 4: {
                    return NumberUtil.formatNumber(data.getTotalSales());
                }
                case 5: {
                    return NumberUtil.formatNumber(data.getNetSales());
                }
                case 6: {
                    return NumberUtil.formatNumber(data.getAverageNetSales());
                }
                case 7: {
                    return NumberUtil.formatNumber(data.getTotalAllocation());
                }
                case 8: {
                    return NumberUtil.formatNumber(data.getGrossSales());
                }
                case 9: {
                    return NumberUtil.formatNumber(data.getSalesDiscount());
                }
                case 10: {
                    return NumberUtil.formatNumber(data.getAverageCheck());
                }
                case 11: {
                    return NumberUtil.formatNumber(data.getAverageGuest());
                }
                case 12: {
                    return NumberUtil.formatNumber(data.getAllocation());
                }
                case 13: {
                    return String.valueOf(data.getCheckCount());
                }
            }
            return null;
        }
    }

    public static class ServerProductivityReportData {
        private String serverName;
        private int totalCheckCount;
        private int totalGuestCount;
        private double totalSales;
        private double totalAllocation;
        private String categoryName;
        private int checkCount;
        private double salesDiscount;
        private double averageCheck;
        private double averageGuest;
        private double grossSales;
        private double netSales;
        private double averageNetSales;
        private double allocation;

        public double getAllocation() {
            return this.allocation;
        }

        public void setAllocation(double allocation) {
            this.allocation = allocation;
        }

        public double getAverageCheck() {
            return this.averageCheck;
        }

        public void setAverageCheck(double averageCheck) {
            this.averageCheck = averageCheck;
        }

        public double getAverageGuest() {
            return this.averageGuest;
        }

        public void setAverageGuest(double averageGuest) {
            this.averageGuest = averageGuest;
        }

        public String getCategoryName() {
            return this.categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public double getSalesDiscount() {
            return this.salesDiscount;
        }

        public void setSalesDiscount(double salesDiscount) {
            this.salesDiscount = salesDiscount;
        }

        public String getServerName() {
            return this.serverName;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public double getTotalAllocation() {
            return this.totalAllocation;
        }

        public void setTotalAllocation(double totalAllocation) {
            this.totalAllocation = totalAllocation;
        }

        public int getTotalCheckCount() {
            return this.totalCheckCount;
        }

        public void setTotalCheckCount(int totalCheckCount) {
            this.totalCheckCount = totalCheckCount;
        }

        public int getTotalGuestCount() {
            return this.totalGuestCount;
        }

        public void setTotalGuestCount(int totalGuestCount) {
            this.totalGuestCount = totalGuestCount;
        }

        public double getTotalSales() {
            return this.totalSales;
        }

        public void setTotalSales(double totalSales) {
            this.totalSales = totalSales;
        }

        public void calculate() {
            if (this.totalCheckCount > 0) {
                this.averageCheck = this.totalSales / (double)this.totalCheckCount;
            }
            if (this.totalGuestCount > 0) {
                this.averageGuest = this.totalSales / (double)this.totalGuestCount;
            }
            this.netSales = this.grossSales - this.salesDiscount;
            if (this.checkCount > 0) {
                this.averageNetSales = this.netSales / (double)this.checkCount;
                this.allocation = (double)this.totalCheckCount / (double)this.checkCount * 100.0;
            }
        }

        public double getAverageNetSales() {
            return this.averageNetSales;
        }

        public void setAverageNetSales(double averageNetSales) {
            this.averageNetSales = averageNetSales;
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

        public int getCheckCount() {
            return this.checkCount;
        }

        public void setCheckCount(int checkCountInCategory) {
            this.checkCount = checkCountInCategory;
        }
    }
}

