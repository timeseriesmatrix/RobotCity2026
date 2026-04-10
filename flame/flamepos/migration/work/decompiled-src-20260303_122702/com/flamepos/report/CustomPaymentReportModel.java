/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.model.CustomPaymentTransaction;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.util.NumberUtil;
import java.text.SimpleDateFormat;
import java.util.List;

public class CustomPaymentReportModel
extends ListTableModel<CustomPaymentTransaction> {
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    public CustomPaymentReportModel(List<CustomPaymentTransaction> data) {
        super(new String[]{"ticketId", "paymentType", "date", "server", "authCode", "tips", "total"}, data);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CustomPaymentTransaction transaction = (CustomPaymentTransaction)this.getRowData(rowIndex);
        switch (columnIndex) {
            case 0: {
                return String.valueOf(transaction.getTicket().getId());
            }
            case 1: {
                return transaction.getPaymentType();
            }
            case 2: {
                return String.valueOf(this.formatter.format(transaction.getTransactionTime()));
            }
            case 3: {
                return transaction.getTicket().getOwner().getFullName();
            }
            case 4: {
                return transaction.getCardAuthCode();
            }
            case 5: {
                return NumberUtil.formatNumber(transaction.getTipsAmount());
            }
            case 6: {
                return NumberUtil.formatNumber(transaction.getAmount());
            }
        }
        return null;
    }
}

