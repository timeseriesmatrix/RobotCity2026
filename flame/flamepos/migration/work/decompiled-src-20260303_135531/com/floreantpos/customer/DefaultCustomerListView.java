/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.customer;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosLog;
import com.floreantpos.customer.CustomerListTableModel;
import com.floreantpos.customer.CustomerSelector;
import com.floreantpos.customer.CustomerTable;
import com.floreantpos.extension.OrderServiceFactory;
import com.floreantpos.model.Customer;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.CustomerDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.QwertyKeyPad;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.forms.QuickCustomerForm;
import com.floreantpos.util.POSUtil;
import com.floreantpos.util.TicketAlreadyExistsException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

public class DefaultCustomerListView
extends CustomerSelector {
    private PosButton btnCreateNewCustomer;
    private CustomerTable customerTable;
    private POSTextField tfMobile;
    private POSTextField tfLoyaltyNo;
    private POSTextField tfName;
    private PosButton btnInfo;
    protected Customer selectedCustomer;
    private PosButton btnRemoveCustomer;
    private Ticket ticket;
    private PosButton btnCancel;
    private QwertyKeyPad qwertyKeyPad;
    private PosButton btnNext;
    private PosButton btnPrevious;
    private CustomerListTableModel customerListTableModel;
    private JLabel lblNumberOfItem;

    public DefaultCustomerListView() {
        this.initUI();
    }

    public DefaultCustomerListView(Ticket ticket) {
        this.ticket = ticket;
        this.initUI();
        this.loadCustomerFromTicket();
    }

    public void initUI() {
        this.setLayout((LayoutManager)new MigLayout("fill", "[grow]", "[grow][grow][grow]"));
        JPanel searchPanel = new JPanel((LayoutManager)new MigLayout());
        JLabel lblByPhone = new JLabel(Messages.getString("CustomerSelectionDialog.1"));
        JLabel lblByLoyality = new JLabel(Messages.getString("CustomerSelectionDialog.16"));
        JLabel lblByName = new JLabel(Messages.getString("CustomerSelectionDialog.19"));
        this.tfMobile = new POSTextField(16);
        this.tfLoyaltyNo = new POSTextField(16);
        this.tfName = new POSTextField(16);
        this.tfName.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultCustomerListView.this.doSearchCustomerByIndex();
            }
        });
        this.tfLoyaltyNo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultCustomerListView.this.doSearchCustomerByIndex();
            }
        });
        this.tfMobile.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultCustomerListView.this.doSearchCustomerByIndex();
            }
        });
        PosButton btnSearch = new PosButton(Messages.getString("CustomerSelectionDialog.15"));
        btnSearch.setFocusable(false);
        btnSearch.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultCustomerListView.this.doSearchCustomerByIndex();
            }
        });
        PosButton btnKeyboard = new PosButton(IconFactory.getIcon("/images/", "keyboard.png"));
        btnKeyboard.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultCustomerListView.this.qwertyKeyPad.setCollapsed(!DefaultCustomerListView.this.qwertyKeyPad.isCollapsed());
            }
        });
        searchPanel.add((Component)lblByPhone, "growy");
        searchPanel.add((Component)this.tfMobile, "growy");
        searchPanel.add((Component)lblByLoyality, "growy");
        searchPanel.add((Component)this.tfLoyaltyNo, "growy");
        searchPanel.add((Component)lblByName, "growy");
        searchPanel.add((Component)this.tfName, "growy");
        searchPanel.add((Component)btnKeyboard, "growy,w " + PosUIManager.getSize(80) + "!,h " + PosUIManager.getSize(35) + "!");
        searchPanel.add((Component)btnSearch, ",growy,h " + PosUIManager.getSize(35) + "!");
        this.add((Component)searchPanel, "cell 0 1");
        JPanel centerPanel = new JPanel(new BorderLayout());
        this.setBorder(new TitledBorder(null, POSConstants.SELECT_CUSTOMER.toUpperCase(), 2, 2, null, null));
        JPanel customerListPanel = new JPanel(new BorderLayout(0, 0));
        customerListPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
        this.customerTable = new CustomerTable();
        this.customerListTableModel = new CustomerListTableModel();
        this.customerListTableModel.setPageSize(20);
        this.customerTable.setModel(this.customerListTableModel);
        this.customerTable.setFocusable(false);
        this.customerTable.setRowHeight(30);
        this.customerTable.getTableHeader().setPreferredSize(new Dimension(100, 35));
        this.customerTable.getSelectionModel().setSelectionMode(0);
        this.customerTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                DefaultCustomerListView.this.selectedCustomer = DefaultCustomerListView.this.customerTable.getSelectedCustomer();
                if (DefaultCustomerListView.this.selectedCustomer == null) {
                    DefaultCustomerListView.this.btnInfo.setEnabled(false);
                }
            }
        });
        PosScrollPane scrollPane = new PosScrollPane();
        scrollPane.setFocusable(false);
        scrollPane.setViewportView((Component)((Object)this.customerTable));
        customerListPanel.add((Component)scrollPane, "Center");
        JPanel panel = new JPanel((LayoutManager)new MigLayout("fill", "[][center, grow][]", ""));
        this.btnInfo = new PosButton(Messages.getString("CustomerSelectionDialog.23"));
        this.btnInfo.setFocusable(false);
        panel.add((Component)this.btnInfo, " skip 1, split 6");
        this.btnInfo.setEnabled(false);
        PosButton btnHistory = new PosButton(Messages.getString("CustomerSelectionDialog.24"));
        btnHistory.setEnabled(false);
        panel.add((Component)btnHistory, "");
        this.btnCreateNewCustomer = new PosButton(Messages.getString("CustomerSelectionDialog.25"));
        this.btnCreateNewCustomer.setFocusable(false);
        panel.add((Component)this.btnCreateNewCustomer, "");
        this.btnCreateNewCustomer.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultCustomerListView.this.doCreateNewCustomer();
            }
        });
        this.btnRemoveCustomer = new PosButton(Messages.getString("CustomerSelectionDialog.26"));
        this.btnRemoveCustomer.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultCustomerListView.this.doRemoveCustomerFromTicket();
            }
        });
        panel.add((Component)this.btnRemoveCustomer, "");
        PosButton btnSelect = new PosButton(Messages.getString("CustomerSelectionDialog.28"));
        btnSelect.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (DefaultCustomerListView.this.isCreateNewTicket()) {
                    DefaultCustomerListView.this.doCreateNewTicket();
                } else {
                    DefaultCustomerListView.this.closeDialog(false);
                }
            }
        });
        panel.add((Component)btnSelect, "");
        this.btnCancel = new PosButton(Messages.getString("CustomerSelectionDialog.29"));
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultCustomerListView.this.closeDialog(true);
            }
        });
        panel.add((Component)this.btnCancel, "");
        this.btnNext = new PosButton("NEXT");
        this.btnNext.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (DefaultCustomerListView.this.customerListTableModel.hasNext()) {
                    DefaultCustomerListView.this.customerListTableModel.setCurrentRowIndex(DefaultCustomerListView.this.customerListTableModel.getNextRowIndex());
                    DefaultCustomerListView.this.doSearchCustomer();
                }
            }
        });
        this.btnPrevious = new PosButton("PREVIOUS");
        this.btnPrevious.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (DefaultCustomerListView.this.customerListTableModel.hasPrevious()) {
                    DefaultCustomerListView.this.customerListTableModel.setCurrentRowIndex(DefaultCustomerListView.this.customerListTableModel.getPreviousRowIndex());
                    DefaultCustomerListView.this.doSearchCustomer();
                }
                DefaultCustomerListView.this.updateButtonStatus();
            }
        });
        this.lblNumberOfItem = new JLabel();
        panel.add(this.lblNumberOfItem);
        panel.add(this.btnPrevious);
        panel.add(this.btnNext);
        customerListPanel.add((Component)panel, "South");
        centerPanel.add((Component)customerListPanel, "Center");
        this.add((Component)centerPanel, "cell 0 2,grow");
        this.qwertyKeyPad = new QwertyKeyPad();
        this.qwertyKeyPad.setCollapsed(false);
        this.add((Component)((Object)this.qwertyKeyPad), "cell 0 3,grow");
        this.updateButtonStatus();
    }

    public void updateButtonStatus() {
        this.btnNext.setEnabled(this.customerListTableModel.hasNext());
        this.btnPrevious.setEnabled(this.customerListTableModel.hasPrevious());
    }

    private void loadCustomer() {
        Customer customer = this.getCustomer();
        if (customer != null) {
            this.doSearchCustomerByIndex();
        } else {
            this.doSearchCustomerByIndex();
        }
    }

    private void loadCustomerFromTicket() {
        String customerIdString = this.ticket.getProperty("CUSTOMER_ID");
        if (StringUtils.isNotEmpty((String)customerIdString)) {
            int customerId = Integer.parseInt(customerIdString);
            Customer customer = CustomerDAO.getInstance().get(customerId);
            ArrayList<Customer> list = new ArrayList<Customer>();
            list.add(customer);
            this.customerTable.setModel(new CustomerListTableModel(list));
        }
    }

    private void closeDialog(boolean canceled) {
        Window windowAncestor = SwingUtilities.getWindowAncestor(this);
        if (windowAncestor instanceof POSDialog) {
            ((POSDialog)windowAncestor).setCanceled(false);
            windowAncestor.dispose();
        }
    }

    protected void doSetCustomer(Customer customer) {
        if (this.ticket != null) {
            this.ticket.setCustomer(customer);
            TicketDAO.getInstance().saveOrUpdate(this.ticket);
        }
    }

    private void doCreateNewTicket() {
        try {
            Customer selectedCustomer = this.getSelectedCustomer();
            if (selectedCustomer == null) {
                return;
            }
            this.closeDialog(false);
            OrderServiceFactory.getOrderService().createNewTicket(this.getOrderType(), null, selectedCustomer);
        }
        catch (TicketAlreadyExistsException ticketAlreadyExistsException) {
            // empty catch block
        }
    }

    protected void doRemoveCustomerFromTicket() {
        int option = POSMessageDialog.showYesNoQuestionDialog(this, Messages.getString("CustomerSelectionDialog.2"), Messages.getString("CustomerSelectionDialog.32"));
        if (option != 0) {
            return;
        }
        this.ticket.removeCustomer();
        TicketDAO.getInstance().saveOrUpdate(this.ticket);
    }

    private void doSearchCustomerByIndex() {
        this.customerListTableModel.setCurrentRowIndex(0);
        this.doSearchCustomer();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doSearchCustomer() {
        try {
            this.setCursor(Cursor.getPredefinedCursor(3));
            this.qwertyKeyPad.setCollapsed(true);
            String mobile = this.tfMobile.getText();
            String name = this.tfName.getText();
            String loyalty = this.tfLoyaltyNo.getText();
            if (StringUtils.isEmpty((String)mobile) && StringUtils.isEmpty((String)loyalty) && StringUtils.isEmpty((String)name)) {
                this.customerListTableModel.setNumRows(CustomerDAO.getInstance().getNumberOfCustomers());
                CustomerDAO.getInstance().loadCustomers(this.customerListTableModel);
            } else {
                this.customerListTableModel.setNumRows(CustomerDAO.getInstance().getNumberOfCustomers(mobile, loyalty, name));
                CustomerDAO.getInstance().findBy(mobile, loyalty, name, this.customerListTableModel);
            }
            int startNumber = this.customerListTableModel.getCurrentRowIndex() + 1;
            int endNumber = this.customerListTableModel.getNextRowIndex();
            int totalNumber = this.customerListTableModel.getNumRows();
            if (endNumber > totalNumber) {
                endNumber = totalNumber;
            }
            this.lblNumberOfItem.setText(String.format("Showing %s to %s of %s", startNumber, endNumber, totalNumber));
            this.customerListTableModel.fireTableDataChanged();
            this.customerTable.repaint();
            this.updateButtonStatus();
        }
        catch (Exception e) {
            PosLog.error(DefaultCustomerListView.class, e);
            e.printStackTrace();
        }
        finally {
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

    protected void doCreateNewCustomer() {
        boolean setKeyPad = true;
        QuickCustomerForm form = new QuickCustomerForm(setKeyPad);
        form.enableCustomerFields(true);
        BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)form);
        dialog.setResizable(false);
        dialog.open();
        if (!dialog.isCanceled()) {
            this.selectedCustomer = (Customer)form.getBean();
            CustomerListTableModel model = (CustomerListTableModel)this.customerTable.getModel();
            model.addItem(this.selectedCustomer);
        }
    }

    @Override
    public String getName() {
        return "C";
    }

    @Override
    public Customer getSelectedCustomer() {
        return this.selectedCustomer;
    }

    public void setRemoveButtonEnable(boolean enable) {
        this.btnRemoveCustomer.setEnabled(enable);
    }

    @Override
    public void updateView(boolean update) {
        if (update) {
            this.btnCancel.setVisible(true);
        } else {
            this.btnCancel.setVisible(false);
        }
        this.loadCustomer();
    }

    @Override
    public void redererCustomers() {
    }
}

