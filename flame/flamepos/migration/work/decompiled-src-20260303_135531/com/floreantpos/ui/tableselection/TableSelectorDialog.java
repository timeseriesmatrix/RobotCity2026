/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.tableselection;

import com.floreantpos.main.Application;
import com.floreantpos.main.PosWindow;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.Ticket;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.tableselection.TableSelector;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.util.List;

public class TableSelectorDialog
extends POSDialog {
    private final TableSelector tableSelector;

    public TableSelectorDialog(TableSelector tableSelector) throws HeadlessException {
        super((Frame)Application.getPosWindow(), true);
        this.tableSelector = tableSelector;
        this.getContentPane().add(tableSelector);
        PosWindow window = Application.getPosWindow();
        this.setSize(window.getSize());
        this.setLocation(window.getLocation());
    }

    public void setCreateNewTicket(boolean createNewTicket) {
        this.tableSelector.setCreateNewTicket(createNewTicket);
    }

    public void updateView(boolean update) {
        this.tableSelector.updateView(update);
    }

    public List<ShopTable> getSelectedTables() {
        return this.tableSelector.getSelectedTables();
    }

    public void setTicket(Ticket thisTicket) {
        this.tableSelector.setTicket(thisTicket);
    }
}

