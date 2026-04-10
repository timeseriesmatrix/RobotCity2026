/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.swing.ItemCheckBoxList;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;
import net.miginfocom.swing.MigLayout;

public class ItemSelectionDialog
extends POSDialog
implements ActionListener {
    private TitlePanel titlePanel;
    private PosButton btnCancel;
    private PosButton btnOk;
    private ItemCheckBoxList cbListItems;

    public ItemSelectionDialog() {
        super((Frame)POSUtil.getBackOfficeWindow(), true);
        this.init();
    }

    private void init() {
        this.setLayout(new BorderLayout(10, 10));
        this.setIconImage(Application.getPosWindow().getIconImage());
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        this.titlePanel = new TitlePanel();
        this.titlePanel.setTitle("Select item");
        contentPane.add((Component)this.titlePanel, "North");
        this.cbListItems = new ItemCheckBoxList();
        contentPane.add((Component)new JScrollPane(this.cbListItems), "Center");
        this.btnOk = new PosButton(POSConstants.OK);
        this.btnOk.setFocusable(false);
        this.btnOk.addActionListener(this);
        this.btnCancel = new PosButton(POSConstants.CANCEL);
        this.btnCancel.setFocusable(false);
        this.btnCancel.addActionListener(this);
        JPanel footerPanel = new JPanel((LayoutManager)new MigLayout("fill, ins 2", "sg, fill", ""));
        footerPanel.add(this.btnOk);
        footerPanel.add(this.btnCancel);
        contentPane.add((Component)footerPanel, "South");
        this.add(contentPane);
        this.setSize(500, 400);
    }

    public void setModel(TableModel items) {
        this.cbListItems.setModel(items);
    }

    public TableModel getModel() {
        return this.cbListItems.getModel();
    }

    private void doOk() {
        this.setCanceled(false);
        this.dispose();
    }

    private void doCancel() {
        this.setCanceled(true);
        this.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (POSConstants.CANCEL.equalsIgnoreCase(actionCommand)) {
            this.doCancel();
        } else if (POSConstants.OK.equalsIgnoreCase(actionCommand)) {
            this.doOk();
        }
    }
}

