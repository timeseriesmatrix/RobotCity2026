/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.customer;

import com.floreantpos.Messages;
import com.floreantpos.model.Customer;
import com.floreantpos.swing.PaginatedTableModel;
import java.util.List;

public class CustomerListTableModel
extends PaginatedTableModel {
    private static final String[] columns = new String[]{Messages.getString("CustomerListTableModel.1"), Messages.getString("CustomerListTableModel.7"), Messages.getString("CustomerListTableModel.0"), Messages.getString("CustomerListTableModel.3"), Messages.getString("CustomerListTableModel.10"), Messages.getString("CustomerListTableModel.4"), Messages.getString("CustomerListTableModel.5")};

    public CustomerListTableModel() {
        super(columns);
    }

    public CustomerListTableModel(List<Customer> customers) {
        super(columns, (List)customers);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Customer customer = (Customer)this.rows.get(rowIndex);
        switch (columnIndex) {
            case 0: {
                return customer.getFirstName();
            }
            case 1: {
                return customer.getLastName();
            }
            case 2: {
                return customer.getMobileNo();
            }
            case 3: {
                return customer.getAddress();
            }
            case 4: {
                return customer.getZipCode();
            }
            case 5: {
                return customer.getCity();
            }
            case 6: {
                return customer.getState();
            }
        }
        return null;
    }
}

