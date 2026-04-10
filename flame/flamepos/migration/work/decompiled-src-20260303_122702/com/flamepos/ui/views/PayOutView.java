/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 */
package com.floreantpos.ui.views;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.model.PayoutReason;
import com.floreantpos.model.PayoutRecepient;
import com.floreantpos.model.dao.PayoutReasonDAO;
import com.floreantpos.model.dao.PayoutRecepientDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.NotesDialog;
import com.floreantpos.ui.views.NumberSelectionView;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

public class PayOutView
extends TransparentPanel {
    private JComboBox cbReason;
    private JComboBox cbRecepient;
    private NumberSelectionView numberSelectionView;
    private JTextArea tfNote;

    public PayOutView() {
        this.init();
    }

    public void initialize() {
        PayoutReasonDAO reasonDAO = new PayoutReasonDAO();
        List<PayoutReason> reasons = reasonDAO.findAll();
        this.cbReason.setModel(new DefaultComboBoxModel<Object>(reasons.toArray()));
        PayoutRecepientDAO recepientDAO = new PayoutRecepientDAO();
        List<PayoutRecepient> recepients = recepientDAO.findAll();
        this.cbRecepient.setModel(new DefaultComboBoxModel<PayoutRecepient>(new Vector<PayoutRecepient>(recepients)));
    }

    private void init() {
        this.setLayout((LayoutManager)new MigLayout("inset 0,fill"));
        this.numberSelectionView = new NumberSelectionView();
        this.numberSelectionView.setTitle(POSConstants.AMOUNT_PAID_OUT);
        this.numberSelectionView.setBorder(BorderFactory.createCompoundBorder(this.numberSelectionView.getBorder(), new EmptyBorder(5, 5, 5, 5)));
        this.numberSelectionView.setDecimalAllowed(true);
        Font font1 = new Font("Tahoma", 1, PosUIManager.getFontSize(12));
        Font font2 = new Font("Tahoma", 1, PosUIManager.getFontSize(18));
        JLabel lblPayoutReason = new JLabel(POSConstants.PAY_OUT_REASON);
        JLabel lblPayOutReceipient = new JLabel(POSConstants.SELECT_PAY_OUT_RECEPIENT);
        JLabel lblNote = new JLabel(Messages.getString("PayOutView.5"));
        lblPayoutReason.setFont(font1);
        lblPayOutReceipient.setFont(font1);
        lblNote.setFont(font1);
        Dimension size = PosUIManager.getSize(300, 40);
        this.cbReason = new JComboBox();
        this.cbReason.setPreferredSize(size);
        this.cbRecepient = new JComboBox();
        this.cbRecepient.setPreferredSize(size);
        this.tfNote = new JTextArea();
        this.cbReason.setFont(font2);
        this.cbRecepient.setFont(font2);
        JScrollPane jScrollPane1 = new JScrollPane();
        this.tfNote.setColumns(20);
        this.tfNote.setEditable(false);
        this.tfNote.setLineWrap(true);
        this.tfNote.setRows(4);
        this.tfNote.setWrapStyleWord(true);
        jScrollPane1.setViewportView(this.tfNote);
        PosButton btnAddNote = new PosButton("...");
        btnAddNote.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                PayOutView.this.btnAddNoteActionPerformed(evt);
            }
        });
        PosButton btnNewReason = new PosButton("...");
        btnNewReason.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PayOutView.this.doNewReason();
            }
        });
        PosButton btnNewRecepient = new PosButton("...");
        btnNewRecepient.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PayOutView.this.doNewRecepient();
            }
        });
        int width = PosUIManager.getSize(70);
        JPanel inputPanel = new JPanel((LayoutManager)new MigLayout("fill", "grow, fill", ""));
        inputPanel.add((Component)lblPayoutReason, "grow,wrap");
        inputPanel.add((Component)this.cbReason, "grow");
        inputPanel.add((Component)btnNewReason, "grow,wrap,w " + width + "!");
        inputPanel.add((Component)lblPayOutReceipient, "grow,gaptop 30,wrap");
        inputPanel.add((Component)this.cbRecepient, "grow");
        inputPanel.add((Component)btnNewRecepient, "grow,wrap,w " + width + "!");
        inputPanel.add((Component)lblNote, "grow,gaptop 30,wrap");
        inputPanel.add((Component)jScrollPane1, "grow,spany,h " + PosUIManager.getSize(120) + "!");
        inputPanel.add((Component)btnAddNote, "grow, wrap,w " + width + "!");
        this.add((Component)this.numberSelectionView, "grow");
        this.add((Component)inputPanel, "grow");
    }

    protected void doNewRecepient() {
        NotesDialog dialog = new NotesDialog();
        dialog.setTitle(Messages.getString("PayOutView.0"));
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return;
        }
        PayoutRecepient recepient = new PayoutRecepient();
        recepient.setName(dialog.getNote());
        PayoutRecepientDAO.getInstance().saveOrUpdate(recepient);
        DefaultComboBoxModel model = (DefaultComboBoxModel)this.cbRecepient.getModel();
        model.addElement(recepient);
    }

    protected void doNewReason() {
        NotesDialog dialog = new NotesDialog();
        dialog.setTitle(Messages.getString("PayOutView.10"));
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return;
        }
        PayoutReason reason = new PayoutReason();
        reason.setReason(dialog.getNote());
        PayoutReasonDAO.getInstance().saveOrUpdate(reason);
        DefaultComboBoxModel model = (DefaultComboBoxModel)this.cbReason.getModel();
        model.addElement(reason);
    }

    private void btnAddNoteActionPerformed(ActionEvent evt) {
        NotesDialog dialog = new NotesDialog();
        dialog.setTitle(POSConstants.ENTER_PAYOUT_NOTE);
        dialog.pack();
        dialog.open();
        if (!dialog.isCanceled()) {
            this.tfNote.setText(dialog.getNote());
        }
    }

    public double getPayoutAmount() {
        return this.numberSelectionView.getValue();
    }

    public String getNote() {
        return this.tfNote.getText();
    }

    public PayoutReason getReason() {
        return (PayoutReason)this.cbReason.getSelectedItem();
    }

    public PayoutRecepient getRecepient() {
        return (PayoutRecepient)this.cbRecepient.getSelectedItem();
    }
}

