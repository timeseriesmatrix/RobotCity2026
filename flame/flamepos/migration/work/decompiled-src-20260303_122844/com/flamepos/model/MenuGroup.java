/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.config.TerminalConfig;
import com.floreantpos.model.base.BaseMenuGroup;
import java.awt.Color;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang.StringUtils;

@XmlRootElement(name="menu-group")
public class MenuGroup
extends BaseMenuGroup {
    private static final long serialVersionUID = 1L;
    private Color buttonColor;
    private Color textColor;

    public MenuGroup() {
    }

    public MenuGroup(Integer id) {
        super(id);
    }

    public MenuGroup(Integer id, String name) {
        super(id, name);
    }

    @Override
    public Integer getSortOrder() {
        return this.sortOrder == null ? 9999 : this.sortOrder;
    }

    @XmlTransient
    public Color getButtonColor() {
        if (this.buttonColor != null) {
            return this.buttonColor;
        }
        if (this.getButtonColorCode() == null || this.getButtonColorCode() == 0) {
            return null;
        }
        this.buttonColor = new Color(this.getButtonColorCode());
        return this.buttonColor;
    }

    public void setButtonColor(Color buttonColor) {
        this.buttonColor = buttonColor;
    }

    @XmlTransient
    public Color getTextColor() {
        if (this.textColor != null) {
            return this.textColor;
        }
        if (this.getTextColorCode() == null) {
            return null;
        }
        this.textColor = new Color(this.getTextColorCode());
        return this.textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
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
        return ("menu_group_" + this.getName() + "_" + this.getId()).replaceAll("\\s+", "_");
    }
}

