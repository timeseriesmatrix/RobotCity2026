/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  jssc.SerialPort
 *  jssc.SerialPortEvent
 *  jssc.SerialPortEventListener
 *  jssc.SerialPortException
 */
package com.floreantpos.util;

import com.floreantpos.PosLog;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialPortUtil {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String readWeight(String comPort) throws SerialPortException {
        final SerialPort serialPort = new SerialPort(comPort);
        serialPort.openPort();
        serialPort.setParams(9600, 7, 2, 2);
        serialPort.setFlowControlMode(15);
        final StringBuilder messageBuilder = new StringBuilder();
        serialPort.addEventListener(new SerialPortEventListener(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void serialEvent(SerialPortEvent event) {
                block7: {
                    try {
                        byte[] buffer;
                        if (!event.isRXCHAR() || event.getEventValue() <= 0) break block7;
                        for (byte b : buffer = serialPort.readBytes()) {
                            if ((b == 13 || b == 10) && messageBuilder.length() > 0) {
                                StringBuilder stringBuilder = messageBuilder;
                                synchronized (stringBuilder) {
                                    messageBuilder.notify();
                                    break;
                                }
                            }
                            messageBuilder.append((char)b);
                        }
                    }
                    catch (Exception e) {
                        PosLog.error(this.getClass(), e);
                    }
                }
            }
        });
        byte[] data = new byte[]{87, 13, 0};
        serialPort.writeBytes(data);
        StringBuilder stringBuilder = messageBuilder;
        synchronized (stringBuilder) {
            try {
                messageBuilder.wait(2000L);
            }
            catch (InterruptedException e) {
                serialPort.closePort();
                return messageBuilder.toString();
            }
        }
        serialPort.closePort();
        return messageBuilder.toString();
    }
}

