/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.miginfocom.swing.MigLayout
 *  org.jfree.util.Log
 */
package com.floreantpos.config.ui;

import com.floreantpos.PosLog;
import com.floreantpos.config.ui.ConfigurationView;
import com.floreantpos.main.Application;
import com.floreantpos.swing.IntegerTextField;
import com.floreantpos.util.JarUtil;
import java.awt.Component;
import java.awt.LayoutManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import net.miginfocom.swing.MigLayout;
import org.jfree.util.Log;

public class InginicoConfigurationView
extends ConfigurationView {
    public static final String COMMSETTING_INI = "commsetting.ini";
    private JLabel lblCommunicationTitle;
    private JLabel lblIp;
    private JLabel lblPort;
    private JLabel lblTimeOut;
    private IntegerTextField txtIp1;
    private IntegerTextField txtIp2;
    private IntegerTextField txtIp3;
    private IntegerTextField txtIp4;
    private IntegerTextField txtPort;
    private IntegerTextField txtTimeOut;

    public InginicoConfigurationView() {
        this.createPacksConfiguration();
    }

    private void createPacksConfiguration() {
        this.lblCommunicationTitle = new JLabel("Communication type:");
        this.lblIp = new JLabel("Ip");
        this.lblPort = new JLabel("Port");
        this.lblTimeOut = new JLabel("Timeout");
        this.txtIp1 = new IntegerTextField(7);
        this.txtIp2 = new IntegerTextField(7);
        this.txtIp3 = new IntegerTextField(7);
        this.txtIp4 = new IntegerTextField(7);
        this.txtPort = new IntegerTextField(20);
        this.txtTimeOut = new IntegerTextField(20);
        this.setLayout((LayoutManager)new MigLayout("", " []10[grow]", ""));
        this.add(this.lblCommunicationTitle);
        this.add((Component)new JLabel("TCP"), "wrap");
        this.add(this.lblPort);
        this.add((Component)this.txtPort, "grow,wrap");
        this.add(this.lblTimeOut);
        this.add((Component)this.txtTimeOut, "grow");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean save() throws Exception {
        if (this.ipPortisEmpty(this.txtPort) || this.ipPortisEmpty(this.txtIp1) || this.ipPortisEmpty(this.txtIp2) || this.ipPortisEmpty(this.txtIp3) || this.ipPortisEmpty(this.txtIp4) || this.ipPortisEmpty(this.txtTimeOut)) {
            JOptionPane.showMessageDialog(null, "Please fill all the fields properly ");
            return false;
        }
        String jarLocation = JarUtil.getJarLocation(Application.class);
        PrintWriter writer = null;
        try {
            File newFile = new File(jarLocation, COMMSETTING_INI);
            writer = new PrintWriter(newFile, "UTF-8");
            writer.println("[COMMUNICATE]");
            writer.println("PORT=" + this.txtPort.getText());
            writer.println("DestPort=10009");
            writer.println("IP=" + this.txtIp1.getText() + String.valueOf(".") + this.txtIp2.getText() + String.valueOf(".") + this.txtIp3.getText() + String.valueOf(".") + this.txtIp4.getText());
            writer.println("DestIP=10.0.2.15");
            writer.println("SERIALPORT=COM1");
            writer.println("TimeOut=3000");
            writer.println("TIMEOUT=" + this.txtTimeOut.getText());
            writer.println("CommType=TCP");
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception exception) {}
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void initialize() throws Exception {
        String jarLocation = JarUtil.getJarLocation(Application.class);
        BufferedReader fileReader = null;
        try {
            String line;
            File file = new File(jarLocation, COMMSETTING_INI);
            if (!file.exists()) {
                this.txtPort.setText(String.valueOf("5656"));
                this.txtTimeOut.setText(String.valueOf("5000"));
                return;
            }
            fileReader = new BufferedReader(new FileReader(file));
            block14: while ((line = fileReader.readLine()) != null) {
                String[] str = line.split("=");
                for (int i = 0; i < str.length; ++i) {
                    if (str[i].equals("PORT")) {
                        String port = str[i + 1];
                        this.txtPort.setText(port);
                        continue block14;
                    }
                    if (str[i].equals("IP")) {
                        String ip = str[i + 1];
                        String[] ipStr = ip.split("\\.");
                        this.txtIp1.setText(ipStr[0]);
                        this.txtIp2.setText(ipStr[1]);
                        this.txtIp3.setText(ipStr[2]);
                        this.txtIp4.setText(ipStr[3]);
                    }
                    if (!str[i].equals("TIMEOUT")) continue;
                    String timeOut = str[i + 1];
                    this.txtTimeOut.setText(timeOut);
                    continue block14;
                }
            }
        }
        catch (IOException e) {
            Log.debug((Object)e);
        }
        finally {
            try {
                fileReader.close();
            }
            catch (Exception exception) {}
        }
    }

    public boolean ipPortisEmpty(IntegerTextField txtF) {
        return txtF.getText().equals(String.valueOf(""));
    }

    @Override
    public String getName() {
        return null;
    }
}

