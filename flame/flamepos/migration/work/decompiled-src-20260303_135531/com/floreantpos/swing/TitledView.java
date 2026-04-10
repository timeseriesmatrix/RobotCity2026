/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.ui.TitlePanel;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;

public class TitledView
extends JPanel {
    private TitlePanel titlePanel = new TitlePanel();
    private JPanel contentPane = new JPanel();

    public TitledView() {
        this("");
    }

    public TitledView(String title) {
        this.setLayout(new BorderLayout());
        this.add((Component)this.titlePanel, "North");
        this.add(this.contentPane);
        this.setTitle(title);
    }

    public void setTitle(String title) {
        this.titlePanel.setTitle(title);
    }

    public String getTitle() {
        return this.titlePanel.getTitle();
    }

    public void setTitlePaneVisible(boolean visible) {
        this.titlePanel.setVisible(visible);
    }

    public boolean isTitlePaneVisible() {
        return this.titlePanel.isVisible();
    }

    public JPanel getContentPane() {
        return this.contentPane;
    }
}

