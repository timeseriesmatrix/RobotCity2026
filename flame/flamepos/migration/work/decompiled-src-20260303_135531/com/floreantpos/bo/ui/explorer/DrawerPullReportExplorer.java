/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jdesktop.swingx.JXDatePicker
 *  org.jdesktop.swingx.JXTable
 *  org.jdesktop.swingx.table.TableColumnExt
 *  org.jdesktop.swingx.table.TableColumnModelExt
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.model.DrawerPullReport;
import com.floreantpos.model.dao.DrawerPullReportDAO;
import com.floreantpos.model.util.DateUtil;
import com.floreantpos.print.PosPrintService;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.util.UiUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;

public class DrawerPullReportExplorer
extends TransparentPanel {
    private JXDatePicker fromDatePicker = UiUtil.getCurrentMonthStart();
    private JXDatePicker toDatePicker = UiUtil.getCurrentMonthEnd();
    private JButton btnGo = new JButton(POSConstants.GO);
    private JButton btnEditActualAmount = new JButton(POSConstants.EDIT_ACTUAL_AMOUNT);
    private JButton btnPrint = new JButton(Messages.getString("DrawerPullReportExplorer.0"));
    private static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd MMM, yyyy hh:mm a");
    private TableColumnModelExt columnModel;
    private JXTable table = new JXTable((TableModel)new DrawerPullExplorerTableModel(null));

    public DrawerPullReportExplorer() {
        super(new BorderLayout());
        this.add(new JScrollPane((Component)this.table));
        this.table.getSelectionModel().setSelectionMode(0);
        this.table.setDefaultRenderer(Object.class, (TableCellRenderer)new PosTableRenderer());
        this.table.setAutoResizeMode(0);
        this.table.setColumnControlVisible(true);
        this.resizeColumnWidth((JTable)this.table);
        this.restoreTableColumnsVisibility();
        this.addTableColumnListener();
        JPanel topPanel = new JPanel((LayoutManager)new MigLayout());
        topPanel.add((Component)new JLabel(POSConstants.FROM), "grow");
        topPanel.add((Component)this.fromDatePicker, "wrap");
        topPanel.add((Component)new JLabel(POSConstants.TO), "grow");
        topPanel.add((Component)this.toDatePicker, "wrap");
        topPanel.add((Component)this.btnGo, "skip 1, al right");
        this.add((Component)topPanel, "North");
        JPanel bottomPanel = new JPanel(new FlowLayout(1));
        bottomPanel.add(this.btnEditActualAmount);
        bottomPanel.add(this.btnPrint);
        this.add((Component)bottomPanel, "South");
        this.btnPrint.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = DrawerPullReportExplorer.this.table.getSelectedRow();
                if (selectedRow < 0) {
                    BOMessageDialog.showError(DrawerPullReportExplorer.this, Messages.getString("DrawerPullReportExplorer.1"));
                    return;
                }
                DrawerPullExplorerTableModel model = (DrawerPullExplorerTableModel)DrawerPullReportExplorer.this.table.getModel();
                DrawerPullReport report = (DrawerPullReport)model.getRowData(selectedRow);
                PosPrintService.printDrawerPullReport(report, report.getTerminal());
            }
        });
        this.btnGo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DrawerPullReportExplorer.this.viewReport();
                    DrawerPullReportExplorer.this.resizeColumnWidth((JTable)DrawerPullReportExplorer.this.table);
                }
                catch (Exception e1) {
                    BOMessageDialog.showError(DrawerPullReportExplorer.this, POSConstants.ERROR_MESSAGE, e1);
                }
            }
        });
        this.btnEditActualAmount.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedRow = DrawerPullReportExplorer.this.table.getSelectedRow();
                    if (selectedRow < 0) {
                        BOMessageDialog.showError(DrawerPullReportExplorer.this, POSConstants.SELECT_DRAWER_PULL_TO_EDIT);
                        return;
                    }
                    String amountString = JOptionPane.showInputDialog(DrawerPullReportExplorer.this, (Object)(POSConstants.ENTER_ACTUAL_AMOUNT + ":"));
                    if (amountString == null) {
                        return;
                    }
                    double amount = 0.0;
                    try {
                        amount = Double.parseDouble(amountString);
                    }
                    catch (Exception x) {
                        BOMessageDialog.showError(DrawerPullReportExplorer.this, POSConstants.INVALID_AMOUNT);
                        return;
                    }
                    DrawerPullExplorerTableModel model = (DrawerPullExplorerTableModel)DrawerPullReportExplorer.this.table.getModel();
                    DrawerPullReport report = (DrawerPullReport)model.getRowData(selectedRow);
                    report.setCashToDeposit(amount);
                    DrawerPullReportDAO dao = new DrawerPullReportDAO();
                    dao.saveOrUpdate(report);
                    model.updateItem(selectedRow);
                }
                catch (Exception e1) {
                    BOMessageDialog.showError(DrawerPullReportExplorer.this, POSConstants.ERROR_MESSAGE, e1);
                }
            }
        });
    }

    private void restoreTableColumnsVisibility() {
        String recordedSelectedColumns = TerminalConfig.getDrawerPullReportHiddenColumns();
        TableColumnModelExt columnModel = (TableColumnModelExt)this.table.getColumnModel();
        if (recordedSelectedColumns.isEmpty()) {
            return;
        }
        String[] str = recordedSelectedColumns.split("\\*");
        for (int i = 0; i < str.length; ++i) {
            Integer columnIndex = Integer.parseInt(str[i]);
            columnModel.getColumnExt(columnIndex - i).setVisible(false);
        }
    }

    private void viewReport() {
        try {
            Date fromDate = this.fromDatePicker.getDate();
            Date toDate = this.toDatePicker.getDate();
            fromDate = DateUtil.startOfDay(fromDate);
            toDate = DateUtil.endOfDay(toDate);
            List<DrawerPullReport> list = new DrawerPullReportDAO().findReports(fromDate, toDate);
            DrawerPullExplorerTableModel model = (DrawerPullExplorerTableModel)this.table.getModel();
            model.setRows(list);
        }
        catch (Exception e) {
            BOMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
        }
    }

    public void resizeColumnWidth(JTable table) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); ++column) {
            int columnWidthByHeader;
            int columnWidthByComponent = this.getColumnWidthByComponentLenght(table, column);
            if (columnWidthByComponent > (columnWidthByHeader = this.getColumnWidthByHeaderLenght(table, column))) {
                columnModel.getColumn(column).setPreferredWidth(columnWidthByComponent);
                continue;
            }
            columnModel.getColumn(column).setPreferredWidth(columnWidthByHeader);
        }
    }

    private int getColumnWidthByHeaderLenght(JTable table, int column) {
        int width = 50;
        TableColumn tcolumn = table.getColumnModel().getColumn(column);
        TableCellRenderer headerRenderer = tcolumn.getHeaderRenderer();
        if (headerRenderer == null) {
            headerRenderer = table.getTableHeader().getDefaultRenderer();
        }
        Object headerValue = tcolumn.getHeaderValue();
        Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, 0, column);
        width = Math.max(width, headerComp.getPreferredSize().width);
        return width + 20;
    }

    private int getColumnWidthByComponentLenght(JTable table, int column) {
        int width = 50;
        for (int row = 0; row < table.getRowCount(); ++row) {
            TableCellRenderer renderer = table.getCellRenderer(row, column);
            Component comp = table.prepareRenderer(renderer, row, column);
            width = Math.max(comp.getPreferredSize().width + 1, width);
        }
        return width + 20;
    }

    private void saveHiddenColumns() {
        List columns = this.columnModel.getColumns(true);
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (TableColumn tableColumn : columns) {
            TableColumnExt c = (TableColumnExt)tableColumn;
            if (c.isVisible()) continue;
            indices.add(c.getModelIndex());
        }
        this.saveTableColumnsVisibility(indices);
    }

    private void saveTableColumnsVisibility(List indices) {
        String selectedColumns = "";
        Iterator iterator = indices.iterator();
        while (iterator.hasNext()) {
            String newSelectedColumn = String.valueOf(iterator.next());
            selectedColumns = selectedColumns + newSelectedColumn;
            if (!iterator.hasNext()) continue;
            selectedColumns = selectedColumns + "*";
        }
        TerminalConfig.setDrawerPullReportHiddenColumns(selectedColumns);
    }

    private void addTableColumnListener() {
        this.columnModel = (TableColumnModelExt)this.table.getColumnModel();
        this.columnModel.addColumnModelListener(new TableColumnModelListener(){

            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
            }

            @Override
            public void columnRemoved(TableColumnModelEvent e) {
                DrawerPullReportExplorer.this.saveHiddenColumns();
            }

            @Override
            public void columnMoved(TableColumnModelEvent e) {
            }

            @Override
            public void columnMarginChanged(ChangeEvent e) {
            }

            @Override
            public void columnAdded(TableColumnModelEvent e) {
                DrawerPullReportExplorer.this.saveHiddenColumns();
            }
        });
    }

    class DrawerPullExplorerTableModel
    extends ListTableModel {
        String[] columnNames = new String[]{POSConstants.ID, POSConstants.TIME, "TICKET COUNT", POSConstants.DRAWER_PULL_AMOUNT, "USER ID", "TERMINAL ID", "BEGIN CASH", "NET SALES", "SALES TAX", "CASH TAX", "TOTAL REVENUE", "GROSS RECEIPTS", "GIFT CERT RETURN COUNT", "GIFT CERT RETURN AMOUNT", "GIFT CERT CHARGE AMOUNT", "CASH RECEIPT NO", "CASH RECEIPT AMOUNT", "CREDIT CARD RECEIPT NO", "CREDIT CARD RECEIPT AMOUNT", "DEBIT CARD RECEIPT NO", "DEBIT CARD RECEIPT AMOUNT", "REFUND RECEIPT COUNT", "REFUND AMOUNT", "RECEIPT DIFFERENTIAL", "CASH BACK", "CASH TIPS", "CHARGED TIPS", "TIPS PAID", "TIPS DIFFERENTIAL", "PAY OUT NO", "PAY OUT AMOUNT", "DRAWER BLEED NO", "DRAWER BLEED AMOUNT", POSConstants.ACTUAL_AMOUNT, "VARIANCE", "TOTAL VOIDWST", "TOTAL VOID", "TOTAL DISCOUNT COUNT", "TOTAL DISCOUNT AMOUNT", "TOTAL DISCOUNT SALES", "TOTAL DISCOUNT GUEST", "TOTAL DISCOUNT PARTY SIZE", "TOTAL DISCOUNT CHECK SIZE", "TOTAL DISCOUNT PERCENTAGE", "TOTAL DISCOUNT RATIO"};

        DrawerPullExplorerTableModel(List<DrawerPullReport> list) {
            this.setRows(list);
            this.setColumnNames(this.columnNames);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DrawerPullReport report = (DrawerPullReport)this.rows.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return report.getId().toString();
                }
                case 1: {
                    return dateTimeFormatter.format(report.getReportTime());
                }
                case 2: {
                    return report.getTicketCount();
                }
                case 3: {
                    return report.getDrawerAccountable();
                }
                case 4: {
                    return report.getAssignedUser().getUserId();
                }
                case 5: {
                    return report.getTerminal().getId();
                }
                case 6: {
                    return report.getBeginCash();
                }
                case 7: {
                    return report.getNetSales();
                }
                case 8: {
                    return report.getSalesTax();
                }
                case 9: {
                    return report.getCashTax();
                }
                case 10: {
                    return report.getTotalRevenue();
                }
                case 11: {
                    return report.getGrossReceipts();
                }
                case 12: {
                    return report.getGiftCertReturnCount();
                }
                case 13: {
                    return report.getGiftCertReturnAmount();
                }
                case 14: {
                    return report.getGiftCertChangeAmount();
                }
                case 15: {
                    return report.getCashReceiptNumber();
                }
                case 16: {
                    return report.getCashReceiptAmount();
                }
                case 17: {
                    return report.getCreditCardReceiptNumber();
                }
                case 18: {
                    return report.getCreditCardReceiptAmount();
                }
                case 19: {
                    return report.getDebitCardReceiptNumber();
                }
                case 20: {
                    return report.getDebitCardReceiptAmount();
                }
                case 21: {
                    return report.getRefundReceiptCount();
                }
                case 22: {
                    return report.getRefundAmount();
                }
                case 23: {
                    return report.getReceiptDifferential();
                }
                case 24: {
                    return report.getCashBack();
                }
                case 25: {
                    return report.getCashTips();
                }
                case 26: {
                    return report.getChargedTips();
                }
                case 27: {
                    return report.getTipsPaid();
                }
                case 28: {
                    return report.getTipsDifferential();
                }
                case 29: {
                    return report.getPayOutNumber();
                }
                case 30: {
                    return report.getPayOutAmount();
                }
                case 31: {
                    return report.getDrawerBleedNumber();
                }
                case 32: {
                    return report.getDrawerBleedAmount();
                }
                case 33: {
                    return report.getCashToDeposit();
                }
                case 34: {
                    return report.getVariance();
                }
                case 35: {
                    return report.getTotalVoidWst();
                }
                case 36: {
                    return report.getTotalVoid();
                }
                case 37: {
                    return report.getTotalDiscountCount();
                }
                case 38: {
                    return report.getTotalDiscountAmount();
                }
                case 39: {
                    return report.getTotalDiscountSales();
                }
                case 40: {
                    return report.getTotalDiscountGuest();
                }
                case 41: {
                    return report.getTotalDiscountPartySize();
                }
                case 42: {
                    return report.getTotalDiscountCheckSize();
                }
                case 43: {
                    return report.getTotalDiscountPercentage();
                }
                case 44: {
                    return report.getTotalDiscountRatio();
                }
            }
            return null;
        }
    }
}

