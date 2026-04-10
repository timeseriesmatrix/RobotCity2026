/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.Messages;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.NumericKeypad;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class GratuityInputDialog
extends OkCancelOptionDialog {
    private DoubleTextField doubleTextField;

    public GratuityInputDialog() {
        super(POSUtil.getFocusedWindow());
        this.setTitlePaneText(Messages.getString("GratuityInputDialog.0"));
        JPanel panel = new JPanel();
        panel.setLayout((LayoutManager)new MigLayout("inset 0", "[grow,fill]", "[grow,fill]"));
        this.doubleTextField = new DoubleTextField();
        this.doubleTextField.setHorizontalAlignment(11);
        this.doubleTextField.setFocusCycleRoot(true);
        this.doubleTextField.setFont(this.doubleTextField.getFont().deriveFont(1, PosUIManager.getNumberFieldFontSize()));
        panel.add((Component)this.doubleTextField, "cell 0 0,alignx left,height 40px,aligny top");
        NumericKeypad numericKeypad = new NumericKeypad();
        panel.add((Component)numericKeypad, "cell 0 1");
        this.getContentPanel().add(panel);
    }

    @Override
    public void doOk() {
        this.setCanceled(false);
        this.dispose();
    }

    public double getGratuityAmount() {
        return this.doubleTextField.getDouble();
    }
}

