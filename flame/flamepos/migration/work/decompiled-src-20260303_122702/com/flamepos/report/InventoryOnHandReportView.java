/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.intellij.uiDesigner.core.GridConstraints
 *  com.intellij.uiDesigner.core.GridLayoutManager
 *  com.intellij.uiDesigner.core.Spacer
 *  net.sf.jasperreports.engine.JRDataSource
 *  net.sf.jasperreports.engine.JRException
 *  net.sf.jasperreports.engine.JasperFillManager
 *  net.sf.jasperreports.engine.JasperPrint
 *  net.sf.jasperreports.engine.JasperReport
 *  net.sf.jasperreports.engine.data.JRTableModelDataSource
 *  net.sf.jasperreports.view.JRViewer
 *  org.jdesktop.swingx.JXDatePicker
 */
package com.floreantpos.report;

import com.floreantpos.POSConstants;
import com.floreantpos.PosLog;
import com.floreantpos.main.Application;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.dao.InventoryItemDAO;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.report.InventoryOnHandReportModel;
import com.floreantpos.report.ReportUtil;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JRViewer;
import org.jdesktop.swingx.JXDatePicker;

public class InventoryOnHandReportView
extends TransparentPanel {
    private JButton btnGo;
    private JXDatePicker fromDatePicker;
    private JXDatePicker toDatePicker;
    private JPanel reportPanel;
    private JPanel contentPane;

    public InventoryOnHandReportView() {
        this.$$$setupUI$$$();
        TerminalDAO terminalDAO = new TerminalDAO();
        List<Terminal> terminals = terminalDAO.findAll();
        terminals.add(0, (Terminal)((Object)POSConstants.ALL));
        this.setLayout(new BorderLayout());
        this.add(this.contentPane);
        this.btnGo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                InventoryItemDAO dao = new InventoryItemDAO();
                List<InventoryItem> findPayroll = dao.findAll();
                InventoryOnHandReportView.this.viewReport(findPayroll);
            }
        });
    }

    public InventoryOnHandReportView(List<InventoryItem> inventoryList) {
        this.$$$setupUI$$$();
        TerminalDAO terminalDAO = new TerminalDAO();
        List<Terminal> terminals = terminalDAO.findAll();
        terminals.add(0, (Terminal)((Object)POSConstants.ALL));
        this.setLayout(new BorderLayout());
        this.add(this.contentPane);
        this.viewReport(inventoryList);
    }

    private void viewReport(List<InventoryItem> inventList) {
        Date toDate;
        Date fromDate = this.fromDatePicker.getDate();
        if (fromDate.after(toDate = this.toDatePicker.getDate())) {
            POSMessageDialog.showError(POSUtil.getFocusedWindow(), POSConstants.FROM_DATE_CANNOT_BE_GREATER_THAN_TO_DATE_);
            return;
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
        try {
            JasperReport report = ReportUtil.getReport("inventoryOnHandReport");
            HashMap<String, Object> properties = new HashMap<String, Object>();
            ReportUtil.populateRestaurantProperties(properties);
            properties.put("fromDate", fromDate);
            properties.put("toDate", toDate);
            properties.put("reportDate", new Date());
            properties.put("reportTitle", "Purchase Order");
            Restaurant restaurant = Application.getInstance().getRestaurant();
            properties.put("companyName", restaurant.getName());
            properties.put("address", restaurant.getAddressLine1());
            properties.put("city", restaurant.getAddressLine2());
            properties.put("phone", restaurant.getTelephone());
            properties.put("fax", restaurant.getZipCode());
            properties.put("email", restaurant.getAddressLine3());
            InventoryOnHandReportModel reportModel = new InventoryOnHandReportModel();
            reportModel.setRows(inventList);
            JasperPrint print = JasperFillManager.fillReport((JasperReport)report, properties, (JRDataSource)new JRTableModelDataSource((TableModel)reportModel));
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
        panel1.setLayout((LayoutManager)new GridLayoutManager(2, 7, new Insets(10, 10, 10, 10), 10, 10));
        this.contentPane.add((Component)panel1, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 1, null, null, null, 0, false));
        JLabel label1 = new JLabel();
        label1.setText(POSConstants.FROM + ":");
        panel1.add((Component)label1, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        JLabel label2 = new JLabel();
        label2.setText(POSConstants.TO + ":");
        panel1.add((Component)label2, new GridConstraints(0, 3, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
        this.fromDatePicker = UiUtil.getCurrentMonthStart();
        panel1.add((Component)this.fromDatePicker, new GridConstraints(0, 1, 1, 1, 8, 0, 3, 3, null, new Dimension(147, 24), null, 0, false));
        this.toDatePicker = UiUtil.getCurrentMonthEnd();
        panel1.add((Component)this.toDatePicker, new GridConstraints(0, 4, 1, 1, 8, 0, 3, 3, null, new Dimension(147, 24), null, 0, false));
        Spacer spacer1 = new Spacer();
        panel1.add((Component)spacer1, new GridConstraints(0, 6, 1, 1, 0, 1, 4, 1, null, null, null, 0, false));
        this.btnGo = new JButton();
        this.btnGo.setText(POSConstants.GO);
        panel1.add((Component)this.btnGo, new GridConstraints(0, 5, 1, 1, 4, 0, 1, 0, null, new Dimension(147, 23), null, 0, false));
        JSeparator separator1 = new JSeparator();
        panel1.add((Component)separator1, new GridConstraints(1, 0, 1, 7, 0, 3, 4, 4, null, null, null, 0, false));
        this.reportPanel = new JPanel();
        this.reportPanel.setLayout(new BorderLayout(0, 0));
        this.contentPane.add((Component)this.reportPanel, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return this.contentPane;
    }
}

