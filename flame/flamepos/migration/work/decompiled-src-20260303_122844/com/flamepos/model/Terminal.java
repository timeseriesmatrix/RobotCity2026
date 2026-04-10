/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseTerminal;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="terminal")
public class Terminal
extends BaseTerminal {
    private static final long serialVersionUID = 1L;

    public Terminal() {
    }

    public Terminal(Integer id) {
        super(id);
    }

    @Override
    public Boolean isHasCashDrawer() {
        return this.hasCashDrawer == null ? Boolean.TRUE : this.hasCashDrawer;
    }

    public boolean isCashDrawerAssigned() {
        return this.getAssignedUser() != null;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}

