/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.model;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.dao.MenuCategoryDAO;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.util.POSUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

public class MenuCategoryForm
extends BeanEditor {
    private JCheckBox chkBeverage;
    private JCheckBox chkVisible;
    private JLabel jLabel1;
    private FixedLengthTextField tfName;
    private IntegerTextField tfSortOrder;
    private JButton btnButtonColor;
    private JLabel lblSortOrder;
    private JLabel lblButtonColor;
    private JLabel lblTranslatedName;
    private FixedLengthTextField tfTranslatedName;
    private JLabel lblTextColor;
    private JButton btnTextColor;

    public MenuCategoryForm() throws Exception {
        this(new MenuCategory());
    }

    public MenuCategoryForm(MenuCategory category) throws Exception {
        this.initComponents();
        this.setBean(category);
    }

    @Override
    public String getDisplayText() {
        MenuCategory foodCategory = (MenuCategory)this.getBean();
        if (foodCategory.getId() == null) {
            return POSConstants.NEW_MENU_CATEGORY;
        }
        return POSConstants.EDIT_MENU_CATEGORY;
    }

    private void initComponents() {
        this.jLabel1 = new JLabel();
        this.chkVisible = new JCheckBox();
        this.tfName = new FixedLengthTextField();
        this.tfName.setLength(120);
        this.chkBeverage = new JCheckBox();
        this.jLabel1.setText(POSConstants.NAME + ":");
        this.chkVisible.setText(POSConstants.VISIBLE);
        this.chkVisible.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.chkVisible.setMargin(new Insets(0, 0, 0, 0));
        this.chkBeverage.setText(POSConstants.BEVERAGE);
        this.chkBeverage.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.chkBeverage.setMargin(new Insets(0, 0, 0, 0));
        this.lblSortOrder = new JLabel(Messages.getString("MenuCategoryForm.1"));
        this.tfSortOrder = new IntegerTextField();
        this.tfSortOrder.setColumns(10);
        this.lblButtonColor = new JLabel(Messages.getString("MenuCategoryForm.2"));
        this.btnButtonColor = new JButton();
        this.btnButtonColor.setPreferredSize(new Dimension(140, 40));
        this.setLayout((LayoutManager)new MigLayout("", "[87px][327px,grow]", "[19px][][19px][][][21px][15px]"));
        this.add((Component)this.jLabel1, "cell 0 0,alignx left,aligny center");
        this.lblTranslatedName = new JLabel(Messages.getString("MenuCategoryForm.7"));
        this.add((Component)this.lblTranslatedName, "cell 0 1,alignx trailing");
        this.tfTranslatedName = new FixedLengthTextField();
        this.tfTranslatedName.setLength(120);
        this.add((Component)this.tfTranslatedName, "cell 1 1,growx");
        this.add((Component)this.lblSortOrder, "cell 0 2,alignx left,aligny center");
        this.add((Component)this.lblButtonColor, "cell 0 3,alignx left,growy");
        this.add((Component)this.tfName, "cell 1 0,growx,aligny top");
        this.add((Component)this.tfSortOrder, "cell 1 2,alignx left,aligny top");
        this.lblTextColor = new JLabel("Text color");
        this.add((Component)this.lblTextColor, "cell 0 4");
        this.btnTextColor = new JButton();
        this.btnTextColor.setText(Messages.getString("MenuCategoryForm.16"));
        this.btnTextColor.setPreferredSize(new Dimension(140, 40));
        this.add((Component)this.btnTextColor, "cell 1 4,growy");
        this.add((Component)this.chkBeverage, "cell 1 5,alignx left,growy");
        this.add((Component)this.chkVisible, "cell 1 6,alignx left,aligny top");
        this.add((Component)this.btnButtonColor, "cell 1 3,alignx left,growy");
        this.btnButtonColor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(MenuCategoryForm.this, Messages.getString("MenuCategoryForm.21"), MenuCategoryForm.this.btnButtonColor.getBackground());
                MenuCategoryForm.this.btnButtonColor.setBackground(color);
                MenuCategoryForm.this.btnTextColor.setBackground(color);
            }
        });
        this.btnTextColor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(MenuCategoryForm.this, Messages.getString("MenuCategoryForm.22"), MenuCategoryForm.this.btnTextColor.getForeground());
                MenuCategoryForm.this.btnTextColor.setForeground(color);
            }
        });
    }

    @Override
    protected void updateView() {
        Color buttonColor;
        MenuCategory menuCategory = (MenuCategory)this.getBean();
        if (menuCategory == null) {
            this.tfName.setText("");
            this.tfTranslatedName.setText("");
            this.tfSortOrder.setText("0");
            this.chkVisible.setSelected(false);
            return;
        }
        this.tfName.setText(menuCategory.getName());
        this.tfTranslatedName.setText(menuCategory.getTranslatedName());
        if (menuCategory.getSortOrder() != null) {
            this.tfSortOrder.setText(menuCategory.getSortOrder().toString());
        }
        if ((buttonColor = menuCategory.getButtonColor()) != null) {
            this.btnButtonColor.setBackground(buttonColor);
            this.btnTextColor.setBackground(buttonColor);
        }
        if (menuCategory.getTextColor() != null) {
            this.btnTextColor.setForeground(menuCategory.getTextColor());
        }
        this.chkBeverage.setSelected(menuCategory.isBeverage());
        if (menuCategory.getId() == null) {
            this.chkVisible.setSelected(true);
        } else {
            this.chkVisible.setSelected(menuCategory.isVisible());
        }
    }

    @Override
    protected boolean updateModel() {
        MenuCategory menuCategory = (MenuCategory)this.getBean();
        if (menuCategory == null) {
            return false;
        }
        String categoryName = this.tfName.getText();
        if (POSUtil.isBlankOrNull(categoryName)) {
            MessageDialog.showError(Messages.getString("MenuCategoryForm.26"));
            return false;
        }
        menuCategory.setName(categoryName);
        menuCategory.setTranslatedName(this.tfTranslatedName.getText());
        menuCategory.setSortOrder(this.tfSortOrder.getInteger());
        menuCategory.setButtonColor(this.btnButtonColor.getBackground());
        menuCategory.setTextColor(this.btnTextColor.getForeground());
        menuCategory.setButtonColorCode(this.btnButtonColor.getBackground().getRGB());
        menuCategory.setTextColorCode(this.btnTextColor.getForeground().getRGB());
        menuCategory.setBeverage(this.chkBeverage.isSelected());
        menuCategory.setVisible(this.chkVisible.isSelected());
        return true;
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
            MenuCategory menuCategory = (MenuCategory)this.getBean();
            MenuCategoryDAO.getInstance().saveOrUpdate(menuCategory);
            return true;
        }
        catch (Exception x) {
            MessageDialog.showError(x);
            return false;
        }
    }
}

