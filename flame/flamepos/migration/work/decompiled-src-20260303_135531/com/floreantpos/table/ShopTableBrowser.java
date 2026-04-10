/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.table;

import com.floreantpos.Messages;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.table.ShopTableForm;
import com.floreantpos.table.ShopTableModelBrowser;
import java.util.List;
import javax.swing.table.TableColumn;

public class ShopTableBrowser
extends ShopTableModelBrowser<ShopTable> {
    public ShopTableBrowser() {
        super(new ShopTableForm());
        BeanTableModel tableModel = new BeanTableModel(ShopTable.class);
        tableModel.addColumn(Messages.getString("ShopTableBrowser.0"), ShopTable.PROP_ID);
        tableModel.addColumn(Messages.getString("ShopTableBrowser.1"), ShopTable.PROP_CAPACITY);
        tableModel.addColumn(Messages.getString("ShopTableBrowser.2"), ShopTable.PROP_DESCRIPTION);
        this.init(tableModel);
        this.browserTable.setAutoResizeMode(4);
    }

    @Override
    public void refreshTable() {
        List<ShopTable> tables = ShopTableDAO.getInstance().findAll();
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

