/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.posserver;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="PrintText")
public class PrintText {
    public static final String CENTER = "center";
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    String text = "";
    int lengthLimit = 35;

    public PrintText() {
    }

    public PrintText(String txt) {
        if (txt.length() > 42) {
            txt = txt.substring(0, 42);
        }
        int space = this.lengthLimit - txt.length();
        for (int i = 1; i < space; ++i) {
            txt = txt + " ";
        }
        this.text = txt;
    }

    public PrintText(String txt, String align) {
        if (txt.length() > 42) {
            txt = txt.substring(0, 42);
        }
        if (align.equals(CENTER)) {
            int space = (this.lengthLimit - txt.length()) / 2;
            for (int i = 1; i < space; ++i) {
                txt = " " + txt + " ";
            }
            this.text = txt;
        } else if (align.equals(RIGHT)) {
            int space = this.lengthLimit - txt.length();
            for (int i = 1; i < space; ++i) {
                txt = " " + txt;
            }
            this.text = txt;
        } else if (align.equals(LEFT)) {
            int space = this.lengthLimit - txt.length();
            for (int i = 1; i < space; ++i) {
                txt = txt + " ";
            }
            this.text = txt;
        }
    }

    @XmlAttribute(name="text")
    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

