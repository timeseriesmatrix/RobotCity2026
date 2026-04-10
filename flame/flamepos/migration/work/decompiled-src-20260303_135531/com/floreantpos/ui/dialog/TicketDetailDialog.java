/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.layout.GroupLayout
 *  org.jdesktop.layout.GroupLayout$Group
 */
package com.floreantpos.ui.dialog;

import com.floreantpos.POSConstants;
import com.floreantpos.model.Ticket;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.views.TicketDetailView;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JSeparator;
import org.jdesktop.layout.GroupLayout;

public class TicketDetailDialog
extends POSDialog {
    private PosButton btnFinish;
    private JSeparator jSeparator1;
    private TicketDetailView ticketDetailView;
    private TitlePanel titlePanel1;

    public TicketDetailDialog() {
        this.initComponents();
        this.setResizable(false);
        this.pack();
    }

    private void initComponents() {
        this.titlePanel1 = new TitlePanel();
        this.jSeparator1 = new JSeparator();
        this.btnFinish = new PosButton();
        this.ticketDetailView = new TicketDetailView();
        this.setDefaultCloseOperation(2);
        this.setTitle(POSConstants.TICKET_DETAIL);
        this.titlePanel1.setTitle(POSConstants.TICKET_DETAIL);
        this.btnFinish.setText(POSConstants.OK);
        this.btnFinish.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                TicketDetailDialog.this.doFinish(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout((LayoutManager)layout);
        layout.setHorizontalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.titlePanel1, -1, 428, Short.MAX_VALUE).add(2, (GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(2).add(1, (Component)this.ticketDetailView, -1, 428, Short.MAX_VALUE).add(1, (Component)this.jSeparator1, -1, 428, Short.MAX_VALUE)).addPreferredGap(0)))).add((GroupLayout.Group)layout.createSequentialGroup().add(140, 140, 140).add((Component)this.btnFinish, -2, 130, -2))).addContainerGap()));
        layout.setVerticalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((Component)this.titlePanel1, -2, -1, -2).addPreferredGap(0).add((Component)this.ticketDetailView, -2, -1, -2).addPreferredGap(0).add((Component)this.jSeparator1, -2, -1, -2).addPreferredGap(0).add((Component)this.btnFinish, -2, 42, -2).addContainerGap(-1, Short.MAX_VALUE)));
        this.pack();
    }

    private void doFinish(ActionEvent evt) {
        this.setCanceled(false);
        this.dispose();
    }

    public void setTicket(Ticket ticket) {
        this.ticketDetailView.setTicket(ticket);
    }
}

