/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.config.ui;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.AppConfig;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.config.ui.MultiPrinterPane;
import com.floreantpos.config.ui.PrinterGroupView;
import com.floreantpos.model.PosPrinters;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class PrintConfigurationView
extends ConfigurationView {
    private JComboBox cbReceiptPrinterName;
    private JComboBox cbReportPrinterName;
    private JCheckBox chkKitchenBtn = new JCheckBox("Show KDS button on login screen");
    private JTextField txtYellowTime;
    private JTextField txtRedTime;
    PosPrinters printers = PosPrinters.load();

    public PrintConfigurationView() {
        this.initComponents();
    }

    @Override
    public String getName() {
        return POSConstants.CONFIG_TAB_PRINT;
    }

    @Override
    public void initialize() throws Exception {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        this.cbReportPrinterName.setModel(new DefaultComboBoxModel<PrintService>(printServices));
        this.cbReceiptPrinterName.setModel(new DefaultComboBoxModel<PrintService>(printServices));
        PrintServiceComboRenderer comboRenderer = new PrintServiceComboRenderer();
        this.cbReportPrinterName.setRenderer(comboRenderer);
        this.cbReceiptPrinterName.setRenderer(comboRenderer);
        this.chkKitchenBtn.setSelected(TerminalConfig.isShowKitchenBtnOnLoginScreen());
        this.setSelectedPrinter(this.cbReportPrinterName, this.printers.getReportPrinter());
        this.setSelectedPrinter(this.cbReceiptPrinterName, this.printers.getReceiptPrinter());
        String yellowTimeOut = AppConfig.getString("YellowTimeOut");
        String redTimeOut = AppConfig.getString("RedTimeOut");
        if (yellowTimeOut != null) {
            this.txtYellowTime.setText(yellowTimeOut);
        }
        if (redTimeOut != null) {
            this.txtRedTime.setText(redTimeOut);
        }
        this.setInitialized(true);
        if (printServices == null || printServices.length == 0) {
            POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), Messages.getString("PrintConfigurationView.0"));
        }
    }

    private void setSelectedPrinter(JComboBox whichPrinter, String printerName) {
        int printerCount = whichPrinter.getItemCount();
        for (int i = 0; i < printerCount; ++i) {
            PrintService printService = (PrintService)whichPrinter.getItemAt(i);
            if (!printService.getName().equals(printerName)) continue;
            whichPrinter.setSelectedIndex(i);
            return;
        }
    }

    @Override
    public boolean save() throws Exception {
        PrintService printService = (PrintService)this.cbReportPrinterName.getSelectedItem();
        this.printers.setReportPrinter(printService == null ? null : printService.getName());
        printService = (PrintService)this.cbReceiptPrinterName.getSelectedItem();
        this.printers.setReceiptPrinter(printService == null ? null : printService.getName());
        AppConfig.put("YellowTimeOut", this.txtYellowTime.getText());
        AppConfig.put("RedTimeOut", this.txtRedTime.getText());
        TerminalConfig.setShowKitchenBtnOnLoginScreen(this.chkKitchenBtn.isSelected());
        return true;
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout((LayoutManager)new MigLayout("", "[][grow,fill]", "[][][][18px,grow][][]"));
        JLabel lblReportPrinter = new JLabel(Messages.getString("PrintConfigurationView.4"));
        this.cbReportPrinterName = new JComboBox();
        JLabel jLabel1 = new JLabel();
        jLabel1.setText(Messages.getString("PrintConfigurationView.8"));
        this.cbReceiptPrinterName = new JComboBox();
        JLabel jLabel2 = new JLabel();
        MultiPrinterPane multiPrinterPane = new MultiPrinterPane("Printers", this.printers.getKitchenPrinters());
        contentPanel.add((Component)multiPrinterPane, "cell 0 1 2 1,growx,h 200!");
        PrinterGroupView printerGroupView = new PrinterGroupView(Messages.getString("PrintConfigurationView.13"));
        printerGroupView.setPreferredSize(new Dimension(0, 400));
        contentPanel.add((Component)printerGroupView, "cell 0 2 2 2,growx,,h 200!,wrap");
        JPanel footerPanel = new JPanel((LayoutManager)new MigLayout());
        this.txtYellowTime = new JTextField(5);
        this.txtRedTime = new JTextField(5);
        this.txtYellowTime.setText("90");
        this.txtRedTime.setText("120");
        footerPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("PrintConfigurationView.6")));
        JLabel lblYellowTime = new JLabel(Messages.getString("PrintConfigurationView.7"));
        JLabel lblRedTime = new JLabel(Messages.getString("PrintConfigurationView.9"));
        footerPanel.add((Component)lblYellowTime, "grow");
        footerPanel.add((Component)this.txtYellowTime, "grow");
        footerPanel.add((Component)new JLabel(Messages.getString("PrintConfigurationView.1")), "grow, wrap");
        footerPanel.add((Component)lblRedTime, "grow");
        footerPanel.add((Component)this.txtRedTime, "grow");
        footerPanel.add((Component)new JLabel("sec"), "grow,wrap");
        footerPanel.add(this.chkKitchenBtn);
        contentPanel.add((Component)footerPanel, "newline, grow, span 2,wrap");
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        this.add(scrollPane);
    }

    private class PrintServiceComboRenderer
    extends DefaultListCellRenderer {
        private PrintServiceComboRenderer() {
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel listCellRendererComponent = (JLabel)super.getListCellRendererComponent((JList<?>)list, value, index, isSelected, cellHasFocus);
            PrintService printService = (PrintService)value;
            if (printService != null) {
                listCellRendererComponent.setText(printService.getName());
            }
            return listCellRendererComponent;
        }
    }
}

