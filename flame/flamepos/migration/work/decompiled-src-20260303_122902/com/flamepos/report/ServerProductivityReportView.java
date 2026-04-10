/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  net.sf.jasperreports.engine.JRDataSource
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
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.report.ReportUtil;
import com.floreantpos.report.ServerProductivityReport;
import com.floreantpos.report.service.ReportService;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JRViewer;
import org.jdesktop.swingx.JXDatePicker;

public class ServerProductivityReportView
extends JPanel {
    private JXDatePicker fromDatePicker = UiUtil.getCurrentMonthStart();
    private JXDatePicker toDatePicker = UiUtil.getCurrentMonthEnd();
    private JButton btnGo = new JButton(POSConstants.GO);
    private JPanel reportContainer;

    public ServerProductivityReportView() {
        super(new BorderLayout());
        JPanel topPanel = new JPanel((LayoutManager)new MigLayout());
        topPanel.add((Component)new JLabel(POSConstants.FROM + ":"), "grow");
        topPanel.add((Component)this.fromDatePicker, "wrap");
        topPanel.add((Component)new JLabel(POSConstants.TO + ":"), "grow");
        topPanel.add((Component)this.toDatePicker, "wrap");
        topPanel.add((Component)this.btnGo, "skip 1, al right");
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
                    ServerProductivityReportView.this.viewReport();
                }
                catch (Exception e1) {
                    POSMessageDialog.showError(ServerProductivityReportView.this, POSConstants.ERROR_MESSAGE, e1);
                }
            }
        });
    }

    private void viewReport() throws Exception {
        Date toDate;
        Date fromDate = this.fromDatePicker.getDate();
        if (fromDate.after(toDate = this.toDatePicker.getDate())) {
            POSMessageDialog.showError(POSUtil.getFocusedWindow(), POSConstants.FROM_DATE_CANNOT_BE_GREATER_THAN_TO_DATE_);
            return;
        }
        fromDate = DateUtil.startOfDay(fromDate);
        toDate = DateUtil.endOfDay(toDate);
        ReportService reportService = new ReportService();
        ServerProductivityReport report = reportService.getServerProductivityReport(fromDate, toDate);
        HashMap<String, String> map = new HashMap<String, String>();
        ReportUtil.populateRestaurantProperties(map);
        map.put("reportTitle", Messages.getString("ServerProductivityReportView.0"));
        map.put("fromDate", ReportService.formatShortDate(fromDate));
        map.put("toDate", ReportService.formatShortDate(toDate));
        map.put("reportTime", ReportService.formatFullDate(new Date()));
        JasperReport jasperReport = ReportUtil.getReport("server_productivity_report");
        JasperPrint jasperPrint = JasperFillManager.fillReport((JasperReport)jasperReport, map, (JRDataSource)new JRTableModelDataSource((TableModel)report.getTableModel()));
        JRViewer viewer = new JRViewer(jasperPrint);
        this.reportContainer.removeAll();
        this.reportContainer.add((Component)viewer);
        this.reportContainer.revalidate();
    }
}

