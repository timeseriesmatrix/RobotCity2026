/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosLog;
import com.floreantpos.main.Application;
import com.floreantpos.model.TipsCashoutReport;
import com.floreantpos.model.TipsCashoutReportTableModel;
import com.floreantpos.print.PosPrintService;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.NumberUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.miginfocom.swing.MigLayout;

public class TipsCashoutReportDialog
extends POSDialog
implements ActionListener {
    private final TipsCashoutReport report;

    public TipsCashoutReportDialog(TipsCashoutReport report) {
        this.report = report;
        this.setTitle(POSConstants.SERVER_TIPS_REPORT);
        JPanel topPanel = new JPanel((LayoutManager)new MigLayout("", "[fill]", ""));
        topPanel.add(new JLabel(Messages.getString("TipsCashoutReportDialog.0")));
        topPanel.add((Component)new JLabel(": " + report.getServer()), "wrap");
        topPanel.add(new JLabel(Messages.getString("TipsCashoutReportDialog.1")));
        topPanel.add((Component)new JLabel(": " + Application.formatDate(report.getFromDate())), "wrap");
        topPanel.add(new JLabel(Messages.getString("TipsCashoutReportDialog.2")));
        topPanel.add((Component)new JLabel(": " + Application.formatDate(report.getToDate())), "wrap");
        topPanel.add(new JLabel(Messages.getString("TipsCashoutReportDialog.3")));
        topPanel.add((Component)new JLabel(": " + Application.formatDate(report.getReportTime())), "wrap");
        topPanel.add(new JLabel(Messages.getString("TipsCashoutReportDialog.4")));
        topPanel.add((Component)new JLabel(": " + (report.getDatas() == null ? "0" : String.valueOf(report.getDatas().size()))), "wrap");
        topPanel.add(new JLabel(Messages.getString("TipsCashoutReportDialog.5")));
        topPanel.add((Component)new JLabel(": " + NumberUtil.formatNumber(report.getCashTipsAmount())), "wrap");
        topPanel.add(new JLabel(Messages.getString("TipsCashoutReportDialog.6")));
        topPanel.add((Component)new JLabel(": " + NumberUtil.formatNumber(report.getChargedTipsAmount())), "wrap");
        topPanel.add(new JLabel(Messages.getString("TipsCashoutReportDialog.7")));
        topPanel.add((Component)new JLabel(": " + report.getTipsDue()), "wrap");
        this.add((Component)topPanel, "North");
        JPanel contentPanel = new JPanel((LayoutManager)new MigLayout("fill"));
        JTable table = new JTable(new TipsCashoutReportTableModel(report.getDatas()));
        contentPanel.add(new JScrollPane(table));
        this.add(contentPanel);
        JPanel bottomPanel = new JPanel(new FlowLayout());
        PosButton print = new PosButton(Messages.getString("TipsCashoutReportDialog.28"));
        print.setPreferredSize(new Dimension(120, 50));
        print.addActionListener(this);
        PosButton close = new PosButton(Messages.getString("TipsCashoutReportDialog.29"));
        close.setPreferredSize(new Dimension(120, 50));
        close.addActionListener(this);
        bottomPanel.add(print);
        bottomPanel.add(close);
        this.add((Component)bottomPanel, "South");
        this.setDefaultCloseOperation(2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (Messages.getString("TipsCashoutReportDialog.30").equals(e.getActionCommand())) {
            this.dispose();
        } else if (Messages.getString("TipsCashoutReportDialog.31").equals(e.getActionCommand())) {
            try {
                PosPrintService.printServerTipsReport(this.report);
            }
            catch (Exception x) {
                PosLog.error(this.getClass(), x);
                POSMessageDialog.showError(this, Messages.getString("TipsCashoutReportDialog.32") + x.getMessage());
            }
        }
    }
}

