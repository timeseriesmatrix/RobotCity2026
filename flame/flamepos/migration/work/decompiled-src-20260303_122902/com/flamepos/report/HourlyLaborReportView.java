/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.intellij.uiDesigner.core.GridConstraints
 *  com.intellij.uiDesigner.core.GridLayoutManager
 *  com.intellij.uiDesigner.core.Spacer
 *  net.sf.jasperreports.engine.JRDataSource
 *  net.sf.jasperreports.engine.JREmptyDataSource
 *  net.sf.jasperreports.engine.JRException
 *  net.sf.jasperreports.engine.JasperFillManager
 *  net.sf.jasperreports.engine.JasperPrint
 *  net.sf.jasperreports.engine.JasperReport
 *  net.sf.jasperreports.engine.data.JRTableModelDataSource
 *  net.sf.jasperreports.view.JRViewer
 *  org.jdesktop.swingx.JXDatePicker
 */
package com.floreantpos.report;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosLog;
import com.floreantpos.model.Shift;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.User;
import com.floreantpos.model.UserType;
import com.floreantpos.model.dao.AttendenceHistoryDAO;
import com.floreantpos.model.dao.ShiftDAO;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.model.dao.UserTypeDAO;
import com.floreantpos.report.HourlyLaborReportModel;
import com.floreantpos.report.ReportUtil;
import com.floreantpos.report.service.ReportService;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;
import com.floreantpos.util.NumberUtil;
import com.floreantpos.util.POSUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JRViewer;
import org.jdesktop.swingx.JXDatePicker;

