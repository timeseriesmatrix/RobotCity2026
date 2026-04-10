/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.Messages;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.QwertyKeyPad;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

public class GiftCertDialog
extends OkCancelOptionDialog {
    private FixedLengthTextField tfGiftCertNumber;
    private DoubleTextField tfFaceValue;
    private QwertyKeyPad qwertyKeyPad;

    public GiftCertDialog(JDialog parent) {
        this.setTitle(Messages.getString("GiftCertDialog.0"));
        this.setTitlePaneText(Messages.getString("GiftCertDialog.1"));
        JPanel panel = this.getContentPanel();
        this.getContentPane().add((Component)panel, "Center");
        panel.setLayout((LayoutManager)new MigLayout("", "[][grow]", "[][]"));
        JLabel lblGiftCertificateNumber = new JLabel(Messages.getString("GiftCertDialog.5"));
        panel.add((Component)lblGiftCertificateNumber, "cell 0 0,alignx trailing");
        this.tfGiftCertNumber = new FixedLengthTextField();
        this.tfGiftCertNumber.setLength(64);
        panel.add((Component)this.tfGiftCertNumber, "cell 1 0,growx");
        JLabel lblFaceValue = new JLabel(Messages.getString("GiftCertDialog.8"));
        panel.add((Component)lblFaceValue, "cell 0 1,alignx trailing");
        this.tfFaceValue = new DoubleTextField();
        this.tfFaceValue.setText("50");
        panel.add((Component)this.tfFaceValue, "cell 1 1,growx");
        this.qwertyKeyPad = new QwertyKeyPad();
        panel.add((Component)((Object)this.qwertyKeyPad), "newline, gaptop 10px, span");
    }

    @Override
    public void doOk() {
        if (StringUtils.isEmpty((String)this.getGiftCertNumber())) {
            POSMessageDialog.showMessage(Messages.getString("GiftCertDialog.14"));
            return;
        }
        if (this.getGiftCertFaceValue() <= 0.0) {
            POSMessageDialog.showMessage(Messages.getString("GiftCertDialog.15"));
            return;
        }
        this.setCanceled(false);
        this.dispose();
    }

    public String getGiftCertNumber() {
        return this.tfGiftCertNumber.getText();
    }

    public double getGiftCertFaceValue() {
        return this.tfFaceValue.getDouble();
    }
}

