/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.posserver;

import com.floreantpos.PosLog;
import com.floreantpos.model.dao._RootDAO;
import com.floreantpos.posserver.PosRequestHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PosServer
implements Runnable {
    public static final int PORT = 5656;

    public PosServer() {
        new Thread(this).start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        ServerSocket ss = null;
        try {
            PosLog.info(this.getClass(), "listening on ...5656");
            ss = new ServerSocket(5656);
            PosServer.listen(ss);
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
        finally {
            if (ss != null) {
                try {
                    ss.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    static void listen(ServerSocket ss) throws Exception {
        String resp = "";
        String ids = "";
        while (true) {
            PosLog.info(PosServer.class, "Waiting For Connections....");
            Socket s = ss.accept();
            PosRequestHandler posRequestHandler = new PosRequestHandler(s);
            posRequestHandler.start();
        }
    }

    public static void main(String[] args) throws Exception {
        _RootDAO.initialize();
    }
}

