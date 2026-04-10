/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.CardConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.CardReader;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.dao.PosTransactionDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.views.payment.CardProcessor;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

class AuthorizationDialog
extends POSDialog
implements Runnable {
    private JLabel label;
    private JTextArea txtStatus;
    private PosButton btnFinish;
    private List<PosTransaction> transactions;

    public AuthorizationDialog(POSDialog parent, List<PosTransaction> transactions) {
        this.transactions = transactions;
        this.initComponents();
        this.setTitle(Messages.getString("PaymentProcessWaitDialog.0"));
        this.setIconImage(Application.getPosWindow().getIconImage());
        this.setLocationRelativeTo(parent);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            Thread authorizationThread = new Thread(this);
            authorizationThread.start();
        }
        super.setVisible(b);
    }

    @Override
    public void run() {
        Iterator<PosTransaction> iterator = this.transactions.iterator();
        while (iterator.hasNext()) {
            PosTransaction transaction = iterator.next();
            try {
                String cardEntryType = transaction.getCardReader();
                CardReader cardReader = CardReader.fromString(cardEntryType);
                if (cardReader == CardReader.EXTERNAL_TERMINAL) {
                    transaction.setCaptured(true);
                    PosTransactionDAO.getInstance().saveOrUpdate(transaction);
                    this.txtStatus.append(Messages.getString("AuthorizationDialog.1") + transaction.getId() + Messages.getString("AuthorizationDialog.2"));
                    continue;
                }
                CardProcessor cardProcessor = CardConfig.getPaymentGateway().getProcessor();
                cardProcessor.captureAuthAmount(transaction);
                transaction.setCaptured(true);
                PosTransactionDAO.getInstance().saveOrUpdate(transaction);
                this.txtStatus.append(Messages.getString("AuthorizationDialog.1") + transaction.getId() + Messages.getString("AuthorizationDialog.2"));
                if (!iterator.hasNext()) continue;
                Thread.sleep(6000L);
            }
            catch (InterruptedException cardEntryType) {
            }
            catch (Exception e) {
                this.txtStatus.append(Messages.getString("AuthorizationDialog.1") + transaction.getId() + Messages.getString("AuthorizationDialog.4") + e.getMessage() + "\n");
            }
        }
        this.label.setText(Messages.getString("AuthorizationDialog.0"));
        this.btnFinish.setVisible(true);
    }

    private void initComponents() {
        TransparentPanel transparentPanel1 = new TransparentPanel();
        transparentPanel1.setLayout(new BorderLayout());
        transparentPanel1.setOpaque(true);
        this.label = new JLabel(Messages.getString("PaymentProcessWaitDialog.1"));
        this.label.setHorizontalAlignment(0);
        this.label.setFont(this.label.getFont().deriveFont(24).deriveFont(1));
        JLabel labelGateway = new JLabel(Messages.getString("PaymentProcessWaitDialog.8") + CardConfig.getPaymentGateway().getProductName());
        labelGateway.setHorizontalAlignment(0);
        labelGateway.setFont(this.label.getFont().deriveFont(24).deriveFont(1));
        this.txtStatus = new JTextArea();
        this.txtStatus.setEditable(false);
        this.txtStatus.setLineWrap(true);
        transparentPanel1.add((Component)labelGateway, "North");
        JScrollPane scrollPane = new JScrollPane(this.txtStatus);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(20, 30, 20, 30), scrollPane.getBorder()));
        transparentPanel1.add((Component)scrollPane, "Center");
        TransparentPanel transparentPanel2 = new TransparentPanel();
        transparentPanel2.setLayout(new FlowLayout(1, 10, 5));
        this.btnFinish = new PosButton();
        this.btnFinish.setText(POSConstants.FINISH);
        this.btnFinish.setPreferredSize(new Dimension(140, 50));
        this.btnFinish.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                AuthorizationDialog.this.doFinish(evt);
            }
        });
        this.btnFinish.setVisible(false);
        transparentPanel2.add(this.btnFinish);
        transparentPanel1.add((Component)transparentPanel2, "South");
        this.getContentPane().add((Component)this.label, "North");
        this.getContentPane().add((Component)transparentPanel1, "Center");
        this.setSize(500, 400);
        this.setDefaultCloseOperation(0);
        this.setResizable(false);
    }

    private void doFinish(ActionEvent evt) {
        this.dispose();
    }
}

