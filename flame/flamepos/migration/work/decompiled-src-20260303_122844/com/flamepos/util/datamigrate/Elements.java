/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.util.datamigrate;

import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.Tax;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="elements")
public class Elements {
    List<Tax> taxes;
    List<MenuCategory> menuCategories;
    List<MenuGroup> menuGroups;
    List<MenuModifier> menuModifiers;
    List<MenuItemModifierGroup> menuItemModifierGroups;
    List<MenuModifierGroup> menuModifierGroups;
    List<MenuItem> menuItems;

    public List<MenuCategory> getMenuCategories() {
        return this.menuCategories;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }

    public List<MenuGroup> getMenuGroups() {
        return this.menuGroups;
    }

    public void setMenuGroups(List<MenuGroup> menuGroups) {
        this.menuGroups = menuGroups;
        if (menuGroups == null) {
            return;
        }
        for (MenuGroup menuGroup : menuGroups) {
            MenuCategory parent = menuGroup.getParent();
            if (parent == null) continue;
            parent.setMenuGroups(null);
        }
    }

    public List<MenuModifier> getMenuModifiers() {
        return this.menuModifiers;
    }

    public void setMenuModifiers(List<MenuModifier> menuModifiers) {
        this.menuModifiers = menuModifiers;
        if (menuModifiers == null) {
            return;
        }
        for (MenuModifier menuModifier : menuModifiers) {
            MenuModifierGroup modifierGroup = menuModifier.getModifierGroup();
            if (modifierGroup == null) continue;
            modifierGroup.setModifiers(null);
        }
    }

    public List<MenuModifierGroup> getMenuModifierGroups() {
        return this.menuModifierGroups;
    }

    public void setMenuModifierGroups(List<MenuModifierGroup> menuModifierGroups) {
        this.menuModifierGroups = menuModifierGroups;
    }

    public List<MenuItem> getMenuItems() {
        return this.menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public List<Tax> getTaxes() {
        return this.taxes;
    }

    public void setTaxes(List<Tax> taxes) {
        this.taxes = taxes;
    }

    public List<MenuItemModifierGroup> getMenuItemModifierGroups() {
        return this.menuItemModifierGroups;
    }

    public void setMenuItemModifierGroups(List<MenuItemModifierGroup> menuItemModifierGroups) {
        this.menuItemModifierGroups = menuItemModifierGroups;
    }
}

