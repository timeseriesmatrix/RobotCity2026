/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.apache.commons.lang.StringUtils
 *  org.apache.commons.logging.LogFactory
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.model.CashDrawer;
import com.floreantpos.model.Currency;
import com.floreantpos.model.Gratuity;
import com.floreantpos.model.PaymentType;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.CashDrawerDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.report.ReceiptPrintService;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.MultiCurrencyTenderDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.views.payment.SettleTicketDialog;
import com.floreantpos.util.CurrencyUtil;
import com.floreantpos.util.NumberUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PaymentView
extends JPanel {
    private static final String ZERO = "0";
    private static final String REMOVE = "1";
    protected SettleTicketDialog settleTicketView;
    private PosButton btnGratuity;
    private PosButton btnCancel;
    private PosButton btnCash;
    private PosButton btnPrint;
    private PosButton btnCreditCard;
    private PosButton btnGift;
    private PosButton btnOther;
    private TransparentPanel calcButtonPanel;
    private JLabel labelDueAmount;
    private JLabel labelTenderedAmount;
    private TransparentPanel actionButtonPanel;
    private PosButton btn7;
    private PosButton btnDot;
    private PosButton btn0;
    private PosButton btnClear;
    private PosButton btn8;
    private PosButton btn9;
    private PosButton btn4;
    private PosButton btn5;
    private PosButton btn6;
    private PosButton btn3;
    private PosButton btn2;
    private PosButton btn1;
    private PosButton btn00;
    private PosButton btnNextAmount;
    private PosButton btnAmount1;
    private PosButton btnAmount2;
    private PosButton btnAmount5;
    private PosButton btnAmount10;
    private PosButton btnAmount20;
    private PosButton btnAmount50;
    private PosButton btnAmount100;
    private PosButton btnExactAmount;
    private JTextField txtTenderedAmount;
    private JTextField txtDueAmount;
    private PosButton btnDiscount;
    private boolean clearPreviousAmount = true;
    Action calAction = new AbstractAction(){

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField textField = PaymentView.this.txtTenderedAmount;
            PosButton button = (PosButton)e.getSource();
            String command = button.getActionCommand();
            if (command.equals(Messages.getString("PaymentView.66"))) {
                textField.setText(PaymentView.ZERO);
            } else if (command.equals(".")) {
                if (textField.getText().indexOf(46) < 0) {
                    textField.setText(textField.getText() + ".");
                }
            } else {
                String string;
                int index;
                if (PaymentView.this.clearPreviousAmount) {
                    textField.setText("");
                    PaymentView.this.clearPreviousAmount = false;
                }
                if ((index = (string = textField.getText()).indexOf(46)) < 0) {
                    double value = 0.0;
                    try {
                        value = Double.parseDouble(string);
                    }
                    catch (NumberFormatException x) {
                        Toolkit.getDefaultToolkit().beep();
                    }
                    if (value == 0.0) {
                        textField.setText(command);
                    } else {
                        textField.setText(string + command);
                    }
                } else {
                    textField.setText(string + command);
                }
            }
        }
    };
    Action nextButtonAction = new AbstractAction(){

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                DecimalFormat format = new DecimalFormat("##.00");
                PosButton button = (PosButton)e.getSource();
                String command = button.getActionCommand();
                if (command.equals("exactAmount")) {
                    double dueAmount = PaymentView.this.getDueAmount();
                    PaymentView.this.txtTenderedAmount.setText(NumberUtil.formatNumber(dueAmount));
                } else if (command.equals("nextAmount")) {
                    double dd = NumberUtil.parse(PaymentView.this.txtDueAmount.getText()).doubleValue();
                    double amount = Math.ceil(dd);
                    PaymentView.this.txtTenderedAmount.setText(String.valueOf(format.format(amount)));
                } else {
                    if (PaymentView.this.clearPreviousAmount) {
                        PaymentView.this.txtTenderedAmount.setText(PaymentView.ZERO);
                        PaymentView.this.clearPreviousAmount = false;
                    }
                    double x = NumberUtil.parse(PaymentView.this.txtTenderedAmount.getText()).doubleValue();
                    double y = Double.parseDouble(command);
                    double z = x + y;
                    PaymentView.this.txtTenderedAmount.setText(String.valueOf(format.format(z)));
                }
            }
            catch (Exception e2) {
                LogFactory.getLog(this.getClass()).error((Object)e2);
            }
        }
    };

    public PaymentView(SettleTicketDialog settleTicketView) {
        this.settleTicketView = settleTicketView;
        this.initComponents();
    }

    private void initComponents() {
        this.setLayout((LayoutManager)new MigLayout("fill", "[grow][grow]", ""));
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        TransparentPanel transparentPanel1 = new TransparentPanel(new BorderLayout(5, 5));
        this.labelDueAmount = new JLabel();
        this.labelTenderedAmount = new JLabel();
        this.txtDueAmount = new JTextField();
        this.txtTenderedAmount = new JTextField();
        Font font1 = new Font("Tahoma", 1, PosUIManager.getFontSize(20));
        Font font2 = new Font("Arial", 0, PosUIManager.getFontSize(34));
        this.labelTenderedAmount.setFont(font1);
        this.labelTenderedAmount.setText(Messages.getString("PaymentView.54") + " " + CurrencyUtil.getCurrencySymbol());
        this.labelTenderedAmount.setForeground(Color.gray);
        this.txtTenderedAmount.setHorizontalAlignment(4);
        this.txtTenderedAmount.setFont(font1);
        this.labelDueAmount.setFont(font1);
        this.labelDueAmount.setText(Messages.getString("PaymentView.52") + " " + CurrencyUtil.getCurrencySymbol());
        this.labelDueAmount.setForeground(Color.gray);
        this.txtDueAmount.setFont(font1);
        this.txtDueAmount.setEditable(false);
        this.txtDueAmount.setHorizontalAlignment(4);
        transparentPanel1.setLayout((LayoutManager)new MigLayout("", "[][grow,fill]", "[grow][][grow]"));
        transparentPanel1.add((Component)this.labelDueAmount, "cell 0 0,alignx right,aligny center");
        transparentPanel1.add((Component)this.labelTenderedAmount, "cell 0 2,alignx left,aligny center");
        transparentPanel1.add((Component)this.txtDueAmount, "cell 1 0,growx,aligny top");
        transparentPanel1.add((Component)this.txtTenderedAmount, "cell 1 2,growx,aligny top");
        leftPanel.add((Component)transparentPanel1, "North");
        this.calcButtonPanel = new TransparentPanel();
        this.calcButtonPanel.setLayout((LayoutManager)new MigLayout("wrap 4,fill, ins 0", "sg, fill", "sg, fill"));
        this.btnNextAmount = new PosButton();
        this.btnAmount1 = new PosButton();
        this.btnAmount1.setFont(font2);
        this.btnAmount2 = new PosButton();
        this.btnAmount2.setFont(font2);
        this.btnAmount5 = new PosButton();
        this.btnAmount5.setFont(font2);
        this.btnAmount10 = new PosButton();
        this.btnAmount10.setFont(font2);
        this.btnAmount20 = new PosButton();
        this.btnAmount20.setFont(font2);
        this.btnAmount50 = new PosButton();
        this.btnAmount50.setFont(font2);
        this.btnAmount100 = new PosButton();
        this.btnAmount100.setFont(font2);
        this.btnExactAmount = new PosButton();
        this.btn7 = new PosButton();
        this.btn8 = new PosButton();
        this.btn9 = new PosButton();
        this.btn4 = new PosButton();
        this.btn5 = new PosButton();
        this.btn6 = new PosButton();
        this.btn1 = new PosButton();
        this.btn2 = new PosButton();
        this.btn3 = new PosButton();
        this.btn0 = new PosButton();
        this.btnDot = new PosButton();
        this.btnClear = new PosButton();
        this.btn00 = new PosButton();
        this.btnAmount1.setForeground(Color.blue);
        this.btnAmount1.setAction(this.nextButtonAction);
        this.btnAmount1.setText(Messages.getString("PaymentView.1"));
        this.btnAmount1.setActionCommand(REMOVE);
        this.btnAmount1.setFocusable(false);
        this.calcButtonPanel.add(this.btnAmount1);
        this.btn7.setAction(this.calAction);
        this.btn7.setText("7");
        this.btn7.setFont(font2);
        this.btn7.setActionCommand("7");
        this.btn7.setFocusable(false);
        this.calcButtonPanel.add(this.btn7);
        this.btn8.setAction(this.calAction);
        this.btn8.setText("8");
        this.btn8.setFont(font2);
        this.btn8.setActionCommand("8");
        this.btn8.setFocusable(false);
        this.calcButtonPanel.add(this.btn8);
        this.btn9.setAction(this.calAction);
        this.btn9.setText("9");
        this.btn9.setFont(font2);
        this.btn9.setActionCommand("9");
        this.btn9.setFocusable(false);
        this.calcButtonPanel.add(this.btn9);
        this.btnAmount2.setForeground(Color.blue);
        this.btnAmount2.setAction(this.nextButtonAction);
        this.btnAmount2.setText(Messages.getString("PaymentView.10"));
        this.btnAmount2.setActionCommand("2");
        this.btnAmount2.setFocusable(false);
        this.calcButtonPanel.add(this.btnAmount2);
        this.btn4.setAction(this.calAction);
        this.btn4.setText("4");
        this.btn4.setFont(font2);
        this.btn4.setActionCommand("4");
        this.btn4.setFocusable(false);
        this.calcButtonPanel.add(this.btn4);
        this.btn5.setAction(this.calAction);
        this.btn5.setText("5");
        this.btn5.setFont(font2);
        this.btn5.setActionCommand("5");
        this.btn5.setFocusable(false);
        this.calcButtonPanel.add(this.btn5);
        this.btn6.setAction(this.calAction);
        this.btn6.setText("6");
        this.btn6.setFont(font2);
        this.btn6.setActionCommand("6");
        this.btn6.setFocusable(false);
        this.calcButtonPanel.add(this.btn6);
        this.btnAmount5.setForeground(Color.blue);
        this.btnAmount5.setAction(this.nextButtonAction);
        this.btnAmount5.setText(Messages.getString("PaymentView.12"));
        this.btnAmount5.setActionCommand("5");
        this.btnAmount5.setFocusable(false);
        this.calcButtonPanel.add(this.btnAmount5);
        this.btn1.setAction(this.calAction);
        this.btn1.setText(REMOVE);
        this.btn1.setFont(font2);
        this.btn1.setActionCommand(REMOVE);
        this.btn1.setFocusable(false);
        this.calcButtonPanel.add(this.btn1);
        this.btn2.setAction(this.calAction);
        this.btn2.setText("2");
        this.btn2.setFont(font2);
        this.btn2.setActionCommand("2");
        this.btn2.setFocusable(false);
        this.calcButtonPanel.add(this.btn2);
        this.btn3.setAction(this.calAction);
        this.btn3.setText("3");
        this.btn3.setFont(font2);
        this.btn3.setActionCommand("3");
        this.btn3.setFocusable(false);
        this.calcButtonPanel.add(this.btn3);
        this.btnAmount10.setForeground(Color.blue);
        this.btnAmount10.setAction(this.nextButtonAction);
        this.btnAmount10.setText(Messages.getString("PaymentView.14"));
        this.btnAmount10.setActionCommand("10");
        this.btnAmount10.setFocusable(false);
        this.calcButtonPanel.add((Component)this.btnAmount10, "grow");
        this.btn0.setAction(this.calAction);
        this.btn0.setText(ZERO);
        this.btn0.setFont(font2);
        this.btn0.setActionCommand(ZERO);
        this.btn0.setFocusable(false);
        this.calcButtonPanel.add(this.btn0);
        this.btn00.setFont(new Font("Arial", 0, 30));
        this.btn00.setAction(this.calAction);
        this.btn00.setText(Messages.getString("PaymentView.18"));
        this.btn00.setActionCommand("00");
        this.btn00.setFocusable(false);
        this.calcButtonPanel.add(this.btn00);
        this.btnDot.setAction(this.calAction);
        this.btnDot.setIcon(IconFactory.getIcon("/ui_icons/", "dot.png"));
        this.btnDot.setActionCommand(".");
        this.btnDot.setFocusable(false);
        this.calcButtonPanel.add(this.btnDot);
        this.btnAmount20.setForeground(Color.BLUE);
        this.btnAmount20.setAction(this.nextButtonAction);
        this.btnAmount20.setText("20");
        this.btnAmount20.setActionCommand("20");
        this.btnAmount20.setFocusable(false);
        this.calcButtonPanel.add((Component)this.btnAmount20, "grow");
        this.btnAmount50.setForeground(Color.blue);
        this.btnAmount50.setAction(this.nextButtonAction);
        this.btnAmount50.setText("50");
        this.btnAmount50.setActionCommand("50");
        this.btnAmount50.setFocusable(false);
        this.calcButtonPanel.add((Component)this.btnAmount50, "grow");
        this.btnAmount100.setForeground(Color.blue);
        this.btnAmount100.setAction(this.nextButtonAction);
        this.btnAmount100.setText("100");
        this.btnAmount100.setActionCommand("100");
        this.btnAmount100.setFocusable(false);
        this.calcButtonPanel.add((Component)this.btnAmount100, "grow");
        this.btnClear.setAction(this.calAction);
        this.btnClear.setIcon(IconFactory.getIcon("/ui_icons/", "clear.png"));
        this.btnClear.setText(Messages.getString("PaymentView.38"));
        this.btnClear.setFocusable(false);
        this.calcButtonPanel.add(this.btnClear);
        this.btnExactAmount.setAction(this.nextButtonAction);
        this.btnExactAmount.setText(Messages.getString("PaymentView.20"));
        this.btnExactAmount.setActionCommand("exactAmount");
        this.btnExactAmount.setFocusable(false);
        this.calcButtonPanel.add((Component)this.btnExactAmount, "span 2,grow");
        this.btnNextAmount.setAction(this.nextButtonAction);
        this.btnNextAmount.setText(Messages.getString("PaymentView.23"));
        this.btnNextAmount.setActionCommand("nextAmount");
        this.btnNextAmount.setFocusable(false);
        this.calcButtonPanel.add((Component)this.btnNextAmount, "span 2,grow");
        this.btnGratuity = new PosButton(POSConstants.ADD_GRATUITY_TEXT);
        this.btnGratuity.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PaymentView.this.doSetGratuity();
            }
        });
        this.btnDiscount = new PosButton(POSConstants.COUPON_DISCOUNT);
        this.btnDiscount.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PaymentView.this.settleTicketView.doApplyCoupon();
            }
        });
        this.btnPrint = new PosButton(POSConstants.PRINT_TICKET);
        this.btnPrint.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                ReceiptPrintService.printTicket(PaymentView.this.settleTicketView.getTicket());
            }
        });
        JPanel panel4 = new JPanel(new GridLayout(1, 0, 5, 5));
        panel4.add(this.btnGratuity);
        panel4.add(this.btnDiscount);
        panel4.add(this.btnPrint);
        this.calcButtonPanel.add((Component)panel4, "span 4,growx");
        leftPanel.add((Component)this.calcButtonPanel, "Center");
        this.actionButtonPanel = new TransparentPanel();
        this.actionButtonPanel.setOpaque(true);
        this.actionButtonPanel.setLayout((LayoutManager)new MigLayout("hidemode 3,wrap 1, ins 0 20 0 0, fill", "sg, fill", ""));
        int width = PosUIManager.getSize(160);
        this.btnCash = new PosButton(Messages.getString("PaymentView.31"));
        this.actionButtonPanel.add((Component)this.btnCash, "grow,w " + width + "!");
        this.btnCash.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                PaymentView.this.doPayByCash();
            }
        });
        PosButton btnMultiCurrencyCash = new PosButton("MULTI CURRENCY CASH");
        this.actionButtonPanel.add((Component)btnMultiCurrencyCash, "grow,w " + width + "!");
        btnMultiCurrencyCash.setVisible(TerminalConfig.isEnabledMultiCurrency());
        btnMultiCurrencyCash.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    List<Currency> currencyList = CurrencyUtil.getAllCurrency();
                    if (currencyList.size() > 1 && !PaymentView.this.adjustCashDrawerBalance(currencyList)) {
                        return;
                    }
                    double x = NumberUtil.parse(PaymentView.this.txtTenderedAmount.getText()).doubleValue();
                    if (x <= 0.0) {
                        POSMessageDialog.showError(Messages.getString("PaymentView.32"));
                        return;
                    }
                    PaymentView.this.settleTicketView.doSettle(PaymentType.CASH);
                }
                catch (Exception e) {
                    LogFactory.getLog(this.getClass()).error((Object)e);
                }
            }
        });
        this.btnCreditCard = new PosButton(Messages.getString("PaymentView.33"));
        this.actionButtonPanel.add((Component)this.btnCreditCard, "grow,w " + width + "!");
        this.btnCreditCard.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PaymentView.this.settleTicketView.doSettle(PaymentType.CREDIT_CARD);
            }
        });
        this.btnGift = new PosButton(Messages.getString("PaymentView.35"));
        this.actionButtonPanel.add((Component)this.btnGift, "grow,w " + width + "!");
        this.btnGift.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PaymentView.this.settleTicketView.doSettle(PaymentType.GIFT_CERTIFICATE);
            }
        });
        this.btnOther = new PosButton("OTHER");
        this.actionButtonPanel.add((Component)this.btnOther, "grow,w " + width + "!");
        this.btnOther.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                PaymentView.this.settleTicketView.doSettle(PaymentType.CUSTOM_PAYMENT);
            }
        });
        this.btnCancel = new PosButton(POSConstants.CANCEL.toUpperCase());
        this.actionButtonPanel.add((Component)this.btnCancel, "grow,w " + width + "!");
        this.btnCancel.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                PaymentView.this.btnCancelActionPerformed(evt);
            }
        });
        this.add((Component)leftPanel, "cell 0 0,grow");
        this.add((Component)this.actionButtonPanel, "cell 1 0,grow");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean adjustCashDrawerBalance(List<Currency> currencyList) {
        MultiCurrencyTenderDialog dialog = new MultiCurrencyTenderDialog(this.settleTicketView.getTicket(), currencyList);
        dialog.pack();
        dialog.open();
        if (dialog.isCanceled()) {
            return false;
        }
        this.txtTenderedAmount.setText(NumberUtil.format3DigitNumber(dialog.getTenderedAmount()));
        CashDrawer cashDrawer = dialog.getCashDrawer();
        Transaction tx = null;
        try (Session session = null;){
            session = CashDrawerDAO.getInstance().createNewSession();
            tx = session.beginTransaction();
            session.saveOrUpdate((Object)cashDrawer);
            tx.commit();
        }
        return true;
    }

    protected void removeKalaId() {
        Ticket ticket = this.settleTicketView.getTicket();
        ticket.getProperties().remove("loyalty_id");
        TicketDAO.getInstance().saveOrUpdate(ticket);
        POSMessageDialog.showMessage(Messages.getString("PaymentView.0"));
    }

    public void addMyKalaId() {
        String loyaltyid = JOptionPane.showInputDialog(Messages.getString("PaymentView.64"));
        if (StringUtils.isEmpty((String)loyaltyid)) {
            return;
        }
        Ticket ticket = this.settleTicketView.getTicket();
        ticket.addProperty("loyalty_id", loyaltyid);
        TicketDAO.getInstance().saveOrUpdate(ticket);
        POSMessageDialog.showMessage(Messages.getString("PaymentView.65"));
    }

    protected void doSetGratuity() {
        this.settleTicketView.doSetGratuity();
    }

    protected void doTaxExempt() {
    }

    private void btnCancelActionPerformed(ActionEvent evt) {
        this.settleTicketView.setCanceled(true);
        this.settleTicketView.dispose();
    }

    public void updateView() {
        double dueAmount = this.getDueAmount();
        this.txtDueAmount.setText(NumberUtil.formatNumber(dueAmount));
        this.txtTenderedAmount.setText(NumberUtil.formatNumber(dueAmount));
    }

    public double getTenderedAmount() throws ParseException {
        double doubleValue = NumberUtil.parse(this.txtTenderedAmount.getText()).doubleValue();
        return doubleValue;
    }

    public SettleTicketDialog getSettleTicketView() {
        return this.settleTicketView;
    }

    public void setSettleTicketView(SettleTicketDialog settleTicketView) {
        this.settleTicketView = settleTicketView;
    }

    protected double getPaidAmount() {
        return this.settleTicketView.getTicket().getPaidAmount();
    }

    protected double getDueAmount() {
        return this.settleTicketView.getTicket().getDueAmount();
    }

    protected double getAdvanceAmount() {
        Gratuity gratuity = this.settleTicketView.getTicket().getGratuity();
        return gratuity != null ? gratuity.getAmount() : 0.0;
    }

    protected double getTotalGratuity() {
        return this.settleTicketView.getTicket().getPaidAmount();
    }

    public void setDefaultFocus() {
        this.txtTenderedAmount.requestFocus();
    }

    private void doPayByCash() {
        try {
            double x = NumberUtil.parse(this.txtTenderedAmount.getText()).doubleValue();
            if (x < 0.0) {
                POSMessageDialog.showError(Messages.getString("PaymentView.32"));
                return;
            }
            this.settleTicketView.doSettle(PaymentType.CASH);
        }
        catch (Exception e) {
            LogFactory.getLog(this.getClass()).error((Object)e);
        }
    }
}

