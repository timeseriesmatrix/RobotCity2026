/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.bo.ui.explorer;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.Discount;
import com.floreantpos.model.dao.DiscountDAO;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.model.CouponForm;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class CouponExplorer
extends TransparentPanel
implements ActionListener {
    private JTable explorerView = new JTable();
    private CouponExplorerTableModel explorerModel;

    public CouponExplorer() {
        this.explorerView.setDefaultRenderer(Object.class, new PosTableRenderer());
        this.setLayout(new BorderLayout(5, 5));
        this.add(new JScrollPane(this.explorerView));
        JButton addButton = new JButton(POSConstants.NEW);
        addButton.setActionCommand(POSConstants.ADD);
        addButton.addActionListener(this);
        JButton editButton = new JButton(POSConstants.EDIT);
        editButton.setActionCommand(POSConstants.EDIT);
        editButton.addActionListener(this);
        JButton deleteButton = new JButton(POSConstants.DELETE);
        deleteButton.setActionCommand(POSConstants.DELETE);
        deleteButton.addActionListener(this);
        TransparentPanel panel = new TransparentPanel();
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        this.add((Component)panel, "South");
    }

    public void initData() throws Exception {
        DiscountDAO dao = new DiscountDAO();
        List<Discount> couponList = dao.findAll();
        this.explorerModel = new CouponExplorerTableModel(couponList);
        this.explorerView.setModel(this.explorerModel);
    }

    private void addNewCoupon() {
        try {
            CouponForm editor = new CouponForm();
            BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
            dialog.open();
            if (dialog.isCanceled()) {
                return;
            }
            Discount coupon = (Discount)editor.getBean();
            this.explorerModel.addCoupon(coupon);
        }
        catch (Exception x) {
            BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
        }
    }

    private void editCoupon(Discount coupon) {
        try {
            CouponForm editor = new CouponForm(coupon);
            BeanEditorDialog dialog = new BeanEditorDialog((Frame)POSUtil.getBackOfficeWindow(), (BeanEditor)editor);
            dialog.open();
            if (dialog.isCanceled()) {
                return;
            }
            this.explorerView.repaint();
        }
        catch (Throwable x) {
            BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
        }
    }

    private void deleteCoupon(int index, Discount coupon) {
        try {
            if (ConfirmDeleteDialog.showMessage(this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) == 0) {
                DiscountDAO dao = new DiscountDAO();
                dao.delete(coupon);
                this.explorerModel.deleteCoupon(coupon, index);
            }
        }
        catch (Exception x) {
            BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (POSConstants.ADD.equals(actionCommand)) {
            this.addNewCoupon();
        } else if (POSConstants.EDIT.equals(actionCommand)) {
            int index = this.explorerView.getSelectedRow();
            if (index < 0) {
                BOMessageDialog.showError(POSConstants.SELECT_COUPON_TO_EDIT);
                return;
            }
            Discount coupon = this.explorerModel.getCoupon(index);
            this.editCoupon(coupon);
        } else if (POSConstants.DELETE.equals(actionCommand)) {
            int index = this.explorerView.getSelectedRow();
            if (index < 0) {
                BOMessageDialog.showError(POSConstants.SELECT_COUPON_TO_DELETE);
                return;
            }
            Discount coupon = this.explorerModel.getCoupon(index);
            this.deleteCoupon(index, coupon);
        }
    }

    private class CouponExplorerTableModel
    extends AbstractTableModel {
        String[] columnNames = new String[]{POSConstants.NAME, POSConstants.COUPON_TYPE, POSConstants.COUPON_VALUE, POSConstants.EXPIRY_DATE, POSConstants.ENABLED, POSConstants.NEVER_EXPIRE};
        List<Discount> couponList;

        CouponExplorerTableModel(List<Discount> list) {
            this.couponList = list;
        }

        @Override
        public int getRowCount() {
            if (this.couponList == null) {
                return 0;
            }
            return this.couponList.size();
        }

        @Override
        public int getColumnCount() {
            return this.columnNames.length;
        }

        @Override
        public String getColumnName(int index) {
            return this.columnNames[index];
        }

        @Override
        public Object getValueAt(int row, int column) {
            if (this.couponList == null) {
                return "";
            }
            Discount coupon = this.couponList.get(row);
            switch (column) {
                case 0: {
                    return coupon.getName();
                }
                case 1: {
                    return "";
                }
                case 2: {
                    return (double)coupon.getValue();
                }
                case 3: {
                    return coupon.getExpiryDate();
                }
                case 4: {
                    return (boolean)coupon.isEnabled();
                }
                case 5: {
                    return (boolean)coupon.isNeverExpire();
                }
            }
            return null;
        }

        public void addCoupon(Discount coupon) {
            int size = this.couponList.size();
            this.couponList.add(coupon);
            this.fireTableRowsInserted(size, size);
        }

        public void deleteCoupon(Discount coupon, int index) {
            this.couponList.remove(coupon);
            this.fireTableRowsDeleted(index, index);
        }

        public Discount getCoupon(int index) {
            return this.couponList.get(index);
        }
    }
}

