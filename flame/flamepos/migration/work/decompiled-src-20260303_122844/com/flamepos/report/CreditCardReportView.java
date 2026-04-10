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
import com.floreantpos.model.CreditCardTransaction;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.dao.PosTransactionDAO;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.report.CardReportModel;
import com.floreantpos.report.ReportUtil;
import com.floreantpos.report.service.ReportService;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;
import com.floreantpos.util.NumberUtil;
import com.floreantpos.util.POSUtil;
import com.floreantpos.util.PanelTester;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

public class CreditCardReportView
extends JPanel {
    private JXDatePicker fromDatePicker = UiUtil.getCurrentMonthStart();
    private JXDatePicker toDatePicker = UiUtil.getCurrentMonthEnd();
    private JButton btnGo = new JButton(POSConstants.GO);
    private JPanel reportContainer;

    public CreditCardReportView() {
        super(new BorderLayout());
        JPanel topPanel = new JPanel((LayoutManager)new MigLayout());
        topPanel.add((Component)new JLabel(POSConstants.FROM + ":"), "grow");
        topPanel.add((Component)this.fromDatePicker);
        topPanel.add((Component)new JLabel(POSConstants.TO + ":"), "grow");
        topPanel.add((Component)this.toDatePicker);
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
                    CreditCardReportView.this.viewReport();
                }
                catch (Exception e1) {
                    POSMessageDialog.showError(CreditCardReportView.this, POSConstants.ERROR_MESSAGE, e1);
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
        Date currentTime = new Date();
        List<? extends PosTransaction> transactions = PosTransactionDAO.getInstance().findTransactions(null, CreditCardTransaction.class, fromDate, toDate);
        int saleCount = 0;
        double totalSales = 0.0;
        double totalTips = 0.0;
        for (CreditCardTransaction creditCardTransaction : transactions) {
            ++saleCount;
            totalSales += creditCardTransaction.getAmount().doubleValue();
            totalTips += creditCardTransaction.getTipsAmount().doubleValue();
        }
        HashMap<String, String> map = new HashMap<String, String>();
        ReportUtil.populateRestaurantProperties(map);
        map.put("reportTitle", Messages.getString("CreditCardReportView.0"));
        map.put("fromDate", ReportService.formatShortDate(fromDate));
        map.put("toDate", ReportService.formatShortDate(toDate));
        map.put("reportTime", ReportService.formatFullDate(currentTime));
        map.put("saleCount", String.valueOf(saleCount));
        map.put("totalSales", NumberUtil.formatNumber(totalSales - totalTips));
        map.put("totalTips", NumberUtil.formatNumber(totalTips));
        map.put("total", NumberUtil.formatNumber(totalSales));
        JasperReport jasperReport = ReportUtil.getReport("credit-card-report");
        JasperPrint jasperPrint = JasperFillManager.fillReport((JasperReport)jasperReport, map, (JRDataSource)new JRTableModelDataSource((TableModel)new CardReportModel(transactions)));
        JRViewer viewer = new JRViewer(jasperPrint);
        this.reportContainer.removeAll();
        this.reportContainer.add((Component)viewer);
        this.reportContainer.revalidate();
    }

    public static void main(String[] args) {
        PanelTester.width = 800;
        PanelTester.height = 500;
        PanelTester.test(new CreditCardReportView());
    }
}

