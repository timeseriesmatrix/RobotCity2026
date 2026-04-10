/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views;

import com.floreantpos.Messages;
import com.floreantpos.swing.POSTextField;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class DateEntryView
extends JPanel {
    private POSTextField tfDay;
    private POSTextField tfMonth;
    private POSTextField tfYear;

    public DateEntryView() {
        this.createUI();
    }

    private void createUI() {
        this.setLayout((LayoutManager)new MigLayout("", "[][grow][][grow][][grow]", "[]"));
        JLabel lblDaye = new JLabel(Messages.getString("DateEntryView.3"));
        this.add((Component)lblDaye, "cell 0 0,alignx trailing");
        this.tfDay = new POSTextField();
        this.tfDay.setColumns(2);
        this.add((Component)this.tfDay, "cell 1 0,growx");
        JLabel lblMonth = new JLabel(Messages.getString("DateEntryView.6"));
        this.add((Component)lblMonth, "cell 2 0,alignx trailing");
        this.tfMonth = new POSTextField();
        this.tfMonth.setColumns(2);
        this.add((Component)this.tfMonth, "cell 3 0,growx");
        JLabel lblYear = new JLabel(Messages.getString("DateEntryView.9"));
        this.add((Component)lblYear, "cell 4 0,alignx trailing");
        this.tfYear = new POSTextField();
        this.tfYear.setColumns(4);
        this.add((Component)this.tfYear, "cell 5 0,growx");
    }

    public String getDay() {
        return this.tfDay.getText();
    }

    public String getMonth() {
        return this.tfMonth.getText();
    }

    public String getYear() {
        return this.tfYear.getText();
    }
}

