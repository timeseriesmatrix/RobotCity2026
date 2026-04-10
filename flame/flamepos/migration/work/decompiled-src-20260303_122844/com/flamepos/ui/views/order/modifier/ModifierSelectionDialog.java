/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views.order.modifier;

import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.order.modifier.ModifierGroupSelectionListener;
import com.floreantpos.ui.views.order.modifier.ModifierGroupView;
import com.floreantpos.ui.views.order.modifier.ModifierSelectionListener;
import com.floreantpos.ui.views.order.modifier.ModifierSelectionModel;
import com.floreantpos.ui.views.order.modifier.ModifierView;
import com.floreantpos.ui.views.order.modifier.TicketItemModifierTableView;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class ModifierSelectionDialog
extends POSDialog
implements ModifierGroupSelectionListener,
ModifierSelectionListener {
    private ModifierSelectionModel modifierSelectionModel;
    private ModifierGroupView modifierGroupView;
    private ModifierView modifierView;
    private TicketItemModifierTableView ticketItemModifierView;
    private JPanel westPanel = new JPanel(new BorderLayout(5, 5));
    private TransparentPanel buttonPanel;
    private PosButton btnSave;
    private PosButton btnCancel;

    public ModifierSelectionDialog(ModifierSelectionModel modifierSelectionModel) {
        this.modifierSelectionModel = modifierSelectionModel;
        this.initComponents();
    }

    private void initComponents() {
        this.setTitle("MODIFIERS");
        this.setLayout(new BorderLayout(10, 10));
        Dimension screenSize = Application.getPosWindow().getSize();
        this.modifierGroupView = new ModifierGroupView(this.modifierSelectionModel);
        this.modifierView = new ModifierView(this.modifierSelectionModel);
        this.ticketItemModifierView = new TicketItemModifierTableView(this.modifierSelectionModel, this);
        this.buttonPanel = new TransparentPanel();
        this.buttonPanel.setLayout((LayoutManager)new MigLayout("fill, ins 4", "fill", ""));
        this.westPanel.add(this.ticketItemModifierView);
        this.add((Component)this.modifierGroupView, "East");
        this.add(this.modifierView);
        this.add((Component)this.westPanel, "West");
        this.createButtonPanel();
        this.setSize(screenSize);
        this.ticketItemModifierView.addModifierSelectionListener(this);
        this.modifierGroupView.addModifierGroupSelectionListener(this);
        this.modifierView.addModifierSelectionListener(this);
        this.modifierGroupView.selectFirst();
    }

    public void createButtonPanel() {
        Dimension preferredButtonSize = new Dimension(100, TerminalConfig.getTouchScreenButtonHeight());
        this.btnSave = new PosButton("DONE");
        this.btnSave.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                ModifierSelectionDialog.this.doFinishModifierSelection();
            }
        });
        this.btnSave.setPreferredSize(preferredButtonSize);
        this.btnCancel = new PosButton(POSConstants.CANCEL.toUpperCase());
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                ModifierSelectionDialog.this.setCanceled(true);
                ModifierSelectionDialog.this.dispose();
            }
        });
        this.btnCancel.setPreferredSize(preferredButtonSize);
        this.buttonPanel.add(this.btnCancel);
        this.buttonPanel.add(this.btnSave);
        this.westPanel.add((Component)this.buttonPanel, "South");
    }

    public ModifierGroupView getModifierGroupView() {
        return this.modifierGroupView;
    }

    public void setModifierGroupView(ModifierGroupView modifierGroupView) {
        this.modifierGroupView = modifierGroupView;
    }

    public ModifierView getModifierView() {
        return this.modifierView;
    }

    public void setModifierView(ModifierView modifierView) {
        this.modifierView = modifierView;
    }

    private void doFinishModifierSelection() {
        List<MenuItemModifierGroup> menuItemModiferGroups = this.modifierSelectionModel.getMenuItem().getMenuItemModiferGroups();
        if (menuItemModiferGroups == null) {
            this.dispose();
            return;
        }
        for (MenuItemModifierGroup menuItemModifierGroup : menuItemModiferGroups) {
            if (ModifierSelectionDialog.isRequiredModifiersAdded(this.modifierSelectionModel.getTicketItem(), menuItemModifierGroup)) continue;
            this.showModifierSelectionMessage(menuItemModifierGroup);
            this.modifierGroupView.setSelectedModifierGroup(menuItemModifierGroup.getModifierGroup());
            return;
        }
        this.setCanceled(false);
        this.dispose();
    }

    @Override
    public void modifierGroupSelected(MenuModifierGroup menuModifierGroup) {
        this.modifierView.setModifierGroup(menuModifierGroup);
    }

    @Override
    public void modifierSelected(MenuModifier modifier, Multiplier multiplier) {
        TicketItem ticketItem = this.modifierSelectionModel.getTicketItem();
        MenuItemModifierGroup menuItemModifierGroup = modifier.getMenuItemModifierGroup();
        int numModifiers = ticketItem.countModifierFromGroup(menuItemModifierGroup);
        int minQuantity = menuItemModifierGroup.getMinQuantity();
        int maxQuantity = menuItemModifierGroup.getMaxQuantity();
        if (maxQuantity < minQuantity) {
            maxQuantity = minQuantity;
        }
        if (numModifiers >= maxQuantity) {
            POSMessageDialog.showError("You have added maximum number of allowed modifiers from group " + modifier.getModifierGroup().getDisplayName());
            return;
        }
        TicketItemModifier ticketItemModifier = ticketItem.findTicketItemModifierFor(modifier, multiplier);
        if (ticketItemModifier == null) {
            OrderType type = ticketItem.getTicket().getOrderType();
            ticketItem.addTicketItemModifier(modifier, 1, type, multiplier);
        } else {
            ticketItemModifier.setItemCount(ticketItemModifier.getItemCount() + 1);
        }
        this.updateView();
        if (numModifiers + 1 == maxQuantity) {
            this.modifierGroupSelectionDone(modifier.getModifierGroup());
        }
    }

    private void updateView() {
        this.modifierSelectionModel.getTicketItem().calculatePrice();
        this.modifierView.updateView();
        this.ticketItemModifierView.updateView();
    }

    @Override
    public void clearModifiers(MenuModifierGroup modifierGroup) {
        List<TicketItemModifier> addOnsList;
        TicketItem ticketItem = this.modifierSelectionModel.getTicketItem();
        List<TicketItemModifier> ticketItemModifiers = ticketItem.getTicketItemModifiers();
        if (ticketItemModifiers != null) {
            Iterator<TicketItemModifier> iterator = ticketItemModifiers.iterator();
            while (iterator.hasNext()) {
                TicketItemModifier ticketItemModifier = iterator.next();
                if (ticketItemModifier.isPrintedToKitchen().booleanValue()) continue;
                iterator.remove();
            }
        }
        if ((addOnsList = ticketItem.getAddOns()) != null) {
            Iterator<TicketItemModifier> iterator = addOnsList.iterator();
            while (iterator.hasNext()) {
                TicketItemModifier addOns = iterator.next();
                if (addOns.isPrintedToKitchen().booleanValue()) continue;
                iterator.remove();
            }
        }
        this.updateView();
    }

    @Override
    public void modifierGroupSelectionDone(MenuModifierGroup modifierGroup) {
        MenuItemModifierGroup menuItemModifierGroup = modifierGroup.getMenuItemModifierGroup();
        if (!ModifierSelectionDialog.isRequiredModifiersAdded(this.modifierSelectionModel.getTicketItem(), menuItemModifierGroup)) {
            this.showModifierSelectionMessage(menuItemModifierGroup);
            this.modifierGroupView.setSelectedModifierGroup(menuItemModifierGroup.getModifierGroup());
            return;
        }
        if (this.modifierGroupView.hasNextMandatoryGroup()) {
            this.modifierGroupView.selectNextGroup();
        }
    }

    public ModifierSelectionModel getModifierSelectionModel() {
        return this.modifierSelectionModel;
    }

    public void setModifierSelectionModel(ModifierSelectionModel modifierSelectionModel) {
        this.modifierSelectionModel = modifierSelectionModel;
    }

    public static boolean isRequiredModifiersAdded(TicketItem ticketItem, MenuItemModifierGroup menuItemModifierGroup) {
        return ticketItem.requiredModifiersAdded(menuItemModifierGroup);
    }

    private void showModifierSelectionMessage(MenuItemModifierGroup menuItemModifierGroup) {
        String displayName = menuItemModifierGroup.getModifierGroup().getDisplayName();
        int minQuantity = menuItemModifierGroup.getMinQuantity();
        POSMessageDialog.showError("You must select at least " + minQuantity + " modifiers from group " + displayName);
    }

    @Override
    public void modifierRemoved(TicketItemModifier modifier) {
        this.updateView();
    }

    @Override
    public void finishModifierSelection() {
        TicketItem ticketItem = this.modifierSelectionModel.getTicketItem();
        List<MenuItemModifierGroup> menuItemModiferGroups = this.modifierSelectionModel.getMenuItem().getMenuItemModiferGroups();
        if (menuItemModiferGroups == null) {
            this.setCanceled(false);
            this.dispose();
            return;
        }
        if (!menuItemModiferGroups.isEmpty()) {
            for (MenuItemModifierGroup ticketItemModifierGroup : menuItemModiferGroups) {
                if (ticketItem.requiredModifiersAdded(ticketItemModifierGroup)) continue;
                this.modifierGroupSelected(ticketItemModifierGroup.getModifierGroup());
                POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), "Please select minimum quantity of each group!");
                return;
            }
        }
        this.setCanceled(false);
        this.dispose();
    }
}

