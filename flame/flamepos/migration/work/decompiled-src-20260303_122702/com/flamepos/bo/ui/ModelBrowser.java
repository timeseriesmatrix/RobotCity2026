/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.bo.ui;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.Command;
import com.floreantpos.bo.ui.CustomCellRenderer;
import com.floreantpos.main.Application;
import com.floreantpos.swing.BeanTableModel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.SearchPanel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;

public class ModelBrowser<E>
extends JPanel
implements ActionListener,
ListSelectionListener {
    protected JXTable browserTable;
    protected BeanEditor<E> beanEditor;
    protected SearchPanel<E> searchPanel;
    protected JPanel buttonPanel;
    protected JPanel browserPanel = new JPanel(new BorderLayout());
    private JPanel beanPanel = new JPanel(new BorderLayout());
    protected JButton btnNew = new JButton(Messages.getString("ModelBrowser.0"));
    protected JButton btnEdit = new JButton(Messages.getString("ModelBrowser.1"));
    protected JButton btnSave = new JButton(Messages.getString("ModelBrowser.2"));
    protected JButton btnDelete = new JButton(Messages.getString("ModelBrowser.3"));
    protected JButton btnCancel = new JButton(Messages.getString("ModelBrowser.4"));

    public ModelBrowser() {
        this((BeanEditor<E>)null);
    }

    public ModelBrowser(BeanEditor<E> beanEditor) {
        this.beanEditor = beanEditor;
    }

    public ModelBrowser(BeanEditor<E> beanEditor, SearchPanel<E> searchPanel) {
        this.beanEditor = beanEditor;
        this.searchPanel = searchPanel;
    }

    public void init(TableModel tableModel) {
        this.browserTable = new JXTable();
        this.browserTable.getSelectionModel().setSelectionMode(0);
        this.browserTable.getSelectionModel().addListSelectionListener(this);
        this.browserTable.setDefaultRenderer(Date.class, (TableCellRenderer)new CustomCellRenderer());
        if (tableModel != null) {
            this.browserTable.setModel(tableModel);
        }
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        if (this.searchPanel != null) {
            this.searchPanel.setModelBrowser(this);
            this.browserPanel.add(this.searchPanel, "North");
        }
        this.browserPanel.add(new JScrollPane((Component)this.browserTable));
        this.add(this.browserPanel);
        this.beanPanel.setBorder(BorderFactory.createEtchedBorder());
        JPanel beanEditorPanel = new JPanel((LayoutManager)new MigLayout());
        beanEditorPanel.add(this.beanEditor);
        this.beanPanel.add(beanEditorPanel);
        this.buttonPanel = new JPanel();
        JButton additionalButton = this.getAdditionalButton();
        if (additionalButton != null) {
            this.buttonPanel.add(additionalButton);
            additionalButton.addActionListener(this);
        }
        this.buttonPanel.add(this.btnNew);
        this.buttonPanel.add(this.btnEdit);
        this.buttonPanel.add(this.btnSave);
        this.buttonPanel.add(this.btnDelete);
        this.buttonPanel.add(this.btnCancel);
        this.beanPanel.setPreferredSize(new Dimension(600, 400));
        this.beanPanel.add((Component)this.buttonPanel, "South");
        this.add((Component)this.beanPanel, "East");
        this.btnNew.addActionListener(this);
        this.btnEdit.addActionListener(this);
        this.btnDelete.addActionListener(this);
        this.btnSave.addActionListener(this);
        this.btnCancel.addActionListener(this);
        this.btnNew.setEnabled(true);
        this.btnEdit.setEnabled(false);
        this.btnSave.setEnabled(false);
        this.btnDelete.setEnabled(false);
        this.btnCancel.setEnabled(false);
        this.beanEditor.clearFields();
        this.beanEditor.setFieldsEnable(false);
        this.refreshTable();
    }

    public void refreshTable() {
    }

    protected JButton getAdditionalButton() {
        return null;
    }

    protected void handleAdditionaButtonActionIfApplicable(ActionEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Command command = Command.fromString(e.getActionCommand());
        try {
            switch (command) {
                case NEW: {
                    this.beanEditor.createNew();
                    this.beanEditor.setFieldsEnable(true);
                    this.btnNew.setEnabled(false);
                    this.btnEdit.setEnabled(false);
                    this.btnSave.setEnabled(true);
                    this.btnDelete.setEnabled(false);
                    this.btnCancel.setEnabled(true);
                    this.browserTable.clearSelection();
                    break;
                }
                case EDIT: {
                    this.beanEditor.edit();
                    this.beanEditor.setFieldsEnable(true);
                    this.btnNew.setEnabled(false);
                    this.btnEdit.setEnabled(false);
                    this.btnSave.setEnabled(true);
                    this.btnDelete.setEnabled(false);
                    this.btnCancel.setEnabled(true);
                    break;
                }
                case CANCEL: {
                    this.doCancelEditing();
                    break;
                }
                case SAVE: {
                    if (!this.beanEditor.save()) break;
                    this.beanEditor.setFieldsEnable(false);
                    this.btnNew.setEnabled(true);
                    this.btnEdit.setEnabled(false);
                    this.btnSave.setEnabled(false);
                    this.btnDelete.setEnabled(false);
                    this.btnCancel.setEnabled(false);
                    this.refreshTable();
                    break;
                }
                case DELETE: {
                    if (!this.beanEditor.delete()) break;
                    this.beanEditor.setBean(null);
                    this.beanEditor.setFieldsEnable(false);
                    this.btnNew.setEnabled(true);
                    this.btnEdit.setEnabled(false);
                    this.btnSave.setEnabled(false);
                    this.btnDelete.setEnabled(false);
                    this.btnCancel.setEnabled(false);
                    this.refreshTable();
                    break;
                }
            }
            this.handleAdditionaButtonActionIfApplicable(e);
        }
        catch (Exception e2) {
            POSMessageDialog.showError(Application.getPosWindow(), e2.getMessage(), e2);
        }
    }

    public void doCancelEditing() {
        if (this.browserTable.getSelectedRow() != -1) {
            this.beanEditor.setFieldsEnable(false);
            this.btnNew.setEnabled(true);
            this.btnEdit.setEnabled(true);
            this.btnSave.setEnabled(false);
            this.btnDelete.setEnabled(true);
            this.btnCancel.setEnabled(false);
            this.beanEditor.cancel();
        } else {
            this.beanEditor.cancel();
            this.beanEditor.clearFields();
            this.beanEditor.setBean(null);
            this.beanEditor.setFieldsEnable(false);
            this.btnNew.setEnabled(true);
            this.btnEdit.setEnabled(false);
            this.btnSave.setEnabled(false);
            this.btnDelete.setEnabled(false);
            this.btnCancel.setEnabled(false);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        BeanTableModel model = (BeanTableModel)this.browserTable.getModel();
        int selectedRow = this.browserTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        if ((selectedRow = this.browserTable.convertRowIndexToModel(selectedRow)) < 0) {
            return;
        }
        Object data = model.getRow(selectedRow);
        this.beanEditor.setBean(data);
        this.btnNew.setEnabled(true);
        this.btnEdit.setEnabled(true);
        this.btnSave.setEnabled(false);
        this.btnDelete.setEnabled(true);
        this.btnCancel.setEnabled(false);
        this.beanEditor.setFieldsEnable(false);
    }

    public void setModels(List<E> models) {
        BeanTableModel tableModel = (BeanTableModel)this.browserTable.getModel();
        tableModel.removeAll();
        tableModel.addRows(models);
    }

    public BeanEditor<E> getBeanEditor() {
        return this.beanEditor;
    }

    public SearchPanel<E> getSearchPanel() {
        return this.searchPanel;
    }

    public void refreshButtonPanel() {
        this.beanEditor.clearFields();
        this.btnNew.setEnabled(true);
        this.btnEdit.setEnabled(false);
        this.btnSave.setEnabled(false);
        this.btnDelete.setEnabled(false);
        this.btnCancel.setEnabled(false);
    }
}

