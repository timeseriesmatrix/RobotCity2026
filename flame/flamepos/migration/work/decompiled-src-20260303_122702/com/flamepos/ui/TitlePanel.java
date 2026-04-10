/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui;

import com.floreantpos.POSConstants;
import com.floreantpos.swing.PosUIManager;
import com.floreantpos.swing.TransparentPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.UIManager;

public class TitlePanel
extends TransparentPanel {
    protected JSeparator jSeparator1;
    protected JLabel lblTitle;

    public TitlePanel() {
        this.initComponents();
    }

    private void initComponents() {
        this.lblTitle = new JLabel();
        this.lblTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.jSeparator1 = new JSeparator();
        this.lblTitle.setFont(this.getTitleFont());
        this.lblTitle.setForeground(this.getTitleColor());
        this.lblTitle.setText(POSConstants.TITLE);
        this.setLayout(new BorderLayout(5, 5));
        this.add(this.lblTitle);
        this.add((Component)this.jSeparator1, "South");
        this.setPreferredSize(PosUIManager.getSize(300, 60));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        int x = 0;
        int y = 0;
        float width = this.getWidth();
        float height = this.getHeight();
        Color color1 = Color.WHITE;
        Color color2 = this.getBackground();
        g2.setPaint(new GradientPaint(x, y, color1, width, height, color2));
        g2.fillRect(x, y, (int)width, (int)height);
    }

    private Font getTitleFont() {
        Font f = this.lblTitle.getFont();
        f = f.deriveFont(1, PosUIManager.getTitleFontSize());
        return f;
    }

    private Color getTitleColor() {
        return UIManager.getColor("TitledBorder.titleColor");
    }

    public String getTitle() {
        return this.lblTitle.getText();
    }

    public void setTitle(String title) {
        this.lblTitle.setText(title);
    }

    public void setTextAlignment(int alignment) {
        this.lblTitle.setHorizontalAlignment(alignment);
    }
}

