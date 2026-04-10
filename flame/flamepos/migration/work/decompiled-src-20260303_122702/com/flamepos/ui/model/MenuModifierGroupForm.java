/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 */
package com.floreantpos.ui.model;

import com.floreantpos.POSConstants;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.dao.ModifierDAO;
import com.floreantpos.model.dao.ModifierGroupDAO;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;
import org.hibernate.Hibernate;
import org.hibernate.Session;

public class MenuModifierGroupForm
extends BeanEditor {
    private JLabel jLabel1;
    private FixedLengthTextField tfName;
    private FixedLengthTextField tfTranslatedName;

    public MenuModifierGroupForm() throws Exception {
        this(new MenuModifierGroup());
    }

    public MenuModifierGroupForm(MenuModifierGroup group) throws Exception {
        this.initComponents();
        this.setBean(group);
    }

    private void initComponents() {
        this.setLayout((LayoutManager)new MigLayout("", "[45px][369px,grow]", "[19px][]"));
        this.jLabel1 = new JLabel();
        this.tfName = new FixedLengthTextField();
        this.tfName.setLength(60);
        this.jLabel1.setText(POSConstants.NAME);
        this.add((Component)this.jLabel1, "cell 0 0,alignx left,aligny center");
        this.add((Component)this.tfName, "cell 1 0,growx,aligny top");
        JLabel lblTranslatedName = new JLabel(POSConstants.TRANSLATED_NAME);
        this.add((Component)lblTranslatedName, "cell 0 1,alignx trailing");
        this.tfTranslatedName = new FixedLengthTextField();
        this.tfTranslatedName.setLength(60);
        this.add((Component)this.tfTranslatedName, "cell 1 1,growx");
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
            MenuModifierGroup group = (MenuModifierGroup)this.getBean();
            ModifierGroupDAO dao = new ModifierGroupDAO();
            dao.saveOrUpdate(group);
        }
        catch (Exception e) {
            MessageDialog.showError(e);
            return false;
        }
        return true;
    }

    @Override
    protected void updateView() {
        MenuModifierGroup group = (MenuModifierGroup)this.getBean();
        if (group.getId() != null && !Hibernate.isInitialized(group.getModifiers())) {
            ModifierDAO dao = new ModifierDAO();
            Session session = dao.getSession();
            group = (MenuModifierGroup)session.merge((Object)group);
            Hibernate.initialize(group.getModifiers());
            session.close();
        }
        this.tfName.setText(group.getName());
        this.tfTranslatedName.setText(group.getTranslatedName());
    }

    @Override
    protected boolean updateModel() {
        MenuModifierGroup group = (MenuModifierGroup)this.getBean();
        String name = this.tfName.getText();
        if (POSUtil.isBlankOrNull(name)) {
            MessageDialog.showError(POSConstants.NAME_REQUIRED);
            return false;
        }
        group.setName(name);
        group.setTranslatedName(this.tfTranslatedName.getText());
        return true;
    }

    @Override
    public String getDisplayText() {
        MenuModifierGroup modifierGroup = (MenuModifierGroup)this.getBean();
        if (modifierGroup.getId() == null) {
            return POSConstants.NEW_MODIFIER_GROUP;
        }
        return POSConstants.EDIT_MODIFIER_GROUP;
    }
}

