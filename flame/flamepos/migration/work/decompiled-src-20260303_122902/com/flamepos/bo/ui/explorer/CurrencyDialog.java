/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.explorer.CurrencyExplorer;
import com.floreantpos.model.Currency;
import com.floreantpos.model.base.BaseCurrency;
import com.floreantpos.model.dao.CurrencyDAO;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.util.List;
import javax.swing.JPanel;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class CurrencyDialog
extends OkCancelOptionDialog {
    private CurrencyExplorer currencyExplorer;

    public CurrencyDialog() {
        JPanel contentPanel = this.getContentPanel();
        this.getContentPane();
        this.setTitle(Messages.getString("CurrencyDialog.0"));
        this.setTitlePaneText(Messages.getString("CurrencyDialog.0"));
        this.currencyExplorer = new CurrencyExplorer();
        contentPanel.add(this.currencyExplorer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void doOk() {
        List<Currency> currencyList = this.currencyExplorer.getModel().getRows();
        BaseCurrency mainCurrency = null;
        boolean isMainSelected = false;
        for (Currency currency : currencyList) {
            if (!currency.isMain().booleanValue()) continue;
            isMainSelected = true;
            mainCurrency = currency;
        }
        if (!isMainSelected) {
            POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), Messages.getString("CurrencyDialog.2"));
            return;
        }
        if (mainCurrency.getExchangeRate() != 1.0) {
            if (POSMessageDialog.showYesNoQuestionDialog(this, Messages.getString("CurrencyDialog.3"), Messages.getString("CurrencyDialog.4")) == 0) {
                mainCurrency.setExchangeRate(1.0);
            } else {
                return;
            }
        }
        Transaction tx = null;
        try (Session session = null;){
            session = CurrencyDAO.getInstance().createNewSession();
            tx = session.beginTransaction();
            for (Currency currency : currencyList) {
                session.saveOrUpdate((Object)currency);
            }
            tx.commit();
        }
        this.setCanceled(true);
        this.dispose();
    }
}

