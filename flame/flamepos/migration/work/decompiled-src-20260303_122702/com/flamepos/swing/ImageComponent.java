/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.swing;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class ImageComponent
extends JPanel {
    private Image image;
    private Integer imageWidth;
    private Integer imageHeight;
    private boolean scaleToSize = true;

    public ImageComponent() {
    }

    public ImageComponent(Image image) {
        this.image = image;
        BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
        this.imageWidth = bimage.getWidth();
        this.imageHeight = bimage.getHeight();
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

    public Integer getImageWidth() {
        return this.imageWidth;
    }

    public Integer getImageHeight() {
        return this.imageHeight;
    }
}

