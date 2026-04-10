/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.model.OrderType;
import com.floreantpos.swing.CheckBoxList;
import com.floreantpos.swing.ComboBoxModel;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosComboRenderer;
import com.floreantpos.ui.dialog.OkCancelOptionDialog;
import com.floreantpos.util.POSUtil;
import java.awt.Component;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;

public class ComboItemSelectionDialog
extends OkCancelOptionDialog {
    private JComboBox cbItems;
    private CheckBoxList chkItems;
    private JLabel lblItemText;
    private String title;
    private List items;
    private String lblText;
    private Object selectedObject;
    private PosButton btnNew = new PosButton("New");
    private JCheckBox chkSelectAll = new JCheckBox("Select All");
    private JPanel contentPane;
    private boolean newItem;
    private boolean allowMutipleSelection;
    private List selectedItems = new ArrayList();

    public ComboItemSelectionDialog(String title, String lblText, List items, boolean allowMutipleSelection) {
        super((Frame)POSUtil.getBackOfficeWindow(), true);
        this.title = title;
        this.lblText = lblText;
        this.items = items;
        this.allowMutipleSelection = allowMutipleSelection;
        this.setTitle(title);
        this.initComponents();
    }

    public void setFirstItem(String firstItem) {
    }

    public void setVisibleNewButton(boolean b) {
        if (b) {
            this.btnNew.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    ComboItemSelectionDialog.this.newItem = true;
                    ComboItemSelectionDialog.this.setCanceled(false);
                    ComboItemSelectionDialog.this.dispose();
                }
            });
            this.contentPane.add((Component)this.btnNew, "h 40");
        }
    }

    private void initComponents() {
        this.contentPane = new JPanel((LayoutManager)new MigLayout("inset 0, hidemode 3,fillx", "", ""));
        this.setTitle(this.title);
        this.setTitlePaneText(this.title);
        this.lblItemText = new JLabel(this.lblText);
        if (!this.allowMutipleSelection) {
            this.contentPane.add((Component)this.lblItemText, "alignx trailing");
        }
        PosComboRenderer comboRenderer = new PosComboRenderer();
        comboRenderer.setEnableDefaultValueShowing(false);
        this.cbItems = new JComboBox();
        this.chkItems = new CheckBoxList(this.items);
        if (this.allowMutipleSelection) {
            JPanel borderedPanel = new JPanel((LayoutManager)new MigLayout("inset 0,fill"));
            borderedPanel.setBorder(new TitledBorder(this.lblText));
            this.chkSelectAll.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (ComboItemSelectionDialog.this.chkSelectAll.isSelected()) {
                        ComboItemSelectionDialog.this.chkItems.selectAll();
                    } else {
                        ComboItemSelectionDialog.this.chkItems.unCheckAll();
                    }
                }
            });
            borderedPanel.add((Component)this.chkSelectAll, "wrap");
            borderedPanel.add((Component)this.chkItems, "grow");
            this.contentPane.add((Component)borderedPanel, "w 200!,grow,split 2");
        } else {
            this.cbItems.setModel(new ComboBoxModel(this.items));
            this.cbItems.setRenderer(comboRenderer);
            this.contentPane.add((Component)this.cbItems, "w 200!, h 40,split 2");
        }
        this.getContentPanel().add(this.contentPane);
    }

    @Override
    public void doCancel() {
        this.setCanceled(true);
        this.dispose();
    }

    @Override
    public void doOk() {
        this.setCanceled(false);
        if (!this.allowMutipleSelection) {
            this.selectedObject = this.cbItems.getSelectedItem();
        } else {
            this.selectedItems = this.chkItems.getCheckedValues();
        }
        this.dispose();
    }

    public Object getSelectedItem() {
        return this.selectedObject;
    }

    public List getSelectedItems() {
        return this.selectedItems;
    }

    public boolean isNewItem() {
        return this.newItem;
    }

    public void setSelectedItem(Object defaultValue) {
        this.cbItems.setSelectedItem(defaultValue);
    }

    public void setSelectedItems(List<OrderType> defaultValues) {
        this.chkItems.selectItems(defaultValues);
    }
}

