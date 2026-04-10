/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.swingx.combobox.ListComboBoxModel
 */
package com.floreantpos.ui.forms;

import com.floreantpos.Messages;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.SoftReference;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

public class ShopTableSelectionDialog
extends POSDialog {
    private JComboBox<ShopTable> cbTables;
    private SoftReference<ShopTableSelectionDialog> instance;
    private JSeparator jSeparator1;
    private JPanel buttonPanel;

    public ShopTableSelectionDialog() {
        super((Window)POSUtil.getBackOfficeWindow(), Messages.getString("ShopTableSelectionDialog.0"), true);
        this.initModel();
    }

    @Override
    protected void initUI() {
        this.getContentPane().setLayout(new BorderLayout(0, 0));
        GridLayout experimentLayout = new GridLayout(0, 3, 5, 0);
        this.buttonPanel = new JPanel(experimentLayout);
        this.buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.getContentPane().add((Component)this.buttonPanel, "South");
        JButton btnOk = new JButton(Messages.getString("ShopTableSelectionDialog.1"));
        btnOk.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ShopTableSelectionDialog.this.setCanceled(false);
                ShopTableSelectionDialog.this.dispose();
            }
        });
        this.buttonPanel.add(btnOk);
        JButton btnCancel = new JButton(Messages.getString("ShopTableSelectionDialog.2"));
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ShopTableSelectionDialog.this.setCanceled(true);
                ShopTableSelectionDialog.this.dispose();
            }
        });
        this.buttonPanel.add(btnCancel);
        JPanel contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(132, 65));
        this.getContentPane().add((Component)contentPanel, "North");
        JLabel lblSelectTable = new JLabel(Messages.getString("ShopTableSelectionDialog.3"));
        contentPanel.add(lblSelectTable);
        this.cbTables = new JComboBox();
        this.cbTables.setPreferredSize(new Dimension(132, 24));
        contentPanel.add(this.cbTables);
        this.jSeparator1 = new JSeparator();
        this.getContentPane().add((Component)this.jSeparator1, "Center");
    }

    private void initModel() {
        try {
            this.cbTables.setModel((ComboBoxModel<ShopTable>)new ListComboBoxModel(ShopTableDAO.getInstance().getAllUnassigned()));
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, Messages.getString("ShopTableSelectionDialog.4"), e);
        }
    }

    public ShopTable getSelectedTable() {
        if (this.isCanceled()) {
            return null;
        }
        return (ShopTable)this.cbTables.getSelectedItem();
    }

    public ShopTableSelectionDialog getInstance() {
        if (this.instance == null || this.instance.get() == null) {
            this.instance = new SoftReference<ShopTableSelectionDialog>(new ShopTableSelectionDialog());
        }
        return this.instance.get();
    }

    public JPanel getButtonPanel() {
        return this.buttonPanel;
    }

    public void refresh() {
        try {
            this.cbTables.setModel((ComboBoxModel<ShopTable>)new ListComboBoxModel(ShopTableDAO.getInstance().getAllUnassigned()));
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, Messages.getString("ShopTableSelectionDialog.4"), e);
        }
    }
}

