/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.IconFactory;
import com.floreantpos.Messages;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.event.KeypadEvent;
import com.floreantpos.swing.event.KeypadEventListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import javax.swing.text.JTextComponent;

public class NumericKeypad
extends JComponent {
    private static final String CLEAR = "CLEAR";
    private EventListenerList eventListeners = new EventListenerList();
    private String text = "";
    private KeypadEvent keypadEvent = null;
    private boolean isProtected = false;
    private JPanel keypadPanel;
    private PosButton posButton0;
    private PosButton posButton1;
    private PosButton btnClear;
    private PosButton posButton2;
    private PosButton posButton3;
    private PosButton posButton4;
    private PosButton posButton5;
    private PosButton posButton6;
    private PosButton posButton7;
    private PosButton posButton8;
    private PosButton posButton9;
    Action goAction = new AbstractAction(){

        @Override
        public void actionPerformed(ActionEvent e) {
            Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            JTextComponent focusedTextComponent = null;
            if (!(focusOwner instanceof JTextComponent)) {
                return;
            }
            focusedTextComponent = (JTextComponent)focusOwner;
            String command = e.getActionCommand();
            if (NumericKeypad.CLEAR.equals(command)) {
                focusedTextComponent.setText("");
            } else {
                focusedTextComponent.setText(focusedTextComponent.getText() + command);
            }
        }
    };
    private PosButton btnDot;

    public NumericKeypad() {
        this.initComponents();
    }

    public synchronized void removeKeypadEventListener(KeypadEventListener listener) {
        this.eventListeners.remove(KeypadEventListener.class, listener);
    }

    public synchronized void addKeypadEventListener(KeypadEventListener listener) {
        this.eventListeners.add(KeypadEventListener.class, listener);
    }

    protected synchronized void fireKeypadEvent(int eventId) {
        Object[] listeners = this.eventListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != KeypadEventListener.class) continue;
            this.keypadEvent = new KeypadEvent(this, eventId);
            ((KeypadEventListener)listeners[i + 1]).receiveKeypadEvent(this.keypadEvent);
        }
    }

    private void initComponents() {
        this.keypadPanel = new JPanel();
        this.posButton7 = new PosButton();
        this.posButton7.setFocusable(false);
        this.posButton8 = new PosButton();
        this.posButton8.setFocusable(false);
        this.posButton9 = new PosButton();
        this.posButton9.setFocusable(false);
        this.posButton4 = new PosButton();
        this.posButton4.setFocusable(false);
        this.posButton5 = new PosButton();
        this.posButton5.setFocusable(false);
        this.posButton6 = new PosButton();
        this.posButton6.setFocusable(false);
        this.posButton1 = new PosButton();
        this.posButton1.setFocusable(false);
        this.posButton2 = new PosButton();
        this.posButton2.setFocusable(false);
        this.posButton3 = new PosButton();
        this.posButton3.setFocusable(false);
        this.posButton0 = new PosButton();
        this.posButton0.setFocusable(false);
        this.keypadPanel.setLayout(new GridLayout(4, 3, 5, 5));
        this.posButton7.setAction(this.goAction);
        this.posButton7.setIcon(IconFactory.getIcon("7.png"));
        this.posButton7.setActionCommand("7");
        this.keypadPanel.add(this.posButton7);
        this.posButton8.setAction(this.goAction);
        this.posButton8.setIcon(IconFactory.getIcon("8.png"));
        this.posButton8.setActionCommand("8");
        this.keypadPanel.add(this.posButton8);
        this.posButton9.setAction(this.goAction);
        this.posButton9.setIcon(IconFactory.getIcon("9.png"));
        this.posButton9.setActionCommand("9");
        this.keypadPanel.add(this.posButton9);
        this.posButton4.setAction(this.goAction);
        this.posButton4.setIcon(IconFactory.getIcon("4.png"));
        this.posButton4.setActionCommand("4");
        this.keypadPanel.add(this.posButton4);
        this.posButton5.setAction(this.goAction);
        this.posButton5.setIcon(IconFactory.getIcon("5.png"));
        this.posButton5.setActionCommand("5");
        this.keypadPanel.add(this.posButton5);
        this.posButton6.setAction(this.goAction);
        this.posButton6.setIcon(IconFactory.getIcon("6.png"));
        this.posButton6.setActionCommand("6");
        this.keypadPanel.add(this.posButton6);
        this.posButton1.setAction(this.goAction);
        this.posButton1.setIcon(IconFactory.getIcon("1.png"));
        this.posButton1.setActionCommand("1");
        this.keypadPanel.add(this.posButton1);
        this.posButton2.setAction(this.goAction);
        this.posButton2.setIcon(IconFactory.getIcon("2.png"));
        this.posButton2.setActionCommand("2");
        this.keypadPanel.add(this.posButton2);
        this.posButton3.setAction(this.goAction);
        this.posButton3.setIcon(IconFactory.getIcon("3.png"));
        this.posButton3.setActionCommand("3");
        this.keypadPanel.add(this.posButton3);
        this.btnDot = new PosButton();
        this.btnDot.setFocusable(false);
        this.btnDot.setAction(this.goAction);
        this.btnDot.setActionCommand(".");
        this.btnDot.setIcon(IconFactory.getIcon("dot.png"));
        this.keypadPanel.add(this.btnDot);
        this.posButton0.setAction(this.goAction);
        this.posButton0.setIcon(IconFactory.getIcon("0.png"));
        this.posButton0.setActionCommand("0");
        this.keypadPanel.add(this.posButton0);
        this.setLayout(new BorderLayout(0, 0));
        this.btnClear = new PosButton();
        this.btnClear.setFocusable(false);
        this.keypadPanel.add(this.btnClear);
        this.btnClear.setAction(this.goAction);
        this.btnClear.setIcon(IconFactory.getIcon("clear.png"));
        this.btnClear.setText(Messages.getString("NumericKeypad.0"));
        this.btnClear.setActionCommand(CLEAR);
        this.add((Component)this.keypadPanel, "Center");
    }

    public String getText() {
        return this.text;
    }

    public void setProtected(boolean isProtected) {
        this.isProtected = isProtected;
    }

    public boolean isProtected() {
        return this.isProtected;
    }

    public static void main(String[] args) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new NumericKeypad());
        JFrame frame = new JFrame();
        frame.getContentPane().add(p);
        frame.pack();
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }
}

