/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.Currency;
import com.floreantpos.model.dao.CurrencyDAO;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.model.CurrencyForm;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

public class CurrencyExplorer
extends TransparentPanel {
    private JXTable table;
    private BeanTableModel<Currency> tableModel = new BeanTableModel(Currency.class);

    public CurrencyExplorer() {
        this.tableModel.addColumn(POSConstants.ID.toUpperCase(), "id");
        this.tableModel.addColumn(POSConstants.NAME.toUpperCase(), "name");
        this.tableModel.addColumn("CODE", "code");
        this.tableModel.addColumn("SYMBOL", "symbol");
        this.tableModel.addColumn("RATE", "exchangeRate");
        this.tableModel.addColumn("MAIN", "main");
        this.tableModel.addColumn("TOLERANCE", "tolerance");
        this.tableModel.addRows(CurrencyDAO.getInstance().findAll());
        this.table = new JXTable(this.tableModel);
        this.table.setDefaultRenderer(Object.class, (TableCellRenderer)new PosTableRenderer());
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane((Component)this.table));
        JButton addButton = new JButton(POSConstants.ADD);
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    CurrencyForm editor = new CurrencyForm();
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    CurrencyExplorer.this.tableModel.addRow((Currency)editor.getBean());
                    CurrencyExplorer.this.refresh();
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        JButton editButton = new JButton(POSConstants.EDIT);
        editButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = CurrencyExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = CurrencyExplorer.this.table.convertColumnIndexToModel(index);
                    Currency currency = (Currency)CurrencyExplorer.this.tableModel.getRow(index);
                    CurrencyForm currencyForm = new CurrencyForm(currency);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)currencyForm);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    CurrencyExplorer.this.refresh();
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        JButton deleteButton = new JButton(POSConstants.DELETE);
        deleteButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = CurrencyExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = CurrencyExplorer.this.table.convertColumnIndexToModel(index);
                    if (ConfirmDeleteDialog.showMessage(POSUtil.getBackOfficeWindow(), POSConstants.CONFIRM_DELETE, POSConstants.DELETE) == 0) {
                        Currency currency = (Currency)CurrencyExplorer.this.tableModel.getRow(index);
                        CurrencyDAO.getInstance().delete(currency);
                        CurrencyExplorer.this.tableModel.removeRow(currency);
                    }
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        TransparentPanel panel = new TransparentPanel();
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        this.add((Component)panel, "South");
    }

    protected BeanTableModel<Currency> getModel() {
        return this.tableModel;
    }

    private void refresh() {
        List<Currency> currencyList = CurrencyDAO.getInstance().findAll();
        this.tableModel.getRows().clear();
        this.tableModel.addRows(currencyList);
        this.table.repaint();
    }
}

