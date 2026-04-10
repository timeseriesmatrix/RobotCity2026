/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.layout.GroupLayout
 *  org.jdesktop.layout.GroupLayout$Group
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.IconFactory;
import com.floreantpos.POSConstants;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import org.jdesktop.layout.GroupLayout;

public class UserListDialog
extends POSDialog {
    private PosButton btnCancel;
    private PosButton btnOk;
    private JComboBox cbUserList;
    private JSeparator jSeparator1;
    private TitlePanel titlePanel1;
    private TransparentPanel transparentPanel1;
    private TransparentPanel transparentPanel2;
    private TransparentPanel transparentPanel3;

    public UserListDialog() {
        this.initComponents();
        this.setTitle(POSConstants.USER_LIST);
        List<User> userList = UserDAO.instance.findAll();
        this.cbUserList.setModel(new ListComboBoxModel(userList));
        this.cbUserList.setFocusable(false);
    }

    private void initComponents() {
        this.titlePanel1 = new TitlePanel();
        this.transparentPanel1 = new TransparentPanel();
        this.transparentPanel2 = new TransparentPanel();
        this.btnOk = new PosButton();
        this.btnCancel = new PosButton();
        this.jSeparator1 = new JSeparator();
        this.transparentPanel3 = new TransparentPanel();
        this.cbUserList = new JComboBox();
        this.setDefaultCloseOperation(2);
        this.titlePanel1.setTitle(POSConstants.SELECT_USER);
        this.getContentPane().add((Component)this.titlePanel1, "North");
        this.transparentPanel1.setLayout(new BorderLayout());
        this.btnOk.setIcon(IconFactory.getIcon("/ui_icons/", "finish.png"));
        this.btnOk.setText(POSConstants.OK);
        this.btnOk.setPreferredSize(new Dimension(120, 50));
        this.btnOk.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                UserListDialog.this.doOk(evt);
            }
        });
        this.transparentPanel2.add(this.btnOk);
        this.btnCancel.setIcon(IconFactory.getIcon("/ui_icons/", "cancel.png"));
        this.btnCancel.setText(POSConstants.CANCEL);
        this.btnCancel.setPreferredSize(new Dimension(120, 50));
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                UserListDialog.this.doCancel(evt);
            }
        });
        this.transparentPanel2.add(this.btnCancel);
        this.transparentPanel1.add((Component)this.transparentPanel2, "Center");
        this.transparentPanel1.add((Component)this.jSeparator1, "North");
        this.getContentPane().add((Component)this.transparentPanel1, "South");
        this.cbUserList.setFont(new Font("Tahoma", 1, 18));
        GroupLayout transparentPanel3Layout = new GroupLayout((Container)this.transparentPanel3);
        this.transparentPanel3.setLayout((LayoutManager)transparentPanel3Layout);
        transparentPanel3Layout.setHorizontalGroup((GroupLayout.Group)transparentPanel3Layout.createParallelGroup(1).add((GroupLayout.Group)transparentPanel3Layout.createSequentialGroup().addContainerGap().add((Component)this.cbUserList, 0, 486, Short.MAX_VALUE).addContainerGap()));
        transparentPanel3Layout.setVerticalGroup((GroupLayout.Group)transparentPanel3Layout.createParallelGroup(1).add((GroupLayout.Group)transparentPanel3Layout.createSequentialGroup().addContainerGap().add((Component)this.cbUserList, -2, 50, -2).addContainerGap(13, Short.MAX_VALUE)));
        this.getContentPane().add((Component)this.transparentPanel3, "Center");
        this.pack();
    }

    private void doOk(ActionEvent evt) {
        this.setCanceled(false);
        this.dispose();
    }

    private void doCancel(ActionEvent evt) {
        this.setCanceled(true);
        this.dispose();
    }

    public User getSelectedUser() {
        return (User)this.cbUserList.getSelectedItem();
    }
}

