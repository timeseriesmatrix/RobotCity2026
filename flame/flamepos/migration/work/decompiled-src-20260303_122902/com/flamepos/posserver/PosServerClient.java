/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.posserver;

import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class PosServerClient
extends POSDialog {
    private static JLabel lblStatus;
    private JTextField txtServerId;
    private JTextField txtTable;
    private JTextArea txtReqMsg;
    private JTextArea txtRespMsg;
    private JButton btnSend;
    private JButton btnRequest;

    public PosServerClient() {
        this.intializeComponents();
    }

    public void intializeComponents() {
        this.setLayout(new BorderLayout());
        JPanel container = new JPanel(new BorderLayout());
        JPanel headerPanel = new JPanel(new BorderLayout());
        TitlePanel titlePanel = new TitlePanel();
        titlePanel.setTitle("POS Server Client");
        lblStatus = new JLabel("");
        headerPanel.add((Component)titlePanel, "North");
        headerPanel.add((Component)lblStatus, "South");
        JLabel lblServerId = new JLabel("Server Id:");
        JLabel lblTable = new JLabel("Table No:");
        this.txtServerId = new JTextField(20);
        this.txtTable = new JTextField(20);
        this.btnSend = new JButton("Send");
        this.btnRequest = new JButton("Request");
        JPanel centerPanel = new JPanel((LayoutManager)new MigLayout());
        centerPanel.add(lblServerId);
        centerPanel.add(this.txtServerId);
        centerPanel.add((Component)this.btnSend, "wrap");
        centerPanel.add(lblTable);
        centerPanel.add((Component)this.txtTable, "wrap");
        centerPanel.add(new JLabel());
        centerPanel.add((Component)this.btnRequest, "wrap");
        JPanel msgPanel = new JPanel((LayoutManager)new MigLayout());
        JLabel lblReq = new JLabel("Request");
        JLabel lblRes = new JLabel("Response");
        this.txtReqMsg = new JTextArea(5, 50);
        this.txtRespMsg = new JTextArea(5, 50);
        this.txtReqMsg.setLineWrap(true);
        this.txtRespMsg.setLineWrap(true);
        msgPanel.add((Component)lblReq, "wrap");
        msgPanel.add((Component)this.txtReqMsg, "grow, wrap");
        msgPanel.add((Component)lblRes, "wrap");
        msgPanel.add((Component)this.txtRespMsg, "grow");
        container.add((Component)headerPanel, "North");
        container.add((Component)msgPanel, "South");
        container.add((Component)centerPanel, "Center");
        this.add((Component)container, "Center");
        this.setSize(500, 500);
    }

    public static void main(String[] args) throws Exception {
        new PosServerClient().open();
    }
}

