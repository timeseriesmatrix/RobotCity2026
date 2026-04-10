/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jdesktop.swingx.JXDatePicker
 */
package com.floreantpos.report;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.UserType;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.model.dao.UserTypeDAO;
import com.floreantpos.report.OpenTicketSummaryReport;
import com.floreantpos.report.Report;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;

public class ReportViewer
extends JPanel {
    private JButton btnRefresh;
    private JComboBox cbReportType;
    private JComboBox cbTerminal;
    private JXDatePicker dpEndDate;
    private JXDatePicker dpStartDate;
    private JLabel lblReportType;
    private JLabel lblFromDate;
    private JLabel lblToDate;
    private JLabel lblTerminal;
    private JCheckBox chkBoxFree;
    private JLabel lblUserType;
    private JComboBox cbUserType;
    private TransparentPanel reportSearchOptionPanel;
    private TransparentPanel reportConstraintPanel;
    private TransparentPanel reportPanel;
    private Report report;

    public ReportViewer() {
        this.initComponents();
    }

    public ReportViewer(Report report) {
        this.initComponents();
        TerminalDAO terminalDAO = new TerminalDAO();
        ArrayList<Object> drawerTerminals = new ArrayList<Object>();
        drawerTerminals.add(0, POSConstants.ALL);
        List<Terminal> terminals = terminalDAO.findAll();
        for (Terminal terminal : terminals) {
            if (!terminal.isHasCashDrawer().booleanValue()) continue;
            drawerTerminals.add(terminal);
        }
        this.cbTerminal.setModel(new ListComboBoxModel(drawerTerminals));
        this.setReport(report);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(5, 5));
        this.reportSearchOptionPanel = new TransparentPanel(new BorderLayout());
        this.reportConstraintPanel = new TransparentPanel();
        this.reportConstraintPanel.setLayout((LayoutManager)new MigLayout());
        this.lblReportType = new JLabel(Messages.getString("ReportViewer.0") + ":");
        this.cbReportType = new JComboBox();
        this.cbReportType.setModel(new DefaultComboBoxModel<String>(new String[]{POSConstants.PREVIOUS_SALE_AFTER_DRAWER_RESET_, POSConstants.SALE_BEFORE_DRAWER_RESET}));
        this.cbReportType.setSelectedIndex(1);
        this.lblTerminal = new JLabel("Terminal");
        this.cbTerminal = new JComboBox();
        this.cbTerminal.setPreferredSize(new Dimension(115, 0));
        this.lblFromDate = new JLabel(POSConstants.START_DATE + ":");
        this.dpStartDate = UiUtil.getCurrentMonthStart();
        this.lblToDate = new JLabel(POSConstants.END_DATE + ":");
        this.dpEndDate = UiUtil.getCurrentMonthEnd();
        this.chkBoxFree = new JCheckBox("Include Free Items");
        this.lblUserType = new JLabel("User Type");
        this.cbUserType = new JComboBox();
        this.btnRefresh = new JButton();
        this.reportPanel = new TransparentPanel();
        this.btnRefresh.setText(POSConstants.REFRESH);
        this.btnRefresh.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                ReportViewer.this.doRefreshReport(evt);
            }
        });
        this.reportConstraintPanel.add(this.lblReportType);
        this.reportConstraintPanel.add((Component)this.cbReportType, "gap 0px 20px");
        this.reportConstraintPanel.add(this.lblTerminal);
        this.reportConstraintPanel.add((Component)this.cbTerminal, "wrap");
        this.reportConstraintPanel.add(this.lblFromDate);
        this.reportConstraintPanel.add((Component)this.dpStartDate);
        this.reportConstraintPanel.add(this.lblToDate);
        this.reportConstraintPanel.add((Component)this.dpEndDate, "wrap");
        this.reportConstraintPanel.add(new JLabel(""));
        this.reportConstraintPanel.add((Component)this.chkBoxFree, "wrap");
        this.reportConstraintPanel.add(new JLabel(""));
        this.reportConstraintPanel.add(this.btnRefresh);
        this.reportSearchOptionPanel.add((Component)this.reportConstraintPanel, "North");
        this.reportSearchOptionPanel.add((Component)new JSeparator(), "Center");
        this.reportPanel.setLayout(new BorderLayout());
        this.add((Component)this.reportSearchOptionPanel, "North");
        this.add((Component)this.reportPanel, "Center");
    }

    private void doRefreshReport(ActionEvent evt) {
        Date toDate;
        Date fromDate = this.dpStartDate.getDate();
        if (fromDate.after(toDate = this.dpEndDate.getDate())) {
            POSMessageDialog.showError(POSUtil.getFocusedWindow(), POSConstants.FROM_DATE_CANNOT_BE_GREATER_THAN_TO_DATE_);
            return;
        }
        try {
            this.reportPanel.removeAll();
            this.reportPanel.revalidate();
            if (this.report != null) {
                int reportType = this.cbReportType.getSelectedIndex();
                this.report.setReportType(reportType);
                UserType userType = null;
                if (this.cbUserType.getSelectedItem() instanceof UserType) {
                    userType = (UserType)this.cbUserType.getSelectedItem();
                }
                this.report.setUserType(userType);
                Terminal terminal = null;
                if (this.cbTerminal.getSelectedItem() instanceof Terminal) {
                    terminal = (Terminal)this.cbTerminal.getSelectedItem();
                }
                this.report.setTerminal(terminal);
                this.report.setStartDate(fromDate);
                this.report.setEndDate(toDate);
                this.report.setIncludeFreeItems(this.chkBoxFree.isSelected());
                this.report.refresh();
                if (this.report != null && this.report.getViewer() != null) {
                    this.reportPanel.add((Component)this.report.getViewer());
                    this.reportPanel.revalidate();
                }
            }
        }
        catch (Exception e) {
            MessageDialog.showError(POSConstants.ERROR_MESSAGE, e);
        }
    }

    public Report getReport() {
        return this.report;
    }

    public void setReport(Report report) {
        this.report = report;
        if (report instanceof OpenTicketSummaryReport) {
            this.reportConstraintPanel.removeAll();
            UserTypeDAO dao = new UserTypeDAO();
            List<UserType> userTypes = dao.findAll();
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(0, POSConstants.ALL);
            list.addAll(userTypes);
            this.cbUserType.setModel(new ListComboBoxModel(list));
            this.cbUserType.setPreferredSize(this.cbTerminal.getPreferredSize());
            this.reportConstraintPanel.add(this.lblUserType);
            this.reportConstraintPanel.add((Component)this.cbUserType, "wrap");
            this.reportConstraintPanel.add(this.lblTerminal);
            this.reportConstraintPanel.add((Component)this.cbTerminal, "wrap");
            this.reportConstraintPanel.add(new JLabel(""));
            this.reportConstraintPanel.add((Component)this.btnRefresh, "wrap");
        }
    }
}

