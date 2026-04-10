/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.intellij.uiDesigner.core.GridConstraints
 *  com.intellij.uiDesigner.core.GridLayoutManager
 *  com.intellij.uiDesigner.core.Spacer
 *  com.jgoodies.forms.layout.CellConstraints
 *  com.jgoodies.forms.layout.FormLayout
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.model.Discount;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketDiscount;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.NumberUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;

public class DiscountListDialog
extends POSDialog
implements ActionListener {
    private JPanel contentPane;
    private PosButton buttonOK;
    private PosButton buttonCancel;
    private PosButton btnScrollUp;
    private PosButton btnScrollDown;
    private PosButton btnDeleteSelected;
    private JTable tableDiscounts;
    private List<Ticket> tickets;
    private DiscountViewTableModel discountViewTableModel;
    private DefaultListSelectionModel selectionModel;
    private boolean modified = false;

    public DiscountListDialog(List<Ticket> tickets) {
        this.$$$setupUI$$$();
        this.tickets = tickets;
        this.setSize(700, 500);
        this.discountViewTableModel = new DiscountViewTableModel();
        this.tableDiscounts.setModel(this.discountViewTableModel);
        this.selectionModel = new DefaultListSelectionModel();
        this.selectionModel.setSelectionMode(0);
        this.tableDiscounts.setSelectionModel(this.selectionModel);
        this.btnScrollUp.setActionCommand("scrollUP");
        this.btnScrollDown.setActionCommand("scrollDown");
        this.btnScrollUp.addActionListener(this);
        this.btnScrollDown.addActionListener(this);
        this.setContentPane(this.contentPane);
        this.setModal(true);
        this.getRootPane().setDefaultButton(this.buttonOK);
        this.buttonOK.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DiscountListDialog.this.onOK();
            }
        });
        this.buttonCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DiscountListDialog.this.onCancel();
            }
        });
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                DiscountListDialog.this.onCancel();
            }
        });
        this.contentPane.registerKeyboardAction(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DiscountListDialog.this.onCancel();
            }
        }, KeyStroke.getKeyStroke(27, 0), 1);
        this.btnDeleteSelected.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                DiscountListDialog.this.doDeleteSelection();
            }
        });
    }

    private void doDeleteSelection() {
        try {
            int selectedRow = this.selectionModel.getLeadSelectionIndex();
            if (selectedRow < 0) {
                POSMessageDialog.showError(this, POSConstants.SELECT_ITEM_TO_DELETE);
                return;
            }
            if (ConfirmDeleteDialog.showMessage(this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != 0) {
                return;
            }
            Object object = this.discountViewTableModel.get(selectedRow);
            this.modified = this.discountViewTableModel.delete((TicketCoupon)object);
        }
        catch (Exception e) {
            POSMessageDialog.showError(this, Messages.getString("DiscountListDialog.2"), e);
        }
    }

    private void onOK() {
        this.tickets = null;
        this.setCanceled(false);
        this.dispose();
    }

    private void onCancel() {
        this.tickets = null;
        this.setCanceled(true);
        this.dispose();
    }

    private void $$$setupUI$$$() {
        this.contentPane = new JPanel();
        this.contentPane.setLayout((LayoutManager)new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        JPanel panel1 = new JPanel();
        panel1.setLayout((LayoutManager)new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        this.contentPane.add((Component)panel1, new GridConstraints(1, 0, 1, 1, 2, 1, 3, 1, null, null, null, 0, false));
        Spacer spacer1 = new Spacer();
        panel1.add((Component)spacer1, new GridConstraints(0, 0, 1, 1, 0, 1, 4, 1, null, null, null, 0, false));
        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(1, 5, 5));
        panel1.add((Component)panel2, new GridConstraints(0, 1, 1, 1, 0, 3, 3, 3, null, null, null, 0, false));
        this.btnDeleteSelected = new PosButton();
        this.btnDeleteSelected.setIcon(IconFactory.getIcon("/ui_icons/", "delete.png"));
        this.btnDeleteSelected.setPreferredSize(new Dimension(140, 50));
        this.btnDeleteSelected.setText(Messages.getString("DiscountListDialog.5"));
        panel2.add(this.btnDeleteSelected);
        this.buttonOK = new PosButton();
        this.buttonOK.setIcon(IconFactory.getIcon("/ui_icons/", "finish.png"));
        this.buttonOK.setPreferredSize(new Dimension(120, 50));
        this.buttonOK.setText(POSConstants.OK);
        panel2.add(this.buttonOK);
        this.buttonCancel = new PosButton();
        this.buttonCancel.setIcon(IconFactory.getIcon("/ui_icons/", "cancel.png"));
        this.buttonCancel.setPreferredSize(new Dimension(120, 50));
        this.buttonCancel.setText(POSConstants.CANCEL);
        panel2.add(this.buttonCancel);
        JPanel panel3 = new JPanel();
        panel3.setLayout((LayoutManager)new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        this.contentPane.add((Component)panel3, new GridConstraints(0, 0, 1, 1, 0, 3, 5, 5, null, new Dimension(458, 310), null, 0, false));
        JScrollPane scrollPane1 = new JScrollPane();
        panel3.add((Component)scrollPane1, new GridConstraints(0, 0, 1, 1, 0, 3, 5, 5, null, null, null, 0, false));
        this.tableDiscounts = new JTable();
        scrollPane1.setViewportView(this.tableDiscounts);
        JPanel panel4 = new JPanel();
        panel4.setLayout((LayoutManager)new FormLayout("fill:p:grow", "center:d:grow,top:4dlu:noGrow,center:d:grow"));
        panel3.add((Component)panel4, new GridConstraints(0, 1, 1, 1, 0, 3, 5, 5, null, null, null, 0, false));
        this.btnScrollUp = new PosButton();
        this.btnScrollUp.setIcon(IconFactory.getIcon("/ui_icons/", "up.png"));
        this.btnScrollUp.setPreferredSize(new Dimension(50, 50));
        this.btnScrollUp.setText("");
        CellConstraints cc = new CellConstraints();
        panel4.add((Component)this.btnScrollUp, cc.xy(1, 1, CellConstraints.CENTER, CellConstraints.BOTTOM));
        this.btnScrollDown = new PosButton();
        this.btnScrollDown.setIcon(IconFactory.getIcon("/ui_icons/", "down.png"));
        this.btnScrollDown.setPreferredSize(new Dimension(50, 50));
        this.btnScrollDown.setText("");
        panel4.add((Component)this.btnScrollDown, cc.xy(1, 3, CellConstraints.CENTER, CellConstraints.TOP));
    }

    public JComponent $$$getRootComponent$$$() {
        return this.contentPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("scrollUP".equals(e.getActionCommand())) {
            int selectedRow = this.selectionModel.getLeadSelectionIndex();
            selectedRow = selectedRow <= 0 ? 0 : --selectedRow;
            this.selectionModel.setLeadSelectionIndex(selectedRow);
            Rectangle cellRect = this.tableDiscounts.getCellRect(selectedRow, 0, false);
            this.tableDiscounts.scrollRectToVisible(cellRect);
        } else if ("scrollDown".equals(e.getActionCommand())) {
            int selectedRow = this.selectionModel.getLeadSelectionIndex();
            if (selectedRow < 0) {
                selectedRow = 0;
            } else if (selectedRow < this.discountViewTableModel.getRowCount() - 1) {
                ++selectedRow;
            }
            this.selectionModel.setLeadSelectionIndex(selectedRow);
            Rectangle cellRect = this.tableDiscounts.getCellRect(selectedRow, 0, false);
            this.tableDiscounts.scrollRectToVisible(cellRect);
        }
    }

    public boolean isModified() {
        return this.modified;
    }

    class TicketCoupon {
        private Ticket ticket;
        private Object discountObject;

        public TicketCoupon() {
        }

        public TicketCoupon(Ticket ticket, Object discount) {
            this.ticket = ticket;
            this.discountObject = discount;
        }

        public Object getDiscountObject() {
            return this.discountObject;
        }

        public void setDiscountObject(Object discount) {
            this.discountObject = discount;
        }

        public Ticket getTicket() {
            return this.ticket;
        }

        public void setTicket(Ticket ticket) {
            this.ticket = ticket;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof TicketCoupon)) {
                return false;
            }
            TicketCoupon other = (TicketCoupon)obj;
            return this.discountObject.equals(other.discountObject);
        }
    }

    class DiscountViewTableModel
    extends AbstractTableModel {
        String[] columnNames = new String[]{Messages.getString("DiscountListDialog.18"), Messages.getString("DiscountListDialog.19"), Messages.getString("DiscountListDialog.20")};
        ArrayList rows = new ArrayList();

        DiscountViewTableModel() {
            for (Ticket ticket : DiscountListDialog.this.tickets) {
                List<TicketDiscount> coupons = ticket.getDiscounts();
                if (coupons == null) continue;
                for (TicketDiscount coupon : coupons) {
                    TicketCoupon ticketDiscount = new TicketCoupon(ticket, coupon);
                    this.rows.add(ticketDiscount);
                }
            }
        }

        @Override
        public int getRowCount() {
            return this.rows.size();
        }

        @Override
        public int getColumnCount() {
            return this.columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return this.columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            TicketCoupon ticketDiscount = (TicketCoupon)this.rows.get(rowIndex);
            Object discountObject = ticketDiscount.getDiscountObject();
            switch (columnIndex) {
                case 0: {
                    if (discountObject instanceof TicketDiscount) {
                        return ((TicketDiscount)discountObject).getName();
                    }
                    return null;
                }
                case 1: {
                    if (discountObject instanceof TicketDiscount) {
                        return Discount.COUPON_TYPE_NAMES[((TicketDiscount)discountObject).getType()];
                    }
                    return null;
                }
                case 2: {
                    if (discountObject instanceof TicketDiscount) {
                        return NumberUtil.formatNumber(((TicketDiscount)discountObject).getValue());
                    }
                    return null;
                }
            }
            return null;
        }

        public boolean delete(TicketCoupon ticketDiscount) {
            Ticket ticket = ticketDiscount.getTicket();
            Object object = ticketDiscount.getDiscountObject();
            if (object instanceof TicketCoupon) {
                boolean b = ticket.getDiscounts().remove(object);
                this.rows.remove(ticketDiscount);
                this.fireTableDataChanged();
                return b;
            }
            return false;
        }

        public Object get(int index) {
            return this.rows.get(index);
        }
    }
}

