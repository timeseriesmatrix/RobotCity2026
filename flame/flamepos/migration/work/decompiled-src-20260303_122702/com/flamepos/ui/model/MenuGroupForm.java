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
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.dao.MenuCategoryDAO;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.swing.ComboBoxModel;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.model.MenuCategoryForm;
import com.floreantpos.util.POSUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

public class MenuGroupForm
extends BeanEditor {
    private JButton btnNewCategory;
    private JComboBox cbCategory;
    private JCheckBox chkVisible;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private FixedLengthTextField tfName;
    private JLabel lblTranslatedName;
    private FixedLengthTextField tfTranslatedName;
    private JLabel lblSortOrder;
    private JLabel lblButtonColor;
    private IntegerTextField tfSortOrder;
    private JButton btnButtonColor;
    private JLabel lblTextColor;
    private JButton btnTextColor;

    public MenuGroupForm() {
        this(new MenuGroup());
    }

    public MenuGroupForm(MenuGroup foodGroup) {
        this.initComponents();
        this.setBean(foodGroup);
    }

    private void initComponents() {
        this.setLayout((LayoutManager)new MigLayout("", "[70px][289px,grow][6px][49px]", "[19px][][25px][][][][15px]"));
        this.setPreferredSize(new Dimension(600, 250));
        this.jLabel1 = new JLabel();
        this.tfName = new FixedLengthTextField(120);
        this.jLabel2 = new JLabel();
        this.cbCategory = new JComboBox();
        this.chkVisible = new JCheckBox();
        this.btnNewCategory = new JButton();
        this.jLabel1.setText(POSConstants.NAME + ":");
        this.jLabel2.setText(POSConstants.CATEGORY + ":");
        this.chkVisible.setText(POSConstants.VISIBLE);
        this.chkVisible.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.chkVisible.setMargin(new Insets(0, 0, 0, 0));
        this.btnNewCategory.setText("...");
        this.btnNewCategory.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                MenuGroupForm.this.doNewCategory(evt);
            }
        });
        this.lblTranslatedName = new JLabel(Messages.getString("MenuGroupForm.6"));
        this.add((Component)this.lblTranslatedName, "cell 0 1,alignx trailing");
        this.tfTranslatedName = new FixedLengthTextField(120);
        this.add((Component)this.tfTranslatedName, "cell 1 1,growx");
        this.add((Component)this.jLabel2, "cell 0 2,alignx left,aligny center");
        this.add((Component)this.jLabel1, "cell 0 0,alignx left,aligny center");
        this.add((Component)this.tfName, "cell 1 0,growx");
        this.lblSortOrder = new JLabel(Messages.getString("MenuGroupForm.12"));
        this.add((Component)this.lblSortOrder, "cell 0 3,alignx leading");
        this.tfSortOrder = new IntegerTextField();
        this.tfSortOrder.setColumns(10);
        this.add((Component)this.tfSortOrder, "cell 1 3");
        this.lblButtonColor = new JLabel(Messages.getString("MenuGroupForm.15"));
        this.add((Component)this.lblButtonColor, "cell 0 4");
        this.btnButtonColor = new JButton("");
        this.btnButtonColor.setPreferredSize(new Dimension(140, 40));
        this.add((Component)this.btnButtonColor, "cell 1 4,growy");
        this.lblTextColor = new JLabel(Messages.getString("MenuGroupForm.19"));
        this.add((Component)this.lblTextColor, "cell 0 5");
        this.btnTextColor = new JButton(Messages.getString("MenuGroupForm.21"));
        this.btnTextColor.setPreferredSize(new Dimension(140, 40));
        this.add((Component)this.btnTextColor, "cell 1 5");
        this.add((Component)this.chkVisible, "cell 1 6,alignx left,aligny top");
        this.add((Component)this.cbCategory, "cell 1 2,growx,aligny top");
        this.add((Component)this.btnNewCategory, "cell 3 2,alignx left,aligny top");
        this.btnButtonColor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(MenuGroupForm.this, Messages.getString("MenuGroupForm.26"), MenuGroupForm.this.btnButtonColor.getBackground());
                MenuGroupForm.this.btnButtonColor.setBackground(color);
                MenuGroupForm.this.btnTextColor.setBackground(color);
            }
        });
        this.btnTextColor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(MenuGroupForm.this, Messages.getString("MenuGroupForm.27"), MenuGroupForm.this.btnTextColor.getForeground());
                MenuGroupForm.this.btnTextColor.setForeground(color);
            }
        });
        MenuCategoryDAO categoryDAO = new MenuCategoryDAO();
        List<MenuCategory> foodCategories = categoryDAO.findAll();
        this.cbCategory.setModel(new ComboBoxModel(foodCategories));
        this.cbCategory.setSelectedItem(null);
    }

    private void doNewCategory(ActionEvent evt) {
        try {
            MenuCategoryForm editor = new MenuCategoryForm();
            BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
            dialog.open();
            if (!dialog.isCanceled()) {
                MenuCategory foodCategory = (MenuCategory)editor.getBean();
                ComboBoxModel model = (ComboBoxModel)this.cbCategory.getModel();
                model.addElement(foodCategory);
                model.setSelectedItem(foodCategory);
            }
        }
        catch (Exception x) {
            MessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
        }
    }

    @Override
    public boolean save() {
        if (!this.updateModel()) {
            return false;
        }
        MenuGroup foodGroup = (MenuGroup)this.getBean();
        try {
            MenuGroupDAO foodGroupDAO = new MenuGroupDAO();
            foodGroupDAO.saveOrUpdate(foodGroup);
        }
        catch (Exception e) {
            MessageDialog.showError(e);
            return false;
        }
        return true;
    }

    @Override
    protected void updateView() {
        Color buttonColor;
        MenuGroup menuGroup = (MenuGroup)this.getBean();
        if (menuGroup == null) {
            this.tfName.setText("");
            this.cbCategory.setSelectedItem(null);
            this.chkVisible.setSelected(false);
            return;
        }
        this.tfName.setText(menuGroup.getName());
        this.tfTranslatedName.setText(menuGroup.getTranslatedName());
        if (menuGroup.getSortOrder() != null) {
            this.tfSortOrder.setText(menuGroup.getSortOrder().toString());
        }
        if ((buttonColor = menuGroup.getButtonColor()) != null) {
            this.btnButtonColor.setBackground(buttonColor);
            this.btnTextColor.setBackground(buttonColor);
        }
        if (menuGroup.getTextColor() != null) {
            this.btnTextColor.setForeground(menuGroup.getTextColor());
        }
        this.chkVisible.setSelected(menuGroup.isVisible());
        this.cbCategory.setSelectedItem(menuGroup.getParent());
    }

    @Override
    protected boolean updateModel() {
        MenuGroup menuGroup = (MenuGroup)this.getBean();
        if (menuGroup == null) {
            return false;
        }
        String name = this.tfName.getText();
        if (POSUtil.isBlankOrNull(name)) {
            MessageDialog.showError(Messages.getString("MenuGroupForm.29"));
            return false;
        }
        MenuCategory category = (MenuCategory)this.cbCategory.getSelectedItem();
        if (category == null) {
            MessageDialog.showError(Messages.getString("MenuGroupForm.30"));
            return false;
        }
        menuGroup.setName(this.tfName.getText());
        menuGroup.setTranslatedName(this.tfTranslatedName.getText());
        menuGroup.setSortOrder(this.tfSortOrder.getInteger());
        menuGroup.setButtonColor(this.btnButtonColor.getBackground());
        menuGroup.setTextColor(this.btnTextColor.getForeground());
        menuGroup.setButtonColorCode(this.btnButtonColor.getBackground().getRGB());
        menuGroup.setTextColorCode(this.btnTextColor.getForeground().getRGB());
        menuGroup.setParent((MenuCategory)this.cbCategory.getSelectedItem());
        menuGroup.setVisible(this.chkVisible.isSelected());
        return true;
    }

    @Override
    public String getDisplayText() {
        MenuGroup foodGroup = (MenuGroup)this.getBean();
        if (foodGroup.getId() == null) {
            return Messages.getString("MenuGroupForm.31");
        }
        return Messages.getString("MenuGroupForm.32");
    }
}

