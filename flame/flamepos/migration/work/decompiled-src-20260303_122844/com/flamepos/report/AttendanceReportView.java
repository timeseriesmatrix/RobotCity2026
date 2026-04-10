/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
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
import com.floreantpos.model.User;
import com.floreantpos.model.dao.AttendenceHistoryDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.report.AttendanceReportData;
import com.floreantpos.report.AttendanceReportModel;
import com.floreantpos.report.ReportUtil;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
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
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JRViewer;
import org.jdesktop.swingx.JXDatePicker;

public class AttendanceReportView
extends TransparentPanel {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyy");
    private JButton btnGo;
    private JXDatePicker fromDatePicker;
    private JXDatePicker toDatePicker;
    private JPanel reportPanel;
    private JComboBox cbUserType;

    public AttendanceReportView() {
        this.setLayout(new BorderLayout());
        this.createUI();
    }

    private void createUI() {
        this.fromDatePicker = UiUtil.getCurrentMonthStart();
        this.toDatePicker = UiUtil.getDeafultDate();
        this.toDatePicker.setDate(new Date());
        this.btnGo = new JButton();
        this.btnGo.setText(POSConstants.GO);
        this.btnGo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                AttendanceReportView.this.viewReport();
            }
        });
        this.cbUserType = new JComboBox();
        UserDAO dao = new UserDAO();
        List<User> userTypes = dao.findAll();
        Vector<Object> list = new Vector<Object>();
        list.add(POSConstants.ALL);
        list.addAll(userTypes);
        this.cbUserType.setModel(new DefaultComboBoxModel(list));
        this.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel((LayoutManager)new MigLayout());
        topPanel.add(new JLabel(POSConstants.START_DATE + ":"));
        topPanel.add((Component)this.fromDatePicker);
        topPanel.add(new JLabel(POSConstants.END_DATE + ":"));
        topPanel.add((Component)this.toDatePicker);
        topPanel.add(new JLabel(POSConstants.USER + ":"));
        topPanel.add(this.cbUserType);
        topPanel.add((Component)this.btnGo, "width 60!");
        this.add((Component)topPanel, "North");
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        centerPanel.add((Component)new JSeparator(), "North");
        this.reportPanel = new JPanel(new BorderLayout());
        centerPanel.add(this.reportPanel);
        this.add(centerPanel);
    }

    private void viewReport() {
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
        User user = null;
        if (!this.cbUserType.getSelectedItem().equals(POSConstants.ALL)) {
            user = (User)this.cbUserType.getSelectedItem();
        }
        AttendenceHistoryDAO dao = new AttendenceHistoryDAO();
        List<AttendanceReportData> attendanceList = dao.findAttendance(fromDate, toDate, user);
        try {
            JasperReport report = ReportUtil.getReport("EmployeeAttendanceReport");
            HashMap<String, String> properties = new HashMap<String, String>();
            ReportUtil.populateRestaurantProperties(properties);
            properties.put("fromDate", this.dateFormat.format(fromDate));
            properties.put("toDate", this.dateFormat.format(toDate));
            properties.put("reportDate", this.dateFormat.format(new Date()));
            AttendanceReportModel reportModel = new AttendanceReportModel();
            reportModel.setRows(attendanceList);
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
}

