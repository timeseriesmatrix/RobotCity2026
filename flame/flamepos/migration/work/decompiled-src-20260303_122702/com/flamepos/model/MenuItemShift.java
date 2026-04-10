/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseMenuItemShift;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="menu-item-shift")
public class MenuItemShift
extends BaseMenuItemShift {
    private static final long serialVersionUID = 1L;

    public MenuItemShift() {
    }

    public MenuItemShift(Integer id) {
        super(id);
    }
}

