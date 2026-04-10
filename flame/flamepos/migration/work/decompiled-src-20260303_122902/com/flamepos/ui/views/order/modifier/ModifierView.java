/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views.order.modifier;

import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.model.dao.MultiplierDAO;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.SelectionView;
import com.floreantpos.ui.views.order.modifier.ModifierSelectionDialog;
import com.floreantpos.ui.views.order.modifier.ModifierSelectionListener;
import com.floreantpos.ui.views.order.modifier.ModifierSelectionModel;
import com.floreantpos.util.CurrencyUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.UIManager;
import net.miginfocom.swing.MigLayout;

public class ModifierView
extends SelectionView {
    private Vector<ModifierSelectionListener> listenerList = new Vector();
    private ModifierSelectionModel modifierSelectionModel;
    private MenuModifierGroup modifierGroup;
    private PosButton btnClear = new PosButton(POSConstants.CLEAR);
    private PosButton btnDone = new PosButton(POSConstants.GROUP.toUpperCase() + " " + "DONE");
    private HashMap<String, ModifierButton> buttonMap = new HashMap();
    private int maxQuantity;
    private boolean showPrice;
    private Multiplier selectedMultiplier;
    private MultiplierButton defaultMultiplierButton;

    public ModifierView(ModifierSelectionModel modifierSelectionModel) {
        super(POSConstants.MODIFIERS);
        this.modifierSelectionModel = modifierSelectionModel;
        this.showPrice = OrderView.getInstance().getCurrentTicket().getOrderType().isShowPriceOnButton();
        this.addMultiplierButtons();
        this.addActionButtons();
    }

    private void addMultiplierButtons() {
        JPanel multiplierPanel = new JPanel((LayoutManager)new MigLayout("ins 0,fillx,center"));
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
        this.actionButtonPanel.add((Component)multiplierPanel, "span");
    }

    private void addActionButtons() {
        this.actionButtonPanel.add(this.btnClear);
        this.actionButtonPanel.add(this.btnDone);
        this.btnDone.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                for (ModifierSelectionListener listener : ModifierView.this.listenerList) {
                    listener.finishModifierSelection();
                }
            }
        });
        this.btnClear.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                for (ModifierSelectionListener listener : ModifierView.this.listenerList) {
                    listener.clearModifiers(ModifierView.this.modifierGroup);
                }
            }
        });
    }

    public void setModifierGroup(MenuModifierGroup modifierGroup) {
        this.modifierGroup = modifierGroup;
        this.buttonMap.clear();
        if (modifierGroup == null) {
            return;
        }
        this.renderTitle();
        try {
            ArrayList<MenuModifier> itemList = new ArrayList<MenuModifier>();
            Set<MenuModifier> modifiers = modifierGroup.getModifiers();
            for (MenuModifier modifier : modifiers) {
                modifier.setMenuItemModifierGroup(modifierGroup.getMenuItemModifierGroup());
                itemList.add(modifier);
            }
            this.setItems(itemList);
        }
        catch (PosException e) {
            POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
        }
    }

    @Override
    protected void renderItems() {
        super.renderItems();
        this.updateView();
    }

    private void renderTitle() {
        String displayName = this.modifierGroup.getDisplayName();
        int minQuantity = this.modifierGroup.getMenuItemModifierGroup().getMinQuantity();
        this.maxQuantity = this.modifierGroup.getMenuItemModifierGroup().getMaxQuantity();
        this.setTitle(displayName + ", Min: " + minQuantity + ", Max: " + this.maxQuantity);
    }

    @Override
    protected AbstractButton createItemButton(Object item) {
        MenuModifier modifier = (MenuModifier)item;
        ModifierButton modifierButton = new ModifierButton(modifier);
        String key = modifier.getId() + "_" + modifier.getModifierGroup().getId();
        this.buttonMap.put(key, modifierButton);
        return modifierButton;
    }

    public void addModifierSelectionListener(ModifierSelectionListener listener) {
        this.listenerList.add(listener);
    }

    public void removeModifierSelectionListener(ModifierSelectionListener listener) {
        this.listenerList.remove(listener);
    }

    public void updateView() {
        SelectionView.ButtonPanel activePanel = this.getActivePanel();
        if (activePanel == null) {
            return;
        }
        Component[] components = activePanel.getComponents();
        if (components == null || components.length == 0) {
            return;
        }
        TicketItem ticketItem = this.modifierSelectionModel.getTicketItem();
        int count = 0;
        for (Component component : components) {
            ModifierButton modifierButton = (ModifierButton)component;
            MenuModifier modifier = modifierButton.menuModifier;
            TicketItemModifier ticketItemModifier = ticketItem.findTicketItemModifierFor(modifier);
            if (ticketItemModifier != null) {
                ++count;
                modifierButton.setText("<html><center>" + modifier.getDisplayName() + " <strong><span style='color:white;background-color:green;margin:0;" + "'>&nbsp; " + ticketItemModifier.getItemCount() + "&nbsp; </span></strong><h4>" + (!this.showPrice ? "" : CurrencyUtil.getCurrencySymbol() + (ticketItemModifier.getItemCount() >= this.maxQuantity ? modifier.getExtraPrice() : modifier.getPrice())) + "</h4></center></html>");
                continue;
            }
            modifierButton.setText("<html><center>" + modifier.getDisplayName() + "<br><h4>" + (!this.showPrice ? "" : CurrencyUtil.getCurrencySymbol() + (count >= this.maxQuantity ? modifier.getExtraPrice() : modifier.getPrice())) + "</h4></center></html>");
        }
        if (ModifierSelectionDialog.isRequiredModifiersAdded(ticketItem, this.modifierGroup.getMenuItemModifierGroup())) {
            this.btnDone.setBackground(Color.green);
        } else {
            this.btnDone.setBackground(UIManager.getColor("Control"));
        }
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

        @Override
        public void actionPerformed(ActionEvent e) {
            ModifierView.this.selectedMultiplier = this.multiplier;
        }

        public Multiplier getMultiplier() {
            return this.multiplier;
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

        public ModifierButton(MenuModifier modifier) {
            this.menuModifier = modifier;
            this.setText("<html><center>" + modifier.getDisplayName() + "</center></html>");
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
            for (ModifierSelectionListener listener : ModifierView.this.listenerList) {
                listener.modifierSelected(this.menuModifier, ModifierView.this.selectedMultiplier);
            }
            ModifierView.this.defaultMultiplierButton.setSelected(true);
            ModifierView.this.selectedMultiplier = ModifierView.this.defaultMultiplierButton.getMultiplier();
        }
    }
}

