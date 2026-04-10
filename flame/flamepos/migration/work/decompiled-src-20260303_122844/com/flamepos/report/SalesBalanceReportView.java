/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  net.sf.jasperreports.engine.JRDataSource
 *  net.sf.jasperreports.engine.JREmptyDataSource
 *  net.sf.jasperreports.engine.JasperFillManager
 *  net.sf.jasperreports.engine.JasperPrint
 *  net.sf.jasperreports.engine.JasperReport
 *  net.sf.jasperreports.view.JRViewer
 *  org.apache.log4j.Logger
 *  org.jdesktop.swingx.JXDatePicker
 */
package com.floreantpos.report;

import com.floreantpos.POSConstants;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.report.ReportUtil;
import com.floreantpos.report.SalesBalanceReport;
import com.floreantpos.report.service.ReportService;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;
import com.floreantpos.util.NumberUtil;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JRViewer;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXDatePicker;

public class SalesBalanceReportView
extends JPanel {
    private SimpleDateFormat fullDateFormatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
    private SimpleDateFormat shortDateFormatter = new SimpleDateFormat("dd MMM yyyy");
    private JXDatePicker fromDatePicker = UiUtil.getCurrentMonthStart();
    private JXDatePicker toDatePicker = UiUtil.getCurrentMonthEnd();
    private JComboBox cbUserType;
    private JButton btnToday;
    private JButton btnGo = new JButton(POSConstants.GO);
    private JPanel reportContainer;

    public SalesBalanceReportView() {
        super(new BorderLayout());
        JPanel topPanel = new JPanel((LayoutManager)new MigLayout());
        this.btnToday = new JButton(POSConstants.TODAYS_REPORT.toUpperCase());
        this.btnToday.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Date today = new Date();
                try {
                    SalesBalanceReportView.this.viewReport(today, today);
                }
                catch (Exception ex) {
                    Logger.getLogger(SalesBalanceReportView.class).debug((Object)ex);
                }
            }
        });
        this.cbUserType = new JComboBox();
        UserDAO dao = new UserDAO();
        List<User> userTypes = dao.findAll();
        Vector<Object> list = new Vector<Object>();
        list.add(POSConstants.ALL);
        list.addAll(userTypes);
        this.cbUserType.setModel(new DefaultComboBoxModel(list));
        topPanel.add(new JLabel(POSConstants.FROM + ":"));
        topPanel.add((Component)this.fromDatePicker);
        topPanel.add(new JLabel(POSConstants.TO + ":"));
        topPanel.add((Component)this.toDatePicker);
        topPanel.add(new JLabel(POSConstants.USER + ":"));
        topPanel.add(this.cbUserType);
        topPanel.add((Component)this.btnGo, "width 60!");
        topPanel.add(this.btnToday);
        this.add((Component)topPanel, "North");
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        centerPanel.add((Component)new JSeparator(), "North");
        this.reportContainer = new JPanel(new BorderLayout());
        centerPanel.add(this.reportContainer);
        this.add(centerPanel);
        this.btnGo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Date fromDate = SalesBalanceReportView.this.fromDatePicker.getDate();
                    Date toDate = SalesBalanceReportView.this.toDatePicker.getDate();
                    SalesBalanceReportView.this.viewReport(fromDate, toDate);
                }
                catch (Exception e1) {
                    POSMessageDialog.showError(SalesBalanceReportView.this, POSConstants.ERROR_MESSAGE, e1);
                }
            }
        });
    }

    private void viewReport(Date fromDate, Date toDate) throws Exception {
        if (fromDate.after(toDate)) {
            POSMessageDialog.showError(POSUtil.getFocusedWindow(), POSConstants.FROM_DATE_CANNOT_BE_GREATER_THAN_TO_DATE_);
            return;
        }
        fromDate = DateUtil.startOfDay(fromDate);
        toDate = DateUtil.endOfDay(toDate);
        User user = null;
        if (!this.cbUserType.getSelectedItem().equals(POSConstants.ALL)) {
            user = (User)this.cbUserType.getSelectedItem();
        }
        ReportService reportService = new ReportService();
        SalesBalanceReport report = reportService.getSalesBalanceReport(fromDate, toDate, user);
        HashMap<String, String> map = new HashMap<String, String>();
        ReportUtil.populateRestaurantProperties(map);
        map.put("fromDate", this.shortDateFormatter.format(fromDate));
        map.put("toDate", this.shortDateFormatter.format(toDate));
        map.put("reportTime", this.fullDateFormatter.format(new Date()));
        map.put("userName", user == null ? POSConstants.ALL : user.getFullName());
        map.put("grossTaxableSales", NumberUtil.formatNumber(report.getGrossTaxableSalesAmount()));
        map.put("grossNonTaxableSales", NumberUtil.formatNumber(report.getGrossNonTaxableSalesAmount()));
        map.put("discounts", NumberUtil.formatNumber(report.getDiscountAmount()));
        map.put("netSales", NumberUtil.formatNumber(report.getNetSalesAmount()));
        map.put("salesTaxes", NumberUtil.formatNumber(report.getSalesTaxAmount()));
        map.put("totalRevenues", NumberUtil.formatNumber(report.getTotalRevenueAmount()));
        map.put("giftCertSold", NumberUtil.formatNumber(report.getGiftCertSalesAmount()));
        map.put("payIns", NumberUtil.formatNumber(report.getPayInsAmount()));
        map.put("chargedTips", NumberUtil.formatNumber(report.getChargedTipsAmount()));
        map.put("grossReceipts", NumberUtil.formatNumber(report.getGrossReceiptsAmount()));
        map.put("cashReceipts", NumberUtil.formatNumber(report.getCashReceiptsAmount()));
        map.put("creditCardReceipts", NumberUtil.formatNumber(report.getCreditCardReceiptsAmount()));
        map.put("grossTipsPaid", NumberUtil.formatNumber(report.getGrossTipsPaidAmount()));
        map.put("arReceipts", NumberUtil.formatNumber(report.getArReceiptsAmount()));
        map.put("giftCertReturns", NumberUtil.formatNumber(report.getGiftCertReturnAmount()));
        map.put("giftCertChange", NumberUtil.formatNumber(report.getGiftCertChangeAmount()));
        map.put("cashBack", NumberUtil.formatNumber(report.getCashBackAmount()));
        map.put("receiptDiff", NumberUtil.formatNumber(report.getReceiptDiffAmount()));
        map.put("tipsDiscount", NumberUtil.formatNumber(report.getTipsDiscountAmount()));
        map.put("cashPayout", NumberUtil.formatNumber(report.getCashPayoutAmount()));
        map.put("cashAccountable", NumberUtil.formatNumber(report.getCashAccountableAmount()));
        map.put("drawerPulls", NumberUtil.formatNumber(report.getDrawerPullsAmount()));
        map.put("coCurrent", NumberUtil.formatNumber(report.getCoCurrentAmount()));
        map.put("coPrevious", NumberUtil.formatNumber(report.getCoPreviousAmount()));
        map.put("coOverShort", NumberUtil.formatNumber(report.getOverShortAmount()));
        map.put("days", String.valueOf((int)((double)(toDate.getTime() - fromDate.getTime()) * (1.15740741 * Math.pow(10.0, -8.0))) + 1));
        map.put("visaCreditCardSum", NumberUtil.formatNumber(report.getVisaCreditCardAmount()));
        map.put("mastercardSum", NumberUtil.formatNumber(report.getMasterCardAmount()));
        map.put("amexSum", NumberUtil.formatNumber(report.getAmexAmount()));
        map.put("discoverySum", NumberUtil.formatNumber(report.getDiscoveryAmount()));
        JasperReport jasperReport = ReportUtil.getReport("sales_summary_balance_report");
        JasperPrint jasperPrint = JasperFillManager.fillReport((JasperReport)jasperReport, map, (JRDataSource)new JREmptyDataSource());
        JRViewer viewer = new JRViewer(jasperPrint);
        this.reportContainer.removeAll();
        this.reportContainer.add((Component)viewer);
        this.reportContainer.revalidate();
    }
}

