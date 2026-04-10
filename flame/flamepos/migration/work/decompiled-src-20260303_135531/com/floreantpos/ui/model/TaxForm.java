/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.layout.GroupLayout
 *  org.jdesktop.layout.GroupLayout$Group
 */
package com.floreantpos.ui.model;

import com.floreantpos.POSConstants;
import com.floreantpos.model.Tax;
import com.floreantpos.model.dao.TaxDAO;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import org.jdesktop.layout.GroupLayout;

public class TaxForm
extends BeanEditor {
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private FixedLengthTextField tfName;
    private DoubleTextField tfRate;

    public TaxForm() {
        this(new Tax());
    }

    public TaxForm(Tax tax) {
        this.initComponents();
        this.setBean(tax);
    }

    private void initComponents() {
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.tfName = new FixedLengthTextField();
        this.tfRate = new DoubleTextField();
        this.jLabel3 = new JLabel();
        this.jLabel1.setText(POSConstants.NAME + ":");
        this.jLabel2.setText(POSConstants.RATE + ":");
        this.tfRate.setHorizontalAlignment(4);
        this.jLabel3.setText("%");
        GroupLayout layout = new GroupLayout((Container)this);
        this.setLayout((LayoutManager)layout);
        layout.setHorizontalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel1).add((Component)this.jLabel2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.tfRate, -2, 122, -2).addPreferredGap(0).add((Component)this.jLabel3)).add((Component)this.tfName, -2, 208, -2)).addContainerGap(-1, Short.MAX_VALUE)));
        layout.setVerticalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel1).add((Component)this.tfName, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel2).add((Component)this.tfRate, -2, -1, -2).add((Component)this.jLabel3)).addContainerGap(-1, Short.MAX_VALUE)));
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
            Tax tax = (Tax)this.getBean();
            TaxDAO dao = new TaxDAO();
            dao.saveOrUpdate(tax);
        }
        catch (Exception e) {
            MessageDialog.showError(e);
            return false;
        }
        return true;
    }

    @Override
    protected void updateView() {
        Tax tax = (Tax)this.getBean();
        this.tfName.setText(tax.getName());
        this.tfRate.setText("" + tax.getRate());
    }

    @Override
    protected boolean updateModel() {
        Tax tax = (Tax)this.getBean();
        String name = this.tfName.getText();
        if (POSUtil.isBlankOrNull(name)) {
            MessageDialog.showError(POSConstants.NAME_REQUIRED);
            return false;
        }
        tax.setName(name);
        tax.setRate(this.tfRate.getDouble());
        return true;
    }

    @Override
    public String getDisplayText() {
        Tax tax = (Tax)this.getBean();
        if (tax.getId() == null) {
            return POSConstants.NEW_TAX_RATE;
        }
        return POSConstants.EDIT_TAX_RATE;
    }
}

