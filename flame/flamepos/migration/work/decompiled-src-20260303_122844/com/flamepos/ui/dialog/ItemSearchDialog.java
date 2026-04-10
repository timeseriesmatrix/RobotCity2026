/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.QwertyKeyPad;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class ItemSearchDialog
extends OkCancelOptionDialog {
    private JTextField tfNumber;
    private String value;
    private QwertyKeyPad qwertyKeyPad;

    public ItemSearchDialog() {
        super(POSUtil.getFocusedWindow(), POSConstants.SEARCH_ITEM_BUTTON_TEXT);
        this.init();
    }

    public ItemSearchDialog(Frame parent) {
        super((Window)parent, POSConstants.SEARCH_ITEM_BUTTON_TEXT);
        this.init();
    }

    private void init() {
        this.setResizable(false);
        JPanel contentPane = this.getContentPanel();
        MigLayout layout = new MigLayout("inset 0");
        contentPane.setLayout((LayoutManager)layout);
        this.tfNumber = new JTextField();
        this.tfNumber.setFont(this.tfNumber.getFont().deriveFont(1, PosUIManager.getNumberFieldFontSize()));
        this.tfNumber.setFocusable(true);
        this.tfNumber.requestFocus();
        this.tfNumber.setBackground(Color.WHITE);
        this.tfNumber.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ItemSearchDialog.this.doOk();
            }
        });
        this.qwertyKeyPad = new QwertyKeyPad();
        contentPane.add((Component)this.tfNumber, "spanx, grow");
        contentPane.add((Component)((Object)this.qwertyKeyPad), "spanx ,grow");
    }

    @Override
    public void doOk() {
        String s = this.tfNumber.getText();
        if (s.equals("0") || s.equals("")) {
            POSMessageDialog.showError(Application.getPosWindow(), "Please enter barcode or item no.");
            return;
        }
        this.setValue(this.tfNumber.getText());
        this.setCanceled(false);
        this.dispose();
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

