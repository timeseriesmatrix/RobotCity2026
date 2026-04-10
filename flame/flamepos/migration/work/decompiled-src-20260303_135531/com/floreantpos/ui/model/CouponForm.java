/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jdesktop.swingx.JXDatePicker
 */
package com.floreantpos.ui.model;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.model.Discount;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.dao.DiscountDAO;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.ItemCheckBoxList;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.ItemSelectionDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;

public class CouponForm
extends BeanEditor
implements ItemListener {
    private JPanel contentPane;
    private JPanel itemPanel;
    private FixedLengthTextField tfCouponName;
    private FixedLengthTextField tfBarcode;
    private JComboBox cbQualificationType;
    private JComboBox cbCouponType;
    private DoubleTextField tfCouponValue;
    private JCheckBox chkEnabled;
    private JCheckBox chkModifiable;
    private JCheckBox chkAutoApply;
    private JCheckBox chkNeverExpire;
    private JXDatePicker dpExperation;
    private JLabel lblMinimum;
    private DoubleTextField tfMinimumQua;
    private JPanel itemSearchPanel;
    private JTextField txtSearchItem;
    private JScrollPane itemScrollPane;
    private ItemCheckBoxList cbListItems;
    private ItemCheckBoxList addedListItems;
    private String uuid;

    public CouponForm() {
        this(new Discount());
    }

    public CouponForm(Discount coupon) {
        this.initializeComponent();
        this.cbCouponType.setModel(new DefaultComboBoxModel<String>(Discount.COUPON_TYPE_NAMES));
        this.cbQualificationType.setModel(new DefaultComboBoxModel<String>(Discount.COUPON_QUALIFICATION_NAMES));
        this.cbQualificationType.addItemListener(this);
        this.cbCouponType.addItemListener(this);
        this.setBean(coupon);
    }

    private void initializeComponent() {
        this.setLayout(new BorderLayout(10, 10));
        this.contentPane = new JPanel();
        this.contentPane.setLayout((LayoutManager)new MigLayout());
        this.contentPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null));
        this.contentPane.setPreferredSize(new Dimension(400, 0));
        JLabel label1 = new JLabel(Messages.getString("CouponForm.0") + ":");
        JLabel label2 = new JLabel(Messages.getString("CouponForm.9") + ":");
        JLabel label3 = new JLabel(Messages.getString("CouponForm.11") + ":");
        JLabel label4 = new JLabel(Messages.getString("CouponForm.13") + ":");
        JLabel label6 = new JLabel(Messages.getString("CouponForm.12"));
        JLabel label5 = new JLabel(Messages.getString("CouponForm.7"));
        this.lblMinimum = new JLabel(Messages.getString("CouponForm.5"));
        this.tfCouponName = new FixedLengthTextField(120);
        this.tfBarcode = new FixedLengthTextField(120);
        this.cbCouponType = new JComboBox();
        this.cbQualificationType = new JComboBox();
        this.dpExperation = new JXDatePicker();
        this.tfCouponValue = new DoubleTextField();
        this.tfMinimumQua = new DoubleTextField();
        this.chkEnabled = new JCheckBox(POSConstants.ENABLED);
        this.chkModifiable = new JCheckBox("Modifiable Amount");
        this.chkAutoApply = new JCheckBox(Messages.getString("CouponForm.6"));
        this.chkNeverExpire = new JCheckBox(Messages.getString("CouponForm.16"));
        this.contentPane.add(label1);
        this.contentPane.add((Component)this.tfCouponName, "grow, wrap");
        this.contentPane.add(label2);
        this.contentPane.add((Component)this.dpExperation, "grow, wrap");
        this.contentPane.add(label3);
        this.contentPane.add((Component)this.cbCouponType, "grow, wrap");
        this.contentPane.add(label6);
        this.contentPane.add((Component)this.tfBarcode, "grow, wrap");
        this.contentPane.add(label5);
        this.contentPane.add((Component)this.cbQualificationType, "grow, wrap");
        this.contentPane.add(this.lblMinimum);
        this.contentPane.add((Component)this.tfMinimumQua, "grow, wrap");
        this.contentPane.add(label4);
        this.contentPane.add((Component)this.tfCouponValue, "grow, wrap");
        this.contentPane.add(new JLabel(""));
        this.contentPane.add((Component)this.chkEnabled, "wrap");
        this.contentPane.add(new JLabel(""));
        this.contentPane.add((Component)this.chkAutoApply, "wrap");
        this.contentPane.add(new JLabel(""));
        this.contentPane.add((Component)this.chkNeverExpire, "wrap");
        this.contentPane.add(new JLabel(""));
        this.contentPane.add((Component)this.chkModifiable, "wrap");
        this.createItemSearchPanel();
        this.itemPanel = new JPanel(new BorderLayout(10, 10));
        this.itemPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null));
        this.cbListItems = new ItemCheckBoxList();
        List<MenuItem> menuItems = MenuItemDAO.getInstance().findAll();
        this.cbListItems.setModel((List)menuItems);
        this.addedListItems = new ItemCheckBoxList();
        this.addedListItems.setModel(this.cbListItems.getCheckedValues());
        this.itemPanel.add((Component)this.itemSearchPanel, "North");
        this.itemScrollPane = new JScrollPane(this.addedListItems);
        this.itemPanel.add((Component)this.itemScrollPane, "Center");
        this.add((Component)this.contentPane, "West");
        this.add((Component)this.itemPanel, "Center");
        this.setPreferredSize(new Dimension(700, 350));
    }

    private void createItemSearchPanel() {
        this.itemSearchPanel = new JPanel();
        this.itemSearchPanel.setLayout(new BorderLayout(5, 5));
        PosButton btnSearch = new PosButton(POSConstants.ADD);
        btnSearch.setPreferredSize(new Dimension(60, 40));
        this.txtSearchItem = new JTextField();
        this.txtSearchItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (CouponForm.this.txtSearchItem.getText().equals("")) {
                    POSMessageDialog.showMessage(Messages.getString("CouponForm.8"));
                    return;
                }
                if (!CouponForm.this.addMenuItemByBarcode(CouponForm.this.txtSearchItem.getText())) {
                    CouponForm.this.addMenuItemByItemId(CouponForm.this.txtSearchItem.getText());
                }
                CouponForm.this.txtSearchItem.setText("");
            }
        });
        btnSearch.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ItemSelectionDialog dialog = new ItemSelectionDialog();
                dialog.setModel(CouponForm.this.cbListItems.getModel());
                dialog.open();
                if (dialog.isCanceled()) {
                    return;
                }
                CouponForm.this.cbListItems.setModel(dialog.getModel());
                CouponForm.this.addedListItems.setModel(CouponForm.this.cbListItems.getCheckedValues());
                CouponForm.this.addedListItems.selectItems(CouponForm.this.cbListItems.getCheckedValues());
                CouponForm.this.txtSearchItem.requestFocus();
            }
        });
        this.itemSearchPanel.add(this.txtSearchItem);
        this.itemSearchPanel.add((Component)btnSearch, "East");
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getItem() == Discount.COUPON_QUALIFICATION_NAMES[0]) {
            List<MenuItem> menuItems = MenuItemDAO.getInstance().findAll();
            this.itemPanel.setVisible(true);
            this.cbListItems.setModel((List)menuItems);
            this.addedListItems.setModel(this.cbListItems.getCheckedValues());
        } else if (event.getItem() == Discount.COUPON_TYPE_NAMES[0]) {
            this.chkModifiable.setVisible(true);
        } else if (event.getItem() == Discount.COUPON_TYPE_NAMES[1]) {
            this.chkModifiable.setVisible(false);
        } else {
            this.itemPanel.setVisible(false);
        }
    }

    private boolean addMenuItemByBarcode(String barcode) {
        MenuItemDAO dao = new MenuItemDAO();
        MenuItem menuItem = dao.getMenuItemByBarcode(barcode);
        return menuItem != null;
    }

    private boolean addMenuItemByItemId(String id) {
        Integer itemId = Integer.parseInt(id);
        MenuItem menuItem = MenuItemDAO.getInstance().get(itemId);
        if (menuItem == null) {
            return false;
        }
        this.cbListItems.setSelected(menuItem);
        this.addedListItems.setModel(this.cbListItems.getCheckedValues());
        this.addedListItems.selectItems(this.cbListItems.getCheckedValues());
        return true;
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
            Discount coupon = (Discount)this.getBean();
            DiscountDAO.getInstance().saveOrUpdate(coupon);
        }
        catch (Exception e) {
            MessageDialog.showError(POSConstants.SAVE_ERROR, e);
            return false;
        }
        return true;
    }

    @Override
    protected void updateView() {
        Discount coupon = (Discount)this.getBean();
        if (coupon.getId() == null) {
            this.chkEnabled.setSelected(true);
            this.tfMinimumQua.setText("0");
            this.cbCouponType.setSelectedIndex(1);
            return;
        }
        this.uuid = coupon.getUUID();
        this.tfCouponName.setText(coupon.getName());
        this.tfMinimumQua.setText(coupon.getMinimunBuy().toString());
        this.tfCouponValue.setText(String.valueOf(coupon.getValue()));
        this.cbCouponType.setSelectedIndex(coupon.getType());
        this.cbQualificationType.setSelectedIndex(coupon.getQualificationType());
        this.dpExperation.setDate(coupon.getExpiryDate());
        this.tfBarcode.setText(coupon.getBarcode());
        this.chkEnabled.setSelected(coupon.isEnabled());
        this.chkModifiable.setSelected(coupon.isModifiable());
        this.chkAutoApply.setSelected(coupon.isAutoApply());
        this.chkNeverExpire.setSelected(coupon.isNeverExpire());
        if (coupon.getQualificationType() == 0) {
            this.cbListItems.selectItems(coupon.getMenuItems());
            this.addedListItems.setModel(this.cbListItems.getCheckedValues());
            this.addedListItems.selectItems(this.cbListItems.getCheckedValues());
        }
    }

    @Override
    protected boolean updateModel() {
        String name = this.tfCouponName.getText();
        String barcode = this.tfBarcode.getText();
        double couponValue = 0.0;
        couponValue = this.tfCouponValue.getDouble();
        int couponMinimumQua = Integer.parseInt(this.tfMinimumQua.getText());
        int couponType = this.cbCouponType.getSelectedIndex();
        Date expiryDate = this.dpExperation.getDate();
        boolean enabled = this.chkEnabled.isSelected();
        boolean modifiable = this.chkModifiable.isSelected();
        boolean autoApply = this.chkAutoApply.isSelected();
        boolean neverExpire = this.chkNeverExpire.isSelected();
        int qualificationType = this.cbQualificationType.getSelectedIndex();
        if (name == null || name.trim().equals("")) {
            POSMessageDialog.showError(null, Messages.getString("CouponForm.1"));
            return false;
        }
        if (couponValue <= 0.0) {
            POSMessageDialog.showError(null, Messages.getString("CouponForm.2"));
            return false;
        }
        if (qualificationType == 0 && this.couponValueOverflow()) {
            POSMessageDialog.showError(null, Messages.getString("CouponForm.10"));
            return false;
        }
        Discount coupon = (Discount)this.getBean();
        coupon.setName(name);
        coupon.setMinimunBuy(couponMinimumQua);
        coupon.setValue(couponValue);
        coupon.setExpiryDate(expiryDate);
        coupon.setBarcode(barcode);
        coupon.setType(couponType);
        coupon.setQualificationType(qualificationType);
        coupon.setEnabled(enabled);
        coupon.setModifiable(modifiable);
        coupon.setAutoApply(autoApply);
        coupon.setNeverExpire(neverExpire);
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
        coupon.setUUID(this.uuid);
        if (qualificationType == 0) {
            if (this.addedListItems.getCheckedValues().size() > 0) {
                coupon.setMenuItems(this.addedListItems.getCheckedValues());
                coupon.setApplyToAll(false);
            } else {
                coupon.setApplyToAll(true);
            }
        }
        return true;
    }

    private boolean couponValueOverflow() {
        List menuItems = this.addedListItems.getCheckedValues();
        double couponValue = Double.parseDouble(this.tfCouponValue.getText());
        if (this.cbCouponType.getSelectedIndex() == 1) {
            couponValue /= 100.0;
        }
        if (Integer.parseInt(this.tfMinimumQua.getText()) > 0) {
            int minimumQua = Integer.parseInt(this.tfMinimumQua.getText());
            for (MenuItem menuItem : menuItems) {
                if (!(couponValue > menuItem.getPrice() * (double)minimumQua)) continue;
                return true;
            }
        } else {
            for (MenuItem menuItem : menuItems) {
                if (!(couponValue > menuItem.getPrice())) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDisplayText() {
        Discount coupon = (Discount)this.getBean();
        if (coupon.getId() == null) {
            return Messages.getString("CouponForm.3");
        }
        return Messages.getString("CouponForm.4");
    }
}

