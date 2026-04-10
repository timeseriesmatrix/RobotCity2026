/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views.order.multipart;

import com.floreantpos.POSConstants;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.dao.MultiplierDAO;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.ScrollableFlowPanel;
import com.floreantpos.ui.views.order.modifier.ModifierGroupSelectionListener;
import com.floreantpos.ui.views.order.modifier.ModifierGroupView;
import com.floreantpos.ui.views.order.modifier.ModifierSelectionListener;
import com.floreantpos.ui.views.order.modifier.ModifierSelectionModel;
import com.floreantpos.ui.views.order.multipart.PizzaModifierSelectionDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;

public class PizzaModifierView
extends JPanel
implements ModifierGroupSelectionListener {
    private ModifierSelectionListener modifierSelectionListener;
    private PosButton btnClear = new PosButton(POSConstants.CLEAR);
    private HashMap<String, ModifierButton> buttonMap = new HashMap();
    private Multiplier selectedMultiplier;
    private MultiplierButton defaultMultiplierButton;
    private ModifierGroupView modifierGroupView;
    private JPanel mainPanel;
    private JPanel contentPanel;
    private PizzaModifierSelectionDialog pizzaModifierSelectionDialog;
    private MenuModifierGroup menuModifierGroup;
    private ScrollableFlowPanel groupPanel;

    public PizzaModifierView(TicketItem ticketItem, MenuItem menuItem, PizzaModifierSelectionDialog pizzaModifierSelectionDialog) {
        ModifierSelectionModel modifierSelectionModel = new ModifierSelectionModel(ticketItem, menuItem);
        this.pizzaModifierSelectionDialog = pizzaModifierSelectionDialog;
        this.setLayout(new BorderLayout());
        this.mainPanel = new JPanel(new BorderLayout());
        this.mainPanel.setBorder(new TitledBorder(null, "MODIFIERS", 2, 2));
        this.contentPanel = new JPanel();
        this.contentPanel.setLayout((LayoutManager)new MigLayout("fillx, aligny top"));
        this.modifierGroupView = new ModifierGroupView(modifierSelectionModel);
        this.add((Component)this.modifierGroupView, "East");
        this.add((Component)this.mainPanel, "Center");
        this.addMultiplierButtons();
        this.modifierGroupView.addModifierGroupSelectionListener(this);
        this.modifierGroupView.selectFirst();
    }

    private void addActionButtons() {
        this.btnClear.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
    }

    private void addMultiplierButtons() {
        JPanel multiplierPanel = new JPanel((LayoutManager)new MigLayout("fillx,center"));
        List<Multiplier> multiplierList = MultiplierDAO.getInstance().findAll();
        ButtonGroup group = new ButtonGroup();
        if (multiplierList != null) {
            for (Multiplier multiplier : multiplierList) {
                MultiplierButton btnMultiplier = new MultiplierButton(multiplier);
                if (multiplier.isDefaultMultiplier().booleanValue()) {
                    this.selectedMultiplier = multiplier;
                    this.defaultMultiplierButton = btnMultiplier;
                    btnMultiplier.setSelected(true);
                }
                multiplierPanel.add((Component)btnMultiplier, "grow");
                group.add(btnMultiplier);
            }
        }
        this.mainPanel.add((Component)multiplierPanel, "South");
    }

    protected AbstractButton createItemButton(Object item) {
        MenuModifier modifier = (MenuModifier)item;
        ModifierButton modifierButton = new ModifierButton(modifier, null, null);
        String key = modifier.getId() + "_" + modifier.getModifierGroup().getId();
        this.buttonMap.put(key, modifierButton);
        return modifierButton;
    }

    public void addModifierSelectionListener(ModifierSelectionListener listener) {
        this.modifierSelectionListener = listener;
    }

    public void removeModifierSelectionListener(ModifierSelectionListener listener) {
        this.modifierSelectionListener = null;
    }

    public void updateView() {
        this.contentPanel.removeAll();
        this.groupPanel = new ScrollableFlowPanel();
        this.groupPanel.setPreferredSize(new Dimension(PosUIManager.getSize(500, 0)));
        JScrollPane js = new JScrollPane(this.groupPanel, 21, 30);
        js.setBorder(null);
        Set<MenuModifier> modifiers = this.menuModifierGroup.getModifiers();
        for (MenuModifier menuModifier : modifiers) {
            if (!menuModifier.isPizzaModifier().booleanValue()) continue;
            menuModifier.setMenuItemModifierGroup(this.menuModifierGroup.getMenuItemModifierGroup());
            this.groupPanel.getContentPane().add(new ModifierButton(menuModifier, this.selectedMultiplier, this.pizzaModifierSelectionDialog.getSelectedSize()));
        }
        this.contentPanel.add((Component)js, "newline,top,center");
        this.mainPanel.add((Component)this.contentPanel, "Center");
        this.contentPanel.repaint();
        this.mainPanel.repaint();
    }

    public void setActionButtonsVisible(boolean b) {
        this.btnClear.setVisible(b);
    }

    @Override
    public void modifierGroupSelected(MenuModifierGroup menuModifierGroup) {
        this.menuModifierGroup = menuModifierGroup;
        this.contentPanel.repaint();
        this.contentPanel.revalidate();
        this.updateView();
    }

    public ModifierGroupView getModifierGroupView() {
        return this.modifierGroupView;
    }

    private class MultiplierButton
    extends POSToggleButton
    implements ActionListener {
        private Multiplier multiplier;

        public MultiplierButton(Multiplier multiplier) {
            Integer textColor;
            this.multiplier = multiplier;
            this.setText(multiplier.getName());
            Integer buttonColor = multiplier.getButtonColor();
            if (buttonColor != null) {
                this.setBackground(new Color(buttonColor));
            }
            if ((textColor = multiplier.getTextColor()) != null) {
                this.setForeground(new Color(textColor));
            }
            this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            this.addActionListener(this);
        }

        public Multiplier getMultiplier() {
            return this.multiplier;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PizzaModifierView.this.selectedMultiplier = this.multiplier;
            this.updateModifierPrice();
        }

        private void updateModifierPrice() {
            PizzaModifierView.this.groupPanel.getContentPane().removeAll();
            Set<MenuModifier> modifiers = PizzaModifierView.this.menuModifierGroup.getModifiers();
            for (MenuModifier menuModifier : modifiers) {
                if (!menuModifier.isPizzaModifier().booleanValue()) continue;
                menuModifier.setMenuItemModifierGroup(PizzaModifierView.this.menuModifierGroup.getMenuItemModifierGroup());
                PizzaModifierView.this.groupPanel.getContentPane().add(new ModifierButton(menuModifier, PizzaModifierView.this.selectedMultiplier, PizzaModifierView.this.pizzaModifierSelectionDialog.getSelectedSize()));
            }
            PizzaModifierView.this.contentPanel.repaint();
            PizzaModifierView.this.mainPanel.repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.isSelected()) {
                this.setBorder(BorderFactory.createLineBorder(new Color(255, 128, 0), 1));
            } else {
                this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }
        }
    }

    private class ModifierButton
    extends PosButton
    implements ActionListener {
        private MenuModifier menuModifier;

        public ModifierButton(MenuModifier modifier, Multiplier multiplier, MenuItemSize menuItemSize) {
            this.menuModifier = modifier;
            this.setText("<html><center>" + modifier.getDisplayName() + "<br/>" + modifier.getPriceForSizeAndMultiplier(menuItemSize, true, multiplier) + "</center></html>");
            if (modifier.getButtonColor() != null) {
                this.setBackground(new Color(modifier.getButtonColor()));
            }
            if (modifier.getTextColor() != null) {
                this.setForeground(new Color(modifier.getTextColor()));
            }
            this.setFocusable(true);
            this.setFocusPainted(true);
            this.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PizzaModifierView.this.modifierSelectionListener.modifierSelected(this.menuModifier, PizzaModifierView.this.selectedMultiplier);
            PizzaModifierView.this.groupPanel.getContentPane().removeAll();
            Set<MenuModifier> modifiers = PizzaModifierView.this.menuModifierGroup.getModifiers();
            for (MenuModifier menuModifier : modifiers) {
                if (!menuModifier.isPizzaModifier().booleanValue()) continue;
                menuModifier.setMenuItemModifierGroup(PizzaModifierView.this.menuModifierGroup.getMenuItemModifierGroup());
                PizzaModifierView.this.groupPanel.getContentPane().add(new ModifierButton(menuModifier, PizzaModifierView.this.selectedMultiplier, PizzaModifierView.this.pizzaModifierSelectionDialog.getSelectedSize()));
            }
            PizzaModifierView.this.contentPanel.repaint();
            PizzaModifierView.this.mainPanel.repaint();
        }
    }
}

