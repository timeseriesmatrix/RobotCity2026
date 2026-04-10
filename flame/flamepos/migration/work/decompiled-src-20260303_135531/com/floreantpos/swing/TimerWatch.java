/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.joda.time.Duration
 *  org.joda.time.Instant
 *  org.joda.time.Interval
 */
package com.floreantpos.swing;

import com.floreantpos.config.AppConfig;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

public class TimerWatch
extends JPanel
implements ActionListener {
    Timer updateTimer = new Timer(1000, this);
    JLabel timerLabel = new JLabel();
    private final Date date;
    public Color backColor;
    public Color textColor;

    public TimerWatch(Date date) {
        this.date = date;
        this.timerLabel.setFont(this.timerLabel.getFont().deriveFont(1));
        this.timerLabel.setHorizontalAlignment(4);
        this.actionPerformed(null);
        this.add(this.timerLabel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Interval interval = new Interval(this.date.getTime(), new Instant().getMillis());
        Duration duration = interval.toDuration();
        int timeOutValueYellow = 300;
        int timeOutValueRed = 600;
        if (AppConfig.getString("YellowTimeOut") != null) {
            timeOutValueYellow = Integer.parseInt(AppConfig.getString("YellowTimeOut"));
        }
        if (AppConfig.getString("RedTimeOut") != null) {
            timeOutValueRed = Integer.parseInt(AppConfig.getString("RedTimeOut"));
        }
        if ((long)timeOutValueYellow < duration.getStandardSeconds() && (long)timeOutValueRed > duration.getStandardSeconds()) {
            this.backColor = Color.yellow;
            this.textColor = Color.black;
        } else if ((long)timeOutValueRed < duration.getStandardSeconds()) {
            this.backColor = Color.red;
            this.textColor = Color.white;
        } else {
            this.backColor = Color.white;
            this.textColor = Color.black;
        }
        this.timerLabel.setText(duration.getStandardHours() + ":" + duration.getStandardMinutes() % 60L + ":" + duration.getStandardSeconds() % 60L);
    }

    public void start() {
        this.updateTimer.start();
    }

    public void stop() {
        this.updateTimer.stop();
    }

    public Color getColor() {
        return null;
    }
}

