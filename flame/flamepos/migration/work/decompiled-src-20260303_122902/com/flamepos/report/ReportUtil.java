/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.sf.jasperreports.engine.JasperCompileManager
 *  net.sf.jasperreports.engine.JasperReport
 *  net.sf.jasperreports.engine.util.JRLoader
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.floreantpos.report;

import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.dao.RestaurantDAO;
import com.floreantpos.report.ReceiptPrintService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReportUtil {
    private static Log logger = LogFactory.getLog(ReportUtil.class);
    private static final String USER_REPORT_DIR = "/printerlayouts/";
    private static final String DEFAULT_REPORT_DIR = "/com/floreantpos/report/template/";

    public static void populateRestaurantProperties(Map map) {
        JasperReport reportHeader = ReportUtil.getReport("report_header");
        RestaurantDAO dao = new RestaurantDAO();
        Restaurant restaurant = dao.get(1);
        map.put("restaurantName", restaurant.getName());
        map.put("addressLine1", restaurant.getAddressLine1());
        map.put("addressLine2", restaurant.getAddressLine2());
        map.put("addressLine3", restaurant.getAddressLine3());
        map.put("phone", restaurant.getTelephone());
        map.put("reportHeader", reportHeader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static JasperReport getReport(String reportName) {
        JasperReport jasperReport;
        InputStream resource;
        block5: {
            resource = null;
            resource = ReceiptPrintService.class.getResourceAsStream(USER_REPORT_DIR + reportName + ".jasper");
            if (resource != null) break block5;
            JasperReport jasperReport2 = ReportUtil.compileReport(reportName);
            IOUtils.closeQuietly((InputStream)resource);
            return jasperReport2;
        }
        try {
            jasperReport = (JasperReport)JRLoader.loadObject((InputStream)resource);
        }
        catch (Exception e) {
            JasperReport jasperReport3;
            try {
                logger.info((Object)(Messages.getString("ReportUtil.8") + reportName + " from user directory, loading default report"));
                jasperReport3 = ReportUtil.getDefaultReport(reportName);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(resource);
                throw throwable;
            }
            IOUtils.closeQuietly((InputStream)resource);
            return jasperReport3;
        }
        IOUtils.closeQuietly((InputStream)resource);
        return jasperReport;
    }

    private static JasperReport compileReport(String reportName) throws Exception {
        JasperReport jasperReport;
        InputStream in = null;
        InputStream in2 = null;
        FileOutputStream out = null;
        File jasperFile = null;
        try {
            File jrxmlFile = new File(ReceiptPrintService.class.getResource(USER_REPORT_DIR + reportName + ".jrxml").getFile());
            File dir = jrxmlFile.getParentFile();
            jasperFile = new File(dir, reportName + ".jasper");
            in = ReceiptPrintService.class.getResourceAsStream(USER_REPORT_DIR + reportName + ".jrxml");
            out = new FileOutputStream(jasperFile);
            JasperCompileManager.compileReportToStream((InputStream)in, (OutputStream)out);
            in2 = ReceiptPrintService.class.getResourceAsStream(USER_REPORT_DIR + reportName + ".jasper");
            jasperReport = (JasperReport)JRLoader.loadObject((InputStream)in2);
        }
        catch (Exception e) {
            try {
                IOUtils.closeQuietly(out);
                if (jasperFile != null) {
                    jasperFile.delete();
                }
                throw e;
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(in2);
                IOUtils.closeQuietly(out);
                throw throwable;
            }
        }
        IOUtils.closeQuietly((InputStream)in);
        IOUtils.closeQuietly((InputStream)in2);
        IOUtils.closeQuietly((OutputStream)out);
        return jasperReport;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static JasperReport getDefaultReport(String reportName) {
        InputStream resource = null;
        try {
            resource = ReceiptPrintService.class.getResourceAsStream(DEFAULT_REPORT_DIR + reportName + ".jasper");
            JasperReport jasperReport = (JasperReport)JRLoader.loadObject((InputStream)resource);
            IOUtils.closeQuietly((InputStream)resource);
            return jasperReport;
        }
        catch (Exception e) {
            logger.error((Object)e);
            JasperReport jasperReport = null;
            return jasperReport;
        }
        finally {
            IOUtils.closeQuietly(resource);
        }
    }

    public static void main(String[] args) {
        URL resource = ReceiptPrintService.class.getResource("/printerlayouts/ticket-receipt.jrxml");
        String externalForm = resource.getFile();
        PosLog.info(ReportUtil.class, resource.getProtocol());
        PosLog.info(ReportUtil.class, externalForm);
    }
}

