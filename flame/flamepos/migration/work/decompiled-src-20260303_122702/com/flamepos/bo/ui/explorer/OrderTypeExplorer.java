/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.bo.ui.CustomCellRenderer;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.dao.OrderTypeDAO;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.model.OrderTypeForm;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

public class OrderTypeExplorer
extends TransparentPanel {
    private JXTable table;
    private BeanTableModel<OrderType> tableModel = new BeanTableModel(OrderType.class);

    public OrderTypeExplorer() {
        this.tableModel.addColumn(POSConstants.ID.toUpperCase(), "id");
        this.tableModel.addColumn(POSConstants.NAME.toUpperCase(), "name");
        this.tableModel.addColumn(Messages.getString("OrderTypeExplorer.0"), "showTableSelection");
        this.tableModel.addColumn(Messages.getString("OrderTypeExplorer.2"), "showGuestSelection");
        this.tableModel.addColumn(POSConstants.PRINT_TO_KITCHEN, "shouldPrintToKitchen");
        this.tableModel.addColumn(POSConstants.ENABLED.toUpperCase(), "enabled");
        this.tableModel.addColumn(Messages.getString("OrderTypeExplorer.4"), "preAuthCreditCard");
        this.tableModel.addRows(OrderTypeDAO.getInstance().findAll());
        this.table = new JXTable(this.tableModel);
        this.table.setDefaultRenderer(Object.class, (TableCellRenderer)new CustomCellRenderer());
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane((Component)this.table));
        this.addButtonPanel();
    }

    private void addButtonPanel() {
        JButton addButton = new JButton(POSConstants.ADD);
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    OrderTypeForm editor = new OrderTypeForm();
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    OrderType ordersType = (OrderType)editor.getBean();
                    OrderTypeExplorer.this.tableModel.addRow(ordersType);
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
                    int index = OrderTypeExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = OrderTypeExplorer.this.table.convertRowIndexToModel(index);
                    OrderType ordersType = (OrderType)OrderTypeExplorer.this.tableModel.getRow(index);
                    OrderTypeForm editor = new OrderTypeForm(ordersType);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    OrderTypeExplorer.this.table.repaint();
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
                    int index = OrderTypeExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = OrderTypeExplorer.this.table.convertRowIndexToModel(index);
                    OrderType orderType = (OrderType)OrderTypeExplorer.this.tableModel.getRow(index);
                    if (POSMessageDialog.showYesNoQuestionDialog(OrderTypeExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != 0) {
                        return;
                    }
                    OrderTypeDAO dao = new OrderTypeDAO();
                    dao.delete(orderType);
                    POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), Messages.getString("TerminalConfigurationView.40"));
                    OrderTypeExplorer.this.tableModel.removeRow(index);
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
}

