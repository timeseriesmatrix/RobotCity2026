/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.swingx.JXCollapsiblePane
 */
package com.floreantpos.swing;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.PosLog;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TransparentPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.JXCollapsiblePane;

public class QwertyKeyPad
extends JXCollapsiblePane
implements ActionListener,
ChangeListener {
    Font buttonFont = this.getFont().deriveFont(1, PosUIManager.getFontSize(24));
    String[] s1 = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
    String[] s2 = new String[]{"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"};
    String[] s3 = new String[]{"a", "s", "d", "f", "g", "h", "j", "k", "l", ";"};
    String[] s4 = new String[]{"z", "x", "c", "v", "b", "n", "m", "-", ",", "."};
    private ArrayList<PosButton> buttons = new ArrayList();
    Dimension size = PosUIManager.getSize(60, 60);

    public QwertyKeyPad() {
        this.createUI();
    }

    private void createUI() {
        this.setLayout(new BorderLayout(0, 0));
        TransparentPanel centerPanel = new TransparentPanel(new GridLayout(0, 1, 2, 2));
        centerPanel.add(this.addButtonsToPanel(this.s1));
        centerPanel.add(this.addButtonsToPanel(this.s2));
        centerPanel.add(this.addButtonsToPanel(this.s3));
        centerPanel.add(this.addButtonsToPanel(this.s4));
        this.add(centerPanel, "Center");
        JPanel eastPanel = new JPanel(new GridLayout(0, 1, 2, 2));
        PosButton button = new PosButton();
        button.setText(Messages.getString("QwertyKeyPad.1"));
        button.setFocusable(false);
        button.addActionListener(this);
        eastPanel.add(button);
        POSToggleButton toggleButton = new POSToggleButton();
        toggleButton.setText(Messages.getString("QwertyKeyPad.2"));
        toggleButton.setFocusable(false);
        toggleButton.addChangeListener(this);
        eastPanel.add(toggleButton);
        button = new PosButton();
        button.setText(POSConstants.CLEAR);
        button.setFocusable(false);
        button.addActionListener(this);
        eastPanel.add(button);
        button = new PosButton();
        button.setFocusable(false);
        button.setText(POSConstants.CLEAR_ALL);
        button.addActionListener(this);
        eastPanel.add(button);
        eastPanel.setPreferredSize(PosUIManager.getSize(90, 0));
        this.add(eastPanel, "East");
    }

    private TransparentPanel addButtonsToPanel(String[] buttonText) {
        TransparentPanel panel = new TransparentPanel(new GridLayout(0, this.s1.length, 2, 2));
        for (int i = 0; i < buttonText.length; ++i) {
            String s = buttonText[i];
            PosButton button = new PosButton();
            button.setText(s);
            button.setMinimumSize(this.size);
            button.addActionListener(this);
            button.setFont(this.buttonFont);
            button.setFocusable(false);
            this.buttons.add(button);
            panel.add(button);
        }
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        JTextComponent note = null;
        if (!(focusOwner instanceof JTextComponent)) {
            return;
        }
        note = (JTextComponent)focusOwner;
        String s = e.getActionCommand();
        if (s.equals(POSConstants.CLEAR)) {
            String str = note.getText();
            if (str.length() > 0) {
                str = str.substring(0, str.length() - 1);
            }
            note.setText(str);
        } else if (s.equals(POSConstants.CLEAR_ALL)) {
            note.setText("");
        } else if (s.equals(Messages.getString("QwertyKeyPad.0"))) {
            String str = note.getText();
            if (str == null) {
                str = "";
            }
            note.setText(str + " ");
        } else {
            String str = note.getText();
            if (str == null) {
                str = "";
            }
            note.setText(str + s);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JToggleButton b = (JToggleButton)e.getSource();
        if (b.isSelected()) {
            for (PosButton button : this.buttons) {
                button.setText(button.getText().toUpperCase());
            }
        } else {
            for (PosButton button : this.buttons) {
                button.setText(button.getText().toLowerCase());
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        final QwertyKeyPad comp = new QwertyKeyPad();
        frame.add((Component)((Object)comp));
        comp.addComponentListener(new ComponentListener(){

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentResized(ComponentEvent e) {
                PosLog.info(QwertyKeyPad.class, "" + comp.getSize());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
        frame.pack();
        frame.setVisible(true);
    }
}

