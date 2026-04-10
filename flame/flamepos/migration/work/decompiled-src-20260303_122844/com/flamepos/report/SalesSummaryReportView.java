/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.sf.jasperreports.engine.JRDataSource
 *  net.sf.jasperreports.engine.JasperFillManager
 *  net.sf.jasperreports.engine.JasperPrint
 *  net.sf.jasperreports.engine.JasperReport
 *  net.sf.jasperreports.engine.data.JRTableModelDataSource
 *  net.sf.jasperreports.view.JRViewer
 *  org.jdesktop.layout.GroupLayout
 *  org.jdesktop.layout.GroupLayout$Group
 *  org.jdesktop.swingx.JXDatePicker
 */
package com.floreantpos.report;

import com.floreantpos.POSConstants;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.UserType;
import com.floreantpos.model.dao.SalesSummaryDAO;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.model.dao.UserTypeDAO;
import com.floreantpos.report.ReportUtil;
import com.floreantpos.report.SalesAnalysisReportModel;
import com.floreantpos.report.SalesStatistics;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;
import com.floreantpos.util.NumberUtil;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JRViewer;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.swingx.JXDatePicker;

public class SalesSummaryReportView
extends JPanel {
    public static final int REPORT_KEY_STATISTICS = 1;
    public static final int REPORT_SALES_ANALYSIS = 2;
    private int reportType;
    private JButton btnGo;
    private JComboBox cbTerminal;
    private JComboBox cbUserType;
    private JXDatePicker fromDatePicker;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JSeparator jSeparator1;
    private JPanel reportPanel;
    private JXDatePicker toDatePicker;
    private SimpleDateFormat fullDateFormatter = new SimpleDateFormat("yyyy MMM dd, hh:mm a");
    private SimpleDateFormat shortDateFormatter = new SimpleDateFormat("yyyy MMM dd");
    private Date fromDate;
    private Date toDate;
    private int dateDiff;
    private UserType userType;
    private Terminal terminal;

    public SalesSummaryReportView() {
        this.initComponents();
        UserTypeDAO dao = new UserTypeDAO();
        List<UserType> userTypes = dao.findAll();
        Vector<UserType> list = new Vector<UserType>();
        list.add(null);
        list.addAll(userTypes);
        this.cbUserType.setModel(new DefaultComboBoxModel(list));
        TerminalDAO terminalDAO = new TerminalDAO();
        List<Terminal> terminals = terminalDAO.findAll();
        terminals.add(0, (Terminal)((Object)POSConstants.ALL));
        this.cbTerminal.setModel(new ListComboBoxModel(terminals));
    }

    private void initComponents() {
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.jLabel3 = new JLabel();
        this.jLabel4 = new JLabel();
        this.fromDatePicker = UiUtil.getCurrentMonthStart();
        this.toDatePicker = UiUtil.getCurrentMonthEnd();
        this.cbUserType = new JComboBox();
        this.cbTerminal = new JComboBox();
        this.btnGo = new JButton();
        this.jSeparator1 = new JSeparator();
        this.reportPanel = new JPanel();
        this.jLabel1.setText(POSConstants.FROM + ":");
        this.jLabel2.setText(POSConstants.TO + ":");
        this.jLabel3.setText(POSConstants.USER_TYPE + ":");
        this.jLabel4.setText(POSConstants.TERMINAL_LABEL + ":");
        this.btnGo.setText(POSConstants.GO);
        this.btnGo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                SalesSummaryReportView.this.showReport(evt);
            }
        });
        this.reportPanel.setLayout(new BorderLayout());
        GroupLayout layout = new GroupLayout((Container)this);
        this.setLayout((LayoutManager)layout);
        layout.setHorizontalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jSeparator1, -1, 502, Short.MAX_VALUE).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel1).add((Component)this.jLabel2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1, false).add((Component)this.toDatePicker, -1, -1, Short.MAX_VALUE).add((Component)this.fromDatePicker, -1, -1, Short.MAX_VALUE)).add(20, 20, 20).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel3).add((Component)this.jLabel4)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1, false).add((Component)this.cbTerminal, 0, -1, Short.MAX_VALUE).add((Component)this.cbUserType, 0, 137, Short.MAX_VALUE)).addPreferredGap(0).add((Component)this.btnGo, -2, 72, -2)).add((Component)this.reportPanel, -1, 502, Short.MAX_VALUE)).addContainerGap()));
        layout.setVerticalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel3).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.cbUserType, -2, -1, -2).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel4).add((Component)this.cbTerminal, -2, -1, -2).add((Component)this.btnGo))).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel1).add((Component)this.fromDatePicker, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel2).add((Component)this.toDatePicker, -2, -1, -2)))).addPreferredGap(0).add((Component)this.jSeparator1, -2, 10, -2).addPreferredGap(0).add((Component)this.reportPanel, -1, 303, Short.MAX_VALUE).addContainerGap()));
        layout.linkSize(new Component[]{this.cbTerminal, this.cbUserType, this.jLabel3, this.jLabel4}, 2);
        layout.linkSize(new Component[]{this.fromDatePicker, this.jLabel1, this.jLabel2, this.toDatePicker}, 2);
    }

    private boolean initCriteria() {
        this.fromDate = this.fromDatePicker.getDate();
        this.toDate = this.toDatePicker.getDate();
        if (this.fromDate.after(this.toDate)) {
            POSMessageDialog.showError(POSUtil.getFocusedWindow(), POSConstants.FROM_DATE_CANNOT_BE_GREATER_THAN_TO_DATE_);
            return false;
        }
        this.dateDiff = (int)((double)(this.toDate.getTime() - this.fromDate.getTime()) * (1.15740741 * Math.pow(10.0, -8.0))) + 1;
        this.userType = (UserType)this.cbUserType.getSelectedItem();
        this.terminal = null;
        if (this.cbTerminal.getSelectedItem() instanceof Terminal) {
            this.terminal = (Terminal)this.cbTerminal.getSelectedItem();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(this.fromDate);
        calendar.set(1, calendar2.get(1));
        calendar.set(2, calendar2.get(2));
        calendar.set(5, calendar2.get(5));
        calendar.set(10, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        this.fromDate = calendar.getTime();
        calendar.clear();
        calendar2.setTime(this.toDate);
        calendar.set(1, calendar2.get(1));
        calendar.set(2, calendar2.get(2));
        calendar.set(5, calendar2.get(5));
        calendar.set(10, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        this.toDate = calendar.getTime();
        return true;
    }

    private void showReport(ActionEvent evt) {
        try {
            if (!this.initCriteria()) {
                return;
            }
            if (this.reportType == 1) {
                this.showKeyStatisticsReport();
            } else if (this.reportType == 2) {
                this.showSalesAnalysisReport();
            }
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
        }
    }

    private void showSalesAnalysisReport() throws Exception {
        SalesSummaryDAO dao = new SalesSummaryDAO();
        List<SalesAnalysisReportModel.SalesAnalysisData> datas = dao.findSalesAnalysis(this.fromDate, this.toDate, this.userType, this.terminal);
        HashMap<String, String> properties = new HashMap<String, String>();
        ReportUtil.populateRestaurantProperties(properties);
        properties.put("subtitle", POSConstants.SALES_SUMMARY_REPORT);
        properties.put("reportTime", this.fullDateFormatter.format(new Date()));
        properties.put("fromDate", this.shortDateFormatter.format(this.fromDate));
        properties.put("toDate", this.shortDateFormatter.format(this.toDate));
        if (this.userType == null) {
            properties.put("reportType", POSConstants.SYSTEM_TOTAL);
        } else {
            properties.put("reportType", this.userType.getName());
        }
        properties.put("shift", POSConstants.ALL);
        properties.put("centre", this.terminal == null ? POSConstants.ALL : this.terminal.getName());
        properties.put("days", String.valueOf(this.dateDiff));
        JasperReport report = ReportUtil.getReport("sales_summary_report2");
        JasperPrint print = JasperFillManager.fillReport((JasperReport)report, properties, (JRDataSource)new JRTableModelDataSource((TableModel)new SalesAnalysisReportModel(datas)));
        this.openReport(print);
    }

    private void showKeyStatisticsReport() throws Exception {
        SalesSummaryDAO dao = new SalesSummaryDAO();
        SalesStatistics summary = dao.findKeyStatistics(this.fromDate, this.toDate, this.userType, this.terminal);
        HashMap<String, String> properties = new HashMap<String, String>();
        ReportUtil.populateRestaurantProperties(properties);
        properties.put("subtitle", POSConstants.SALES_SUMMARY_REPORT);
        properties.put("Capacity", String.valueOf(summary.getCapacity()));
        properties.put("GuestCount", String.valueOf(summary.getGuestCount()));
        properties.put("GuestPerSeat", NumberUtil.formatNumber(summary.getGuestPerSeat()));
        properties.put("reportTime", this.fullDateFormatter.format(new Date()));
        properties.put("fromDate", this.shortDateFormatter.format(this.fromDate));
        properties.put("toDate", this.shortDateFormatter.format(this.toDate));
        if (this.userType == null) {
            properties.put("reportType", POSConstants.SYSTEM_TOTAL);
        } else {
            properties.put("reportType", this.userType.getName());
        }
        properties.put("shift", POSConstants.ALL);
        properties.put("centre", this.terminal == null ? POSConstants.ALL : this.terminal.getName());
        properties.put("days", String.valueOf(this.dateDiff));
        properties.put("Capacity", String.valueOf(summary.getCapacity()));
        properties.put("GuestCount", String.valueOf(summary.getGuestCount()));
        properties.put("GuestPerSeat", NumberUtil.formatNumber(summary.getGuestPerCheck()));
        properties.put("TableTrnOvr", NumberUtil.formatNumber(summary.getTableTurnOver()));
        properties.put("AVGGuest", NumberUtil.formatNumber(summary.getAvgGuest()));
        properties.put("OpenChecks", String.valueOf(summary.getOpenChecks()));
        properties.put("VOIDChecks", String.valueOf(summary.getVoidChecks()));
        properties.put("OPPDChecks", String.valueOf(" "));
        properties.put("TRNGChecks", String.valueOf(" "));
        properties.put("ROPNChecks", String.valueOf(summary.getRopnChecks()));
        properties.put("MergeChecks", String.valueOf(" "));
        properties.put("LaborHour", NumberUtil.formatNumber(summary.getLaborHour()));
        properties.put("LaborSales", NumberUtil.formatNumber(summary.getGrossSale()));
        properties.put("Tables", String.valueOf(summary.getTables()));
        properties.put("CheckCount", String.valueOf(summary.getCheckCount()));
        properties.put("GuestPerChecks", NumberUtil.formatNumber(summary.getGuestPerCheck()));
        properties.put("TrnOvrTime", String.valueOf(" "));
        properties.put("AVGChecks", NumberUtil.formatNumber(summary.getAvgCheck()));
        properties.put("OPENAmount", NumberUtil.formatNumber(summary.getOpenAmount()));
        properties.put("VOIDAmount", NumberUtil.formatNumber(summary.getVoidAmount()));
        properties.put("PAIDChecks", String.valueOf(summary.getPaidChecks()));
        properties.put("TRNGAmount", String.valueOf(" "));
        properties.put("ROPNAmount", NumberUtil.formatNumber(summary.getRopnAmount()));
        properties.put("NTaxChecks", String.valueOf(summary.getNtaxChecks()));
        properties.put("NTaxAmount", NumberUtil.formatNumber(summary.getNtaxAmount()));
        properties.put("MergeAmount", String.valueOf(" "));
        properties.put("Labor", NumberUtil.formatNumber(summary.getLaborCost()));
        properties.put("LaborCost", NumberUtil.formatNumber(summary.getLaborCost() / summary.getGrossSale() * 100.0));
        JasperReport report = ReportUtil.getReport("sales_summary_report1");
        JasperPrint print = JasperFillManager.fillReport((JasperReport)report, properties, (JRDataSource)new JRTableModelDataSource((TableModel)new SalesStatistics.ShiftwiseDataTableModel(summary.getSalesTableDataList())));
        this.openReport(print);
    }

    private void openReport(JasperPrint print) {
        JRViewer viewer = new JRViewer(print);
        this.reportPanel.removeAll();
        this.reportPanel.add((Component)viewer);
        this.reportPanel.revalidate();
    }

    public int getReportType() {
        return this.reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }
}

