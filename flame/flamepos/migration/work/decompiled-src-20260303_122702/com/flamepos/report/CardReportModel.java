/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.model.CreditCardTransaction;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.util.NumberUtil;
import java.text.SimpleDateFormat;
import java.util.List;

public class CardReportModel
extends ListTableModel<CreditCardTransaction> {
    final String DATE_FORMAT = "dd-MM-yyyy";

    public CardReportModel(List<CreditCardTransaction> datas) {
        super(new String[]{"ticketId", "cardType", "date", "server", "authCode", "tips", "total"}, datas);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CreditCardTransaction transaction = (CreditCardTransaction)this.getRowData(rowIndex);
        switch (columnIndex) {
            case 0: {
                return String.valueOf(transaction.getTicket().getId());
            }
            case 1: {
                return transaction.getCardType();
            }
            case 2: {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                return String.valueOf(sdf.format(transaction.getTransactionTime()));
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

