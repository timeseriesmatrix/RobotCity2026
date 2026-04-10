/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 */
package com.floreantpos.ui.model;

import com.floreantpos.POSConstants;
import com.floreantpos.model.Currency;
import com.floreantpos.model.dao.CurrencyDAO;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class CurrencyForm
extends BeanEditor {
    private FixedLengthTextField tfCode;
    private FixedLengthTextField tfName;
    private JTextField tfSymbol;
    private DoubleTextField tfExchangeRate;
    private DoubleTextField tfTolerance;
    private JCheckBox chkMain;

    public CurrencyForm() {
        this(new Currency());
    }

    public CurrencyForm(Currency currency) {
        this.initComponents();
        this.setBean(currency);
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel((LayoutManager)new MigLayout("fill"));
        JLabel lblCode = new JLabel("Code:");
        this.tfCode = new FixedLengthTextField();
        JLabel lblName = new JLabel(POSConstants.NAME + ":");
        this.tfName = new FixedLengthTextField();
        JLabel lblExchangeRate = new JLabel("Exchange Rate:");
        this.tfExchangeRate = new DoubleTextField();
        JLabel lblTolerance = new JLabel("Tolerance:");
        this.tfTolerance = new DoubleTextField();
        JLabel lblSymbol = new JLabel("Symbol");
        this.tfSymbol = new JTextField();
        this.chkMain = new JCheckBox("Main");
        contentPanel.add((Component)lblName, "cell 0 0");
        contentPanel.add((Component)this.tfName, "cell 1 0");
        contentPanel.add((Component)lblCode, "cell 0 1");
        contentPanel.add((Component)this.tfCode, "cell 1 1");
        contentPanel.add((Component)lblSymbol, "cell 0 2");
        contentPanel.add((Component)this.tfSymbol, "grow,cell 1 2");
        contentPanel.add((Component)lblExchangeRate, "cell 0 3");
        contentPanel.add((Component)this.tfExchangeRate, "grow,cell 1 3");
        contentPanel.add((Component)lblTolerance, "cell 0 4");
        contentPanel.add((Component)this.tfTolerance, "grow,cell 1 4");
        contentPanel.add((Component)this.chkMain, "cell 1 5");
        this.add(contentPanel);
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
            Currency currency = (Currency)this.getBean();
            CurrencyDAO dao = new CurrencyDAO();
            dao.saveOrUpdate(currency);
        }
        catch (Exception e) {
            MessageDialog.showError(e);
            return false;
        }
        return true;
    }

    @Override
    protected void updateView() {
        Currency currency = (Currency)this.getBean();
        if (currency == null) {
            return;
        }
        this.tfCode.setText(currency.getCode());
        this.tfName.setText(currency.getName());
        this.tfSymbol.setText(currency.getSymbol());
        this.tfExchangeRate.setText("" + currency.getExchangeRate());
        this.tfTolerance.setText("" + currency.getTolerance());
        this.chkMain.setSelected(currency.isMain());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean updateModel() {
        Currency currency = (Currency)this.getBean();
        String code = this.tfCode.getText();
        String name = this.tfName.getText();
        if (POSUtil.isBlankOrNull(code)) {
            MessageDialog.showError("Code is required");
            return false;
        }
        double exchangeRate = this.tfExchangeRate.getDouble();
        if (this.chkMain.isSelected() && exchangeRate != 1.0) {
            POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), "Exchange rate must be 1.0 for main currency");
            return false;
        }
        currency.setCode(code);
        currency.setName(name);
        currency.setSymbol(this.tfSymbol.getText());
        currency.setMain(this.chkMain.isSelected());
        currency.setExchangeRate(exchangeRate);
        currency.setTolerance(this.tfTolerance.getDouble());
        if (this.chkMain.isSelected()) {
            CurrencyDAO dao = new CurrencyDAO();
            List<Currency> currencyList = dao.findAll();
            Transaction transaction = null;
            try (Session session = null;){
                session = CurrencyDAO.getInstance().createNewSession();
                transaction = session.beginTransaction();
                for (Currency curr : currencyList) {
                    curr.setMain(false);
                    session.saveOrUpdate((Object)curr);
                }
                transaction.commit();
            }
        }
        return true;
    }

    @Override
    public String getDisplayText() {
        Currency currency = (Currency)this.getBean();
        if (currency.getId() == null) {
            return "New Currency";
        }
        return "Edit Currency";
    }
}

