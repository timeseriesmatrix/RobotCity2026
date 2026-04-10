/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.util.datamigrate;

import com.floreantpos.Database;
import com.floreantpos.Messages;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class DbPanel
extends JPanel {
    private JTextField tfServer;
    private JTextField tfPort;
    private JTextField tfDbName;
    private JTextField tfUser;
    private JTextField tfPassword;
    private JComboBox comboBox;

    public DbPanel() {
        this.setLayout(new BorderLayout(0, 0));
        JPanel panel = new JPanel();
        this.add((Component)panel, "North");
        JLabel lblDatabase = new JLabel(Messages.getString("DbPanel.0"));
        panel.add(lblDatabase);
        this.comboBox = new JComboBox<Database>(Database.values());
        panel.add(this.comboBox);
        JPanel panel_1 = new JPanel();
        this.add((Component)panel_1, "Center");
        panel_1.setLayout((LayoutManager)new MigLayout("", "[][grow]", "[][][][][]"));
        JLabel lblNewLabel_1 = new JLabel(Messages.getString("DbPanel.4"));
        panel_1.add((Component)lblNewLabel_1, "cell 0 1,alignx trailing");
        this.tfServer = new JTextField("localhost");
        panel_1.add((Component)this.tfServer, "cell 1 1,growx");
        this.tfServer.setColumns(10);
        JLabel lblNewLabel_2 = new JLabel(Messages.getString("DbPanel.8"));
        panel_1.add((Component)lblNewLabel_2, "cell 0 2,alignx trailing");
        this.tfPort = new JTextField("51527");
        panel_1.add((Component)this.tfPort, "cell 1 2,growx");
        this.tfPort.setColumns(10);
        JLabel lblNewLabel_3 = new JLabel(Messages.getString("DbPanel.12"));
        panel_1.add((Component)lblNewLabel_3, "cell 0 3,alignx trailing");
        this.tfDbName = new JTextField("posdb");
        panel_1.add((Component)this.tfDbName, "cell 1 3,growx");
        this.tfDbName.setColumns(10);
        JLabel lblNewLabel_4 = new JLabel(Messages.getString("DbPanel.16"));
        panel_1.add((Component)lblNewLabel_4, "cell 0 4,alignx trailing");
        this.tfUser = new JTextField("app");
        panel_1.add((Component)this.tfUser, "cell 1 4,growx");
        this.tfUser.setColumns(10);
        JLabel lblNewLabel_5 = new JLabel(Messages.getString("DbPanel.20"));
        panel_1.add((Component)lblNewLabel_5, "cell 0 5,alignx trailing");
        this.tfPassword = new JTextField("sa");
        panel_1.add((Component)this.tfPassword, "cell 1 5,growx");
        this.tfPassword.setColumns(10);
        this.comboBox.setSelectedIndex(1);
    }

    public Database getDatabase() {
        return (Database)((Object)this.comboBox.getSelectedItem());
    }

    public String getConnectString() {
        Database db = (Database)((Object)this.comboBox.getSelectedItem());
        String connectString = db.getConnectString(this.tfServer.getText(), this.tfPort.getText(), this.tfDbName.getText());
        return connectString;
    }

    public String getUser() {
        return this.tfUser.getText();
    }

    public String getPass() {
        return this.tfPassword.getText();
    }
}

