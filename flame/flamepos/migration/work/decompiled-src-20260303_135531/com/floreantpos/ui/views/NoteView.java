/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.swing.FixedLengthDocument;
import com.floreantpos.swing.POSToggleButton;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TransparentPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class NoteView
extends JPanel
implements ActionListener,
ChangeListener {
    Font buttonFont = this.getFont().deriveFont(1, PosUIManager.getFontSize(24));
    String[] s1 = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
    String[] s2 = new String[]{"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"};
    String[] s3 = new String[]{"a", "s", "d", "f", "g", "h", "j", "k", "l", ";"};
    String[] s4 = new String[]{"z", "x", "c", "v", "b", "n", "m", "-", ",", "."};
    JTextArea note = new JTextArea();
    private ArrayList<PosButton> buttons = new ArrayList();
    Dimension size = PosUIManager.getSize(60, 60);

    public NoteView() {
        this.setLayout(new BorderLayout(5, 5));
        this.note.setWrapStyleWord(true);
        this.note.setLineWrap(true);
        this.note.setDocument(new FixedLengthDocument(255));
        TransparentPanel northPanel = new TransparentPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(this.note);
        northPanel.setPreferredSize(new Dimension(100, 60));
        northPanel.add(scrollPane);
        this.add((Component)northPanel, "North");
        TransparentPanel centerPanel = new TransparentPanel(new GridLayout(0, 1, 2, 2));
        centerPanel.add(this.addButtonsToPanel(this.s1));
        centerPanel.add(this.addButtonsToPanel(this.s2));
        centerPanel.add(this.addButtonsToPanel(this.s3));
        centerPanel.add(this.addButtonsToPanel(this.s4));
        this.add((Component)centerPanel, "Center");
        JPanel eastPanel = new JPanel(new GridLayout(0, 1, 2, 2));
        PosButton button = new PosButton();
        button.setText(Messages.getString("NoteView.40"));
        button.addActionListener(this);
        eastPanel.add(button);
        POSToggleButton toggleButton = new POSToggleButton();
        toggleButton.setText(Messages.getString("NoteView.41"));
        toggleButton.addChangeListener(this);
        eastPanel.add(toggleButton);
        button = new PosButton();
        button.setText(POSConstants.CLEAR);
        button.addActionListener(this);
        eastPanel.add(button);
        button = new PosButton();
        button.setText(POSConstants.CLEAR_ALL);
        button.addActionListener(this);
        eastPanel.add(button);
        eastPanel.setPreferredSize(PosUIManager.getSize(90, 70));
        this.add((Component)eastPanel, "East");
    }

    private TransparentPanel addButtonsToPanel(String[] buttonText) {
        TransparentPanel panel = new TransparentPanel(new GridLayout(0, this.s1.length, 2, 2));
        for (int i = 0; i < buttonText.length; ++i) {
            String s = buttonText[i];
            PosButton button = new PosButton();
            button.setText(s);
            button.setPreferredSize(this.size);
            button.addActionListener(this);
            button.setFont(this.buttonFont);
            this.buttons.add(button);
            panel.add(button);
        }
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.note.requestFocus();
        String s = e.getActionCommand();
        if (s.equals(POSConstants.CLEAR)) {
            String str = this.note.getText();
            if (str.length() > 0) {
                str = str.substring(0, str.length() - 1);
            }
            this.note.setText(str);
        } else if (s.equals(POSConstants.CLEAR_ALL)) {
            this.note.setText("");
        } else if (s.equals(Messages.getString("NoteView.43"))) {
            String str = this.note.getText();
            if (str == null) {
                str = "";
            }
            this.note.setText(str + " ");
        } else {
            String str = this.note.getText();
            if (str == null) {
                str = "";
            }
            this.note.setText(str + s);
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

    public String getNote() {
        return this.note.getText();
    }

    public void setNoteLength(int length) {
        this.note.setDocument(new FixedLengthDocument(length));
    }
}

