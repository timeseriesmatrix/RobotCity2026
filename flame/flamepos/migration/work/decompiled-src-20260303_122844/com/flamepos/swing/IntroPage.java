/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import com.floreantpos.IconFactory;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

public class IntroPage
extends JPanel {
    private Image image;
    private Image image2 = IconFactory.getIcon("/images", "open_initiative.png").getImage();
    private boolean scaleToSize = true;

    public IntroPage() {
    }

    public IntroPage(Image image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = 0;
        int y = 0;
        int width = this.getWidth();
        int height = this.getHeight();
        if (this.scaleToSize) {
            g.drawImage(this.image, x, y, width, height, this);
        } else {
            g.drawImage(this.image, x, y, this);
        }
        g.drawImage(this.image2, x + 30, height - 190, width - 60, 200, this);
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public boolean isScaleToSize() {
        return this.scaleToSize;
    }

    public void setScaleToSize(boolean scaleToSize) {
        this.scaleToSize = scaleToSize;
    }
}

