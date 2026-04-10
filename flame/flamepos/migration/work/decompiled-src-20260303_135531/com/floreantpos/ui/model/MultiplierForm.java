/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.model;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.dao.MultiplierDAO;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.util.POSUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class MultiplierForm
extends BeanEditor {
    private FixedLengthTextField tfName;
    private FixedLengthTextField tfTicketPrefix;
    private DoubleTextField tfRate;
    private IntegerTextField tfSortOrder;
    private JButton btnButtonColor;
    private JButton btnTextColor;

    public MultiplierForm() {
        this(new Multiplier());
    }

    public MultiplierForm(Multiplier multiplier) {
        this.initComponents();
        this.setBean(multiplier);
    }

    private void initComponents() {
        this.tfName = new FixedLengthTextField();
        this.tfName.setLength(20);
        this.tfRate = new DoubleTextField();
        this.tfTicketPrefix = new FixedLengthTextField();
        this.tfTicketPrefix.setLength(20);
        this.tfRate.setHorizontalAlignment(4);
        this.tfSortOrder = new IntegerTextField();
        JLabel lblButtonColor = new JLabel(Messages.getString("MenuModifierForm.1"));
        JLabel lblTextColor = new JLabel(Messages.getString("MenuModifierForm.27"));
        this.btnButtonColor = new JButton("");
        this.btnButtonColor.setPreferredSize(new Dimension(140, 40));
        this.btnTextColor = new JButton(Messages.getString("MenuModifierForm.29"));
        this.btnTextColor.setPreferredSize(new Dimension(140, 40));
        this.btnButtonColor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(POSUtil.getBackOfficeWindow(), Messages.getString("MenuModifierForm.39"), MultiplierForm.this.btnButtonColor.getBackground());
                MultiplierForm.this.btnButtonColor.setBackground(color);
                MultiplierForm.this.btnTextColor.setBackground(color);
            }
        });
        this.btnTextColor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(POSUtil.getBackOfficeWindow(), Messages.getString("MenuModifierForm.40"), MultiplierForm.this.btnTextColor.getForeground());
                MultiplierForm.this.btnTextColor.setForeground(color);
            }
        });
        JPanel contentPanel = new JPanel((LayoutManager)new MigLayout("fillx"));
        contentPanel.add(new JLabel("Name"));
        contentPanel.add((Component)this.tfName, "grow,wrap");
        contentPanel.add(new JLabel("Ticket prefix"));
        contentPanel.add((Component)this.tfTicketPrefix, "grow,wrap");
        contentPanel.add(new JLabel("Percentage (%)"));
        contentPanel.add((Component)this.tfRate, "grow,wrap");
        contentPanel.add(new JLabel("Sort Order"));
        contentPanel.add((Component)this.tfSortOrder, "grow,wrap");
        contentPanel.add(lblButtonColor);
        contentPanel.add((Component)this.btnButtonColor, "wrap");
        contentPanel.add(lblTextColor);
        contentPanel.add((Component)this.btnTextColor, "wrap");
        this.add(contentPanel);
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
            MultiplierDAO dao = new MultiplierDAO();
            Multiplier multiplier = (Multiplier)this.getBean();
            if (dao.get(multiplier.getName()) == null) {
                dao.save(multiplier);
            } else {
                dao.update(multiplier);
            }
        }
        catch (Exception e) {
            MessageDialog.showError(e);
            return false;
        }
        return true;
    }

    @Override
    protected void updateView() {
        Color color;
        Multiplier multiplier = (Multiplier)this.getBean();
        this.tfName.setText(multiplier.getName());
        this.tfTicketPrefix.setText(multiplier.getTicketPrefix());
        this.tfRate.setText("" + multiplier.getRate());
        this.tfSortOrder.setText(String.valueOf(multiplier.getSortOrder()));
        if (multiplier.getButtonColor() != null) {
            color = new Color(multiplier.getButtonColor());
            this.btnButtonColor.setBackground(color);
            this.btnTextColor.setBackground(color);
        }
        if (multiplier.getTextColor() != null) {
            color = new Color(multiplier.getTextColor());
            this.btnTextColor.setForeground(color);
        }
    }

    @Override
    protected boolean updateModel() {
        String name = this.tfName.getText();
        Multiplier multiplier = (Multiplier)this.getBean();
        if (POSUtil.isBlankOrNull(name)) {
            MessageDialog.showError(POSConstants.NAME_REQUIRED);
            return false;
        }
        multiplier.setName(name);
        multiplier.setRate(this.tfRate.getDouble());
        multiplier.setTicketPrefix(this.tfTicketPrefix.getText());
        multiplier.setSortOrder(this.tfSortOrder.getInteger());
        multiplier.setButtonColor(this.btnButtonColor.getBackground().getRGB());
        multiplier.setTextColor(this.btnTextColor.getForeground().getRGB());
        return true;
    }

    @Override
    public String getDisplayText() {
        Multiplier multiplier = (Multiplier)this.getBean();
        if (multiplier.getName() == null) {
            return "New multiplier";
        }
        return "Edit multiplier";
    }
}

