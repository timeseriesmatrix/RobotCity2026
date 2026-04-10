/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.MenuModifierGroup;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public abstract class BaseMenuModifierGroup
implements Comparable,
Serializable {
    public static String REF = "MenuModifierGroup";
    public static String PROP_NAME = "name";
    public static String PROP_EXCLUSIVE = "exclusive";
    public static String PROP_REQUIRED = "required";
    public static String PROP_ENABLE = "enable";
    public static String PROP_ID = "id";
    public static String PROP_TRANSLATED_NAME = "translatedName";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected String translatedName;
    protected Boolean enable;
    protected Boolean exclusive;
    protected Boolean required;
    private Set<MenuModifier> modifiers;

    public BaseMenuModifierGroup() {
        this.initialize();
    }

    public BaseMenuModifierGroup(Integer id) {
        this.setId(id);
        this.initialize();
    }

    protected void initialize() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
        this.hashCode = Integer.MIN_VALUE;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTranslatedName() {
        return this.translatedName;
    }

    public void setTranslatedName(String translatedName) {
        this.translatedName = translatedName;
    }

    public Boolean isEnable() {
        return this.enable == null ? Boolean.FALSE : this.enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean isExclusive() {
        return this.exclusive == null ? Boolean.FALSE : this.exclusive;
    }

    public void setExclusive(Boolean exclusive) {
        this.exclusive = exclusive;
    }

    public Boolean isRequired() {
        return this.required == null ? Boolean.FALSE : this.required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Set<MenuModifier> getModifiers() {
        return this.modifiers;
    }

    public void setModifiers(Set<MenuModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public void addTomodifiers(MenuModifier menuModifier) {
        if (null == this.getModifiers()) {
            this.setModifiers(new TreeSet<MenuModifier>());
        }
        this.getModifiers().add(menuModifier);
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof MenuModifierGroup)) {
            return false;
        }
        MenuModifierGroup menuModifierGroup = (MenuModifierGroup)obj;
        if (null == this.getId() || null == menuModifierGroup.getId()) {
            return false;
        }
        return this.getId().equals(menuModifierGroup.getId());
    }

    public int hashCode() {
        if (Integer.MIN_VALUE == this.hashCode) {
            if (null == this.getId()) {
                return super.hashCode();
            }
            String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
            this.hashCode = hashStr.hashCode();
        }
        return this.hashCode;
    }

    public int compareTo(Object obj) {
        if (obj.hashCode() > this.hashCode()) {
            return 1;
        }
        if (obj.hashCode() < this.hashCode()) {
            return -1;
        }
        return 0;
    }

    public String toString() {
        return super.toString();
    }
}

