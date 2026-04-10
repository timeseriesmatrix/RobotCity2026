/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang3.StringUtils
 */
package com.floreantpos.ui.views.order.modifier;

import com.floreantpos.IconFactory;
import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.views.order.actions.OrderListener;
import com.floreantpos.ui.views.order.modifier.ModifierSelectionListener;
import com.floreantpos.ui.views.order.modifier.ModifierSelectionModel;
import com.floreantpos.ui.views.order.modifier.ModifierViewerTable;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

public class TicketItemModifierTableView
extends JPanel {
    private Vector<OrderListener> orderListeners = new Vector();
    public static final String VIEW_NAME = "TICKET_MODIFIER_VIEW";
    private Vector<ModifierSelectionListener> listenerList = new Vector();
    private ModifierSelectionModel modifierSelectionModel;
    private TransparentPanel ticketActionPanel = new TransparentPanel();
    private PosButton btnDelete = new PosButton(IconFactory.getIcon("/ui_icons/", "delete.png"));
    private PosButton btnScrollDown;
    private PosButton btnScrollUp = new PosButton(IconFactory.getIcon("/ui_icons/", "up.png"));
    private TransparentPanel ticketItemActionPanel;
    private JScrollPane ticketScrollPane;
    private ModifierViewerTable modifierViewerTable;
    private TitledBorder titledBorder = new TitledBorder("");
    private Border border = new CompoundBorder(this.titledBorder, new EmptyBorder(5, 5, 5, 5));
    private ModifierSelectionListener listener;

    public TicketItemModifierTableView(ModifierSelectionModel modifierSelectionModel, ModifierSelectionListener listener) {
        this.modifierSelectionModel = modifierSelectionModel;
        this.listener = listener;
        this.initComponents();
    }

    private void initComponents() {
        this.titledBorder.setTitle(this.modifierSelectionModel.getTicketItem().getName());
        this.titledBorder.setTitleJustification(2);
        this.setBorder(this.border);
        this.setLayout(new BorderLayout(5, 5));
        this.ticketItemActionPanel = new TransparentPanel();
        this.btnScrollDown = new PosButton();
        this.modifierViewerTable = new ModifierViewerTable(this.modifierSelectionModel.getTicketItem());
        this.ticketScrollPane = new PosScrollPane(this.modifierViewerTable);
        this.ticketScrollPane.setHorizontalScrollBarPolicy(31);
        this.ticketScrollPane.setVerticalScrollBarPolicy(21);
        this.ticketScrollPane.setPreferredSize(new Dimension(180, 200));
        this.createTicketActionPanel();
        this.createTicketItemControlPanel();
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(this.ticketScrollPane);
        centerPanel.add((Component)this.createItemDescriptionPanel(), "North");
        this.add(centerPanel);
        this.add((Component)this.ticketActionPanel, "South");
        centerPanel.add((Component)this.ticketItemActionPanel, "East");
        this.modifierViewerTable.getRenderer().setInTicketScreen(true);
        this.modifierViewerTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    TicketItemModifierTableView.this.updateSelectionView();
                }
            }
        });
        this.modifierViewerTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                Object selected = TicketItemModifierTableView.this.modifierViewerTable.getSelected();
                if (!(selected instanceof ITicketItem)) {
                    return;
                }
                ITicketItem item = (ITicketItem)selected;
                Boolean printedToKitchen = item.isPrintedToKitchen();
                TicketItemModifierTableView.this.btnDelete.setEnabled(printedToKitchen == false);
            }
        });
        this.setPreferredSize(new Dimension(360, 463));
    }

    private JPanel createItemDescriptionPanel() {
        MenuItem menuItem = this.modifierSelectionModel.getMenuItem();
        JPanel itemDescriptionPanel = new JPanel((LayoutManager)new MigLayout("inset 0,center"));
        String description = menuItem.getDescription();
        if (StringUtils.isEmpty((CharSequence)description) && menuItem.getImage() == null) {
            return itemDescriptionPanel;
        }
        itemDescriptionPanel.setBorder(BorderFactory.createTitledBorder("-"));
        JLabel lblDescription = new JLabel();
        lblDescription.setText("<html><body>" + description + "</body></html>");
        JLabel pictureLabel = new JLabel(menuItem.getImage());
        itemDescriptionPanel.add(pictureLabel);
        itemDescriptionPanel.add(lblDescription);
        return itemDescriptionPanel;
    }

    private void createTicketActionPanel() {
        this.ticketActionPanel.setLayout(new GridLayout(1, 0, 5, 5));
    }

    private void createTicketItemControlPanel() {
        this.ticketItemActionPanel.setLayout(new GridLayout(0, 1, 5, 5));
        this.btnScrollUp.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                TicketItemModifierTableView.this.doScrollUp(evt);
            }
        });
        this.btnScrollDown.setIcon(IconFactory.getIcon("/ui_icons/", "down.png"));
        this.btnScrollDown.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                TicketItemModifierTableView.this.doScrollDown(evt);
            }
        });
        this.btnDelete.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                TicketItemModifierTableView.this.doDeleteSelection(evt);
            }
        });
        this.ticketItemActionPanel.add(this.btnScrollUp);
        this.ticketItemActionPanel.add(this.btnDelete);
        this.ticketItemActionPanel.add(this.btnScrollDown);
        this.ticketItemActionPanel.setPreferredSize(new Dimension(60, 360));
    }

    public void addModifierSelectionListener(ModifierSelectionListener listener) {
        this.listenerList.add(listener);
    }

    public void removeModifierSelectionListener(ModifierSelectionListener listener) {
        this.listenerList.remove(listener);
    }

    private void doDeleteSelection(ActionEvent evt) {
        Object object = this.modifierViewerTable.deleteSelectedItem();
        if (object != null) {
            this.updateView();
            this.listener.modifierRemoved((TicketItemModifier)object);
        }
    }

    private void doScrollDown(ActionEvent evt) {
        this.modifierViewerTable.scrollDown();
    }

    private void doScrollUp(ActionEvent evt) {
        this.modifierViewerTable.scrollUp();
    }

    public void removeModifier(TicketItem parent, TicketItemModifier modifier) {
        modifier.setItemCount(0);
        this.modifierViewerTable.removeModifier(parent, modifier);
    }

    public void updateAllView() {
        this.modifierViewerTable.updateView();
        this.updateView();
    }

    public void selectRow(int rowIndex) {
        this.modifierViewerTable.selectRow(rowIndex);
    }

    public void updateView() {
        this.modifierViewerTable.updateView();
    }

    public void addOrderListener(OrderListener listenre) {
        this.orderListeners.add(listenre);
    }

    public void removeOrderListener(OrderListener listenre) {
        this.orderListeners.remove(listenre);
    }

    public void setControlsVisible(boolean visible) {
        if (visible) {
            this.ticketActionPanel.setVisible(true);
            this.btnDelete.setEnabled(true);
        } else {
            this.ticketActionPanel.setVisible(false);
            this.btnDelete.setEnabled(false);
        }
    }

    private void updateSelectionView() {
    }

    public ModifierViewerTable getTicketViewerTable() {
        return this.modifierViewerTable;
    }
}

