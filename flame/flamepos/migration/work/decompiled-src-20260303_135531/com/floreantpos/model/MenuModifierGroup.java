/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.config.TerminalConfig;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.base.BaseMenuModifierGroup;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang.StringUtils;

@XmlRootElement(name="menu-modifier-group")
public class MenuModifierGroup
extends BaseMenuModifierGroup {
    private static final long serialVersionUID = 1L;
    private MenuItemModifierGroup menuItemModifierGroup;

    public MenuModifierGroup() {
    }

    public MenuModifierGroup(Integer id) {
        super(id);
    }

    public String getDisplayName() {
        if (TerminalConfig.isUseTranslatedName() && StringUtils.isNotEmpty((String)this.getTranslatedName())) {
            return this.getTranslatedName();
        }
        return super.getName();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public String getUniqueId() {
        return ("menu_modifiergroup_" + this.getName() + "_" + this.getId()).replaceAll("\\s+", "_");
    }

    public MenuItemModifierGroup getMenuItemModifierGroup() {
        return this.menuItemModifierGroup;
    }

    public void setMenuItemModifierGroup(MenuItemModifierGroup menuItemModifierGroup) {
        this.menuItemModifierGroup = menuItemModifierGroup;
    }
}

