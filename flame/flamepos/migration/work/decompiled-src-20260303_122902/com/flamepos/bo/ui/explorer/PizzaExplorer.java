/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.bo.ui.explorer.MenuItemSizeExplorer;
import com.floreantpos.bo.ui.explorer.ModifierGroupExplorer;
import com.floreantpos.bo.ui.explorer.PizzaCrustExplorer;
import com.floreantpos.bo.ui.explorer.PizzaItemExplorer;
import com.floreantpos.bo.ui.explorer.PizzaModifierExplorer;
import com.floreantpos.swing.TransparentPanel;
import java.awt.BorderLayout;
import java.awt.FontMetrics;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class PizzaExplorer
extends TransparentPanel {
    private JTabbedPane mainTab;

    public PizzaExplorer() {
        this.initComponents();
    }

    private void initComponents() {
        this.mainTab = new JTabbedPane(1, 1);
        this.setLayout(new BorderLayout());
        this.mainTab.setUI(new BasicTabbedPaneUI(){

            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 30;
            }

            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                return 125;
            }
        });
        this.mainTab.addTab("Item", new PizzaItemExplorer());
        this.mainTab.addTab("Modifier", new PizzaModifierExplorer());
        this.mainTab.addTab("Size", new MenuItemSizeExplorer());
        this.mainTab.addTab("Crust", new PizzaCrustExplorer());
        this.mainTab.addTab("Modifier Group", new ModifierGroupExplorer());
        this.add(this.mainTab);
    }
}

