/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.customPayment;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.ModelBrowser;
import com.floreantpos.customPayment.CustomPaymentForm;
import com.floreantpos.model.CustomPayment;
import com.floreantpos.model.dao.CustomPaymentDAO;
import com.floreantpos.swing.BeanTableModel;
import java.util.List;
import javax.swing.table.TableColumn;

public class CustomPaymentBrowser
extends ModelBrowser<CustomPayment> {
    public CustomPaymentBrowser() {
        super(new CustomPaymentForm());
        BeanTableModel tableModel = new BeanTableModel(CustomPayment.class);
        tableModel.addColumn(Messages.getString("CustomPaymentBrowser.0"), CustomPayment.PROP_ID);
        tableModel.addColumn(Messages.getString("CustomPaymentBrowser.1"), CustomPayment.PROP_NAME);
        tableModel.addColumn(Messages.getString("CustomPaymentBrowser.2"), CustomPayment.PROP_REF_NUMBER_FIELD_NAME);
        this.init(tableModel);
        this.browserTable.setAutoResizeMode(4);
    }

    @Override
    public void refreshTable() {
        List<CustomPayment> tables = CustomPaymentDAO.getInstance().findAll();
        BeanTableModel tableModel = (BeanTableModel)this.browserTable.getModel();
        tableModel.removeAll();
        tableModel.addRows(tables);
    }

    private void setColumnWidth(int columnNumber, int width) {
        TableColumn column = this.browserTable.getColumnModel().getColumn(columnNumber);
        column.setPreferredWidth(width);
        column.setMaxWidth(width);
        column.setMinWidth(width);
    }
}

