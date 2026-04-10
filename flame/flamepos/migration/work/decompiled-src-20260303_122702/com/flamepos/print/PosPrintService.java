/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.sf.jasperreports.engine.JRDataSource
 *  net.sf.jasperreports.engine.JRExporterParameter
 *  net.sf.jasperreports.engine.JasperFillManager
 *  net.sf.jasperreports.engine.JasperPrint
 *  net.sf.jasperreports.engine.JasperReport
 *  net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
 *  net.sf.jasperreports.engine.data.JRTableModelDataSource
 *  net.sf.jasperreports.engine.export.JRPrintServiceExporter
 *  net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.floreantpos.print;

import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.main.Application;
import com.floreantpos.model.DrawerPullReport;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.TipsCashoutReport;
import com.floreantpos.model.TipsCashoutReportTableModel;
import com.floreantpos.model.dao.RestaurantDAO;
import com.floreantpos.report.ReceiptPrintService;
import com.floreantpos.report.ReportUtil;
import com.floreantpos.util.NumberUtil;
import com.floreantpos.util.PrintServiceUtil;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PosPrintService {
    private static Log logger = LogFactory.getLog(PosPrintService.class);

    public static void printDrawerPullReport(DrawerPullReport drawerPullReport, Terminal terminal) {
        try {
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            Restaurant restaurant = RestaurantDAO.getInstance().get(1);
            parameters.put("headerLine1", restaurant.getName());
            parameters.put("terminal", "Terminal # " + terminal.getId());
            if (drawerPullReport.getAssignedUser() != null) {
                parameters.put("user", Messages.getString("PosPrintService.4") + drawerPullReport.getAssignedUser().getFullName());
            }
            parameters.put("date", new Date());
            parameters.put("totalVoid", drawerPullReport.getTotalVoid());
            JasperReport subReportCurrencyBalance = ReportUtil.getReport("drawer-currency-balance");
            JasperReport subReport = ReportUtil.getReport("drawer-pull-void-veport");
            parameters.put("currencyBalanceReport", subReportCurrencyBalance);
            parameters.put("subreportParameter", subReport);
            JasperReport mainReport = ReportUtil.getReport("drawer-pull-report");
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Arrays.asList(drawerPullReport));
            JasperPrint jasperPrint = JasperFillManager.fillReport((JasperReport)mainReport, parameters, (JRDataSource)dataSource);
            jasperPrint.setProperty("printerName", Application.getPrinters().getReceiptPrinter());
            jasperPrint.setName("DrawerPullReport" + drawerPullReport.getId());
            JRPrintServiceExporter exporter = new JRPrintServiceExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, (Object)jasperPrint);
            exporter.setParameter((JRExporterParameter)JRPrintServiceExporterParameter.PRINT_SERVICE, (Object)PrintServiceUtil.getPrintServiceForPrinter(jasperPrint.getProperty("printerName")));
            exporter.exportReport();
        }
        catch (Exception e) {
            PosLog.error(PosPrintService.class, e.getMessage());
            logger.error((Object)"error print drawer pull report", (Throwable)e);
        }
    }

    public static void printServerTipsReport(TipsCashoutReport report) {
        try {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("server", report.getServer());
            parameters.put("fromDate", Application.formatDate(report.getFromDate()));
            parameters.put("toDate", Application.formatDate(report.getToDate()));
            parameters.put("reportDate", Application.formatDate(report.getReportTime()));
            parameters.put("transactionCount", report.getDatas() == null ? "0" : "" + report.getDatas().size());
            parameters.put("cashTips", NumberUtil.formatNumber(report.getCashTipsAmount()));
            parameters.put("chargedTips", NumberUtil.formatNumber(report.getChargedTipsAmount()));
            parameters.put("tipsDue", NumberUtil.formatNumber(report.getTipsDue()));
            Restaurant restaurant = RestaurantDAO.getInstance().get(1);
            parameters.put("headerLine1", restaurant.getName());
            JasperReport mainReport = ReportUtil.getReport("ServerTipsReport");
            JRTableModelDataSource dataSource = new JRTableModelDataSource((TableModel)new TipsCashoutReportTableModel(report.getDatas(), new String[]{"ticketId", "saleType", "ticketTotal", "tips"}));
            JasperPrint jasperPrint = JasperFillManager.fillReport((JasperReport)mainReport, parameters, (JRDataSource)dataSource);
            jasperPrint.setProperty("printerName", Application.getPrinters().getReceiptPrinter());
            ReceiptPrintService.printQuitely(jasperPrint);
        }
        catch (Exception e) {
            PosLog.error(PosPrintService.class, e.getMessage());
            logger.error((Object)"error print tips report", (Throwable)e);
        }
    }
}

