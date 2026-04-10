/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.layout.GroupLayout
 *  org.jdesktop.layout.GroupLayout$Group
 */
package com.floreantpos.ui.model;

import com.floreantpos.POSConstants;
import com.floreantpos.PosRuntimeException;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.dao.MenuModifierGroupDAO;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.jdesktop.layout.GroupLayout;

public class MenuItemModifierGroupForm
extends BeanEditor {
    private JComboBox cbModifierGroups;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JTextField tfMaxQuantity;
    private JTextField tfMinQuantity;

    public MenuItemModifierGroupForm() {
        this(new MenuItemModifierGroup());
    }

    public MenuItemModifierGroupForm(MenuItemModifierGroup modifierGroup) {
        this.initComponents();
        try {
            MenuModifierGroupDAO dao = new MenuModifierGroupDAO();
            List<MenuModifierGroup> groups = dao.findAll();
            this.cbModifierGroups.setModel(new ListComboBoxModel(groups));
        }
        catch (Exception e) {
            throw new PosRuntimeException(POSConstants.ERROR_MESSAGE);
        }
        this.setBean(modifierGroup);
    }

    private void initComponents() {
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.jLabel3 = new JLabel();
        this.cbModifierGroups = new JComboBox();
        this.tfMinQuantity = new JTextField();
        this.tfMaxQuantity = new JTextField();
        this.jLabel1.setText(POSConstants.MODIFIER_GROUP + ":");
        this.jLabel2.setText(POSConstants.MIN_QUANTITY + ":");
        this.jLabel3.setText(POSConstants.MAX_QUANTITY + ":");
        GroupLayout layout = new GroupLayout((Container)this);
        this.setLayout((LayoutManager)layout);
        layout.setHorizontalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel3).add((Component)this.jLabel2).add((Component)this.jLabel1)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.cbModifierGroups, 0, 256, Short.MAX_VALUE).add((GroupLayout.Group)layout.createParallelGroup(2, false).add(1, (Component)this.tfMaxQuantity).add(1, (Component)this.tfMinQuantity, -1, 106, Short.MAX_VALUE))).addContainerGap()));
        layout.setVerticalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel1).add((Component)this.cbModifierGroups, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel2).add((Component)this.tfMinQuantity, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel3).add((Component)this.tfMaxQuantity, -2, -1, -2)).addContainerGap(-1, Short.MAX_VALUE)));
    }

    @Override
    public boolean save() {
        return this.updateModel();
    }

    @Override
    protected void updateView() {
        MenuItemModifierGroup modifierGroup = (MenuItemModifierGroup)this.getBean();
        if (modifierGroup == null) {
            return;
        }
        this.cbModifierGroups.setSelectedItem(modifierGroup.getModifierGroup());
        this.tfMinQuantity.setText(String.valueOf(modifierGroup.getMinQuantity()));
        this.tfMaxQuantity.setText(String.valueOf(modifierGroup.getMaxQuantity()));
    }

    @Override
    protected boolean updateModel() {
        int minQuantity = 0;
        int maxQuantity = 0;
        try {
            minQuantity = Integer.parseInt(this.tfMinQuantity.getText());
            maxQuantity = Integer.parseInt(this.tfMaxQuantity.getText());
        }
        catch (Exception exception) {
            // empty catch block
        }
        MenuModifierGroup group = (MenuModifierGroup)this.cbModifierGroups.getSelectedItem();
        if (group == null) {
            POSMessageDialog.showError(this, POSConstants.MODIFIER_GROUP_REQUIRED);
            return false;
        }
        MenuItemModifierGroup modifierGroup = (MenuItemModifierGroup)this.getBean();
        modifierGroup.setModifierGroup(group);
        modifierGroup.setMinQuantity(minQuantity);
        modifierGroup.setMaxQuantity(maxQuantity);
        return true;
    }

    @Override
    public String getDisplayText() {
        MenuItemModifierGroup modifierGroup = (MenuItemModifierGroup)this.getBean();
        if (modifierGroup.getId() == null) {
            return POSConstants.ADD_NEW_MODIFIER_GROUP_IN_MENU_ITEM_;
        }
        return POSConstants.EDIT_MODIFIER_GROUP_IN_MENU_ITEM_;
    }
}

