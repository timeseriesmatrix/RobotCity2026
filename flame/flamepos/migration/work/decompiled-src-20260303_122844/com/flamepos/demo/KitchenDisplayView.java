/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.demo;

import com.floreantpos.Messages;
import com.floreantpos.actions.LogoutAction;
import com.floreantpos.demo.KitchenFilterDialog;
import com.floreantpos.demo.KitchenTicketListPanel;
import com.floreantpos.main.Application;
import com.floreantpos.model.KitchenTicket;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PosPrinters;
import com.floreantpos.model.Printer;
import com.floreantpos.model.dao.KitchenTicketDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosComboRenderer;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.ui.HeaderPanel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.ui.views.order.ViewPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

public class KitchenDisplayView
extends ViewPanel
implements ActionListener {
    public static final String VIEW_NAME = "KD";
    private static KitchenDisplayView instance;
    private JComboBox<Printer> cbPrinters = new JComboBox();
    private JComboBox<OrderType> cbTicketTypes = new JComboBox();
    private HeaderPanel headerPanel;
    private JPanel filterPanel;
    private JLabel lblFilter;
    private PosButton btnFilter;
    private KitchenTicketListPanel ticketPanel = new KitchenTicketListPanel();
    private Timer viewUpdateTimer;
    private PosButton btnLogout;

    public KitchenDisplayView(boolean showHeader) {
        this.setLayout(new BorderLayout(5, 5));
        PosPrinters printers = Application.getPrinters();
        List<Printer> kitchenPrinters = printers.getKitchenPrinters();
        DefaultComboBoxModel<Printer> printerModel = new DefaultComboBoxModel<Printer>();
        printerModel.addElement(null);
        for (Printer printer : kitchenPrinters) {
            printerModel.addElement(printer);
        }
        Font font = this.getFont().deriveFont(18.0f);
        this.cbPrinters.setFont(font);
        this.cbPrinters.setRenderer((ListCellRenderer<Printer>)((Object)new PosComboRenderer()));
        this.cbPrinters.setModel(printerModel);
        this.cbPrinters.addActionListener(this);
        JPanel firstTopPanel = new JPanel(new BorderLayout(5, 5));
        if (showHeader) {
            this.headerPanel = new HeaderPanel();
            firstTopPanel.add((Component)this.headerPanel, "North");
        }
        this.filterPanel = new JPanel();
        PosButton btnBack = new PosButton(Messages.getString("KitchenDisplayView.1"));
        btnBack.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                RootView.getInstance().showDefaultView();
            }
        });
        JLabel label = new JLabel(Messages.getString("KitchenDisplayView.5"));
        label.setFont(font);
        JLabel label2 = new JLabel(Messages.getString("KitchenDisplayView.6"));
        label2.setFont(font);
        this.filterPanel.setLayout((LayoutManager)new MigLayout("", "[][][][][fill,grow][]", ""));
        this.filterPanel.add(label);
        this.filterPanel.add(this.cbPrinters);
        this.filterPanel.add(label2);
        this.filterPanel.add(this.cbTicketTypes);
        this.btnFilter = new PosButton(Messages.getString("KitchenDisplayView.2"));
        this.btnFilter.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                KitchenFilterDialog dialog = new KitchenFilterDialog();
                dialog.add((Component)KitchenDisplayView.this.filterPanel, "Center");
                dialog.open();
            }
        });
        JPanel topPanel = new JPanel((LayoutManager)new MigLayout("fill, ins 2 2 0 2", "[][fill, grow][]", ""));
        Dimension size = PosUIManager.getSize(60, 40);
        Font filterFont = this.getFont().deriveFont(1, 12.0f);
        this.lblFilter = new JLabel("Filter: All Printers- All Orders");
        this.lblFilter.setForeground(new Color(49, 106, 196));
        this.lblFilter.setFont(filterFont);
        topPanel.add(this.lblFilter);
        topPanel.add((Component)this.btnFilter, "w " + size.width + "!,h " + size.height + "!");
        topPanel.add((Component)btnBack, "w " + size.width + "!, h " + size.height + "!");
        topPanel.setBackground(Color.white);
        this.cbTicketTypes.setFont(font);
        this.cbTicketTypes.setRenderer((ListCellRenderer<OrderType>)((Object)new PosComboRenderer()));
        DefaultComboBoxModel<OrderType> ticketTypeModel = new DefaultComboBoxModel<OrderType>();
        for (OrderType orderType : Application.getInstance().getOrderTypes()) {
            ticketTypeModel.addElement(orderType);
        }
        ticketTypeModel.insertElementAt(null, 0);
        this.cbTicketTypes.setModel(ticketTypeModel);
        this.cbTicketTypes.setSelectedIndex(0);
        this.cbTicketTypes.addActionListener(this);
        this.btnLogout = new PosButton(new LogoutAction(true, false));
        topPanel.add((Component)this.btnLogout, "w " + size.width + "!, h " + size.height + "!, wrap");
        topPanel.add((Component)new JSeparator(), "grow,span");
        firstTopPanel.setPreferredSize(new Dimension(0, PosUIManager.getSize(50)));
        firstTopPanel.add(topPanel);
        this.add((Component)firstTopPanel, "North");
        this.ticketPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.add(this.ticketPanel);
        this.add((Component)this.ticketPanel.getPaginationPanel(), "South");
        this.viewUpdateTimer = new Timer(5000, this);
        this.viewUpdateTimer.setRepeats(true);
    }

    public void addTicket(KitchenTicket ticket) {
        this.addTicket(ticket, true);
    }

    private synchronized void addTicket(KitchenTicket ticket, boolean updateView) {
        if (!this.isShowing()) {
            return;
        }
        Printer selectedPrinter = (Printer)this.cbPrinters.getSelectedItem();
        if (selectedPrinter != null && !selectedPrinter.equals(ticket.getPrinters())) {
            return;
        }
        OrderType selectedTicketType = (OrderType)this.cbTicketTypes.getSelectedItem();
        if (selectedTicketType != null && selectedTicketType != ticket.getType()) {
            return;
        }
        if (this.ticketPanel.addTicket(ticket) && updateView) {
            this.ticketPanel.repaint();
        }
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            this.updateTicketView();
            if (!this.viewUpdateTimer.isRunning()) {
                this.viewUpdateTimer.start();
            }
        } else {
            this.cleanup();
        }
    }

    public synchronized void cleanup() {
        this.viewUpdateTimer.stop();
        this.ticketPanel.removeAll();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() != null && e.getActionCommand().equalsIgnoreCase("log out")) {
            Application.getInstance().doLogout();
        }
        if (e.getSource() == this.viewUpdateTimer) {
            this.updateTicketView();
        } else {
            this.ticketPanel.removeAll();
            this.updateTicketView();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void updateTicketView() {
        try {
            this.viewUpdateTimer.stop();
            List<KitchenTicket> list = KitchenTicketDAO.getInstance().findAllOpen();
            for (KitchenTicket kitchenTicket : list) {
                this.addTicket(kitchenTicket, false);
            }
            this.ticketPanel.repaint();
        }
        catch (Exception e2) {
            POSMessageDialog.showError(this, e2.getMessage(), e2);
        }
        finally {
            this.viewUpdateTimer.restart();
        }
    }

    @Override
    public String getViewName() {
        return VIEW_NAME;
    }

    public static synchronized KitchenDisplayView getInstance() {
        if (instance == null) {
            instance = new KitchenDisplayView(false);
        }
        return instance;
    }
}

