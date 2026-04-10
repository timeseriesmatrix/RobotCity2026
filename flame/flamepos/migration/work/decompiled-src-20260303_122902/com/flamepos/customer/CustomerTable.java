/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.customer;

import com.floreantpos.customer.CustomerListTableModel;
import com.floreantpos.model.Customer;
import java.util.Vector;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.JXTable;

public class CustomerTable
extends JXTable {
    public CustomerTable() {
    }

    public CustomerTable(TableModel dm) {
        super(dm);
    }

    public CustomerTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    public CustomerTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public CustomerTable(Vector<?> rowData, Vector<?> columnNames) {
        super(rowData, columnNames);
    }

    public CustomerTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    public CustomerTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    public Customer getSelectedCustomer() {
        TableModel model = this.getModel();
        if (model instanceof CustomerListTableModel) {
            if (this.getSelectedRow() == -1) {
                return null;
            }
            return (Customer)((CustomerListTableModel)model).getRowData(this.getSelectedRow());
        }
        return null;
    }
}

