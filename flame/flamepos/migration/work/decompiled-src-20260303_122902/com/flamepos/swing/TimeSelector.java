/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.swing;

import com.floreantpos.Messages;
import com.floreantpos.swing.POSComboBox;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import net.miginfocom.swing.MigLayout;

public class TimeSelector
extends JPanel {
    private JToggleButton tbAM;
    private JToggleButton tbPM;
    private POSComboBox cbHour;
    private POSComboBox cbMin;

    public TimeSelector() {
        this.setLayout((LayoutManager)new MigLayout("", "[][grow][][grow][][]", "[]"));
        JLabel lblHour = new JLabel(Messages.getString("TimeSelector.0"));
        this.add((Component)lblHour, "cell 0 0,alignx trailing");
        Vector<Integer> hours = new Vector<Integer>();
        for (int i = 1; i <= 12; ++i) {
            hours.add(i);
        }
        this.cbHour = new POSComboBox((Vector)hours);
        this.add((Component)this.cbHour, "cell 1 0,growy");
        JLabel lblMin = new JLabel(Messages.getString("TimeSelector.1"));
        this.add((Component)lblMin, "cell 2 0,alignx trailing");
        Vector<Integer> minutes = new Vector<Integer>();
        for (int i = 0; i <= 45; i += 15) {
            minutes.add(i);
        }
        this.cbMin = new POSComboBox((Vector)minutes);
        this.add((Component)this.cbMin, "cell 3 0,growy");
        ButtonGroup group = new ButtonGroup();
        group.add(this.tbAM);
        group.add(this.tbPM);
        this.tbAM = new JToggleButton(Messages.getString("TimeSelector.9"));
        this.add((Component)this.tbAM, "cell 4 0,w 70!,grow");
        this.tbPM = new JToggleButton(Messages.getString("TimeSelector.11"));
        this.add((Component)this.tbPM, "cell 5 0,w 70!,grow");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(this.tbAM);
        buttonGroup.add(this.tbPM);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(10);
        int min = calendar.get(12);
        int i = calendar.get(9);
        this.setSelectedHour(hour);
        this.setSelectedMin(min);
        if (i == 0) {
            this.tbAM.setSelected(true);
        } else {
            this.tbPM.setSelected(true);
        }
    }

    public int getSelectedHour() {
        int hour = (Integer)this.cbHour.getSelectedItem();
        return hour;
    }

    public void setSelectedHour(int hour) {
        if (hour == 0) {
            hour = 12;
        }
        this.cbHour.setSelectedItem(hour);
    }

    public int getSelectedMin() {
        int minute = (Integer)this.cbMin.getSelectedItem();
        return minute;
    }

    public void setSelectedMin(int min) {
        this.cbMin.setSelectedItem(min);
    }

    public int getAmPm() {
        if (this.tbAM.isSelected()) {
            return 0;
        }
        return 1;
    }

    public void setEnable(boolean enable) {
        this.cbHour.setEnabled(enable);
        this.cbMin.setEnabled(enable);
        this.tbAM.setEnabled(enable);
        this.tbPM.setEnabled(enable);
    }
}

