/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.sf.jasperreports.engine.JRDataSource
 *  net.sf.jasperreports.engine.JasperFillManager
 *  net.sf.jasperreports.engine.JasperPrint
 *  net.sf.jasperreports.engine.JasperReport
 *  net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
 *  net.sf.jasperreports.view.JasperViewer
 */
package com.floreantpos.report;

import com.floreantpos.report.ReportUtil;
import com.floreantpos.swing.ListTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

public class SalesAnalysisReportModel
extends ListTableModel {
    public SalesAnalysisReportModel(List<SalesAnalysisData> dataList) {
        super(new String[]{"shiftName", "categoryName", "count", "gross", "discount", "netSales", "avgGross", "avgDiscount", "avgNet", "percentage"}, dataList);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SalesAnalysisData data = (SalesAnalysisData)this.rows.get(rowIndex);
        switch (columnIndex) {
            case 0: {
                return data.shiftName;
            }
            case 1: {
                return data.categoryName;
            }
            case 2: {
                return String.valueOf(data.count);
            }
            case 3: {
                return String.valueOf(data.gross);
            }
            case 4: {
                return String.valueOf(data.discount);
            }
            case 5: {
                return String.valueOf(data.netSales);
            }
            case 6: {
                return " ";
            }
            case 7: {
                return " ";
            }
            case 8: {
                return " ";
            }
            case 9: {
                return " ";
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        ArrayList<SalesAnalysisData> list = new ArrayList<SalesAnalysisData>();
        SalesAnalysisData data = new SalesAnalysisData();
        data.setShiftName("SHIFT1");
        data.setCategoryName("C");
        list.add(data);
        data = new SalesAnalysisData();
        data.setShiftName("SHIFT1");
        data.setCategoryName("C2");
        list.add(data);
        data = new SalesAnalysisData();
        data.setShiftName("SHIFT2");
        data.setCategoryName("C");
        list.add(data);
        JasperReport report = ReportUtil.getReport("sales_summary_report2");
        JasperPrint print = JasperFillManager.fillReport((JasperReport)report, new HashMap(), (JRDataSource)new JRBeanCollectionDataSource(list));
        JasperViewer.viewReport((JasperPrint)print, (boolean)true);
    }

    public static class SalesAnalysisData {
        private String shiftName;
        private String categoryName;
        private int count;
        private double gross;
        private double discount;
        private double netSales;
        private double avgGross;
        private double avgDiscount;
        private double avgNet;
        private double percentage;

        public void calculate() {
            this.netSales = this.gross - this.discount;
        }

        public double getAvgDiscount() {
            return this.avgDiscount;
        }

        public void setAvgDiscount(double avgDiscount) {
            this.avgDiscount = avgDiscount;
        }

        public double getAvgGross() {
            return this.avgGross;
        }

        public void setAvgGross(double avgGross) {
            this.avgGross = avgGross;
        }

        public double getAvgNet() {
            return this.avgNet;
        }

        public void setAvgNet(double avgNet) {
            this.avgNet = avgNet;
        }

        public String getCategoryName() {
            return this.categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
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

        public double getGross() {
            return this.gross;
        }

        public void setGross(double gross) {
            this.gross = gross;
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

        public String getShiftName() {
            return this.shiftName;
        }

        public void setShiftName(String shiftName) {
            this.shiftName = shiftName;
        }
    }
}

