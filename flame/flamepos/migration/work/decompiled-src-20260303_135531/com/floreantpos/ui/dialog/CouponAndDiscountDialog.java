/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.layout.GroupLayout
 *  org.jdesktop.layout.GroupLayout$Group
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.PosException;
import com.floreantpos.model.Discount;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketDiscount;
import com.floreantpos.model.dao.DiscountDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.NumberUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.layout.GroupLayout;

public class CouponAndDiscountDialog
extends POSDialog
implements ActionListener,
ListSelectionListener {
    private List<Discount> couponList;
    private TicketDiscount ticketCoupon;
    private Ticket ticket;
    private PosButton btnCancel;
    private PosButton btnDown;
    private JButton btnEditValue;
    private PosButton btnOk;
    private PosButton btnUp;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JScrollPane jScrollPane1;
    private JSeparator jSeparator1;
    private JSeparator jSeparator2;
    private JSeparator jSeparator3;
    private JLabel lblTotalDiscount;
    private JList listCoupons;
    private JTextField tfName;
    private JTextField tfNumber;
    private JTextField tfType;
    private JTextField tfValue;
    private TitlePanel titlePanel1;

    public CouponAndDiscountDialog() {
        this.initComponents();
        this.tfValue.getDocument().addDocumentListener(new DocumentListener(){

            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    double totalDiscount = Double.parseDouble(CouponAndDiscountDialog.this.tfValue.getText());
                    CouponAndDiscountDialog.this.lblTotalDiscount.setText(NumberUtil.formatNumber(totalDiscount));
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    double totalDiscount = Double.parseDouble(CouponAndDiscountDialog.this.tfValue.getText());
                    CouponAndDiscountDialog.this.lblTotalDiscount.setText(NumberUtil.formatNumber(totalDiscount));
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    double totalDiscount = Double.parseDouble(CouponAndDiscountDialog.this.tfValue.getText());
                    CouponAndDiscountDialog.this.lblTotalDiscount.setText(NumberUtil.formatNumber(totalDiscount));
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        });
        this.lblTotalDiscount.setText("");
        this.btnEditValue.setEnabled(false);
        this.btnUp.setActionCommand("scrollUP");
        this.btnDown.setActionCommand("scrollDown");
        this.btnUp.addActionListener(this);
        this.btnDown.addActionListener(this);
        this.listCoupons.addListSelectionListener(this);
        this.listCoupons.setCellRenderer(new CouponListRenderer());
        this.ticketCoupon = new TicketDiscount();
    }

    private void initComponents() {
        this.titlePanel1 = new TitlePanel();
        this.jScrollPane1 = new JScrollPane();
        this.listCoupons = new JList();
        this.btnCancel = new PosButton();
        this.btnOk = new PosButton();
        this.jSeparator1 = new JSeparator();
        this.btnUp = new PosButton();
        this.btnDown = new PosButton();
        this.jSeparator2 = new JSeparator();
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.jLabel3 = new JLabel();
        this.jLabel4 = new JLabel();
        this.tfName = new JTextField();
        this.tfNumber = new JTextField();
        this.tfType = new JTextField();
        this.tfValue = new JTextField();
        this.btnEditValue = new JButton();
        this.jSeparator3 = new JSeparator();
        this.jLabel5 = new JLabel();
        this.lblTotalDiscount = new JLabel();
        this.setDefaultCloseOperation(2);
        this.titlePanel1.setTitle(Messages.getString("CouponAndDiscountDialog.3"));
        this.jScrollPane1.setViewportView(this.listCoupons);
        this.btnCancel.setIcon(IconFactory.getIcon("/ui_icons/", "cancel.png"));
        this.btnCancel.setText(Messages.getString("CouponAndDiscountDialog.6"));
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                CouponAndDiscountDialog.this.doCancel(evt);
            }
        });
        this.btnOk.setIcon(IconFactory.getIcon("/ui_icons/", "finish.png"));
        this.btnOk.setText(Messages.getString("CouponAndDiscountDialog.9"));
        this.btnOk.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                CouponAndDiscountDialog.this.doOk(evt);
            }
        });
        this.btnUp.setIcon(IconFactory.getIcon("/ui_icons/", "up.png"));
        this.btnDown.setIcon(IconFactory.getIcon("/ui_icons/", "down.png"));
        this.jSeparator2.setOrientation(1);
        this.jLabel1.setText(Messages.getString("CouponAndDiscountDialog.14") + ":");
        this.jLabel2.setText(Messages.getString("CouponAndDiscountDialog.16") + ":");
        this.jLabel3.setText(Messages.getString("CouponAndDiscountDialog.18") + ":");
        this.jLabel4.setText(Messages.getString("CouponAndDiscountDialog.20") + ":");
        this.tfName.setEditable(false);
        this.tfNumber.setEditable(false);
        this.tfType.setEditable(false);
        this.tfValue.setEditable(false);
        this.btnEditValue.setText(Messages.getString("CouponAndDiscountDialog.22"));
        this.btnEditValue.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                CouponAndDiscountDialog.this.doEnterValue(evt);
            }
        });
        this.jLabel5.setFont(new Font("Tahoma", 1, 18));
        this.jLabel5.setHorizontalAlignment(0);
        this.jLabel5.setText(Messages.getString("CouponAndDiscountDialog.24"));
        this.lblTotalDiscount.setFont(new Font("Tahoma", 1, 18));
        this.lblTotalDiscount.setForeground(new Color(204, 51, 0));
        this.lblTotalDiscount.setHorizontalAlignment(0);
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout((LayoutManager)layout);
        layout.setHorizontalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jSeparator1, -1, 642, Short.MAX_VALUE).add((Component)this.titlePanel1, -1, 642, Short.MAX_VALUE).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jScrollPane1, -2, 216, -2).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.btnUp, -2, -1, -2).add((Component)this.btnDown, -2, -1, -2)).addPreferredGap(0).add((Component)this.jSeparator2, -2, -1, -2).add((GroupLayout.Group)layout.createParallelGroup(1).add(2, (GroupLayout.Group)layout.createSequentialGroup().addPreferredGap(0).add((Component)this.btnOk, -2, 114, -2).addPreferredGap(0).add((Component)this.btnCancel, -2, 117, -2)).add((GroupLayout.Group)layout.createSequentialGroup().add(17, 17, 17).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel4).add((Component)this.jLabel3).add((Component)this.jLabel2).add((Component)this.jLabel1)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.tfValue, -1, 130, Short.MAX_VALUE).addPreferredGap(0).add((Component)this.btnEditValue)).add((Component)this.tfType, -1, 225, Short.MAX_VALUE).add((Component)this.tfNumber, -1, 225, Short.MAX_VALUE).add((Component)this.tfName, -1, 225, Short.MAX_VALUE))).add((GroupLayout.Group)layout.createSequentialGroup().add(18, 18, 18).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel5, -1, 354, Short.MAX_VALUE).add((Component)this.jSeparator3, -1, 354, Short.MAX_VALUE).add((Component)this.lblTotalDiscount, -1, 354, Short.MAX_VALUE)))))).addContainerGap()));
        layout.setVerticalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((Component)this.titlePanel1, -2, -1, -2).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jScrollPane1, -1, 323, Short.MAX_VALUE).add((Component)this.jSeparator2, -1, 323, Short.MAX_VALUE).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel1).add((Component)this.tfName, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel2).add((Component)this.tfNumber, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel3).add((Component)this.tfType, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel4).add((Component)this.btnEditValue).add((Component)this.tfValue, -2, -1, -2)).addPreferredGap(0).add((Component)this.jSeparator3, -2, 10, -2).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(5, 5, 5).add((Component)this.btnUp, -2, -1, -2).addPreferredGap(0).add((Component)this.btnDown, -2, -1, -2)).add((GroupLayout.Group)layout.createSequentialGroup().addPreferredGap(0).add((Component)this.jLabel5).addPreferredGap(0).add((Component)this.lblTotalDiscount))))).addPreferredGap(0).add((Component)this.jSeparator1, -2, 10, -2).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(2).add((Component)this.btnCancel, -2, -1, -2).add((Component)this.btnOk, -2, -1, -2)).addContainerGap()));
        this.pack();
    }

    public TicketDiscount getSelectedCoupon() {
        try {
            double parseDouble = NumberUtil.parse(this.tfValue.getText()).doubleValue();
            this.ticketCoupon.setValue(parseDouble);
        }
        catch (Exception x) {
            throw new PosException(Messages.getString("CouponAndDiscountDialog.27"));
        }
        return this.ticketCoupon;
    }

    private void doEnterValue(ActionEvent evt) {
        NumberSelectionDialog2 dialog = new NumberSelectionDialog2();
        dialog.setFloatingPoint(true);
        dialog.setTitle(Messages.getString("CouponAndDiscountDialog.28"));
        dialog.pack();
        dialog.open();
        if (!dialog.isCanceled()) {
            double value = dialog.getValue();
            this.tfValue.setText(NumberUtil.formatNumber(value));
        }
    }

    private void doOk(ActionEvent evt) {
        try {
            TicketDiscount selectedCoupon = this.getSelectedCoupon();
            if (selectedCoupon == null) {
                POSMessageDialog.showError(this, Messages.getString("CouponAndDiscountDialog.29"));
                return;
            }
            this.setCanceled(false);
            this.dispose();
        }
        catch (PosException e) {
            POSMessageDialog.showError(this, e.getMessage());
        }
    }

    private void doCancel(ActionEvent evt) {
        this.setCanceled(true);
        this.dispose();
    }

    public void initData() throws Exception {
        DiscountDAO dao = new DiscountDAO();
        this.couponList = dao.getValidCoupons();
        this.listCoupons.setModel(new CouponListModel());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("scrollUP".equals(e.getActionCommand())) {
            if (this.couponList == null || this.couponList.size() == 0) {
                return;
            }
            int selectedRow = this.listCoupons.getSelectedIndex();
            selectedRow = selectedRow <= 0 ? 0 : --selectedRow;
            this.listCoupons.setSelectedIndex(selectedRow);
            Rectangle cellRect = this.listCoupons.getCellBounds(selectedRow, selectedRow);
            this.listCoupons.scrollRectToVisible(cellRect);
        } else if ("scrollDown".equals(e.getActionCommand())) {
            if (this.couponList == null || this.couponList.size() == 0) {
                return;
            }
            int selectedRow = this.listCoupons.getSelectedIndex();
            if (selectedRow < 0) {
                selectedRow = 0;
            } else if (selectedRow < this.couponList.size() - 1) {
                ++selectedRow;
            }
            this.listCoupons.setSelectedIndex(selectedRow);
            Rectangle cellRect = this.listCoupons.getCellBounds(selectedRow, selectedRow);
            cellRect.y += 20;
            this.listCoupons.scrollRectToVisible(cellRect);
        }
    }

    public void updateCouponView(Discount coupon) {
        if (coupon == null) {
            this.tfName.setText("");
            this.tfNumber.setText("");
            this.tfType.setText("");
            this.tfValue.setText("");
            return;
        }
        this.btnEditValue.setEnabled(false);
        this.tfName.setText(coupon.getName());
        if (coupon.getType() == 0) {
            this.btnEditValue.setEnabled(true);
        }
        this.tfNumber.setText(String.valueOf(coupon.getId()));
        this.tfType.setText(Discount.COUPON_TYPE_NAMES[coupon.getType()]);
        this.tfValue.setText(NumberUtil.formatNumber(coupon.getValue()));
        double totalDiscount = 0.0;
        double subtotal = this.ticket.getSubtotalAmount();
        this.ticketCoupon.setDiscountId(coupon.getId());
        this.ticketCoupon.setName(coupon.getName());
        this.ticketCoupon.setType(coupon.getType());
        this.ticketCoupon.setValue(coupon.getValue());
        totalDiscount = this.ticket.calculateDiscountFromType(this.ticketCoupon, subtotal);
        this.ticketCoupon.setValue(totalDiscount);
        this.lblTotalDiscount.setText(NumberUtil.formatNumber(totalDiscount));
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Discount coupon = (Discount)this.listCoupons.getSelectedValue();
        this.updateCouponView(coupon);
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    class CouponListRenderer
    extends DefaultListCellRenderer {
        CouponListRenderer() {
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Discount coupon = (Discount)value;
            return super.getListCellRendererComponent((JList<?>)list, coupon.getName(), index, isSelected, cellHasFocus);
        }
    }

    class CouponListModel
    extends AbstractListModel {
        CouponListModel() {
        }

        @Override
        public int getSize() {
            if (CouponAndDiscountDialog.this.couponList == null) {
                return 0;
            }
            return CouponAndDiscountDialog.this.couponList.size();
        }

        @Override
        public Object getElementAt(int index) {
            return CouponAndDiscountDialog.this.couponList.get(index);
        }
    }
}

