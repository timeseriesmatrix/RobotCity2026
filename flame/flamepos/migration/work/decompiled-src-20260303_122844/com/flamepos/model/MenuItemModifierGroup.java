/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseMenuItemModifierGroup;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="menuItemModifierGroup")
public class MenuItemModifierGroup
extends BaseMenuItemModifierGroup {
    private static final long serialVersionUID = 1L;

    public MenuItemModifierGroup() {
    }

    public MenuItemModifierGroup(Integer id) {
        super(id);
    }

    @Override
    public String toString() {
        if (this.getModifierGroup() != null) {
            return this.getModifierGroup().getName();
        }
        return "";
    }

    public String getUniqueId() {
        return ("menuitem_modifiergroup_" + this.toString() + "_" + this.getId()).replaceAll("\\s+", "_");
    }
}