public class HourlyLaborReportView
extends TransparentPanel {
    private JButton btnGo;
    private JComboBox cbTerminal;
    private JXDatePicker fromDatePicker;
    private JXDatePicker toDatePicker;
    private JPanel reportPanel;
    private JPanel contentPane;
    private JComboBox cbUserType;

    public HourlyLaborReportView() {
        this.$$$setupUI$$$();
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
        this.setLayout(new BorderLayout());
        this.add(this.contentPane);
        this.btnGo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                HourlyLaborReportView.this.viewReport();
            }
        });
    }

    private void viewReport() {
        Date toDate;
        Date fromDate = this.fromDatePicker.getDate();
        if (fromDate.after(toDate = this.toDatePicker.getDate())) {
            POSMessageDialog.showError(POSUtil.getFocusedWindow(), POSConstants.FROM_DATE_CANNOT_BE_GREATER_THAN_TO_DATE_);
            return;
        }
        UserType userType = (UserType)this.cbUserType.getSelectedItem();
        Terminal terminal = null;
        if (this.cbTerminal.getSelectedItem() instanceof Terminal) {
            terminal = (Terminal)this.cbTerminal.getSelectedItem();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(fromDate);
        calendar.set(1, calendar2.get(1));
        calendar.set(2, calendar2.get(2));
        calendar.set(5, calendar2.get(5));
        calendar.set(10, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        fromDate = calendar.getTime();
        calendar.clear();
        calendar2.setTime(toDate);
        calendar.set(1, calendar2.get(1));
        calendar.set(2, calendar2.get(2));
        calendar.set(5, calendar2.get(5));
        calendar.set(10, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        toDate = calendar.getTime();
        TicketDAO ticketDAO = TicketDAO.getInstance();
        AttendenceHistoryDAO attendenceHistoryDAO = new AttendenceHistoryDAO();
        ArrayList<LaborReportData> rows = new ArrayList<LaborReportData>();
        DecimalFormat formatter = new DecimalFormat("00");
        int grandTotalChecks = 0;
        int grandTotalGuests = 0;
        double grandTotalSales = 0.0;
        double grandTotalMHr = 0.0;
        double grandTotalLabor = 0.0;
        double grandTotalSalesPerMHr = 0.0;
        double grandTotalGuestsPerMHr = 0.0;
        double grandTotalCheckPerMHr = 0.0;
        double grandTotalLaborCost = 0.0;
        for (int i = 0; i < 24; ++i) {
            List<Ticket> tickets = ticketDAO.findTicketsForLaborHour(fromDate, toDate, i, userType, terminal);
            List<User> users = attendenceHistoryDAO.findNumberOfClockedInUserAtHour(fromDate, toDate, i, userType, terminal);
            int manHour = users.size();
            int totalChecks = 0;
            int totalGuests = 0;
            double totalSales = 0.0;
            double labor = 0.0;
            double salesPerMHr = 0.0;
            double guestsPerMHr = 0.0;
            double checksPerMHr = 0.0;
            for (Ticket ticket : tickets) {
                ++totalChecks;
                totalGuests += ticket.getNumberOfGuests().intValue();
                totalSales += ticket.getTotalAmount().doubleValue();
            }
            for (User user : users) {
                labor += user.getCostPerHour() == null ? 0.0 : user.getCostPerHour();
            }
            if (manHour > 0) {
                labor /= (double)manHour;
                salesPerMHr = totalSales / (double)manHour;
                guestsPerMHr = (double)totalGuests / (double)manHour;
                checksPerMHr = totalChecks / manHour;
            }
            LaborReportData reportData = new LaborReportData();
            reportData.setPeriod(formatter.format(i) + ":00 - " + formatter.format(i) + ":59");
            reportData.setManHour(manHour);
            reportData.setNoOfChecks(totalChecks);
            reportData.setSales(totalSales);
            reportData.setNoOfGuests(totalGuests);
            reportData.setLabor(labor);
            reportData.setSalesPerMHr(salesPerMHr);
            reportData.setGuestsPerMHr(guestsPerMHr);
            reportData.setCheckPerMHr(checksPerMHr);
            rows.add(reportData);
            grandTotalChecks += totalChecks;
            grandTotalGuests += totalGuests;
            grandTotalSales += totalSales;
            grandTotalMHr += (double)manHour;
            grandTotalLabor += labor;
            grandTotalSalesPerMHr += salesPerMHr;
            grandTotalCheckPerMHr += checksPerMHr;
            grandTotalGuestsPerMHr += guestsPerMHr;
        }
        ArrayList<LaborReportData> shiftReportRows = new ArrayList<LaborReportData>();
        ShiftDAO shiftDAO = new ShiftDAO();
        List<Shift> shifts = shiftDAO.findAll();
        for (Shift shift : shifts) {
            List<Ticket> tickets = ticketDAO.findTicketsForShift(fromDate, toDate, shift, userType, terminal);
            List<User> users = attendenceHistoryDAO.findNumberOfClockedInUserAtShift(fromDate, toDate, shift, userType, terminal);
            int manHour = users.size();
            int totalChecks = 0;
            int totalGuests = 0;
            double totalSales = 0.0;
            double labor = 0.0;
            double salesPerMHr = 0.0;
            double guestsPerMHr = 0.0;
            double checksPerMHr = 0.0;
            for (Ticket ticket : tickets) {
                ++totalChecks;
                totalGuests += ticket.getNumberOfGuests().intValue();
                totalSales += ticket.getTotalAmount().doubleValue();
            }
            for (User user : users) {
                labor += user.getCostPerHour() == null ? 0.0 : user.getCostPerHour();
            }
            if (manHour > 0) {
                labor /= (double)manHour;
                salesPerMHr = totalSales / (double)manHour;
                guestsPerMHr = (double)totalGuests / (double)manHour;
                checksPerMHr = totalChecks / manHour;
            }
            LaborReportData reportData = new LaborReportData();
            reportData.setPeriod(shift.getName());
            reportData.setManHour(manHour);
            reportData.setNoOfChecks(totalChecks);
            reportData.setSales(totalSales);
            reportData.setNoOfGuests(totalGuests);
            reportData.setLabor(labor);
            reportData.setSalesPerMHr(salesPerMHr);
            reportData.setGuestsPerMHr(guestsPerMHr);
            reportData.setCheckPerMHr(checksPerMHr);
            shiftReportRows.add(reportData);
        }
        try {
            JasperReport hourlyReport = ReportUtil.getReport("hourly_labor_subreport");
            JasperReport shiftReport = ReportUtil.getReport("hourly_labor_shift_subreport");
            JasperReport report = ReportUtil.getReport("hourly_labor_report");
            HashMap<String, String> properties = new HashMap<String, String>();
            ReportUtil.populateRestaurantProperties(properties);
            properties.put("reportTitle", POSConstants.HOURLY_LABOR_REPORT);
            properties.put("reportTime", ReportService.formatFullDate(new Date()));
            properties.put("fromDay", ReportService.formatShortDate(fromDate));
            properties.put("toDay", ReportService.formatShortDate(toDate));
            properties.put(POSConstants.TYPE, POSConstants.BY_RANGE_ACTUAL);
            properties.put("dept", userType == null ? POSConstants.ALL : userType.getName());
            properties.put("incr", Messages.getString("HourlyLaborReportView.0"));
            properties.put("cntr", terminal == null ? POSConstants.ALL : terminal.getName());
            properties.put("totalChecks", String.valueOf(grandTotalChecks));
            properties.put("totalGuests", String.valueOf(grandTotalGuests));
            properties.put("totalSales", NumberUtil.formatNumber(grandTotalSales));
            properties.put("totalMHr", NumberUtil.formatNumber(grandTotalMHr));
            properties.put("totalLabor", NumberUtil.formatNumber(grandTotalLabor));
            properties.put("totalSalesPerMhr", NumberUtil.formatNumber(grandTotalSalesPerMHr));
            properties.put("totalGuestsPerMhr", NumberUtil.formatNumber(grandTotalCheckPerMHr));
            properties.put("totalCheckPerMHr", NumberUtil.formatNumber(grandTotalGuestsPerMHr));
            properties.put("totalLaborCost", NumberUtil.formatNumber(grandTotalLaborCost));
            properties.put("hourlyReport", (String)hourlyReport);
            properties.put("hourlyReportDatasource", (String)new JRTableModelDataSource((TableModel)new HourlyLaborReportModel(rows)));
            properties.put("shiftReport", (String)shiftReport);
            properties.put("shiftReportDatasource", (String)new JRTableModelDataSource((TableModel)new HourlyLaborReportModel(shiftReportRows)));
            JasperPrint print = JasperFillManager.fillReport((JasperReport)report, properties, (JRDataSource)new JREmptyDataSource());
            JRViewer viewer = new JRViewer(print);
            this.reportPanel.removeAll();
            this.reportPanel.add((Component)viewer);
            this.reportPanel.revalidate();
        }
        catch (JRException e) {
            PosLog.error(this.getClass(), (Exception)((Object)e));
        }
    }

    private void $$$setupUI$$$() {
        this.contentPane = new JPanel();
        this.contentPane.setLayout((LayoutManager)new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        JPanel panel1 = new JPanel();
        panel1.setLayout((LayoutManager)new GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        this.contentPane.add((Component)panel1, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 1, null, null, null, 0, false));
        JLabel label1 = new JLabel();
        label1.setText(POSConstants.FROM + ":");
        panel1.add((Component)label1, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        JLabel label2 = new JLabel();
        label2.setText(POSConstants.TO + ":");
        panel1.add((Component)label2, new GridConstraints(1, 0, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        JLabel label3 = new JLabel();
        label3.setText(POSConstants.TERMINAL_LABEL);
        panel1.add((Component)label3, new GridConstraints(3, 0, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        this.fromDatePicker = UiUtil.getCurrentMonthStart();
        panel1.add((Component)this.fromDatePicker, new GridConstraints(0, 1, 1, 1, 0, 0, 3, 3, null, new Dimension(147, 24), null, 0, false));
        this.toDatePicker = UiUtil.getCurrentMonthEnd();
        panel1.add((Component)this.toDatePicker, new GridConstraints(1, 1, 1, 1, 0, 0, 3, 3, null, new Dimension(147, 24), null, 0, false));
        Spacer spacer1 = new Spacer();
        panel1.add((Component)spacer1, new GridConstraints(0, 2, 1, 1, 0, 1, 4, 1, null, null, null, 0, false));
        this.cbTerminal = new JComboBox();
        panel1.add((Component)this.cbTerminal, new GridConstraints(3, 1, 1, 1, 8, 1, 2, 0, null, new Dimension(147, 22), null, 0, false));
        this.btnGo = new JButton();
        this.btnGo.setText(POSConstants.GO);
        panel1.add((Component)this.btnGo, new GridConstraints(4, 1, 1, 1, 4, 0, 1, 0, null, new Dimension(147, 23), null, 0, false));
        JLabel label4 = new JLabel();
        label4.setText(POSConstants.USER_TYPE + ":");
        panel1.add((Component)label4, new GridConstraints(2, 0, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        this.cbUserType = new JComboBox();
        panel1.add((Component)this.cbUserType, new GridConstraints(2, 1, 1, 1, 8, 1, 2, 0, null, new Dimension(147, 22), null, 0, false));
        JSeparator separator1 = new JSeparator();
        panel1.add((Component)separator1, new GridConstraints(5, 0, 1, 3, 0, 3, 4, 4, null, null, null, 0, false));
        this.reportPanel = new JPanel();
        this.reportPanel.setLayout(new BorderLayout(0, 0));
        this.contentPane.add((Component)this.reportPanel, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return this.contentPane;
    }

    public static class LaborReportData {
        private String period;
        private int noOfChecks;
        private int noOfGuests;
        private double sales;
        private double manHour;
        private double labor;
        private double salesPerMHr;
        private double guestsPerMHr;
        private double checkPerMHr;
        private double laborCost;

        public double getCheckPerMHr() {
            return this.checkPerMHr;
        }

        public void setCheckPerMHr(double checkPerMHr) {
            this.checkPerMHr = checkPerMHr;
        }

        public double getGuestsPerMHr() {
            return this.guestsPerMHr;
        }

        public void setGuestsPerMHr(double guestsPerMHr) {
            this.guestsPerMHr = guestsPerMHr;
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

        public double getManHour() {
            return this.manHour;
        }

        public void setManHour(double manHour) {
            this.manHour = manHour;
        }

        public int getNoOfChecks() {
            return this.noOfChecks;
        }

        public void setNoOfChecks(int noOfChecks) {
            this.noOfChecks = noOfChecks;
        }

        public int getNoOfGuests() {
            return this.noOfGuests;
        }

        public void setNoOfGuests(int noOfGuests) {
            this.noOfGuests = noOfGuests;
        }

        public String getPeriod() {
            return this.period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public double getSales() {
            return this.sales;
        }

        public void setSales(double sales) {
            this.sales = sales;
        }

        public double getSalesPerMHr() {
            return this.salesPerMHr;
        }

        public void setSalesPerMHr(double salesPerMHr) {
            this.salesPerMHr = salesPerMHr;
        }
    }
}

