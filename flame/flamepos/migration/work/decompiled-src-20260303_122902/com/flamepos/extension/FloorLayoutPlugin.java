/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.floreantpos.extension.AbstractFloreantPlugin
 */
package com.floreantpos.extension;

import com.floreantpos.extension.AbstractFloreantPlugin;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.Ticket;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.tableselection.TableSelector;
import java.awt.Dimension;
import java.util.List;

public abstract class FloorLayoutPlugin
extends AbstractFloreantPlugin {
    public static final Dimension defaultFloorImageSize = new Dimension(400, 400);

    public abstract void initialize();

    public abstract void openTicketsAndTablesDisplay();

    public abstract TableSelector createTableSelector();

    public abstract void updateView();

    public abstract List<ShopTable> captureTableNumbers(Ticket var1);

    public abstract BeanEditor getBeanEditor();
}

