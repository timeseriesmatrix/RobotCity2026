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
import com.floreantpos.POSConstants;
import com.floreantpos.main.Application;
import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.Gratuity;
import com.floreantpos.model.PaymentType;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.RefundTransaction;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.model.TransactionType;
import com.floreantpos.model.User;
import com.floreantpos.model.VoidReason;
import com.floreantpos.model.base.BasePosTransaction;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.model.dao.VoidReasonDAO;
import com.floreantpos.report.ReceiptPrintService;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.NotesDialog;
import com.floreantpos.ui.dialog.NumberSelectionDialog2;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.TicketDetailView;
import com.floreantpos.util.NumberUtil;
import com.floreantpos.util.POSUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import org.jdesktop.layout.GroupLayout;

public class VoidTicketDialog
extends POSDialog {
    private PosButton btnCancel;
    private PosButton btnNewVoidReason;
    private PosButton btnVoid;
    private JComboBox cbVoidReasons;
    private JCheckBox chkItemsWasted;
    private TransparentPanel contentPane;
    private JLabel jLabel1;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JSeparator jSeparator1;
    private TicketDetailView ticketDetailView;
    private TitlePanel titlePanel1;
    private TransparentPanel transparentPanel1;
    private TransparentPanel transparentPanel2;
    private TransparentPanel transparentPanel3;
    private TransparentPanel transparentPanel4;
    private Ticket ticket;

    public VoidTicketDialog() {
        this.initComponents();
        try {
            VoidReasonDAO dao = new VoidReasonDAO();
            List<VoidReason> voidReasons = dao.findAll();
            this.cbVoidReasons.setModel(new ListComboBoxModel(voidReasons));
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), POSConstants.CANNOT_LOAD_VOID_REASONS, e);
        }
        this.setSize(450, 650);
    }

    private void initComponents() {
        this.contentPane = new TransparentPanel();
        this.titlePanel1 = new TitlePanel();
        this.transparentPanel1 = new TransparentPanel();
        this.jPanel1 = new JPanel();
        this.jPanel2 = new JPanel();
        this.ticketDetailView = new TicketDetailView();
        this.transparentPanel2 = new TransparentPanel();
        this.cbVoidReasons = new JComboBox();
        this.btnNewVoidReason = new PosButton();
        this.chkItemsWasted = new JCheckBox();
        this.jLabel1 = new JLabel();
        this.transparentPanel3 = new TransparentPanel();
        this.transparentPanel4 = new TransparentPanel();
        this.btnVoid = new PosButton();
        this.btnCancel = new PosButton();
        this.jSeparator1 = new JSeparator();
        this.setDefaultCloseOperation(2);
        this.contentPane.setLayout(new BorderLayout());
        this.titlePanel1.setPreferredSize(new Dimension(400, 60));
        this.titlePanel1.setTitle(POSConstants.VOID_TICKET);
        this.contentPane.add((Component)this.titlePanel1, "North");
        this.transparentPanel1.setLayout(new BorderLayout());
        this.jPanel1.setOpaque(false);
        this.jPanel1.setLayout(new BorderLayout());
        this.transparentPanel1.add((Component)this.jPanel1, "West");
        this.jPanel2.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        this.jPanel2.setOpaque(false);
        this.jPanel2.setLayout(new BorderLayout());
        this.jPanel2.add((Component)this.ticketDetailView, "Center");
        this.transparentPanel2.setPreferredSize(new Dimension(0, 80));
        this.btnNewVoidReason.setText("...");
        this.btnNewVoidReason.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                VoidTicketDialog.this.btnNewVoidReasonActionPerformed(evt);
            }
        });
        this.chkItemsWasted.setText(POSConstants.ITEMS_WASTED);
        this.chkItemsWasted.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.chkItemsWasted.setMargin(new Insets(0, 0, 0, 0));
        this.jLabel1.setText(POSConstants.VOID_REASON + ":");
        GroupLayout transparentPanel2Layout = new GroupLayout((Container)this.transparentPanel2);
        this.transparentPanel2.setLayout((LayoutManager)transparentPanel2Layout);
        transparentPanel2Layout.setHorizontalGroup((GroupLayout.Group)transparentPanel2Layout.createParallelGroup(1).add((GroupLayout.Group)transparentPanel2Layout.createSequentialGroup().add((Component)this.jLabel1).addPreferredGap(0).add((GroupLayout.Group)transparentPanel2Layout.createParallelGroup(1).add((Component)this.chkItemsWasted).add(2, (GroupLayout.Group)transparentPanel2Layout.createSequentialGroup().add((Component)this.cbVoidReasons, 0, 0, Short.MAX_VALUE).addPreferredGap(0).add((Component)this.btnNewVoidReason, -2, 79, -2))).addContainerGap()));
        transparentPanel2Layout.setVerticalGroup((GroupLayout.Group)transparentPanel2Layout.createParallelGroup(1).add((GroupLayout.Group)transparentPanel2Layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)transparentPanel2Layout.createParallelGroup(1).add(2, (GroupLayout.Group)transparentPanel2Layout.createSequentialGroup().add((GroupLayout.Group)transparentPanel2Layout.createParallelGroup(3).add((Component)this.jLabel1).add((Component)this.cbVoidReasons, -1, 20, Short.MAX_VALUE)).addPreferredGap(0).add((Component)this.chkItemsWasted).add(34, 34, 34)).add((GroupLayout.Group)transparentPanel2Layout.createSequentialGroup().add((Component)this.btnNewVoidReason, -1, -1, Short.MAX_VALUE).add(53, 53, 53)))));
        this.jPanel2.add((Component)this.transparentPanel2, "South");
        this.transparentPanel1.add((Component)this.jPanel2, "Center");
        this.contentPane.add((Component)this.transparentPanel1, "Center");
        this.transparentPanel3.setLayout(new BorderLayout());
        this.btnVoid.setIcon(IconFactory.getIcon("/ui_icons/", "void_ticket.png"));
        this.btnVoid.setText(POSConstants.VOID);
        this.btnVoid.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                VoidTicketDialog.this.btnVoidActionPerformed(evt);
            }
        });
        this.transparentPanel4.add(this.btnVoid);
        this.btnCancel.setIcon(IconFactory.getIcon("/ui_icons/", "cancel.png"));
        this.btnCancel.setText(POSConstants.CANCEL);
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                VoidTicketDialog.this.btnCancelActionPerformed(evt);
            }
        });
        this.transparentPanel4.add(this.btnCancel);
        this.transparentPanel3.add((Component)this.transparentPanel4, "Center");
        this.transparentPanel3.add((Component)this.jSeparator1, "North");
        this.contentPane.add((Component)this.transparentPanel3, "South");
        this.getContentPane().add((Component)this.contentPane, "Center");
        this.pack();
    }

    private void btnNewVoidReasonActionPerformed(ActionEvent evt) {
        try {
            NotesDialog dialog = new NotesDialog();
            dialog.setTitle(POSConstants.ENTER_VOID_REASON);
            dialog.pack();
            dialog.open();
            if (!dialog.isCanceled()) {
                String newVoidReason = dialog.getNote();
                VoidReason voidReason = new VoidReason();
                voidReason.setReasonText(newVoidReason);
                VoidReasonDAO dao = new VoidReasonDAO();
                dao.save(voidReason);
                if (this.cbVoidReasons.getModel() instanceof ListComboBoxModel) {
                    ListComboBoxModel model = (ListComboBoxModel)this.cbVoidReasons.getModel();
                    model.addElement(voidReason);
                }
            }
        }
        catch (Throwable e) {
            POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, e);
        }
    }

    private void btnCancelActionPerformed(ActionEvent evt) {
        this.canceled = true;
        this.dispose();
    }

    private void btnVoidActionPerformed(ActionEvent evt) {
        try {
            double refundAmount = 0.0;
            RefundTransaction refundTransaction = null;
            if (this.ticket.getPaidAmount() > 0.0) {
                double tipsAmount = this.ticket.getGratuityAmount();
                double ticketTotalWithoutTips = NumberUtil.roundToTwoDigit(this.ticket.getTotalAmount() - tipsAmount);
                double paidAmount = this.ticket.getPaidAmount();
                refundAmount = NumberSelectionDialog2.takeDoubleInput("Enter refund amount", paidAmount < ticketTotalWithoutTips ? this.ticket.getPaidAmount() : paidAmount - tipsAmount);
                if (refundAmount == -1.0) {
                    return;
                }
                if (tipsAmount > 0.0 && POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(), "Do you want to refund tips?", "Confirm") == 0) {
                    Gratuity gratuity = this.ticket.getGratuity();
                    gratuity.setRefunded(true);
                    refundAmount += gratuity.getAmount().doubleValue();
                }
                if (refundAmount > paidAmount) {
                    POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), "Refund amount cannot be greater than paid amount.");
                    return;
                }
                refundTransaction = this.doCreateRefundTransaction(this.ticket, refundAmount);
            } else {
                Gratuity gratuity = this.ticket.getGratuity();
                if (gratuity != null) {
                    gratuity.setAmount(0.0);
                }
            }
            VoidReason voidReason = (VoidReason)this.cbVoidReasons.getSelectedItem();
            if (voidReason != null) {
                this.ticket.setVoidReason(voidReason.getReasonText());
            }
            this.ticket.setWasted(this.chkItemsWasted.isSelected());
            this.ticket.setVoidedBy(Application.getCurrentUser());
            TicketDAO dao = new TicketDAO();
            if (this.ticket.getPaidAmount() == 0.0 && !this.printedToKitchen()) {
                ArrayList<Ticket> list = new ArrayList<Ticket>();
                list.add(this.ticket);
                dao.deleteTickets(list);
            } else {
                dao.voidTicket(this.ticket);
            }
            try {
                String title = "- " + Messages.getString("VoidTicketDialog.0");
                String data = Messages.getString("VoidTicketDialog.1") + this.ticket.getId() + " was voided.";
                if (refundTransaction != null && refundAmount > 0.0) {
                    ReceiptPrintService.printRefundTicket(this.ticket, refundTransaction);
                }
                ReceiptPrintService.printGenericReport(title, data);
            }
            catch (Exception ee) {
                String message = Messages.getString("VoidTicketDialog.9") + ee.getMessage();
                POSMessageDialog.showError(Application.getPosWindow(), message, ee);
            }
            this.canceled = false;
            ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), ActionHistory.VOID_CHECK, POSConstants.RECEIPT_REPORT_TICKET_NO_LABEL + ":" + this.ticket.getId() + "; Total" + ": " + NumberUtil.formatNumber(this.ticket.getTotalAmount()));
            this.dispose();
        }
        catch (Exception e) {
            POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, e);
        }
    }

    private boolean printedToKitchen() {
        for (TicketItem ticketItem : this.ticket.getTicketItems()) {
            if (ticketItem.isPrintedToKitchen().booleanValue()) {
                return true;
            }
            if (!ticketItem.isHasModifiers().booleanValue()) continue;
            for (TicketItemModifier modifier : ticketItem.getTicketItemModifiers()) {
                if (!modifier.isPrintedToKitchen().booleanValue()) continue;
                return true;
            }
        }
        return false;
    }

    private RefundTransaction doCreateRefundTransaction(Ticket ticket, double refundAmount) {
        BasePosTransaction posTransaction = null;
        User currentUser = Application.getCurrentUser();
        Terminal terminal = Application.getInstance().getTerminal();
        double oldRefundAmount = 0.0;
        if (ticket.getTransactions() != null) {
            for (PosTransaction t : ticket.getTransactions()) {
                if (!(t instanceof RefundTransaction)) continue;
                posTransaction = (RefundTransaction)t;
                oldRefundAmount = posTransaction.getAmount();
                break;
            }
        }
        if (posTransaction == null) {
            posTransaction = new RefundTransaction();
        }
        posTransaction.setTicket(ticket);
        posTransaction.setPaymentType(PaymentType.CASH.name());
        posTransaction.setTransactionType(TransactionType.DEBIT.name());
        posTransaction.setAmount(refundAmount);
        posTransaction.setTerminal(terminal);
        posTransaction.setUser(currentUser);
        posTransaction.setTransactionTime(new Date());
        ticket.setRefunded(true);
        ticket.setClosingDate(new Date());
        double currentBalance = terminal.getCurrentBalance();
        double newBalance = currentBalance + oldRefundAmount - refundAmount;
        terminal.setCurrentBalance(newBalance);
        ticket.addTotransactions((PosTransaction)posTransaction);
        return posTransaction;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        this.ticketDetailView.setTicket(ticket);
    }
}

