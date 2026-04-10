/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.actions.ActionCommand;
import com.floreantpos.swing.PosButton;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.Timer;

public class PosBlinkButton
extends PosButton
implements ActionListener {
    private boolean blinking = false;
    private boolean blinked = false;
    private Color originalColor = this.getBackground();
    private Timer blinkTimer = new Timer(500, this);

    public PosBlinkButton() {
    }

    public PosBlinkButton(Action a) {
        super(a);
    }

    public PosBlinkButton(ActionCommand command, ActionListener listener) {
        super(command, listener);
    }

    public PosBlinkButton(ActionCommand command) {
        super(command);
    }

    public PosBlinkButton(ImageIcon imageIcon) {
        super(imageIcon);
    }

    public PosBlinkButton(String text, Action action) {
        super(text, action);
    }

    public PosBlinkButton(String text, ActionCommand command) {
        super(text, command);
    }

    public PosBlinkButton(String text) {
        super(text);
    }

    public boolean isBlinking() {
        return this.blinking;
    }

    public void setBlinking(boolean blinking) {
        this.blinking = blinking;
        if (blinking) {
            this.blinkTimer.start();
        } else {
            this.blinkTimer.stop();
            this.setBackground(this.originalColor);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean bl = this.blinked = !this.blinked;
        if (this.blinked) {
            this.setBackground(Color.YELLOW.darker());
        } else {
            this.setBackground(Color.YELLOW);
        }
    }
}

