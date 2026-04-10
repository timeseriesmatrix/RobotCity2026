/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.beanutils.PropertyUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.jdesktop.swingx.JXTable
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.bo.ui.CustomCellRenderer;
import com.floreantpos.bo.ui.explorer.ExplorerButtonPanel;
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.dao.MenuModifierGroupDAO;
import com.floreantpos.model.dao.ModifierDAO;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.model.MenuModifierForm;
import com.floreantpos.util.CurrencyUtil;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXTable;

public class ModifierExplorer
extends TransparentPanel {
    private String currencySymbol;
    private JXTable table;
    private ModifierExplorerModel tableModel;

    public ModifierExplorer() {
        this.setLayout(new BorderLayout(5, 5));
        this.currencySymbol = CurrencyUtil.getCurrencySymbol();
        this.tableModel = new ModifierExplorerModel();
        this.table = new JXTable((TableModel)this.tableModel);
        this.table.setSelectionMode(0);
        this.table.setDefaultRenderer(Object.class, (TableCellRenderer)new CustomCellRenderer());
        this.add(new JScrollPane((Component)this.table));
        this.createActionButtons();
        this.add((Component)this.buildSearchForm(), "North");
        this.updateModifierList();
        this.table.addMouseListener((MouseListener)new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    ModifierExplorer.this.doEditSelectedMenuModifier();
                }
            }
        });
    }

    private void createActionButtons() {
        ExplorerButtonPanel explorerButtonPanel = new ExplorerButtonPanel();
        JButton editButton = explorerButtonPanel.getEditButton();
        JButton addButton = explorerButtonPanel.getAddButton();
        JButton deleteButton = explorerButtonPanel.getDeleteButton();
        JButton duplicateButton = new JButton(POSConstants.DUPLICATE);
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MenuModifierForm editor = new MenuModifierForm();
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    MenuModifier modifier = (MenuModifier)editor.getBean();
                    ModifierExplorer.this.tableModel.addModifier(modifier);
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        editButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ModifierExplorer.this.doEditSelectedMenuModifier();
            }
        });
        deleteButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = ModifierExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = ModifierExplorer.this.table.convertRowIndexToModel(index);
                    if (ConfirmDeleteDialog.showMessage(ModifierExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != 1) {
                        MenuModifier category = (MenuModifier)ModifierExplorer.this.tableModel.getRowData(index);
                        ModifierDAO modifierDAO = new ModifierDAO();
                        modifierDAO.delete(category);
                        ModifierExplorer.this.tableModel.deleteModifier(category, index);
                    }
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        duplicateButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int index = ModifierExplorer.this.table.getSelectedRow();
                    if (index < 0) {
                        return;
                    }
                    index = ModifierExplorer.this.table.convertRowIndexToModel(index);
                    MenuModifier existingModifier = (MenuModifier)ModifierExplorer.this.tableModel.getRowData(index);
                    MenuModifier newMenuModifier = new MenuModifier();
                    PropertyUtils.copyProperties((Object)newMenuModifier, (Object)existingModifier);
                    newMenuModifier.setId(null);
                    String newName = ModifierExplorer.this.doDuplicateName(existingModifier);
                    newMenuModifier.setName(newName);
                    newMenuModifier.setModifierGroup(existingModifier.getModifierGroup());
                    newMenuModifier.setSortOrder(existingModifier.getSortOrder());
                    newMenuModifier.setTax(existingModifier.getTax());
                    newMenuModifier.setButtonColor(existingModifier.getButtonColor());
                    newMenuModifier.setTextColor(existingModifier.getTextColor());
                    newMenuModifier.setShouldPrintToKitchen(existingModifier.isShouldPrintToKitchen());
                    MenuModifierForm editor = new MenuModifierForm(newMenuModifier);
                    BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
                    dialog.open();
                    if (dialog.isCanceled()) {
                        return;
                    }
                    MenuModifier menuModifier = (MenuModifier)editor.getBean();
                    ModifierExplorer.this.tableModel.addModifier(menuModifier);
                    ModifierExplorer.this.table.getSelectionModel().addSelectionInterval(ModifierExplorer.this.tableModel.getRowCount() - 1, ModifierExplorer.this.tableModel.getRowCount() - 1);
                    ModifierExplorer.this.table.scrollRowToVisible(ModifierExplorer.this.tableModel.getRowCount() - 1);
                }
                catch (Throwable x) {
                    BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
                }
            }
        });
        TransparentPanel panel = new TransparentPanel();
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(duplicateButton);
        this.add((Component)panel, "South");
    }

    private void doEditSelectedMenuModifier() {
        try {
            int index = this.table.getSelectedRow();
            if (index < 0) {
                return;
            }
            index = this.table.convertRowIndexToModel(index);
            MenuModifier modifier = (MenuModifier)this.tableModel.getRowData(index);
            MenuModifierForm editor = new MenuModifierForm(modifier);
            BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
            dialog.open();
            if (dialog.isCanceled()) {
                return;
            }
            this.table.repaint();
        }
        catch (Throwable x) {
            BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
        }
    }

    private JPanel buildSearchForm() {
        JPanel panel = new JPanel();
        panel.setLayout((LayoutManager)new MigLayout("", "[][]30[][]30[]", "[]20[]"));
        JLabel nameLabel = new JLabel(Messages.getString("ModifierExplorer.3"));
        JLabel groupLabel = new JLabel(Messages.getString("ModifierExplorer.4"));
        final JTextField nameField = new JTextField(15);
        List<MenuModifierGroup> grpName = MenuModifierGroupDAO.getInstance().findAll();
        final JComboBox<Object> cbGroup = new JComboBox<Object>();
        cbGroup.addItem(Messages.getString("ModifierExplorer.5"));
        for (MenuModifierGroup s : grpName) {
            cbGroup.addItem(s);
        }
        JButton searchBttn = new JButton(Messages.getString("ModifierExplorer.6"));
        panel.add((Component)nameLabel, "align label");
        panel.add(nameField);
        panel.add(groupLabel);
        panel.add(cbGroup);
        panel.add(searchBttn);
        Border loweredetched = BorderFactory.createEtchedBorder(1);
        TitledBorder title = BorderFactory.createTitledBorder(loweredetched, Messages.getString("ModifierExplorer.8"));
        title.setTitleJustification(1);
        panel.setBorder(title);
        searchBttn.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String txName = nameField.getText();
                Object selectedItem = cbGroup.getSelectedItem();
                List<MenuModifier> modifierList = selectedItem instanceof MenuModifierGroup ? ModifierDAO.getInstance().findModifier(txName, (MenuModifierGroup)selectedItem) : ModifierDAO.getInstance().findModifier(txName, null);
                ModifierExplorer.this.setModifierList(modifierList);
            }
        });
        return panel;
    }

    public synchronized void updateModifierList() {
        this.setModifierList(ModifierDAO.getInstance().findAll());
    }

    public void setModifierList(List<MenuModifier> modifierList) {
        this.tableModel.setRows(modifierList);
    }

    private String doDuplicateName(MenuModifier existingModifier) {
        String existingName = existingModifier.getName();
        String newName = new String();
        int lastIndexOf = existingName.lastIndexOf(" ");
        if (lastIndexOf == -1) {
            newName = existingName + " 1";
        } else {
            String processName = existingName.substring(lastIndexOf + 1, existingName.length());
            if (StringUtils.isNumeric((CharSequence)processName)) {
                Integer count = Integer.valueOf(processName);
                count = count + 1;
                newName = existingName.replace(processName, String.valueOf(count));
            } else {
                newName = existingName + " 1";
            }
        }
        return newName;
    }

    private class ModifierExplorerModel
    extends ListTableModel {
        public ModifierExplorerModel() {
            super(new String[]{POSConstants.ID, POSConstants.NAME, POSConstants.TRANSLATED_NAME, POSConstants.PRICE + " (" + ModifierExplorer.this.currencySymbol + ")", POSConstants.EXTRA_PRICE, POSConstants.TAX + "(%)", POSConstants.MODIFIER_GROUP, POSConstants.BUTTON_COLOR, POSConstants.SORT_ORDER});
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            List modifierList = this.getRows();
            MenuModifier modifier = (MenuModifier)modifierList.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return String.valueOf(modifier.getId());
                }
                case 1: {
                    return modifier.getName();
                }
                case 2: {
                    return modifier.getTranslatedName();
                }
                case 3: {
                    return (double)modifier.getPrice();
                }
                case 4: {
                    return (double)modifier.getExtraPrice();
                }
                case 5: {
                    if (modifier.getTax() == null) {
                        return "";
                    }
                    return (double)modifier.getTax().getRate();
                }
                case 6: {
                    if (modifier.getModifierGroup() == null) {
                        return "";
                    }
                    return modifier.getModifierGroup().getName();
                }
                case 7: {
                    if (modifier.getButtonColor() != null) {
                        return new Color(modifier.getButtonColor());
                    }
                    return null;
                }
                case 8: {
                    return modifier.getSortOrder();
                }
            }
            return null;
        }

        public void addModifier(MenuModifier category) {
            int size = this.getRows().size();
            this.getRows().add(category);
            this.fireTableRowsInserted(size, size);
        }

        public void deleteModifier(MenuModifier category, int index) {
            this.getRows().remove(category);
            this.fireTableRowsDeleted(index, index);
        }
    }
}

