/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.MenuItem;
import com.floreantpos.model.Recepie;
import com.floreantpos.model.RecepieItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecepie
implements Comparable,
Serializable {
    public static String REF = "Recepie";
    public static String PROP_MENU_ITEM = "menuItem";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    private MenuItem menuItem;
    private List<RecepieItem> recepieItems;

    public BaseRecepie() {
        this.initialize();
    }

    public BaseRecepie(Integer id) {
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

    public MenuItem getMenuItem() {
        return this.menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public List<RecepieItem> getRecepieItems() {
        return this.recepieItems;
    }

    public void setRecepieItems(List<RecepieItem> recepieItems) {
        this.recepieItems = recepieItems;
    }

    public void addTorecepieItems(RecepieItem recepieItem) {
        if (null == this.getRecepieItems()) {
            this.setRecepieItems(new ArrayList<RecepieItem>());
        }
        this.getRecepieItems().add(recepieItem);
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof Recepie)) {
            return false;
        }
        Recepie recepie = (Recepie)obj;
        if (null == this.getId() || null == recepie.getId()) {
            return false;
        }
        return this.getId().equals(recepie.getId());
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

