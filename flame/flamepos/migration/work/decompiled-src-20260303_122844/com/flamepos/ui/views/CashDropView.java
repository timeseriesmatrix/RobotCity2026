/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.layout.GroupLayout
 *  org.jdesktop.layout.GroupLayout$Group
 */
package com.floreantpos.ui.views;

import java.awt.Container;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import org.jdesktop.layout.GroupLayout;

public class CashDropView
extends JPanel {
    public CashDropView() {
        this.initComponents();
    }

    private void initComponents() {
        GroupLayout layout = new GroupLayout((Container)this);
        this.setLayout((LayoutManager)layout);
        layout.setHorizontalGroup((GroupLayout.Group)layout.createParallelGroup(1).add(0, 400, Short.MAX_VALUE));
        layout.setVerticalGroup((GroupLayout.Group)layout.createParallelGroup(1).add(0, 300, Short.MAX_VALUE));
    }
}

