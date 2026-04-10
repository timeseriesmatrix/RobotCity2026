/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.swing.PosUIManager;
import java.awt.Font;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.ComboPopup;

public class POSComboBox
extends JComboBox {
    Font font = new Font("Tahoma", 0, PosUIManager.getFontSize(18));

    public POSComboBox() {
        this.setHeight(40);
        this.setFont(this.font);
    }

    public POSComboBox(Object[] items) {
        super(items);
        this.setHeight(40);
        this.setFont(this.font);
    }

    public void setHeight(int height) {
        this.setMinimumSize(PosUIManager.getSize(60, 40));
        this.setPreferredSize(PosUIManager.getSize(0, height));
        Accessible popup = this.getUI().getAccessibleChild(this, 0);
        if (popup instanceof ComboPopup) {
            JList jlist = ((ComboPopup)((Object)popup)).getList();
            jlist.setFixedCellHeight(PosUIManager.getSize(height));
        }
    }

    public POSComboBox(Vector items) {
        super(items);
        this.setHeight(40);
        this.setFont(this.font);
    }
}

