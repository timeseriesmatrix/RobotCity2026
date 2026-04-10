/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.config.ui;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

public abstract class ConfigurationView
extends JPanel {
    private boolean initialized = false;

    public ConfigurationView() {
        this.setBorder(new CompoundBorder(new EtchedBorder(), new EmptyBorder(10, 10, 10, 10)));
    }

    protected JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(4);
        return label;
    }

    protected void addRow(String title, JTextField textField) {
        this.add((Component)this.createLabel(title), "newline, grow");
        this.add((Component)textField, "w 250,height pref");
    }

    protected void addRow(String title, JTextField textField, String constraints) {
        this.add((Component)this.createLabel(title), "newline, grow");
        this.add((Component)textField, constraints);
    }

    public abstract boolean save() throws Exception;

    public abstract void initialize() throws Exception;

    @Override
    public abstract String getName();

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}

