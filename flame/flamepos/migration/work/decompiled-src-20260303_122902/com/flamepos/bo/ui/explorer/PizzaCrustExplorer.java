/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.PizzaCrust;
import com.floreantpos.model.dao.PizzaCrustDAO;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.model.PizzaCrustForm;
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

public class PizzaCrustExplorer
extends TransparentPanel {
    private JXTable table;
    private BeanTableModel<PizzaCrust> tableModel = new BeanTableModel(PizzaCrust.class);
    private List<PizzaCrust> pizzaCrustList;

    public PizzaCrustExplorer() {
        this.tableModel.addColumn(POSConstants.ID.toUpperCase(), "id");
        this.tableModel.addColumn(POSConstants.NAME.toUpperCase(), "name");
        this.tableModel.addColumn("TRANSLATED NAME", "translatedName");
        this.tableModel.addColumn("DESCRIPTION", "description");
        this.tableModel.addColumn("SORT", "sortOrder");
        this.tableModel.addColumn("DEFAULT", "defaultCrust");
        this.pizzaCrustList = PizzaCrustDAO.getInstance().findAll();
        this.tableModel.addRows(this.pizzaCrustList);
        this.table = new JXTable(this.tableModel);
        this.table.setDefaultRenderer(Object.class, (TableCellRenderer)new PosTableRenderer());
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane((Component)this.table));
        JButton addButton = new JButton(POSConstants.ADD);
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PizzaCrustForm editor = new PizzaCrustForm();
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    PizzaCrust foodCategory = (PizzaCrust)editor.getBean();
                    PizzaCrustExplorer.this.tableModel.addRow(foodCategory);
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
                    int index = PizzaCrustExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = PizzaCrustExplorer.this.table.convertRowIndexToModel(index);
                    PizzaCrust pizzaCrust = (PizzaCrust)PizzaCrustExplorer.this.tableModel.getRow(index);
                    PizzaCrustForm editor = new PizzaCrustForm(pizzaCrust);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    PizzaCrustExplorer.this.table.repaint();
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
                    int index = PizzaCrustExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    if (ConfirmDeleteDialog.showMessage(PizzaCrustExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) == 0) {
                        PizzaCrust pizzaCrust = (PizzaCrust)PizzaCrustExplorer.this.tableModel.getRow(index);
                        PizzaCrustDAO dao = new PizzaCrustDAO();
                        dao.delete(pizzaCrust);
                        PizzaCrustExplorer.this.tableModel.removeRow(index);
                    }
                }
                catch (Exception x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        JButton defaultButton = new JButton("Set Default");
        defaultButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = PizzaCrustExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = PizzaCrustExplorer.this.table.convertRowIndexToModel(index);
                    PizzaCrust pizzaCrust = (PizzaCrust)PizzaCrustExplorer.this.tableModel.getRow(index);
                    for (PizzaCrust item : PizzaCrustExplorer.this.pizzaCrustList) {
                        if (pizzaCrust.getId() == item.getId()) {
                            item.setDefaultCrust(true);
                            continue;
                        }
                        item.setDefaultCrust(false);
                    }
                    PizzaCrustDAO dao = new PizzaCrustDAO();
                    dao.setDefault(PizzaCrustExplorer.this.pizzaCrustList);
                    PizzaCrustExplorer.this.tableModel.fireTableDataChanged();
                    PizzaCrustExplorer.this.table.revalidate();
                    PizzaCrustExplorer.this.table.repaint();
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
        panel.add(defaultButton);
        this.add((Component)panel, "South");
    }
}

