/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.jidesoft.swing.SimpleScrollPane
 */
package com.floreantpos.ui.views.order.modifier;

import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.ScrollableFlowPanel;
import com.floreantpos.ui.views.order.modifier.ModifierGroupSelectionListener;
import com.floreantpos.ui.views.order.modifier.ModifierSelectionModel;
import com.jidesoft.swing.SimpleScrollPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class ModifierGroupView
extends JPanel
implements ComponentListener {
    private Vector<ModifierGroupSelectionListener> listenerList = new Vector();
    private ModifierSelectionModel modifierSelectionModel;
    private ButtonGroup modifierGroupButtonGroup;
    private SimpleScrollPane simpleScrollPane;
    private ScrollableFlowPanel contentPanel;
    public static final String VIEW_NAME = "MODIFIER_GROUP_VIEW";

    public ModifierGroupView(ModifierSelectionModel modifierSelectionModel) {
        this.modifierSelectionModel = modifierSelectionModel;
        this.setLayout(new BorderLayout());
        TitledBorder border = new TitledBorder(POSConstants.GROUPS);
        border.setTitleJustification(2);
        this.setBorder(border);
        this.contentPanel = new ScrollableFlowPanel();
        this.simpleScrollPane = new SimpleScrollPane((Component)this.contentPanel);
        this.simpleScrollPane.setBorder(null);
        this.simpleScrollPane.setAutoscrolls(false);
        this.simpleScrollPane.setScrollOnRollover(false);
        this.simpleScrollPane.setVerticalUnitIncrement(TerminalConfig.getTouchScreenButtonHeight());
        this.simpleScrollPane.getScrollUpButton().setPreferredSize(new Dimension(100, TerminalConfig.getTouchScreenButtonHeight()));
        this.simpleScrollPane.getScrollDownButton().setPreferredSize(new Dimension(100, TerminalConfig.getTouchScreenButtonHeight()));
        this.add((Component)this.simpleScrollPane);
        this.modifierGroupButtonGroup = new ButtonGroup();
        this.setPreferredSize(new Dimension(120, 100));
        this.init();
        this.addComponentListener(this);
    }

    public void reset() {
        this.modifierGroupButtonGroup = new ButtonGroup();
    }

    private void init() {
        List<MenuItemModifierGroup> modifierGroups = this.modifierSelectionModel.getMenuItem().getMenuItemModiferGroups();
        Collections.sort(modifierGroups, new Comparator<MenuItemModifierGroup>(){

            @Override
            public int compare(MenuItemModifierGroup o1, MenuItemModifierGroup o2) {
                return o2.getMinQuantity() - o1.getMinQuantity();
            }
        });
        for (MenuItemModifierGroup menuItemModifierGroup : modifierGroups) {
            MenuModifierGroup menuModifierGroup = menuItemModifierGroup.getModifierGroup();
            Set<MenuModifier> modifiers = menuModifierGroup.getModifiers();
            if (modifiers == null || modifiers.size() == 0) continue;
            menuModifierGroup.setMenuItemModifierGroup(menuItemModifierGroup);
            this.contentPanel.add(this.createItemButton(menuModifierGroup));
        }
        this.contentPanel.revalidate();
        this.contentPanel.repaint();
    }

    protected AbstractButton createItemButton(Object item) {
        MenuModifierGroup menuModifierGroup = (MenuModifierGroup)item;
        ModifierGroupButton button = new ModifierGroupButton(menuModifierGroup);
        button.setPreferredSize(new Dimension(100, 80));
        this.modifierGroupButtonGroup.add(button);
        return button;
    }

    public void addModifierGroupSelectionListener(ModifierGroupSelectionListener listener) {
        this.listenerList.add(listener);
    }

    public void removeModifierGroupSelectionListener(ModifierGroupSelectionListener listener) {
        this.listenerList.remove(listener);
    }

    private void fireModifierGroupSelected(MenuModifierGroup foodModifierGroup) {
        for (ModifierGroupSelectionListener listener : this.listenerList) {
            listener.modifierGroupSelected(foodModifierGroup);
        }
    }

    public void setSelectedModifierGroup(MenuModifierGroup modifierGroup) {
        Component[] components = this.contentPanel.getContentPane().getComponents();
        if (components != null && components.length > 0) {
            for (Component component : components) {
                ModifierGroupButton button = (ModifierGroupButton)component;
                if (button.menuModifierGroup.getId() != modifierGroup.getId()) continue;
                button.setSelected(true);
                Rectangle bounds = button.getBounds();
                bounds.height *= 2;
                this.simpleScrollPane.scrollRectToVisible(bounds);
                this.fireModifierGroupSelected(button.menuModifierGroup);
                break;
            }
        }
    }

    public void selectFirst() {
        Component[] components = this.contentPanel.getContentPane().getComponents();
        if (components != null && components.length > 0) {
            ModifierGroupButton button = (ModifierGroupButton)components[0];
            button.setSelected(true);
            this.fireModifierGroupSelected(button.menuModifierGroup);
        }
    }

    public void selectNextGroup() {
        ModifierGroupButton button = this.getNextMandatoryGroup();
        button.setSelected(true);
        this.fireModifierGroupSelected(button.menuModifierGroup);
    }

    public boolean hasNextMandatoryGroup() {
        return this.getNextMandatoryGroup() != null;
    }

    private ModifierGroupButton getNextMandatoryGroup() {
        Component[] components = this.contentPanel.getContentPane().getComponents();
        if (components != null && components.length > 0) {
            for (int i = 0; i < components.length; ++i) {
                ModifierGroupButton button = (ModifierGroupButton)components[i];
                if (!button.isSelected() || i >= components.length - 1) continue;
                ModifierGroupButton modifierGroupButton = (ModifierGroupButton)components[i + 1];
                if (modifierGroupButton.menuModifierGroup.getMenuItemModifierGroup().getMinQuantity() <= 0) continue;
                return modifierGroupButton;
            }
        }
        return null;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int verticalUnitIncrement = this.simpleScrollPane.getViewport().getVisibleRect().height - TerminalConfig.getTouchScreenButtonHeight();
        if (verticalUnitIncrement < TerminalConfig.getTouchScreenButtonHeight()) {
            verticalUnitIncrement = TerminalConfig.getTouchScreenButtonHeight();
        }
        this.simpleScrollPane.setVerticalUnitIncrement(verticalUnitIncrement);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    private class ModifierGroupButton
    extends POSToggleButton
    implements ActionListener {
        MenuModifierGroup menuModifierGroup;

        ModifierGroupButton(MenuModifierGroup menuModifierGroup) {
            this.menuModifierGroup = menuModifierGroup;
            this.updateButtonText();
            this.addActionListener(this);
        }

        private void updateButtonText() {
            String string = "";
            string = "<html><body><center> " + this.menuModifierGroup.getDisplayName() + "<br/>" + "<strong><span style='color:white;background-color:orange;margin:0;" + "'>&nbsp; " + this.menuModifierGroup.getMenuItemModifierGroup().getMinQuantity() + "&nbsp; </span></center></body></html>";
            this.setText(string);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (this.isSelected()) {
                ModifierGroupView.this.fireModifierGroupSelected(this.menuModifierGroup);
            }
        }
    }
}

