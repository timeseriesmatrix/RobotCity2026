/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.ecs.Document
 *  org.apache.ecs.Element
 *  org.apache.ecs.html.BR
 *  org.apache.ecs.html.HR
 *  org.apache.ecs.html.P
 *  org.apache.ecs.html.TD
 *  org.apache.ecs.html.TR
 *  org.apache.ecs.html.Table
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.CashDrawer;
import com.floreantpos.model.CurrencyBalance;
import com.floreantpos.model.DrawerPullReport;
import com.floreantpos.model.DrawerPullVoidTicketEntry;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.dao.CashDrawerDAO;
import com.floreantpos.print.DrawerpullReportService;
import com.floreantpos.print.PosPrintService;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.NumberUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;
import org.apache.ecs.Document;
import org.apache.ecs.Element;
import org.apache.ecs.html.BR;
import org.apache.ecs.html.HR;
import org.apache.ecs.html.P;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;

public class DrawerPullReportDialog
extends POSDialog {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private DrawerPullReport drawerPullReport;
    private Terminal terminal;
    private PosButton btnFinish;
    private PosButton btnPrint;
    private JEditorPane taReport;
    private TitlePanel titlePanel1;

    public DrawerPullReportDialog() {
        super((Frame)Application.getPosWindow(), true);
        this.initComponents();
    }

    public DrawerPullReportDialog(JDialog parent, boolean modal) {
        super((Frame)Application.getPosWindow(), true);
        this.initComponents();
    }

    public void initialize() throws Exception {
        this.terminal = Application.getInstance().refreshAndGetTerminal();
        this.drawerPullReport = DrawerpullReportService.buildDrawerPullReport();
        this.drawerPullReport.setAssignedUser(this.terminal.getAssignedUser());
        this.taReport.setContentType("text/html");
        this.taReport.setEditable(false);
        this.taReport.setMargin(new Insets(0, 10, 0, 10));
        this.taReport.setText(this.createReport());
        this.taReport.setCaretPosition(0);
        this.taReport.setPreferredSize(PosUIManager.getSize(360, 100));
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(5, 5));
        this.titlePanel1 = new TitlePanel();
        this.add((Component)this.titlePanel1, "North");
        this.taReport = new JEditorPane();
        this.taReport.setContentType("text/html");
        PosScrollPane scrollPane = new PosScrollPane(this.taReport);
        this.add(scrollPane);
        JPanel buttonPanel = new JPanel((LayoutManager)new MigLayout("fill", "", "[fill, grow][]"));
        buttonPanel.add((Component)new JSeparator(), "grow,span,wrap");
        this.btnPrint = new PosButton(Messages.getString("DrawerPullReportDialog.8"));
        buttonPanel.add((Component)this.btnPrint, "grow");
        this.btnFinish = new PosButton(Messages.getString("DrawerPullReportDialog.0"));
        buttonPanel.add((Component)this.btnFinish, "grow");
        this.add((Component)buttonPanel, "South");
        this.btnFinish.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DrawerPullReportDialog.this.doCloseDialog();
            }
        });
        this.btnPrint.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DrawerPullReportDialog.this.doPrintReport();
            }
        });
    }

    private void doCloseDialog() {
        this.dispose();
    }

    void createReportHeader(Document document) {
        P p = new P();
        p.addAttribute("align", "center");
        p.addElement("===================================");
        p.addElement((Element)new BR());
        p.addElement(Messages.getString("DrawerPullReportDialog.15"));
        p.addElement((Element)new BR());
        p.addElement(Messages.getString("DrawerPullReportDialog.16") + Application.getInstance().getTerminal().getName());
        p.addElement((Element)new BR());
        p.addElement("===================================");
        document.appendBody((Element)p);
    }

    void createSectionHeader(Document document, String headerText) {
        P p = new P();
        p.addAttribute("align", "center");
        p.addElement(headerText);
        p.addElement((Element)new HR());
        document.appendBody((Element)p);
    }

    void addSeparator(Document document) {
        P p = new P();
        HR hr = new HR();
        hr.addAttribute("style", "border: dashed;");
        p.addElement((Element)hr);
        document.appendBody((Element)p);
    }

    void addTableSeparator(Table table) {
        TR tr = new TR();
        TD td = new TD();
        td.addAttribute("colspan", 2);
        td.addAttribute("align", "right");
        HR hr = new HR();
        hr.addAttribute("style", "border: dashed;");
        td.addElement((Element)hr);
        tr.addElement((Element)td);
        table.addElement((Element)tr);
    }

    void addExceptionTableSeparator(Table table) {
        TR tr = new TR();
        TD td = new TD();
        td.addAttribute("colspan", 5);
        td.addAttribute("align", "right");
        HR hr = new HR();
        hr.addAttribute("style", "border: dashed;");
        td.addElement((Element)hr);
        tr.addElement((Element)td);
        table.addElement((Element)tr);
    }

    void addTableRow(Table table, String column1, String coulmn2) {
        TR tr = new TR();
        tr.addElement((Element)new TD().addElement(column1));
        tr.addElement(new TD().addAttribute("align", "right").addElementToRegistry(coulmn2));
        table.addElement((Element)tr);
    }

    void addDiscountTableRow(Table table, String column1, String coulmn2) {
        TR tr = new TR();
        tr.addElement(new TD().addAttribute("style", "padding-left: 50px;").addElementToRegistry(column1));
        tr.addElement(new TD().addAttribute("align", "right").addElementToRegistry(coulmn2));
        table.addElement((Element)tr);
    }

    void addExceptionTableRow(Table table, String column1, String coulmn2, String coulmn3, String column5) {
        TR tr = new TR();
        TD td = new TD();
        td.addAttribute("valign", "top");
        td.addElement(column1);
        tr.addElement((Element)td);
        td = new TD();
        td.addAttribute("valign", "top");
        td.addElement(coulmn2);
        tr.addElement((Element)td);
        td = new TD();
        td.addAttribute("valign", "top");
        td.addAttribute("align", "right");
        td.addElement(coulmn3);
        tr.addElement((Element)td);
        td = new TD();
        td.addAttribute("valign", "top");
        td.addAttribute("align", "right");
        td.addElement(column5);
        tr.addElement((Element)td);
        table.addElement((Element)tr);
    }

    public String createReport() throws Exception {
        CashDrawer cashDrawer;
        Document document = new Document();
        Table table = null;
        this.createReportHeader(document);
        P p = new P();
        p.addElement(Messages.getString("DrawerPullReportDialog.1") + this.dateFormat.format(new Date()));
        document.appendBody((Element)p);
        this.createSectionHeader(document, Messages.getString("DrawerPullReportDialog.2"));
        table = new Table();
        table.addAttribute("width", "100%");
        this.addTableRow(table, "&nbsp;" + Messages.getString("DrawerPullReportDialog.3"), this.decimalFormat.format(this.drawerPullReport.getNetSales()));
        this.addTableRow(table, "+" + Messages.getString("DrawerPullReportDialog.5"), this.decimalFormat.format(this.drawerPullReport.getSalesTax()));
        this.addTableRow(table, "+" + Messages.getString("SALES_DELIVERY_CHARGE"), this.decimalFormat.format(this.drawerPullReport.getSalesDeliveryCharge()));
        this.addTableRow(table, "=" + Messages.getString("DrawerPullReportDialog.7"), this.decimalFormat.format(this.drawerPullReport.getTotalRevenue()));
        this.addTableRow(table, "+" + Messages.getString("DrawerPullReportDialog.10"), this.decimalFormat.format(this.drawerPullReport.getChargedTips()));
        this.addTableSeparator(table);
        this.addTableRow(table, "=" + Messages.getString("DrawerPullReportDialog.12"), this.decimalFormat.format(this.drawerPullReport.getGrossReceipts()));
        document.appendBody((Element)table);
        document.appendBody((Element)new BR());
        table = new Table();
        table.addAttribute("width", "100%");
        this.addTableRow(table, "-CASH RECEIPTS (" + this.drawerPullReport.getCashReceiptCount() + ")", this.decimalFormat.format(this.drawerPullReport.getCashReceiptAmount()));
        this.addTableRow(table, "-CREDIT CARDS (" + this.drawerPullReport.getCreditCardReceiptCount() + ")", this.decimalFormat.format(this.drawerPullReport.getCreditCardReceiptAmount()));
        this.addTableRow(table, "-DEBIT CARDS (" + this.drawerPullReport.getDebitCardReceiptCount() + ")", this.decimalFormat.format(this.drawerPullReport.getDebitCardReceiptAmount()));
        this.addTableRow(table, "-GIFT RETURNS (" + this.drawerPullReport.getGiftCertReturnCount() + ")", this.decimalFormat.format(this.drawerPullReport.getGiftCertReturnAmount()));
        this.addTableRow(table, "+" + Messages.getString("DrawerPullReportDialog.23"), this.decimalFormat.format(this.drawerPullReport.getGiftCertChangeAmount()));
        this.addTableRow(table, "+" + Messages.getString("DrawerPullReportDialog.25"), this.decimalFormat.format(this.drawerPullReport.getCashBack()));
        this.addTableRow(table, "+REFUND (" + this.drawerPullReport.getRefundReceiptCount() + ")", this.decimalFormat.format(this.drawerPullReport.getRefundAmount()));
        this.addTableSeparator(table);
        this.addTableRow(table, "=" + Messages.getString("DrawerPullReportDialog.29"), this.decimalFormat.format(this.drawerPullReport.getReceiptDifferential()));
        document.appendBody((Element)table);
        document.appendBody((Element)new BR());
        table = new Table();
        table.addAttribute("width", "100%");
        this.addTableRow(table, "+" + Messages.getString("DrawerPullReportDialog.31"), this.decimalFormat.format(this.drawerPullReport.getChargedTips()));
        this.addTableRow(table, "-" + Messages.getString("DrawerPullReportDialog.33"), this.decimalFormat.format(this.drawerPullReport.getTipsPaid()));
        this.addTableSeparator(table);
        this.addTableRow(table, "=" + Messages.getString("DrawerPullReportDialog.35"), this.decimalFormat.format(this.drawerPullReport.getTipsDifferential()));
        document.appendBody((Element)table);
        document.appendBody((Element)new BR());
        this.createSectionHeader(document, Messages.getString("DrawerPullReportDialog.36"));
        table = new Table();
        table.addAttribute("width", "100%");
        this.addTableRow(table, "CASH (" + this.drawerPullReport.getCashReceiptCount() + ")", this.decimalFormat.format(this.drawerPullReport.getCashReceiptAmount()));
        this.addTableRow(table, "-" + Messages.getString("DrawerPullReportDialog.39"), this.decimalFormat.format(this.drawerPullReport.getTipsPaid()));
        this.addTableRow(table, "-PAY OUT       (" + this.drawerPullReport.getPayOutCount() + ")", this.decimalFormat.format(this.drawerPullReport.getPayOutAmount()));
        this.addTableRow(table, "-" + Messages.getString("DrawerPullReportDialog.43"), this.decimalFormat.format(this.drawerPullReport.getCashBack()));
        this.addTableRow(table, "-REFUND (" + this.drawerPullReport.getRefundReceiptCount() + ")", this.decimalFormat.format(this.drawerPullReport.getRefundAmount()));
        this.addTableRow(table, "+" + Messages.getString("DrawerPullReportDialog.47"), this.decimalFormat.format(this.terminal.getOpeningBalance()));
        this.addTableRow(table, "-DRAWER BLEED  (" + this.drawerPullReport.getDrawerBleedCount() + ")", this.decimalFormat.format(this.drawerPullReport.getDrawerBleedAmount()));
        this.addTableSeparator(table);
        this.addTableRow(table, "=" + Messages.getString("DrawerPullReportDialog.51"), this.decimalFormat.format(this.drawerPullReport.getDrawerAccountable()));
        this.addTableRow(table, ">" + Messages.getString("DrawerPullReportDialog.53"), this.decimalFormat.format(this.drawerPullReport.getCashToDeposit()));
        this.addTableSeparator(table);
        if (TerminalConfig.isEnabledMultiCurrency() && (cashDrawer = CashDrawerDAO.getInstance().findByTerminal(Application.getInstance().getTerminal())) != null && cashDrawer.getCurrencyBalanceList() != null) {
            for (CurrencyBalance currencyBalance : cashDrawer.getCurrencyBalanceList()) {
                this.addTableRow(table, currencyBalance.getCurrency().getName() + "", "" + this.decimalFormat.format(currencyBalance.getBalance()));
            }
        }
        document.appendBody((Element)table);
        this.createSectionHeader(document, Messages.getString("DrawerPullReportDialog.54"));
        this.createSectionHeader(document, Messages.getString("DrawerPullReportDialog.55"));
        table = new Table();
        table.addAttribute("width", "100%");
        this.addExceptionTableRow(table, Messages.getString("DrawerPullReportDialog.99"), Messages.getString("DrawerPullReportDialog.100"), Messages.getString("DrawerPullReportDialog.101"), Messages.getString("DrawerPullReportDialog.102"));
        this.addExceptionTableSeparator(table);
        Set<DrawerPullVoidTicketEntry> voidTickets = this.drawerPullReport.getVoidTickets();
        if (voidTickets != null) {
            for (DrawerPullVoidTicketEntry entry : voidTickets) {
                this.addExceptionTableRow(table, String.valueOf(entry.getCode()), entry.getReason(), entry.getHast(), NumberUtil.formatNumber(entry.getAmount()));
            }
        }
        this.addExceptionTableSeparator(table);
        document.appendBody((Element)table);
        table = new Table();
        table.addAttribute("width", "100%");
        this.addTableRow(table, Messages.getString("DrawerPullReportDialog.105"), this.decimalFormat.format(this.drawerPullReport.getTotalVoidWst()));
        this.addTableRow(table, Messages.getString("DrawerPullReportDialog.106"), this.decimalFormat.format(this.drawerPullReport.getTotalVoid()));
        document.appendBody((Element)table);
        this.createSectionHeader(document, Messages.getString("DrawerPullReportDialog.107"));
        table = new Table();
        document.appendBody((Element)table);
        table.addAttribute("width", "100%");
        this.addTableRow(table, Messages.getString("DrawerPullReportDialog.110"), "");
        this.addDiscountTableRow(table, Messages.getString("DrawerPullReportDialog.112"), String.valueOf(this.drawerPullReport.getTotalDiscountCount()));
        this.addDiscountTableRow(table, Messages.getString("DrawerPullReportDialog.113"), NumberUtil.formatNumber(this.drawerPullReport.getTotalDiscountAmount()));
        this.addDiscountTableRow(table, Messages.getString("DrawerPullReportDialog.114"), NumberUtil.formatNumber(this.drawerPullReport.getTotalDiscountSales()));
        this.addDiscountTableRow(table, Messages.getString("DrawerPullReportDialog.115"), String.valueOf(this.drawerPullReport.getTotalDiscountGuest()));
        this.addDiscountTableRow(table, Messages.getString("DrawerPullReportDialog.116"), String.valueOf(this.drawerPullReport.getTotalDiscountPartySize()));
        this.addDiscountTableRow(table, Messages.getString("DrawerPullReportDialog.117"), String.valueOf(this.drawerPullReport.getTotalDiscountCheckSize()));
        this.addDiscountTableRow(table, Messages.getString("DrawerPullReportDialog.118"), String.valueOf(" "));
        this.addDiscountTableRow(table, Messages.getString("DrawerPullReportDialog.120"), String.valueOf(" "));
        return document.toString();
    }

    @Override
    public void setTitle(String title) {
        this.titlePanel1.setTitle(title);
        super.setTitle(title);
    }

    private void doPrintReport() {
        try {
            PosPrintService.printDrawerPullReport(this.drawerPullReport, this.terminal);
        }
        catch (Exception ex) {
            POSMessageDialog.showError(this, Messages.getString("DrawerPullReportDialog.122") + ex.getMessage());
            PosLog.error(this.getClass(), ex);
        }
    }
}

