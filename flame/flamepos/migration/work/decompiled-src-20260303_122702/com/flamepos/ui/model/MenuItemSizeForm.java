/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.model;

import com.floreantpos.POSConstants;
import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.dao.MenuItemSizeDAO;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class MenuItemSizeForm
extends BeanEditor {
    private FixedLengthTextField tfName;
    private FixedLengthTextField tfDescription;
    private FixedLengthTextField tfTranslatedName;
    private IntegerTextField tfSortOrder;
    private DoubleTextField tfSizeInInch;

    public MenuItemSizeForm() {
        this(new MenuItemSize());
    }

    public MenuItemSizeForm(MenuItemSize menuItemSize) {
        this.initComponents();
        this.setBean(menuItemSize);
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel((LayoutManager)new MigLayout("", "[][]", ""));
        JLabel lblName = new JLabel(POSConstants.NAME + ":");
        JLabel lblDescription = new JLabel("Description");
        JLabel lblTranslatedName = new JLabel("Translated Name");
        JLabel lblSortOrder = new JLabel("Sort Order");
        JLabel lblSize = new JLabel("Size in Inch");
        this.tfName = new FixedLengthTextField(60);
        this.tfName.setColumns(30);
        this.tfDescription = new FixedLengthTextField(120);
        this.tfDescription.setColumns(30);
        this.tfTranslatedName = new FixedLengthTextField(60);
        this.tfTranslatedName.setColumns(30);
        this.tfSortOrder = new IntegerTextField();
        this.tfSortOrder.setColumns(10);
        this.tfSizeInInch = new DoubleTextField();
        this.tfSizeInInch.setColumns(10);
        contentPanel.add((Component)lblName, "");
        contentPanel.add((Component)this.tfName, "");
        contentPanel.add((Component)lblTranslatedName, "newline");
        contentPanel.add((Component)this.tfTranslatedName, "");
        contentPanel.add((Component)lblDescription, "newline");
        contentPanel.add((Component)this.tfDescription, "");
        contentPanel.add((Component)lblSize, "newline");
        contentPanel.add((Component)this.tfSizeInInch, "");
        contentPanel.add((Component)lblSortOrder, "newline");
        contentPanel.add((Component)this.tfSortOrder, "");
        this.add(contentPanel);
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
            MenuItemSize menuItemSize = (MenuItemSize)this.getBean();
            MenuItemSizeDAO dao = new MenuItemSizeDAO();
            dao.saveOrUpdate(menuItemSize);
        }
        catch (Exception e) {
            MessageDialog.showError(e);
            return false;
        }
        return true;
    }

    @Override
    protected void updateView() {
        MenuItemSize menuItemSize = (MenuItemSize)this.getBean();
        if (menuItemSize == null) {
            return;
        }
        this.tfName.setText(menuItemSize.getName());
        this.tfDescription.setText(menuItemSize.getDescription());
        this.tfTranslatedName.setText(menuItemSize.getTranslatedName());
        this.tfSortOrder.setText(String.valueOf(menuItemSize.getSortOrder()));
        this.tfSizeInInch.setText(String.valueOf(menuItemSize.getSizeInInch()));
    }

    @Override
    protected boolean updateModel() {
        MenuItemSize menuItemSize = (MenuItemSize)this.getBean();
        String name = this.tfName.getText();
        String description = this.tfDescription.getText();
        String translatedName = this.tfTranslatedName.getText();
        int sortOrder = this.tfSortOrder.getInteger();
        double size = this.tfSizeInInch.getDouble();
        if (POSUtil.isBlankOrNull(name)) {
            MessageDialog.showError("Name is required");
            return false;
        }
        menuItemSize.setName(name);
        menuItemSize.setDescription(description);
        menuItemSize.setTranslatedName(translatedName);
        menuItemSize.setSortOrder(sortOrder);
        menuItemSize.setSizeInInch(size);
        MenuItemSizeDAO dao = new MenuItemSizeDAO();
        dao.saveOrUpdate(menuItemSize);
        return true;
    }

    @Override
    public String getDisplayText() {
        MenuItemSize menuItemSize = (MenuItemSize)this.getBean();
        if (menuItemSize.getId() == null) {
            return "New Size";
        }
        return "Edit Size";
    }
}

