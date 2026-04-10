/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.hibernate.StaleObjectStateException
 */
package com.floreantpos.customPayment;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.CustomPayment;
import com.floreantpos.model.dao.CustomPaymentDAO;
import com.floreantpos.model.util.IllegalModelStateException;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;
import org.hibernate.StaleObjectStateException;

public class CustomPaymentForm
extends BeanEditor<CustomPayment> {
    private JLabel lblName;
    private JLabel lblRefNumberFieldName;
    private FixedLengthTextField txtName;
    private FixedLengthTextField txtRefNumberFieldName;
    private JCheckBox cbRequiredRefNumber;

    public CustomPaymentForm() {
        this.initComponent();
    }

    private void initComponent() {
        this.cbRequiredRefNumber = new JCheckBox(Messages.getString("CustomPaymentForm.0"));
        this.lblRefNumberFieldName = new JLabel(Messages.getString("CustomPaymentForm.1"));
        this.lblName = new JLabel(Messages.getString("CustomPaymentForm.2"));
        this.txtName = new FixedLengthTextField(60);
        this.txtRefNumberFieldName = new FixedLengthTextField(60);
        this.setLayout((LayoutManager)new MigLayout("hidemode 1", "", ""));
        this.add((Component)this.lblName, "split 2");
        this.add((Component)this.txtName, "wrap");
        this.add((Component)this.cbRequiredRefNumber, "wrap");
        this.add((Component)this.lblRefNumberFieldName, "split 2");
        this.add(this.txtRefNumberFieldName);
        this.cbRequiredRefNumber.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (CustomPaymentForm.this.cbRequiredRefNumber.isSelected()) {
                    CustomPaymentForm.this.lblRefNumberFieldName.setVisible(true);
                    CustomPaymentForm.this.txtRefNumberFieldName.setVisible(true);
                } else {
                    CustomPaymentForm.this.lblRefNumberFieldName.setVisible(false);
                    CustomPaymentForm.this.txtRefNumberFieldName.setVisible(false);
                }
            }
        });
        this.lblRefNumberFieldName.setVisible(false);
        this.txtRefNumberFieldName.setVisible(false);
        this.txtName.setEnabled(false);
        this.cbRequiredRefNumber.setEnabled(false);
    }

    @Override
    public void setFieldsEnable(boolean enable) {
        this.txtName.setEnabled(enable);
        this.cbRequiredRefNumber.setEnabled(enable);
        this.txtRefNumberFieldName.setEnabled(enable);
    }

    @Override
    public void createNew() {
        this.setBean(new CustomPayment());
        this.lblRefNumberFieldName.setVisible(false);
        this.txtRefNumberFieldName.setVisible(false);
        this.cbRequiredRefNumber.setSelected(false);
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
            CustomPayment payment = (CustomPayment)this.getBean();
            CustomPaymentDAO.getInstance().saveOrUpdate(payment);
            this.updateView();
            return true;
        }
        catch (IllegalModelStateException payment) {
        }
        catch (StaleObjectStateException e) {
            BOMessageDialog.showError(this, Messages.getString("CustomPaymentForm.10"));
        }
        return false;
    }

    @Override
    protected void updateView() {
        CustomPayment payment = (CustomPayment)this.getBean();
        this.txtName.setText(payment.getName());
        if (payment.isRequiredRefNumber().booleanValue()) {
            this.txtRefNumberFieldName.setVisible(true);
            this.lblRefNumberFieldName.setVisible(true);
            this.txtRefNumberFieldName.setText(payment.getRefNumberFieldName());
            this.cbRequiredRefNumber.setSelected(payment.isRequiredRefNumber());
        } else {
            this.lblRefNumberFieldName.setVisible(false);
            this.txtRefNumberFieldName.setVisible(false);
            this.cbRequiredRefNumber.setSelected(payment.isRequiredRefNumber());
        }
    }

    @Override
    protected boolean updateModel() throws IllegalModelStateException {
        CustomPayment payment = (CustomPayment)this.getBean();
        if (this.txtName.getText().equals("")) {
            POSMessageDialog.showMessage(null, Messages.getString("CustomPaymentForm.12"));
            return false;
        }
        payment.setName(this.txtName.getText());
        if (this.cbRequiredRefNumber.isSelected()) {
            if (this.txtRefNumberFieldName.getText().equals("")) {
                POSMessageDialog.showMessage(null, Messages.getString("CustomPaymentForm.14"));
                return false;
            }
            payment.setRefNumberFieldName(this.txtRefNumberFieldName.getText());
            payment.setRequiredRefNumber(true);
        } else {
            payment.setRefNumberFieldName("");
            payment.setRequiredRefNumber(false);
        }
        return true;
    }

    @Override
    public boolean delete() {
        try {
            CustomPayment payment = (CustomPayment)this.getBean();
            if (payment == null) {
                return false;
            }
            CustomPaymentDAO.getInstance().delete(payment);
            return true;
        }
        catch (Exception e) {
            POSMessageDialog.showError(POSUtil.getBackOfficeWindow(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getDisplayText() {
        return null;
    }
}

