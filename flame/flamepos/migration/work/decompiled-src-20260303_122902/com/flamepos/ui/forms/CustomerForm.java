/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang.StringUtils
 *  org.hibernate.StaleObjectStateException
 */
package com.floreantpos.ui.forms;

import com.floreantpos.Messages;
import com.floreantpos.PosLog;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.model.Customer;
import com.floreantpos.model.dao.CustomerDAO;
import com.floreantpos.model.util.IllegalModelStateException;
import com.floreantpos.swing.DoubleTextField;
import com.floreantpos.swing.FixedLengthTextField;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.swing.PosSmallButton;
import com.floreantpos.swing.QwertyKeyPad;
import com.floreantpos.ui.BeanEditor;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import com.floreantpos.util.PosGuiUtil;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.StaleObjectStateException;

public class CustomerForm
extends BeanEditor<Customer> {
    static MyOwnFocusTraversalPolicy newPolicy;
    private FixedLengthTextField tfLoyaltyNo;
    private JTextField tfAddress;
    private FixedLengthTextField tfCity;
    private FixedLengthTextField tfZip;
    private FixedLengthTextField tfCountry;
    private DoubleTextField tfCreditLimit;
    private JCheckBox cbVip;
    private FixedLengthTextField tfFirstName;
    private FixedLengthTextField tfLastName;
    private FixedLengthTextField tfEmail;
    private JLabel lblDob;
    private FixedLengthTextField tfDoB;
    private JLabel lblLoyaltyPoint;
    private IntegerTextField tfLoyaltyPoint;
    private JLabel lblPicture;
    private JPanel picturePanel;
    private PosSmallButton btnSelectImage;
    private PosSmallButton btnClearImage;
    private BufferedImage image;
    private JComboBox cbSalutation;
    private JLabel lblHomePhone;
    private JLabel lblWorkPhone;
    private JLabel lblMobile;
    private JLabel lblSocialSecurityNumber;
    private FixedLengthTextField tfHomePhone;
    private FixedLengthTextField tfWorkPhone;
    private IntegerTextField tfMobile;
    private FixedLengthTextField tfSocialSecurityNumber;
    private QwertyKeyPad qwertyKeyPad;
    public boolean isKeypad;

    public CustomerForm() {
        this.createCustomerForm();
    }

    public CustomerForm(boolean enable) {
        this.isKeypad = enable;
        this.createCustomerForm();
    }

    private void createCustomerForm() {
        this.setOpaque(true);
        this.setLayout((LayoutManager)new MigLayout("", "[][][grow][][grow]", "[][][][][][][][][][][][][][][][][]"));
        this.picturePanel = new JPanel((LayoutManager)new MigLayout());
        this.lblPicture = new JLabel("");
        this.lblPicture.setIconTextGap(0);
        this.lblPicture.setHorizontalAlignment(0);
        this.picturePanel.setBorder(new TitledBorder(null, Messages.getString("CustomerForm.10"), 4, 2, null, null));
        this.picturePanel.add((Component)this.lblPicture, "wrap,center");
        this.btnSelectImage = new PosSmallButton();
        this.btnSelectImage.setText(Messages.getString("CustomerForm.44"));
        this.picturePanel.add((Component)this.btnSelectImage, "split 2");
        this.btnClearImage = new PosSmallButton();
        this.btnClearImage.setText(Messages.getString("CustomerForm.45"));
        this.picturePanel.add(this.btnClearImage);
        this.add((Component)this.picturePanel, "cell 0 0 0 8");
        JLabel lblSalutation = new JLabel(Messages.getString("CustomerForm.0"));
        this.add((Component)lblSalutation, "cell 1 0,right");
        this.cbSalutation = new JComboBox();
        this.cbSalutation.addItem(Messages.getString("CustomerForm.2"));
        this.cbSalutation.addItem(Messages.getString("CustomerForm.4"));
        this.cbSalutation.addItem(Messages.getString("CustomerForm.5"));
        this.cbSalutation.addItem(Messages.getString("CustomerForm.6"));
        this.cbSalutation.setPreferredSize(new Dimension(100, 0));
        this.add((Component)this.cbSalutation, "cell 2 0,grow");
        JLabel lblFirstName = new JLabel(Messages.getString("CustomerForm.3"));
        this.add((Component)lblFirstName, "cell 1 1,right ");
        this.tfFirstName = new FixedLengthTextField(30);
        this.add((Component)this.tfFirstName, "cell 2 1,grow");
        JLabel lblLastName = new JLabel(Messages.getString("CustomerForm.11"));
        this.add((Component)lblLastName, "cell 1 2,right");
        this.tfLastName = new FixedLengthTextField();
        this.add((Component)this.tfLastName, "cell 2 2,grow");
        this.lblDob = new JLabel("DoB (MM-DD-YYYY)");
        this.add((Component)this.lblDob, "cell 1 3,right");
        this.tfDoB = new FixedLengthTextField();
        this.add((Component)this.tfDoB, "cell 2 3,grow");
        JLabel lblAddress = new JLabel(Messages.getString("CustomerForm.18"));
        this.add((Component)lblAddress, "cell 1 4,right");
        this.tfAddress = new JTextField();
        this.add((Component)this.tfAddress, "cell 2 4,grow");
        JLabel lblZip = new JLabel(Messages.getString("CustomerForm.21"));
        this.add((Component)lblZip, "cell 1 5,right");
        this.tfZip = new FixedLengthTextField();
        this.add((Component)this.tfZip, "cell 2 5,grow");
        this.lblSocialSecurityNumber = new JLabel(Messages.getString("CustomerForm.22"));
        this.add((Component)this.lblSocialSecurityNumber, "cell 3 0,right");
        this.tfSocialSecurityNumber = new FixedLengthTextField();
        this.add((Component)this.tfSocialSecurityNumber, "cell 4 0,grow");
        JLabel lblCitytown = new JLabel(Messages.getString("CustomerForm.24"));
        this.add((Component)lblCitytown, "cell 3 1,right");
        this.tfCity = new FixedLengthTextField();
        this.add((Component)this.tfCity, "cell 4 1,grow");
        JLabel lblCountry = new JLabel(Messages.getString("CustomerForm.27"));
        this.add((Component)lblCountry, "cell 3 2,right");
        this.tfCountry = new FixedLengthTextField();
        this.tfCountry.setText(Messages.getString("CustomerForm.29"));
        this.add((Component)this.tfCountry, "cell 4 2,grow");
        this.lblMobile = new JLabel(Messages.getString("CustomerForm.32"));
        this.add((Component)this.lblMobile, "cell 3 3 ,right");
        this.tfMobile = new IntegerTextField(10);
        this.add((Component)this.tfMobile, "cell 4 3,grow");
        this.lblHomePhone = new JLabel("Home Phone");
        this.add((Component)this.lblHomePhone, "cell 3 4,right");
        this.tfHomePhone = new FixedLengthTextField();
        this.add((Component)this.tfHomePhone, "cell 4 4,grow");
        this.lblWorkPhone = new JLabel(Messages.getString("CustomerForm.39"));
        this.add((Component)this.lblWorkPhone, "cell 3 5,right");
        this.tfWorkPhone = new FixedLengthTextField();
        this.add((Component)this.tfWorkPhone, "cell 4 5,grow");
        JLabel lblEmail = new JLabel(Messages.getString("CustomerForm.15"));
        this.add((Component)lblEmail, "cell 3 6 ,right");
        this.tfEmail = new FixedLengthTextField();
        this.add((Component)this.tfEmail, "cell 4 6,grow");
        this.lblLoyaltyPoint = new JLabel(Messages.getString("CustomerForm.34"));
        this.add((Component)this.lblLoyaltyPoint, "cell 3 7,right");
        this.tfLoyaltyPoint = new IntegerTextField();
        this.add((Component)this.tfLoyaltyPoint, "cell 4 7,grow");
        this.cbVip = new JCheckBox(Messages.getString("CustomerForm.41"));
        this.cbVip.setFocusable(false);
        this.add((Component)this.cbVip, "cell 4 8,wrap");
        JLabel lblLoyaltyNo = new JLabel(Messages.getString("CustomerForm.31"));
        this.add((Component)lblLoyaltyNo, "cell 1 6,right");
        this.tfLoyaltyNo = new FixedLengthTextField();
        this.tfLoyaltyNo.setLength(8);
        this.add((Component)this.tfLoyaltyNo, "cell 2 6,grow");
        JLabel lblCreditLimit = new JLabel(Messages.getString("CustomerForm.37"));
        this.add((Component)lblCreditLimit, "cell 1 7,right");
        this.tfCreditLimit = new DoubleTextField();
        this.tfCreditLimit.setText("500.00");
        this.add((Component)this.tfCreditLimit, "cell 2 7,grow");
        this.qwertyKeyPad = new QwertyKeyPad();
        if (this.isKeypad) {
            this.add((Component)((Object)this.qwertyKeyPad), "cell 0 10 5 5,grow");
        }
        this.btnSelectImage.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    BufferedImage tmpImage = PosGuiUtil.selectImageFile();
                    if (tmpImage != null) {
                        CustomerForm.this.image = tmpImage;
                    }
                    if (CustomerForm.this.image == null) {
                        return;
                    }
                    ImageIcon imageIcon = new ImageIcon(CustomerForm.this.image);
                    CustomerForm.this.lblPicture.setIcon(imageIcon);
                }
                catch (Exception e1) {
                    PosLog.error(this.getClass(), e1);
                }
            }
        });
        this.btnClearImage.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                CustomerForm.this.setDefaultCustomerPicture();
            }
        });
        this.setDefaultCustomerPicture();
        this.enableCustomerFields(false);
        this.callOrderController();
    }

    public void callOrderController() {
        Vector<Component> order = new Vector<Component>();
        order.add(this.tfFirstName);
        order.add(this.tfLastName);
        order.add(this.tfDoB);
        order.add(this.tfAddress);
        order.add(this.tfZip);
        order.add(this.tfLoyaltyNo);
        order.add(this.tfCreditLimit);
        order.add(this.tfSocialSecurityNumber);
        order.add(this.tfCity);
        order.add(this.tfCountry);
        order.add(this.tfMobile);
        order.add(this.tfHomePhone);
        order.add(this.tfWorkPhone);
        order.add(this.tfEmail);
        order.add(this.tfLoyaltyPoint);
        newPolicy = new MyOwnFocusTraversalPolicy(order);
        this.setFocusCycleRoot(true);
        this.setFocusTraversalPolicy(newPolicy);
    }

    public void enableCustomerFields(boolean enable) {
        this.cbSalutation.setEnabled(enable);
        this.tfLastName.setEnabled(enable);
        this.tfFirstName.setEnabled(enable);
        this.tfEmail.setEnabled(enable);
        this.tfLoyaltyNo.setEnabled(enable);
        this.tfAddress.setEnabled(enable);
        this.tfCity.setEnabled(enable);
        this.tfCreditLimit.setEnabled(enable);
        this.tfZip.setEnabled(enable);
        this.tfCountry.setEnabled(enable);
        this.cbVip.setEnabled(enable);
        this.tfDoB.setEnabled(enable);
        this.btnClearImage.setEnabled(enable);
        this.btnSelectImage.setEnabled(enable);
        this.tfLoyaltyPoint.setEnabled(enable);
        this.tfHomePhone.setEnabled(enable);
        this.tfWorkPhone.setEnabled(enable);
        this.tfMobile.setEnabled(enable);
        this.tfSocialSecurityNumber.setEnabled(enable);
    }

    @Override
    public void setFieldsEnable(boolean enable) {
        this.cbSalutation.setEnabled(enable);
        this.tfFirstName.setEnabled(enable);
        this.tfLastName.setEnabled(enable);
        this.tfEmail.setEnabled(enable);
        this.tfLoyaltyNo.setEnabled(enable);
        this.tfAddress.setEnabled(enable);
        this.tfCity.setEnabled(enable);
        this.tfCreditLimit.setEnabled(enable);
        this.tfZip.setEnabled(enable);
        this.tfCountry.setEnabled(enable);
        this.cbVip.setEnabled(enable);
        this.tfDoB.setEnabled(enable);
        this.btnClearImage.setEnabled(enable);
        this.btnSelectImage.setEnabled(enable);
        this.tfLoyaltyPoint.setEnabled(enable);
        this.tfHomePhone.setEnabled(enable);
        this.tfWorkPhone.setEnabled(enable);
        this.tfMobile.setEnabled(enable);
        this.tfSocialSecurityNumber.setEnabled(enable);
    }

    public void setFieldsEditable(boolean editable) {
        this.cbSalutation.setEditable(editable);
        this.tfFirstName.setEditable(editable);
        this.tfLastName.setEditable(editable);
        this.tfEmail.setEditable(editable);
        this.tfLoyaltyNo.setEditable(editable);
        this.tfAddress.setEditable(editable);
        this.tfCity.setEditable(editable);
        this.tfCreditLimit.setEditable(editable);
        this.tfZip.setEditable(editable);
        this.tfCountry.setEditable(editable);
        this.cbVip.setEnabled(editable);
        this.tfDoB.setEditable(editable);
        this.btnClearImage.setEnabled(editable);
        this.btnSelectImage.setEnabled(editable);
        this.tfLoyaltyPoint.setEditable(editable);
        this.tfHomePhone.setEditable(editable);
        this.tfWorkPhone.setEditable(editable);
        this.tfMobile.setEditable(editable);
        this.tfSocialSecurityNumber.setEditable(editable);
    }

    @Override
    public void createNew() {
        this.setBean(new Customer());
        this.tfFirstName.setText("");
        this.tfLastName.setText("");
        this.cbSalutation.setSelectedIndex(0);
        this.tfDoB.setText("");
        this.tfAddress.setText("");
        this.tfCity.setText("");
        this.tfCountry.setText("");
        this.tfCreditLimit.setText("");
        this.tfEmail.setText("");
        this.tfLoyaltyNo.setText("");
        this.tfLoyaltyPoint.setText("");
        this.tfHomePhone.setText("");
        this.tfZip.setText("");
        this.cbVip.setSelected(false);
        this.tfWorkPhone.setText("");
        this.tfMobile.setText("");
        this.tfSocialSecurityNumber.setText("");
        this.setDefaultCustomerPicture();
    }

    @Override
    public boolean save() {
        try {
            if (!this.updateModel()) {
                return false;
            }
            Customer customer = (Customer)this.getBean();
            CustomerDAO.getInstance().saveOrUpdate(customer);
            this.updateView();
            return true;
        }
        catch (IllegalModelStateException customer) {
        }
        catch (StaleObjectStateException e) {
            BOMessageDialog.showError(this, Messages.getString("CustomerForm.47"));
        }
        return false;
    }

    @Override
    protected void updateView() {
        byte[] picture;
        Customer customer = (Customer)this.getBean();
        if (customer == null) {
            return;
        }
        this.cbSalutation.setSelectedItem(customer.getSalutation());
        this.tfFirstName.setText(customer.getFirstName());
        this.tfLastName.setText(customer.getLastName());
        this.tfDoB.setText(customer.getDob());
        this.tfAddress.setText(customer.getAddress());
        this.tfCity.setText(customer.getCity());
        this.tfCountry.setText(customer.getCountry());
        this.tfCreditLimit.setText(String.valueOf(customer.getCreditLimit()));
        this.tfEmail.setText(customer.getEmail());
        this.tfLoyaltyNo.setText(customer.getLoyaltyNo());
        this.tfLoyaltyPoint.setText(customer.getLoyaltyPoint().toString());
        this.tfHomePhone.setText(customer.getHomePhoneNo());
        this.tfZip.setText(customer.getZipCode());
        this.cbVip.setSelected(customer.isVip());
        this.tfWorkPhone.setText(customer.getWorkPhoneNo());
        this.tfMobile.setText(customer.getMobileNo());
        if (customer.getSocialSecurityNumber() != null) {
            this.tfSocialSecurityNumber.setText(String.valueOf(customer.getSocialSecurityNumber()));
        }
        if ((picture = customer.getPicture()) != null) {
            this.lblPicture.setIcon(new ImageIcon(picture));
        } else {
            this.setDefaultCustomerPicture();
        }
    }

    private void setDefaultCustomerPicture() {
        try {
            InputStream stream = this.getClass().getResourceAsStream("/images/generic-profile-pic-v2.png");
            byte[] picture2 = IOUtils.toByteArray((InputStream)stream);
            IOUtils.closeQuietly((InputStream)stream);
            this.lblPicture.setIcon(new ImageIcon(picture2));
        }
        catch (IOException e) {
            PosLog.error(this.getClass(), e);
        }
    }

    @Override
    protected boolean updateModel() throws IllegalModelStateException {
        String mobile = this.tfMobile.getText();
        String fname = this.tfFirstName.getText();
        String loyaltyNo = this.tfLoyaltyNo.getText();
        if (StringUtils.isEmpty((String)mobile) && StringUtils.isEmpty((String)fname) && StringUtils.isEmpty((String)loyaltyNo)) {
            POSMessageDialog.showError(null, Messages.getString("CustomerForm.60"));
            return false;
        }
        Customer customer = (Customer)this.getBean();
        if (customer == null) {
            customer = new Customer();
            this.setBean(customer, false);
        }
        customer.setSalutation(this.cbSalutation.getSelectedItem().toString());
        customer.setFirstName(this.tfFirstName.getText());
        customer.setLastName(this.tfLastName.getText());
        customer.setDob(this.tfDoB.getText());
        customer.setAddress(this.tfAddress.getText());
        customer.setCity(this.tfCity.getText());
        customer.setCountry(this.tfCountry.getText());
        customer.setCreditLimit(PosGuiUtil.parseDouble(this.tfCreditLimit));
        customer.setEmail(this.tfEmail.getText());
        customer.setLoyaltyNo(this.tfLoyaltyNo.getText());
        customer.setLoyaltyPoint(this.tfLoyaltyPoint.getInteger());
        customer.setHomePhoneNo(this.tfHomePhone.getText());
        customer.setZipCode(this.tfZip.getText());
        customer.setVip(this.cbVip.isSelected());
        customer.setMobileNo(this.tfMobile.getText());
        customer.setSocialSecurityNumber(this.tfSocialSecurityNumber.getText());
        customer.setWorkPhoneNo(this.tfWorkPhone.getText());
        if (this.image != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write((RenderedImage)this.image, "jpg", baos);
                byte[] bytes = baos.toByteArray();
                customer.setPicture(bytes);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return true;
    }

    @Override
    public boolean delete() {
        try {
            Customer bean2 = (Customer)this.getBean();
            if (bean2 == null) {
                return false;
            }
            int option = POSMessageDialog.showYesNoQuestionDialog(POSUtil.getBackOfficeWindow(), "Are you sure to delete selected table?", "Confirm");
            if (option != 0) {
                return false;
            }
            CustomerDAO.getInstance().delete(bean2);
            return true;
        }
        catch (Exception e) {
            POSMessageDialog.showError(POSUtil.getBackOfficeWindow(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getDisplayText() {
        return Messages.getString("CustomerForm.54");
    }

    public static class MyOwnFocusTraversalPolicy
    extends FocusTraversalPolicy {
        Vector<Component> order;

        public MyOwnFocusTraversalPolicy(Vector<Component> order) {
            this.order = new Vector(order.size());
            this.order.addAll(order);
        }

        @Override
        public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
            int idx = (this.order.indexOf(aComponent) + 1) % this.order.size();
            return this.order.get(idx);
        }

        @Override
        public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
            int idx = this.order.indexOf(aComponent) - 1;
            if (idx < 0) {
                idx = this.order.size() - 1;
            }
            return this.order.get(idx);
        }

        @Override
        public Component getDefaultComponent(Container focusCycleRoot) {
            return this.order.get(0);
        }

        @Override
        public Component getLastComponent(Container focusCycleRoot) {
            return this.order.lastElement();
        }

        @Override
        public Component getFirstComponent(Container focusCycleRoot) {
            return this.order.get(0);
        }
    }
}

