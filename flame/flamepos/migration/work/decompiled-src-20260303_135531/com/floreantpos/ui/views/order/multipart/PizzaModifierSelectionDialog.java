/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jdesktop.swingx.JXCollapsiblePane
 */
package com.floreantpos.ui.views.order.multipart;

import com.floreantpos.IconFactory;
import com.floreantpos.POSConstants;
import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PizzaCrust;
import com.floreantpos.model.PizzaPrice;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.order.OrderView;
import com.floreantpos.ui.views.order.modifier.ModifierSelectionListener;
import com.floreantpos.ui.views.order.multipart.PizzaModifierView;
import com.floreantpos.ui.views.order.multipart.PizzaTicketItemTableModel;
import com.floreantpos.util.CurrencyUtil;
import com.floreantpos.util.NumberUtil;
import com.floreantpos.util.POSUtil;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.table.TableColumnModel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXCollapsiblePane;

public class PizzaModifierSelectionDialog
extends POSDialog
implements ModifierSelectionListener,
ActionListener {
    private static final String PROP_PIZZA_PRICE = "pizzaPrice";
    private SizeAndCrustSelectionPane sizeAndCrustPanel;
    private PizzaModifierView modifierView;
    private List<Section> sectionList;
    private boolean crustSelected = false;
    private Section sectionQuarter1;
    private Section sectionQuarter2;
    private Section sectionQuarter3;
    private Section sectionQuarter4;
    private Section sectionHalf1;
    private Section sectionHalf2;
    private Section sectionWhole;
    private TicketItemModifier crustModifier;
    private CardLayout sectionLayout = new CardLayout();
    JPanel sectionView = new Pizza(this.sectionLayout);
    private JPanel fullSectionLayout = new TransparentPanel(new GridLayout(1, 1, 2, 2));
    private JPanel halfSectionLayout = new TransparentPanel(new GridLayout(1, 2, 2, 2));
    private JPanel quarterSectionLayout = new TransparentPanel(new GridLayout(2, 2, 2, 2));
    private JTable table;
    private MenuItemSize previousMenuItemSize;
    private MenuItemSize itemSize;
    private TicketItem ticketItem;
    private JPanel wholeSectionView;
    private final MenuItem menuItem;
    private PizzaTicketItemTableModel ticketItemViewerModel;
    private boolean editMode;
    private PosButton btnCustomQuantity;
    private int pizzaQuantity;
    private POSToggleButton btnFull;
    private POSToggleButton btnHalf;
    private JToggleButton btnQuarter;
    private ButtonGroup btnGroup;
    private JToggleButton currentButton;

    public PizzaModifierSelectionDialog(TicketItem cloneTicketItem, MenuItem menuItem, boolean editMode) {
        this.menuItem = menuItem;
        this.ticketItem = cloneTicketItem;
        this.editMode = editMode;
        this.resetPizzaQuantityAndPrice();
        this.initComponents();
        this.updateView();
    }

    private void initComponents() {
        this.setTitle("MODIFY PIZZA");
        this.setLayout(new BorderLayout(10, 10));
        JPanel panel = (JPanel)this.getContentPane();
        panel.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.add((Component)this.createSectionPanel(), "Center");
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel ticketItemTableViewPanel = new JPanel(new BorderLayout());
        ticketItemTableViewPanel.setPreferredSize(PosUIManager.getSize(0, 200));
        this.table = new JTable();
        this.ticketItemViewerModel = new PizzaTicketItemTableModel();
        this.table.setModel(this.ticketItemViewerModel);
        this.table.setRowHeight(30);
        this.table.setDefaultRenderer(Object.class, new PosTableRenderer());
        this.table.setAutoResizeMode(3);
        TableColumnModel columnModel = this.table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(200);
        columnModel.getColumn(1).setPreferredWidth(50);
        JScrollPane scrollPane = new JScrollPane(this.table);
        ticketItemTableViewPanel.add(scrollPane);
        int size = PosUIManager.getSize(40);
        JXCollapsiblePane pizzaItemActionPanel = new JXCollapsiblePane();
        pizzaItemActionPanel.setBackground(Color.WHITE);
        pizzaItemActionPanel.setLayout((LayoutManager)new MigLayout("fillx,ins 2 0 2 0", "[" + size + "px][grow][" + size + "px]", "[" + size + "]"));
        pizzaItemActionPanel.setAnimated(true);
        pizzaItemActionPanel.setCollapsed(false);
        pizzaItemActionPanel.setVisible(true);
        PosButton btnIncreaseQuantity = new PosButton();
        btnIncreaseQuantity.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PizzaModifierSelectionDialog.this.pizzaQuantity++;
                PizzaModifierSelectionDialog.this.btnCustomQuantity.setText(String.valueOf(PizzaModifierSelectionDialog.this.pizzaQuantity));
            }
        });
        this.btnCustomQuantity = new PosButton(String.valueOf(this.pizzaQuantity));
        this.btnCustomQuantity.setForeground(Color.BLUE);
        this.btnCustomQuantity.setBackground(Color.WHITE);
        Border lineBorder = BorderFactory.createLineBorder(new Color(0.0f, 0.0f, 0.0f, 0.1f), 1);
        this.btnCustomQuantity.setBorder(lineBorder);
        this.btnCustomQuantity.setFont(new Font(this.btnCustomQuantity.getFont().getName(), 1, 20));
        this.btnCustomQuantity.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                NumberSelectionDialog2 dialog = new NumberSelectionDialog2();
                dialog.setTitle("Enter quantity");
                dialog.setFloatingPoint(false);
                dialog.pack();
                dialog.open();
                if (dialog.isCanceled()) {
                    return;
                }
                PizzaModifierSelectionDialog.this.pizzaQuantity = (int)dialog.getValue();
                PizzaModifierSelectionDialog.this.btnCustomQuantity.setText(String.valueOf(PizzaModifierSelectionDialog.this.pizzaQuantity));
            }
        });
        PosButton btnDecreaseQuantity = new PosButton();
        btnDecreaseQuantity.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (PizzaModifierSelectionDialog.this.pizzaQuantity == 1) {
                    return;
                }
                PizzaModifierSelectionDialog.this.pizzaQuantity--;
                PizzaModifierSelectionDialog.this.btnCustomQuantity.setText(String.valueOf(PizzaModifierSelectionDialog.this.pizzaQuantity));
            }
        });
        btnIncreaseQuantity.setIcon(IconFactory.getIcon("/ui_icons/", "plus_32.png"));
        btnDecreaseQuantity.setIcon(IconFactory.getIcon("/ui_icons/", "minus_32.png"));
        pizzaItemActionPanel.add((Component)btnDecreaseQuantity);
        pizzaItemActionPanel.add((Component)this.btnCustomQuantity, (Object)"grow");
        pizzaItemActionPanel.add((Component)btnIncreaseQuantity);
        ticketItemTableViewPanel.add((Component)pizzaItemActionPanel, "South");
        westPanel.add((Component)ticketItemTableViewPanel, "North");
        this.sizeAndCrustPanel = new SizeAndCrustSelectionPane();
        centerPanel.add((Component)this.sizeAndCrustPanel, "North");
        this.modifierView = new PizzaModifierView(this.ticketItem, this.menuItem, this);
        this.modifierView.addModifierSelectionListener(this);
        PosScrollPane modifierSc = new PosScrollPane(this.modifierView);
        modifierSc.setBorder(null);
        centerPanel.add((Component)modifierSc, "Center");
        this.add((Component)centerPanel, "Center");
        this.add((Component)westPanel, "West");
        this.createButtonPanel();
    }

    private void updateView() {
        if (this.ticketItem.getSizeModifier() == null) {
            if (this.crustModifier != null) {
                this.ticketItem.setSizeModifier(this.crustModifier);
                this.ticketItem.getSizeModifier().calculatePrice();
            }
        } else {
            this.ticketItem.getSizeModifier().calculatePrice();
        }
        this.ticketItemViewerModel.setTicketItem(this.ticketItem);
        this.ticketItemViewerModel.updateView();
        List<TicketItemModifier> ticketItemModifiers = this.ticketItem.getTicketItemModifiers();
        if (ticketItemModifiers == null) {
            return;
        }
        for (Section section : this.sectionList) {
            for (TicketItemModifier ticketItemModifier : ticketItemModifiers) {
                if (ticketItemModifier.isInfoOnly().booleanValue() || !section.getSectionName().equals(ticketItemModifier.getSectionName())) continue;
                section.sectionModifierTableModel.addItem(ticketItemModifier);
            }
        }
        if (this.ticketItem.getPizzaSectionMode() != null) {
            if (this.ticketItem.getPizzaSectionMode() == TicketItem.PIZZA_SECTION_MODE.FULL) {
                this.btnFull.setSelected(true);
                this.currentButton = this.btnFull;
                this.doFullSectionMode();
                if (this.ticketItem.isPrintedToKitchen().booleanValue()) {
                    this.btnHalf.setEnabled(false);
                    this.btnQuarter.setEnabled(false);
                }
            } else if (this.ticketItem.getPizzaSectionMode() == TicketItem.PIZZA_SECTION_MODE.HALF) {
                this.btnHalf.setSelected(true);
                this.currentButton = this.btnHalf;
                this.doHalfSectionMode();
                if (this.ticketItem.isPrintedToKitchen().booleanValue()) {
                    this.btnFull.setEnabled(false);
                    this.btnQuarter.setEnabled(false);
                }
            } else if (this.ticketItem.getPizzaSectionMode() == TicketItem.PIZZA_SECTION_MODE.QUARTER) {
                this.btnQuarter.setSelected(true);
                this.currentButton = this.btnQuarter;
                this.doQuarterSectionMode();
                if (this.ticketItem.isPrintedToKitchen().booleanValue()) {
                    this.btnHalf.setEnabled(false);
                    this.btnFull.setEnabled(false);
                }
            }
        }
    }

    private JPanel createSectionPanel() {
        JPanel westPanel = new JPanel(new BorderLayout(5, 5));
        this.sectionList = new ArrayList<Section>();
        this.sectionWhole = new Section("WHOLE", "WHOLE", 0, true, 1.0);
        this.sectionQuarter1 = new Section("Quarter 1", "Quarter 1", 1, false, 0.25);
        this.sectionQuarter2 = new Section("Quarter 2", "Quarter 2", 2, false, 0.25);
        this.sectionQuarter3 = new Section("Quarter 3", "Quarter 3", 3, false, 0.25);
        this.sectionQuarter4 = new Section("Quarter 4", "Quarter 4", 4, false, 0.25);
        this.sectionHalf1 = new Section("Half 1", "Half 1", 5, false, 0.5);
        this.sectionHalf2 = new Section("Half 2", "Half 2", 6, false, 0.5);
        this.sectionList.add(this.sectionWhole);
        this.sectionList.add(this.sectionQuarter1);
        this.sectionList.add(this.sectionQuarter2);
        this.sectionList.add(this.sectionQuarter3);
        this.sectionList.add(this.sectionQuarter4);
        this.sectionList.add(this.sectionHalf1);
        this.sectionList.add(this.sectionHalf2);
        this.fullSectionLayout.add(this.sectionWhole);
        this.halfSectionLayout.add(this.sectionHalf1);
        this.halfSectionLayout.add(this.sectionHalf2);
        this.quarterSectionLayout.add(this.sectionQuarter1);
        this.quarterSectionLayout.add(this.sectionQuarter2);
        this.quarterSectionLayout.add(this.sectionQuarter3);
        this.quarterSectionLayout.add(this.sectionQuarter4);
        this.sectionView.add((Component)this.fullSectionLayout, "full");
        this.sectionView.add((Component)this.halfSectionLayout, "half");
        this.sectionView.add((Component)this.quarterSectionLayout, "quarter");
        this.sectionLayout.show(this.sectionView, "full");
        this.wholeSectionView = new JPanel((LayoutManager)new MigLayout("fill,ins 0 0 0 0"));
        this.sectionView.setOpaque(false);
        westPanel.setOpaque(false);
        westPanel.add((Component)this.sectionView, "Center");
        westPanel.add((Component)this.wholeSectionView, "South");
        return westPanel;
    }

    public void createButtonPanel() {
        TransparentPanel buttonPanel = new TransparentPanel();
        buttonPanel.setLayout((LayoutManager)new MigLayout("fill, ins 2", "", ""));
        this.btnGroup = new ButtonGroup();
        this.btnFull = new POSToggleButton("FULL");
        this.btnFull.setSelected(true);
        this.currentButton = this.btnFull;
        this.btnFull.addActionListener(this);
        this.btnHalf = new POSToggleButton("HALF");
        this.btnHalf.addActionListener(this);
        this.btnQuarter = new POSToggleButton("QUARTER");
        this.btnQuarter.addActionListener(this);
        this.btnGroup.add(this.btnFull);
        this.btnGroup.add(this.btnHalf);
        this.btnGroup.add(this.btnQuarter);
        PosButton btnClear = new PosButton("CLEAR");
        btnClear.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                Section section = PizzaModifierSelectionDialog.this.getSelectedSection();
                if (section == null) {
                    return;
                }
                section.clearSelectedItem();
            }
        });
        PosButton btnClearAll = new PosButton("CLEAR ALL");
        btnClearAll.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                Section section = PizzaModifierSelectionDialog.this.getSelectedSection();
                if (section == null) {
                    return;
                }
                section.clearItems();
            }
        });
        PosButton btnSave = new PosButton("DONE");
        btnSave.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (PizzaModifierSelectionDialog.this.doFinishModifierSelection()) {
                    PizzaModifierSelectionDialog.this.setCanceled(false);
                    PizzaModifierSelectionDialog.this.dispose();
                }
            }
        });
        PosButton btnCancel = new PosButton(POSConstants.CANCEL.toUpperCase());
        btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                PizzaModifierSelectionDialog.this.setCanceled(true);
                PizzaModifierSelectionDialog.this.dispose();
            }
        });
        int width = PosUIManager.getSize(80);
        JSeparator separator = new JSeparator(1);
        buttonPanel.add((Component)this.btnFull, "w " + width + "!, split 5");
        buttonPanel.add((Component)this.btnHalf, "w " + width + "!");
        buttonPanel.add((Component)this.btnQuarter, "w " + width + "!");
        buttonPanel.add((Component)separator, "growy");
        buttonPanel.add((Component)btnClear, "grow");
        buttonPanel.add((Component)btnClearAll, "grow");
        buttonPanel.add((Component)btnCancel, "grow");
        buttonPanel.add((Component)btnSave, "grow");
        this.add((Component)buttonPanel, "South");
    }

    private boolean doFinishModifierSelection() {
        if (!this.crustSelected) {
            POSMessageDialog.showError("Please select size and crust.");
            return false;
        }
        List<MenuItemModifierGroup> menuItemModiferGroups = this.ticketItem.getMenuItem().getMenuItemModiferGroups();
        for (MenuItemModifierGroup menuItemModifierGroup : menuItemModiferGroups) {
            if (this.ticketItem.requiredModifiersAdded(menuItemModifierGroup)) continue;
            POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), String.format("Required modifiers for group %s not added!", menuItemModifierGroup.getModifierGroup().getDisplayName()));
            return false;
        }
        this.updatePizzaQuantityAndPrice();
        if (!this.editMode) {
            OrderView.getInstance().getTicketView().addTicketItem(this.ticketItem);
            int showYesNoQuestionDialog = POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(), "Do you want to create more pizza?", "More Pizza");
            if (showYesNoQuestionDialog == 0) {
                TicketItem newTicketItem = this.ticketItem.clone(this.ticketItem);
                newTicketItem.setId(null);
                this.ticketItem = newTicketItem;
                this.reset();
                return false;
            }
        }
        return true;
    }

    private void updatePizzaQuantityAndPrice() {
        this.ticketItem.setItemCount(this.pizzaQuantity);
        List<TicketItemModifier> ticketItemModifiers = this.ticketItem.getTicketItemModifiers();
        if (ticketItemModifiers != null) {
            for (TicketItemModifier ticketItemModifier : ticketItemModifiers) {
                if (ticketItemModifier.isInfoOnly().booleanValue()) continue;
                ticketItemModifier.setItemCount(ticketItemModifier.getItemCount() * this.pizzaQuantity);
            }
        }
        this.ticketItem.calculatePrice();
    }

    private void resetPizzaQuantityAndPrice() {
        this.pizzaQuantity = this.ticketItem.getItemCount();
        this.ticketItem.setItemCount(1);
        List<TicketItemModifier> ticketItemModifiers = this.ticketItem.getTicketItemModifiers();
        if (ticketItemModifiers != null) {
            for (TicketItemModifier ticketItemModifier : ticketItemModifiers) {
                if (ticketItemModifier.isInfoOnly().booleanValue()) continue;
                ticketItemModifier.setItemCount(ticketItemModifier.getItemCount() / this.pizzaQuantity);
            }
        }
        this.ticketItem.calculatePrice();
    }

    private void reset() {
        for (Section section : this.sectionList) {
            if (section.sectionModifierTableModel.getRows() == null) continue;
            Iterator iterator2 = section.sectionModifierTableModel.getRows().iterator();
            while (iterator2.hasNext()) {
                TicketItemModifier ticketItemModifier = (TicketItemModifier)iterator2.next();
                if (ticketItemModifier == null) continue;
                iterator2.remove();
            }
            section.sectionModifierTableModel.fireTableDataChanged();
            section.repaint();
        }
        this.modifierView.getModifierGroupView().selectFirst();
        this.ticketItem.setSizeModifier(this.getSizeAndCrustModifer());
        this.ticketItem.getSizeModifier().calculatePrice();
        if (this.ticketItem.getTicketItemModifiers() != null) {
            this.ticketItem.getTicketItemModifiers().clear();
        }
        this.ticketItemViewerModel.setTicketItem(this.ticketItem);
        this.ticketItemViewerModel.updateView();
    }

    private boolean isMaxModifierAddedFromGroup(MenuItemModifierGroup menuItemModifierGroup, int currentModifierCount) {
        int minQuantity = menuItemModifierGroup.getMinQuantity();
        int maxQuantity = menuItemModifierGroup.getMaxQuantity();
        if (maxQuantity < minQuantity) {
            maxQuantity = minQuantity;
        }
        return currentModifierCount >= maxQuantity;
    }

    @Override
    public void modifierSelected(MenuModifier modifier, Multiplier multiplier) {
        int questionDialog;
        int countModifier;
        MenuItemModifierGroup menuItemModifierGroup = modifier.getMenuItemModifierGroup();
        if (this.isMaxModifierAddedFromGroup(menuItemModifierGroup, countModifier = this.ticketItem.countModifierFromGroup(menuItemModifierGroup))) {
            POSMessageDialog.showError("You have added maximum number of allowed modifiers from group " + modifier.getModifierGroup().getDisplayName());
            this.modifierGroupSelectionDone(menuItemModifierGroup.getModifierGroup());
            return;
        }
        Section selectedSection = this.getSelectedSection();
        boolean itemSizeSame = false;
        this.itemSize = this.sizeAndCrustPanel.getMenuItemSize();
        if (this.itemSize != null) {
            if (this.previousMenuItemSize != null) {
                if (this.previousMenuItemSize == this.itemSize) {
                    itemSizeSame = true;
                }
            } else {
                this.previousMenuItemSize = this.itemSize;
                itemSizeSame = true;
            }
        }
        TicketItemModifier findTicketItemModifierForWholeSection = this.ticketItem.findTicketItemModifierFor(modifier, this.getMainSection().getSectionName(), null);
        TicketItemModifier ticketItemModifier = this.ticketItem.findTicketItemModifierFor(modifier, selectedSection.getSectionName(), null);
        if (!selectedSection.mainSection) {
            int countSection = 0;
            for (Section sec : this.sectionList) {
                int questionDialog2;
                TicketItemModifier ticItemModifier;
                if (sec.getSectionName().startsWith("Half")) {
                    if (sec == selectedSection || (ticItemModifier = this.ticketItem.findTicketItemModifierFor(modifier, sec.getSectionName(), null)) == null || ticketItemModifier != null) continue;
                    if (findTicketItemModifierForWholeSection != null) {
                        POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), "Item already added in WHOLE section!");
                        return;
                    }
                    questionDialog2 = POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(), "Would you like to add this item in WHOLE section?", "Add Modifier");
                    if (questionDialog2 != 0) break;
                    sec.clearItem(ticItemModifier, sec.sectionModifierTableModel);
                    selectedSection = this.getMainSection();
                    continue;
                }
                if (!sec.getSectionName().startsWith("Quarter") || sec == selectedSection || (ticItemModifier = this.ticketItem.findTicketItemModifierFor(modifier, sec.getSectionName(), null)) == null || ticketItemModifier != null) continue;
                if (countSection == 2) {
                    if (findTicketItemModifierForWholeSection != null) {
                        POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), "Item already added in WHOLE section!");
                        return;
                    }
                    questionDialog2 = POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(), "Would you like to add this item in WHOLE section?", "Add Modifier");
                    if (questionDialog2 == 0) {
                        for (Section sectionClear : this.sectionList) {
                            if (sectionClear.getSectionName().startsWith("Quarter")) {
                                sectionClear.clearItem(ticItemModifier, sectionClear.sectionModifierTableModel);
                            }
                            sectionClear.repaint();
                        }
                        selectedSection = this.getMainSection();
                    }
                }
                ++countSection;
            }
        }
        if (findTicketItemModifierForWholeSection != null && !selectedSection.mainSection && ticketItemModifier == null && (questionDialog = POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(), "Item already added in pizza, Would you like to add again?", "Add Modifier")) == 1) {
            return;
        }
        if (ticketItemModifier == null || ticketItemModifier.isPrintedToKitchen().booleanValue() || !itemSizeSame) {
            OrderType orderType = this.ticketItem.getTicket().getOrderType();
            ticketItemModifier = this.convertToTicketItemModifier(this.ticketItem, modifier, orderType, multiplier);
            ticketItemModifier.setSectionName(selectedSection.getSectionName());
            if (ticketItemModifier.isShouldSectionWisePrice().booleanValue()) {
                ticketItemModifier.setUnitPrice(selectedSection.calculatePrice(ticketItemModifier.getUnitPrice()));
            }
            selectedSection.addItem(ticketItemModifier);
            TicketItemModifier separator = this.getSeparatorIfNeeded(selectedSection.getSectionName());
            if (separator != null) {
                this.ticketItem.addToticketItemModifiers(separator);
            }
            this.ticketItem.addToticketItemModifiers(ticketItemModifier);
            if (!ticketItemModifier.isInfoOnly().booleanValue()) {
                double defaultSellPortion = this.menuItem.getDefaultSellPortion().intValue();
                this.ticketItem.updateModifiersUnitPrice(defaultSellPortion);
            }
        } else {
            POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), "Item already added!");
            return;
        }
        if (countModifier + 1 >= menuItemModifierGroup.getMinQuantity()) {
            this.modifierGroupSelectionDone(menuItemModifierGroup.getModifierGroup());
        }
        this.ticketItemViewerModel.updateView();
    }

    private TicketItemModifier getSeparatorIfNeeded(String sectionName) {
        TicketItemModifier lastItem;
        List<TicketItemModifier> ticketItemModifiers = this.ticketItem.getTicketItemModifiers();
        if (this.ticketItemViewerModel.getRowCount() == 1 && sectionName.equals("WHOLE")) {
            return null;
        }
        if (ticketItemModifiers != null && !ticketItemModifiers.isEmpty() && sectionName.equals((lastItem = ticketItemModifiers.get(ticketItemModifiers.size() - 1)).getSectionName())) {
            return null;
        }
        TicketItemModifier ticketItemModifier = new TicketItemModifier();
        ticketItemModifier.setName("== " + sectionName + " ==");
        ticketItemModifier.setModifierType(6);
        ticketItemModifier.setInfoOnly(true);
        ticketItemModifier.setSectionName(sectionName);
        ticketItemModifier.setTicketItem(this.ticketItem);
        return ticketItemModifier;
    }

    @Override
    public void clearModifiers(MenuModifierGroup modifierGroup) {
    }

    @Override
    public void modifierGroupSelectionDone(MenuModifierGroup modifierGroup) {
        MenuItemModifierGroup menuItemModifierGroup = modifierGroup.getMenuItemModifierGroup();
        if (!PizzaModifierSelectionDialog.isRequiredModifiersAdded(this.ticketItem, menuItemModifierGroup)) {
            this.showModifierSelectionMessage(menuItemModifierGroup);
            this.modifierView.getModifierGroupView().setSelectedModifierGroup(menuItemModifierGroup.getModifierGroup());
            return;
        }
        if (this.modifierView.getModifierGroupView().hasNextMandatoryGroup()) {
            this.modifierView.getModifierGroupView().selectNextGroup();
        }
    }

    public static boolean isRequiredModifiersAdded(TicketItem ticketItem, MenuItemModifierGroup menuItemModifierGroup) {
        return true;
    }

    private TicketItemModifier convertToTicketItemModifier(TicketItem ticketItem, MenuModifier menuModifier, OrderType type, Multiplier multiplier) {
        TicketItemModifier ticketItemModifier = new TicketItemModifier();
        ticketItemModifier.setModifierId(menuModifier.getId());
        MenuItemModifierGroup menuItemModifierGroup = menuModifier.getMenuItemModifierGroup();
        if (menuItemModifierGroup != null) {
            ticketItemModifier.setMenuItemModifierGroupId(menuItemModifierGroup.getId());
        }
        ticketItemModifier.setItemCount(1);
        ticketItemModifier.setName(menuModifier.getDisplayName().trim());
        ticketItemModifier.setTicketItem(ticketItem);
        double priceForSize = menuModifier.getPriceForSizeAndMultiplier(this.getSelectedSize(), false, multiplier);
        if (multiplier != null) {
            ticketItemModifier.setMultiplierName(multiplier.getName());
            ticketItemModifier.setName(multiplier.getTicketPrefix() + " " + menuModifier.getDisplayName());
        }
        ticketItemModifier.setUnitPrice(priceForSize);
        ticketItemModifier.setTaxRate(menuModifier.getTaxByOrderType(type));
        ticketItemModifier.setModifierType(1);
        ticketItemModifier.setShouldPrintToKitchen(menuModifier.isShouldPrintToKitchen());
        ticketItemModifier.setShouldSectionWisePrice(menuModifier.isShouldSectionWisePrice());
        return ticketItemModifier;
    }

    public MenuItemSize getSelectedSize() {
        List<POSToggleButton> sizeButtonList = this.sizeAndCrustPanel.sizeButtonList;
        for (POSToggleButton posToggleButton : sizeButtonList) {
            if (!posToggleButton.isSelected()) continue;
            PizzaPrice pizzaPrice = (PizzaPrice)posToggleButton.getClientProperty(PROP_PIZZA_PRICE);
            return pizzaPrice.getSize();
        }
        return null;
    }

    public void setSelectedSection(Section section) {
        if (section.isSelected()) {
            return;
        }
        for (Section sec : this.sectionList) {
            sec.lblTitle.setBackground(Color.lightGray);
            sec.setSelected(false);
        }
        section.lblTitle.setBackground(Color.green);
        section.setSelected(true);
    }

    public Section getSelectedSection() {
        for (Section sec : this.sectionList) {
            if (!sec.isSelected()) continue;
            return sec;
        }
        return this.getMainSection();
    }

    public Section getMainSection() {
        for (Section sec : this.sectionList) {
            if (!sec.mainSection) continue;
            return sec;
        }
        return null;
    }

    private TicketItemModifier getSizeAndCrustModifer() {
        if (this.ticketItem != null && this.ticketItem.getSizeModifier() != null) {
            this.crustModifier = this.ticketItem.getSizeModifier();
            return this.crustModifier;
        }
        return this.crustModifier;
    }

    @Override
    public void modifierRemoved(TicketItemModifier modifier) {
    }

    private void showModifierSelectionMessage(MenuItemModifierGroup menuItemModifierGroup) {
        String displayName = menuItemModifierGroup.getModifierGroup().getDisplayName();
        int minQuantity = menuItemModifierGroup.getMinQuantity();
        POSMessageDialog.showError("You must select at least " + minQuantity + " modifiers from group " + displayName);
    }

    @Override
    public void finishModifierSelection() {
    }

    private void pizzaCrustSelected(POSToggleButton button) {
        PizzaPrice pizzaPrice = (PizzaPrice)button.getClientProperty(PROP_PIZZA_PRICE);
        this.ticketItem.setUnitPrice(pizzaPrice.getPrice(this.menuItem.getDefaultSellPortion()));
        TicketItemModifier sizeAndCrustModifer = this.getSizeAndCrustModifer();
        if (sizeAndCrustModifer != null) {
            sizeAndCrustModifer.setName(pizzaPrice.getSize().getName() + " " + pizzaPrice.getCrust());
            this.crustModifier = sizeAndCrustModifer;
        } else {
            this.crustModifier = new TicketItemModifier();
            this.crustModifier.setName(pizzaPrice.getSize().getName() + " " + pizzaPrice.getCrust());
            this.crustModifier.setModifierType(5);
            this.crustModifier.setInfoOnly(true);
            this.crustModifier.setTicketItem(this.ticketItem);
        }
        this.crustSelected = true;
    }

    private void doFullSectionMode() {
        this.wholeSectionView.removeAll();
        this.fullSectionLayout.removeAll();
        this.fullSectionLayout.add(this.sectionWhole);
        this.sectionLayout.show(this.sectionView, "full");
        this.ticketItem.setPizzaSectionMode(TicketItem.PIZZA_SECTION_MODE.FULL);
    }

    private void allSectionModifierClear() {
        for (Section section : this.sectionList) {
            if (section.sectionModifierTableModel.getRows() == null) continue;
            section.clearItems();
        }
    }

    private void doHalfSectionMode() {
        this.wholeSectionView.add((Component)this.sectionWhole, "grow");
        this.sectionLayout.show(this.sectionView, "half");
        this.ticketItem.setPizzaSectionMode(TicketItem.PIZZA_SECTION_MODE.HALF);
    }

    private void doQuarterSectionMode() {
        this.wholeSectionView.add((Component)this.sectionWhole, "grow");
        this.sectionLayout.show(this.sectionView, "quarter");
        this.ticketItem.setPizzaSectionMode(TicketItem.PIZZA_SECTION_MODE.QUARTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "FULL") {
            if (this.ticketItem.getTicketItemModifiers() != null && this.ticketItem.getTicketItemModifiers().size() > 0) {
                int questionDialog = POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(), "Items of the section will be deleted, Are you sure to change section mode?", "Change Section Mode");
                if (questionDialog != 0) {
                    this.currentButton.setSelected(true);
                    return;
                }
                this.allSectionModifierClear();
            }
            this.doFullSectionMode();
        } else if (e.getActionCommand() == "HALF") {
            if (this.ticketItem.getTicketItemModifiers() != null && this.ticketItem.getTicketItemModifiers().size() > 0) {
                int questionDialog = POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(), "Items of the section will be deleted, Are you sure to change section mode?", "Change Section Mode");
                if (questionDialog != 0) {
                    this.currentButton.setSelected(true);
                    return;
                }
                this.allSectionModifierClear();
            }
            this.doHalfSectionMode();
        } else if (e.getActionCommand() == "QUARTER") {
            if (this.ticketItem.getTicketItemModifiers() != null && this.ticketItem.getTicketItemModifiers().size() > 0) {
                int questionDialog = POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(), "Items of the section will be deleted, Are you sure to change section mode?", "Change Section Mode");
                if (questionDialog != 0) {
                    this.currentButton.setSelected(true);
                    return;
                }
                this.allSectionModifierClear();
            }
            this.doQuarterSectionMode();
        }
        this.currentButton = (JToggleButton)e.getSource();
    }

    class SectionModifierTableModel
    extends ListTableModel<TicketItemModifier> {
        public SectionModifierTableModel() {
            super(new String[]{"Item", "Price"});
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            TicketItemModifier item = (TicketItemModifier)this.rows.get(rowIndex);
            if (item instanceof TicketItemModifier) {
                item.calculatePrice();
            }
            switch (columnIndex) {
                case 0: {
                    if (item instanceof TicketItemModifier) {
                        return item.getName();
                    }
                    return " " + item.getNameDisplay();
                }
                case 1: {
                    Double total = null;
                    if (item instanceof TicketItemModifier) {
                        total = item.getUnitPrice();
                        return NumberUtil.roundToTwoDigit(total);
                    }
                    return null;
                }
            }
            return null;
        }

        public void deleteGivenItem(TicketItemModifier ticketItemModifier) {
            Iterator iterator = this.rows.iterator();
            while (iterator.hasNext()) {
                TicketItemModifier tModifier = (TicketItemModifier)iterator.next();
                if (ticketItemModifier != tModifier) continue;
                iterator.remove();
            }
            this.fireTableDataChanged();
        }

        public void deleteGivenItemByName(TicketItemModifier ticketItemModifier) {
            Iterator iterator = this.rows.iterator();
            while (iterator.hasNext()) {
                TicketItemModifier tModifier = (TicketItemModifier)iterator.next();
                if (!ticketItemModifier.getName().equals(tModifier.getName())) continue;
                iterator.remove();
            }
            this.fireTableDataChanged();
        }
    }

    public class ModifierTableCellRenderer
    extends PosTableRenderer {
        private boolean inTicketScreen = false;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            ITicketItem ticketItem;
            Component rendererComponent = null;
            SectionModifierTableModel model = (SectionModifierTableModel)table.getModel();
            Object object = model.getRowData(row);
            if (column == 1) {
                rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            } else {
                rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                if (column == 0) {
                    this.setHorizontalAlignment(0);
                } else {
                    this.setHorizontalAlignment(4);
                }
            }
            if (isSelected) {
                rendererComponent.setBackground(Color.BLUE);
                rendererComponent.setForeground(Color.WHITE);
                return rendererComponent;
            }
            rendererComponent.setBackground(table.getBackground());
            rendererComponent.setForeground(Color.BLACK);
            if (object instanceof TicketItemModifier && (ticketItem = (ITicketItem)object).isPrintedToKitchen().booleanValue()) {
                rendererComponent.setBackground(Color.YELLOW);
                rendererComponent.setForeground(Color.BLACK);
            }
            return rendererComponent;
        }
    }

    class SizeAndCrustSelectionPane
    extends JPanel {
        List<PizzaPrice> priceList;
        List<POSToggleButton> sizeButtonList = new ArrayList<POSToggleButton>();
        List<POSToggleButton> crustButtonList = new ArrayList<POSToggleButton>();
        JPanel sizePanel = new JPanel();
        JPanel crustPanel = new JPanel();
        ButtonGroup sizeBtnGroup = new ButtonGroup();
        ButtonGroup crustBtnGroup = new ButtonGroup();
        MenuItemSize menuItemSize;
        PizzaCrust pizzaCrust;

        public SizeAndCrustSelectionPane() {
            this.priceList = PizzaModifierSelectionDialog.this.menuItem.getPizzaPriceList();
            this.setLayout(new BorderLayout());
            this.sizePanel.setBorder(BorderFactory.createTitledBorder(null, "SIZE", 2, 2));
            this.crustPanel.setBorder(BorderFactory.createTitledBorder(null, "CRUST", 2, 2));
            this.crustPanel.setLayout(new FlowLayout());
            HashSet<MenuItemSize> uniqueSizeList = new HashSet<MenuItemSize>();
            for (PizzaPrice pizzaPrice : this.priceList) {
                MenuItemSize size = pizzaPrice.getSize();
                if (uniqueSizeList.contains(size)) continue;
                uniqueSizeList.add(size);
                this.addSizeButton(pizzaPrice, size);
            }
            this.selectExistingSizeAndCrust();
            this.add((Component)this.sizePanel, "West");
            this.add(this.crustPanel);
        }

        private void selectExistingSizeAndCrust() {
            TicketItemModifier sizeAndCrustModifer = PizzaModifierSelectionDialog.this.getSizeAndCrustModifer();
            if (sizeAndCrustModifer != null) {
                PizzaPrice pizzaPrice;
                String sizeAndCrustName = sizeAndCrustModifer.getName();
                String[] split = sizeAndCrustName.split(" ");
                String sizeName = split[0];
                String crustName = split[1];
                for (POSToggleButton sizeButton : this.sizeButtonList) {
                    pizzaPrice = (PizzaPrice)sizeButton.getClientProperty(PizzaModifierSelectionDialog.PROP_PIZZA_PRICE);
                    if (pizzaPrice.getSize().getName().equalsIgnoreCase(sizeName)) {
                        sizeButton.setSelected(true);
                        this.renderCrusts(pizzaPrice.getSize());
                        continue;
                    }
                    if (!PizzaModifierSelectionDialog.this.ticketItem.isPrintedToKitchen().booleanValue()) continue;
                    sizeButton.setEnabled(false);
                }
                for (POSToggleButton crustButton : this.crustButtonList) {
                    pizzaPrice = (PizzaPrice)crustButton.getClientProperty(PizzaModifierSelectionDialog.PROP_PIZZA_PRICE);
                    if (pizzaPrice.getCrust().getName().startsWith(crustName)) {
                        crustButton.setSelected(true);
                        PizzaModifierSelectionDialog.this.crustSelected = true;
                        continue;
                    }
                    if (!PizzaModifierSelectionDialog.this.ticketItem.isPrintedToKitchen().booleanValue()) continue;
                    crustButton.setEnabled(false);
                }
            } else if (!this.sizeButtonList.isEmpty()) {
                ArrayList<Boolean> isBtnSelected = new ArrayList<Boolean>();
                for (POSToggleButton button : this.sizeButtonList) {
                    if (!button.isSelected()) continue;
                    PizzaPrice pizzaPrice = (PizzaPrice)button.getClientProperty(PizzaModifierSelectionDialog.PROP_PIZZA_PRICE);
                    this.renderCrusts(pizzaPrice.getSize());
                    isBtnSelected.add(true);
                }
                if (isBtnSelected.isEmpty() || isBtnSelected.contains(false)) {
                    POSToggleButton button = this.sizeButtonList.get(0);
                    PizzaPrice pizzaPrice = (PizzaPrice)button.getClientProperty(PizzaModifierSelectionDialog.PROP_PIZZA_PRICE);
                    this.renderCrusts(pizzaPrice.getSize());
                    button.setSelected(true);
                }
            }
        }

        private void addSizeButton(PizzaPrice pizzaPrice, MenuItemSize size) {
            POSToggleButton sizeButton = new POSToggleButton(size.getName());
            sizeButton.putClientProperty(PizzaModifierSelectionDialog.PROP_PIZZA_PRICE, pizzaPrice);
            if (size.isDefaultSize().booleanValue()) {
                sizeButton.setSelected(true);
            }
            sizeButton.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    POSToggleButton button = (POSToggleButton)e.getSource();
                    PizzaPrice pizzaPrice = (PizzaPrice)button.getClientProperty(PizzaModifierSelectionDialog.PROP_PIZZA_PRICE);
                    SizeAndCrustSelectionPane.this.renderCrusts(pizzaPrice.getSize());
                    if (PizzaModifierSelectionDialog.this.getSizeAndCrustModifer() != null) {
                        PizzaModifierSelectionDialog.this.ticketItem.setSizeModifier(PizzaModifierSelectionDialog.this.getSizeAndCrustModifer());
                        PizzaModifierSelectionDialog.this.ticketItem.getSizeModifier().calculatePrice();
                        PizzaModifierSelectionDialog.this.ticketItemViewerModel.updateView();
                    }
                    PizzaModifierSelectionDialog.this.modifierView.updateView();
                }
            });
            this.sizeBtnGroup.add(sizeButton);
            this.sizeButtonList.add(sizeButton);
            this.sizePanel.add(sizeButton);
        }

        protected void renderCrusts(MenuItemSize size) {
            this.setMenuItemSize(size);
            for (POSToggleButton component : this.crustButtonList) {
                this.crustBtnGroup.remove(component);
            }
            this.crustPanel.removeAll();
            Set<PizzaPrice> availablePrices = PizzaModifierSelectionDialog.this.menuItem.getAvailablePrices(size);
            TicketItemModifier sizeAndCrustModifer = PizzaModifierSelectionDialog.this.getSizeAndCrustModifer();
            for (final PizzaPrice pizzaPrice : availablePrices) {
                POSToggleButton crustButton = new POSToggleButton();
                crustButton.setText("<html><center>" + pizzaPrice.getCrust().getName() + "<br/>" + CurrencyUtil.getCurrencySymbol() + pizzaPrice.getPrice(PizzaModifierSelectionDialog.this.menuItem.getDefaultSellPortion()) + "</center></html>");
                crustButton.putClientProperty(PizzaModifierSelectionDialog.PROP_PIZZA_PRICE, pizzaPrice);
                if (availablePrices.size() == 1) {
                    PizzaModifierSelectionDialog.this.crustSelected = true;
                    crustButton.setSelected(true);
                    PizzaModifierSelectionDialog.this.pizzaCrustSelected(crustButton);
                    this.setPizzaCrust(pizzaPrice.getCrust());
                }
                if (pizzaPrice.getCrust().isDefaultCrust().booleanValue() && sizeAndCrustModifer == null) {
                    PizzaModifierSelectionDialog.this.crustSelected = true;
                    crustButton.setSelected(true);
                    PizzaModifierSelectionDialog.this.pizzaCrustSelected(crustButton);
                    this.setPizzaCrust(pizzaPrice.getCrust());
                }
                if (sizeAndCrustModifer != null) {
                    String sizeAndCrustName = sizeAndCrustModifer.getName();
                    String[] split = sizeAndCrustName.split(" ");
                    String crustName = split[1];
                    if (pizzaPrice.getCrust().getName().startsWith(crustName)) {
                        PizzaModifierSelectionDialog.this.crustSelected = true;
                        crustButton.setSelected(true);
                        PizzaModifierSelectionDialog.this.pizzaCrustSelected(crustButton);
                        this.setPizzaCrust(pizzaPrice.getCrust());
                    }
                }
                crustButton.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        POSToggleButton button = (POSToggleButton)e.getSource();
                        PizzaModifierSelectionDialog.this.pizzaCrustSelected(button);
                        SizeAndCrustSelectionPane.this.setPizzaCrust(pizzaPrice.getCrust());
                        if (PizzaModifierSelectionDialog.this.getSizeAndCrustModifer() != null) {
                            PizzaModifierSelectionDialog.this.ticketItem.setSizeModifier(PizzaModifierSelectionDialog.this.getSizeAndCrustModifer());
                            PizzaModifierSelectionDialog.this.ticketItem.getSizeModifier().calculatePrice();
                            PizzaModifierSelectionDialog.this.ticketItemViewerModel.updateView();
                        }
                    }
                });
                this.crustBtnGroup.add(crustButton);
                this.crustButtonList.add(crustButton);
                this.crustPanel.add(crustButton);
            }
            this.crustPanel.revalidate();
            this.crustPanel.repaint();
        }

        public MenuItemSize getMenuItemSize() {
            return this.menuItemSize;
        }

        public void setMenuItemSize(MenuItemSize menuItemSize) {
            this.menuItemSize = menuItemSize;
        }

        public PizzaCrust getPizzaCrust() {
            return this.pizzaCrust;
        }

        public void setPizzaCrust(PizzaCrust pizzaCrust) {
            this.pizzaCrust = pizzaCrust;
        }
    }

    public class Pizza
    extends JPanel {
        int size;

        public Pizza(LayoutManager layoutManager) {
            super(layoutManager);
            this.setOpaque(false);
            this.setBackground(Color.white);
            this.setPreferredSize(PosUIManager.getSize(250, 250));
        }

        public void setSize(int size) {
            this.size = size;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int x = 15;
            int width = this.getWidth() - 30;
            int y = (this.getHeight() + 40) / 2 - width / 2;
            g.setColor(Color.WHITE);
            Graphics2D g2d = (Graphics2D)g;
            g.setColor(new Color(255, 251, 211));
            Ellipse2D.Double circle = new Ellipse2D.Double(x, y, width, width);
            g2d.fill(circle);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(Color.green);
            Section selectedSection = PizzaModifierSelectionDialog.this.getSelectedSection();
            if (selectedSection == null) {
                return;
            }
            if (selectedSection.getSectionName().equalsIgnoreCase("Quarter 1")) {
                this.fillQuarter1(g2, x, y, width);
            } else if (selectedSection.getSectionName().equalsIgnoreCase("Quarter 2")) {
                this.fillQuarter2(g2, x, y, width);
            } else if (selectedSection.getSectionName().equalsIgnoreCase("Quarter 3")) {
                this.fillQuarter3(g2, x, y, width);
            } else if (selectedSection.getSectionName().equalsIgnoreCase("Quarter 4")) {
                this.fillQuarter4(g2, x, y, width);
            } else if (selectedSection.getSectionName().equalsIgnoreCase("Half 1")) {
                this.fillHalf1(g2, x, y, width);
            } else if (selectedSection.getSectionName().equalsIgnoreCase("Half 2")) {
                this.fillHalf2(g2, x, y, width);
            }
        }

        void drawCircleByCenter(Graphics g, int x, int y, int radius) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(Color.lightGray);
            g.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
        }

        void fillQuarter1(Graphics2D g2, int x, int y, int width) {
            g2.fillArc(x, y, width, width, 90, 90);
        }

        void fillQuarter2(Graphics2D g2, int x, int y, int width) {
            g2.fillArc(x, y, width, width, 360, 90);
        }

        void fillQuarter3(Graphics2D g2, int x, int y, int width) {
            g2.fillArc(x, y, width, width, 180, 90);
        }

        void fillQuarter4(Graphics2D g2, int x, int y, int width) {
            g2.fillArc(x, y, width, width, 270, 90);
        }

        void fillHalf1(Graphics2D g2, int x, int y, int width) {
            g2.fillArc(x, y, width, width, 90, 180);
        }

        void fillHalf2(Graphics2D g2, int x, int y, int width) {
            g2.fillArc(x, y, width, width, 270, 180);
        }
    }

    private class Section
    extends JPanel
    implements MouseListener {
        boolean selected;
        private boolean mainSection;
        private JLabel lblTitle;
        private final String sectionName;
        private final int sortOrder;
        private final String displayTitle;
        private final double price;
        private JTable sectionTable;
        private SectionModifierTableModel sectionModifierTableModel;

        public Section(String sectionName, String displayTitle, int sortOrder, boolean main, double price) {
            this.sectionName = sectionName;
            this.displayTitle = displayTitle;
            this.sortOrder = sortOrder;
            this.mainSection = main;
            this.price = price;
            this.setLayout(new BorderLayout());
            this.lblTitle = new JLabel(sectionName);
            this.lblTitle.setBackground(Color.LIGHT_GRAY);
            this.lblTitle.setHorizontalAlignment(0);
            this.lblTitle.setFont(this.lblTitle.getFont().deriveFont(1, 14.0f));
            this.lblTitle.setOpaque(true);
            this.add((Component)this.lblTitle, "North");
            this.setOpaque(false);
            this.setPreferredSize(PosUIManager.getSize(160, 170));
            this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            this.sectionTable = new JTable();
            this.sectionTable.setTableHeader(null);
            this.sectionTable.setRowHeight(PosUIManager.getSize(30));
            this.sectionModifierTableModel = new SectionModifierTableModel();
            this.sectionTable.setDefaultRenderer(Object.class, new ModifierTableCellRenderer());
            this.sectionTable.setModel(this.sectionModifierTableModel);
            JScrollPane scrollPane = new JScrollPane(this.sectionTable);
            scrollPane.setBorder(null);
            JViewport viewPort = scrollPane.getViewport();
            viewPort.setOpaque(false);
            scrollPane.setOpaque(false);
            scrollPane.setBorder(null);
            this.add((Component)scrollPane, "Center");
            this.addMouseListener(this);
            viewPort.addMouseListener(this);
            this.sectionTable.addMouseListener(this);
            this.resizeColumnWidth(this.sectionTable);
        }

        public void resizeColumnWidth(JTable table) {
            TableColumnModel columnModel = table.getColumnModel();
            for (int column = 0; column < table.getColumnCount(); ++column) {
                columnModel.getColumn(column).setPreferredWidth((Integer)this.getColumnWidth().get(column));
            }
        }

        private List getColumnWidth() {
            ArrayList<Integer> columnWidth = new ArrayList<Integer>();
            columnWidth.add(70);
            columnWidth.add(30);
            return columnWidth;
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D graphics2d = (Graphics2D)g;
            AlphaComposite composite = (AlphaComposite)graphics2d.getComposite();
            AlphaComposite composite2 = composite.derive(0.75f);
            graphics2d.setComposite(composite2);
            super.paintComponent(g);
        }

        public boolean isEmpty() {
            return this.sectionModifierTableModel.getRows().size() <= 0;
        }

        public void clearItems() {
            boolean isPrintedModifierExist = false;
            Iterator<TicketItemModifier> iterator = PizzaModifierSelectionDialog.this.ticketItem.getTicketItemModifiers().iterator();
            while (iterator.hasNext()) {
                TicketItemModifier ticketItemModifier = iterator.next();
                if (!ticketItemModifier.isPrintedToKitchen().booleanValue()) {
                    if (!ticketItemModifier.getSectionName().equals(this.getSectionName())) continue;
                    iterator.remove();
                    PizzaModifierSelectionDialog.this.ticketItem.deleteTicketItemModifier(ticketItemModifier);
                    this.sectionModifierTableModel.deleteGivenItem(ticketItemModifier);
                    continue;
                }
                isPrintedModifierExist = true;
            }
            PizzaModifierSelectionDialog.this.ticketItemViewerModel.updateView();
            this.sectionTable.repaint();
            this.repaint();
            if (isPrintedModifierExist) {
                POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), "Modifiers that sent to kitchen can not be deleted!");
            }
            PizzaModifierSelectionDialog.this.previousMenuItemSize = null;
        }

        public void clearSelectedItem() {
            boolean isPrintedModifierExist = false;
            if (this.sectionModifierTableModel.getRows() == null) {
                return;
            }
            if (this.sectionModifierTableModel.getRows().size() == 0) {
                return;
            }
            int index = this.sectionTable.getSelectedRow();
            if (index < 0) {
                return;
            }
            TicketItemModifier selectedModifier = (TicketItemModifier)this.sectionModifierTableModel.getRowData(index);
            if (selectedModifier == null) {
                return;
            }
            if (!selectedModifier.isPrintedToKitchen().booleanValue()) {
                PizzaModifierSelectionDialog.this.ticketItem.deleteTicketItemModifier(selectedModifier);
                this.sectionModifierTableModel.deleteGivenItem(selectedModifier);
                if (this.sectionModifierTableModel.getRows().size() == 0) {
                    this.clearItems();
                }
            } else {
                isPrintedModifierExist = true;
            }
            PizzaModifierSelectionDialog.this.ticketItemViewerModel.updateView();
            this.repaint();
            if (isPrintedModifierExist) {
                POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), "Modifiers that sent to kitchen can not be deleted!");
            }
            PizzaModifierSelectionDialog.this.previousMenuItemSize = null;
        }

        public void clearItem(TicketItemModifier ticketItemModifier, SectionModifierTableModel sectionModifierTableModel) {
            boolean isPrintedModifierExist = false;
            if (sectionModifierTableModel.getRows() == null) {
                return;
            }
            if (sectionModifierTableModel.getRows().size() == 0) {
                return;
            }
            if (!ticketItemModifier.isPrintedToKitchen().booleanValue()) {
                PizzaModifierSelectionDialog.this.ticketItem.deleteTicketItemModifierByName(ticketItemModifier);
                sectionModifierTableModel.deleteGivenItemByName(ticketItemModifier);
                if (sectionModifierTableModel.getRows().size() == 0) {
                    this.clearItems();
                }
            } else {
                isPrintedModifierExist = true;
            }
            PizzaModifierSelectionDialog.this.ticketItemViewerModel.updateView();
            this.repaint();
            if (isPrintedModifierExist) {
                POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), "Modifiers that sent to kitchen can not be deleted!");
            }
            PizzaModifierSelectionDialog.this.previousMenuItemSize = null;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            this.repaint();
        }

        public boolean isSelected() {
            return this.selected;
        }

        public void addItem(TicketItemModifier newModifier) {
            this.sectionModifierTableModel.addItem(newModifier);
            this.repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            PizzaModifierSelectionDialog.this.setSelectedSection(this);
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        public String getSectionName() {
            return this.sectionName;
        }

        public double getPrice() {
            return this.price;
        }

        public double calculatePrice(double modifierPrice) {
            return modifierPrice * this.getPrice();
        }
    }
}

