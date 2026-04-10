/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.model;

import com.floreantpos.POSConstants;
import com.floreantpos.model.PizzaCrust;
import com.floreantpos.model.dao.PizzaCrustDAO;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class PizzaCrustForm
extends BeanEditor {
    private FixedLengthTextField tfName;
    private FixedLengthTextField tfDescription;
    private FixedLengthTextField tfTranslatedName;
    private FixedLengthTextField tfSortOrder;

    public PizzaCrustForm() {
        this(new PizzaCrust());
    }

    public PizzaCrustForm(PizzaCrust pizzaCrust) {
        this.initComponents();
        this.setBean(pizzaCrust);
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel((LayoutManager)new MigLayout("fill"));
        JLabel lblName = new JLabel(POSConstants.NAME + ":");
        JLabel lblDescription = new JLabel("Description");
        JLabel lblTranslatedName = new JLabel("Translated Name");
        JLabel lblSortOrder = new JLabel("Sort Order");
        this.tfName = new FixedLengthTextField();
        this.tfDescription = new FixedLengthTextField();
        this.tfTranslatedName = new FixedLengthTextField();
        this.tfSortOrder = new FixedLengthTextField();
        contentPanel.add((Component)lblName, "cell 0 0");
        contentPanel.add((Component)this.tfName, "cell 1 0");
        contentPanel.add((Component)lblTranslatedName, "cell 0 1");
        contentPanel.add((Component)this.tfTranslatedName, "cell 1 1");
        contentPanel.add((Component)lblDescription, "cell 0 2");
        contentPanel.add((Component)this.tfDescription, "cell 1 2");
        contentPanel.add((Component)lblSortOrder, "cell 0 3");
        contentPanel.add((Component)this.tfSortOrder, "cell 1 3");
        this.add(contentPanel);
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
            PizzaCrust pizzaCrust = (PizzaCrust)this.getBean();
            PizzaCrustDAO dao = new PizzaCrustDAO();
            dao.saveOrUpdate(pizzaCrust);
        }
        catch (Exception e) {
            MessageDialog.showError(e);
            return false;
        }
        return true;
    }

    @Override
    protected void updateView() {
        PizzaCrust pizzaCrust = (PizzaCrust)this.getBean();
        if (pizzaCrust == null) {
            return;
        }
        this.tfName.setText(pizzaCrust.getName());
        this.tfDescription.setText(pizzaCrust.getDescription());
        this.tfTranslatedName.setText(pizzaCrust.getTranslatedName());
        this.tfSortOrder.setText(String.valueOf(pizzaCrust.getSortOrder()));
    }

    @Override
    protected boolean updateModel() {
        PizzaCrust pizzaCrust = (PizzaCrust)this.getBean();
        String name = this.tfName.getText();
        String description = this.tfDescription.getText();
        String translatedName = this.tfTranslatedName.getText();
        String sortOrder = this.tfSortOrder.getText();
        if (POSUtil.isBlankOrNull(name)) {
            MessageDialog.showError("Name is required");
            return false;
        }
        pizzaCrust.setName(name);
        pizzaCrust.setDescription(description);
        pizzaCrust.setTranslatedName(translatedName);
        pizzaCrust.setSortOrder(Integer.valueOf(sortOrder));
        PizzaCrustDAO dao = new PizzaCrustDAO();
        dao.saveOrUpdate(pizzaCrust);
        return true;
    }

    @Override
    public String getDisplayText() {
        PizzaCrust pizzaCrust = (PizzaCrust)this.getBean();
        if (pizzaCrust.getId() == null) {
            return "New Pizza Crust";
        }
        return "Edit Pizza Crust";
    }
}

