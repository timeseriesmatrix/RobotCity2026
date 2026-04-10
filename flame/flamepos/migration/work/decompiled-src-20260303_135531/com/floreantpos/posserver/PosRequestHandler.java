/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jfree.util.Log
 */
package com.floreantpos.posserver;

import com.floreantpos.PosLog;
import com.floreantpos.main.Application;
import com.floreantpos.model.PaymentType;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.posserver.Check;
import com.floreantpos.posserver.Checks;
import com.floreantpos.posserver.POSDefaultInfo;
import com.floreantpos.posserver.POSRequest;
import com.floreantpos.posserver.POSResponse;
import com.floreantpos.posserver.PrintText;
import com.floreantpos.services.PosTransactionService;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.ui.views.payment.SettleTicketDialog;
import java.io.DataOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.jfree.util.Log;
import org.xml.sax.InputSource;

public class PosRequestHandler
extends Thread {
    private Socket socket;

    public PosRequestHandler(Socket socket) throws Exception {
        this.socket = socket;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try {
            while (true) {
                byte[] b1 = new byte[3000];
                this.socket.getInputStream().read(b1);
                String request = new String(b1).trim();
                if (request.length() <= 0) {
                    break;
                }
                PosLog.info(this.getClass(), "Request From Terminal==>[" + request + "]");
                int index = request.indexOf("<");
                request = request.substring(index);
                POSRequest posRequest = this.createRequest(request);
                POSResponse posResponse = this.createResponse(posRequest);
                String resp = this.convertResponseToString(posResponse);
                PosLog.info(this.getClass(), "Reponse to Terminal===>[" + resp + "]");
                DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());
                byte[] tosend = resp.getBytes();
                dos.write(tosend, 0, tosend.length);
                dos.flush();
            }
        }
        catch (Exception e) {
            Log.debug((Object)("Error:" + e));
        }
        finally {
            try {
                Thread.sleep(5000L);
                this.socket.close();
            }
            catch (Exception e) {
                Log.debug((Object)("Error:" + e));
            }
        }
    }

    private POSRequest createRequest(String requestString) throws Exception {
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(requestString));
        JAXBContext jaxbContext = JAXBContext.newInstance(POSRequest.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (POSRequest)unmarshaller.unmarshal(is);
    }

    private POSResponse createResponse(POSRequest posRequest) {
        POSResponse posResponse = new POSResponse();
        return posResponse;
    }

    private String convertResponseToString(POSResponse posResponse) throws Exception {
        JAXBContext messageContext = JAXBContext.newInstance(POSResponse.class);
        Marshaller marshaller = messageContext.createMarshaller();
        StringWriter dataWriter = new StringWriter();
        marshaller.marshal((Object)posResponse, dataWriter);
        String resp = "";
        resp = dataWriter.toString();
        resp = resp.replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"\\?>", "");
        String len = String.format("%05d", resp.length());
        resp = len + resp;
        return resp;
    }

    private POSResponse addAllTables(POSRequest posRequest) {
        POSResponse posResponse = new POSResponse();
        User user = UserDAO.getInstance().findUserBySecretKey(posRequest.posDefaultInfo.server);
        List<Ticket> ticketsForUser = TicketDAO.getInstance().findOpenTicketsForUser(user);
        Checks checks = new Checks();
        checks.setCheckList(new ArrayList<Check>());
        for (Ticket ticket : ticketsForUser) {
            List<Integer> tableNumbers = ticket.getTableNumbers();
            if (tableNumbers == null || tableNumbers.size() <= 0) continue;
            Check chk = new Check();
            String tableNumber = tableNumbers.get(0).toString();
            if (tableNumbers.get(0) < 10) {
                tableNumber = "0" + tableNumbers.get(0).toString();
            }
            chk.setTableNo(tableNumber);
            chk.setTableName("");
            chk.setChkName(String.valueOf(ticket.getId()));
            chk.setChkNo(String.valueOf(ticket.getId()));
            chk.setAmt(String.valueOf(Math.round((ticket.getDueAmount() - ticket.getTaxAmount()) * 100.0)));
            chk.setTax(String.valueOf(Math.round(ticket.getTaxAmount() * 100.0)));
            checks.getCheckList().add(chk);
        }
        posResponse.setChecks(checks);
        POSDefaultInfo posDefaultInfo = new POSDefaultInfo();
        posDefaultInfo.setServer(posRequest.posDefaultInfo.server);
        posDefaultInfo.setTable(posRequest.posDefaultInfo.table);
        posDefaultInfo.setCheck(posRequest.posDefaultInfo.check);
        posDefaultInfo.setRes("1");
        posDefaultInfo.setrText("success");
        posResponse.setPosDefaultInfo(posDefaultInfo);
        return posResponse;
    }

    private POSResponse addTable(POSRequest posRequest) {
        POSResponse posResponse = new POSResponse();
        User user = UserDAO.getInstance().findUserBySecretKey(posRequest.posDefaultInfo.server);
        List<Ticket> ticketsForUser = TicketDAO.getInstance().findOpenTicketsForUser(user);
        Checks checks = new Checks();
        checks.setCheckList(new ArrayList<Check>());
        for (Ticket ticket : ticketsForUser) {
            List<Integer> tableNumbers = ticket.getTableNumbers();
            if (tableNumbers == null || tableNumbers.size() <= 0 || !tableNumbers.contains(Integer.parseInt(posRequest.posDefaultInfo.table))) continue;
            Check chk = new Check();
            String tableNumber = tableNumbers.get(0).toString();
            if (tableNumbers.get(0) < 10) {
                tableNumber = "0" + tableNumbers.get(0).toString();
            }
            chk.setTableNo(String.valueOf(tableNumber));
            chk.setTableName("");
            chk.setChkName("");
            chk.setChkNo(String.valueOf(ticket.getId()));
            chk.setAmt(String.valueOf(Math.round((ticket.getDueAmount() - ticket.getTaxAmount()) * 100.0)));
            chk.setTax(String.valueOf(Math.round(ticket.getTaxAmount() * 100.0)));
            checks.getCheckList().add(chk);
            break;
        }
        posResponse.setChecks(checks);
        POSDefaultInfo posDefaultInfo = new POSDefaultInfo();
        posDefaultInfo.setServer(posRequest.posDefaultInfo.server);
        posDefaultInfo.setTable(posRequest.posDefaultInfo.table);
        posDefaultInfo.setCheck(posRequest.posDefaultInfo.check);
        posDefaultInfo.setRes("1");
        posDefaultInfo.setrText("success");
        posResponse.setPosDefaultInfo(posDefaultInfo);
        return posResponse;
    }

    private POSResponse applyPayment(POSRequest posRequest) {
        POSResponse posResponse = new POSResponse();
        Ticket ticket = TicketDAO.getInstance().loadFullTicket(Integer.parseInt(posRequest.posDefaultInfo.check));
        String paymentType = posRequest.payment.cardType;
        PosTransaction transaction = null;
        if (paymentType.equals("8")) {
            transaction = PaymentType.CASH.createTransaction();
            transaction.setCaptured(true);
        } else {
            if (paymentType.equals("1")) {
                transaction = PaymentType.CREDIT_MASTER_CARD.createTransaction();
            } else if (paymentType.equals("2")) {
                transaction = PaymentType.CREDIT_VISA.createTransaction();
            } else if (paymentType.equals("4")) {
                transaction = PaymentType.CREDIT_DISCOVERY.createTransaction();
            } else if (paymentType.equals("5")) {
                transaction = PaymentType.CREDIT_AMEX.createTransaction();
            }
            transaction.setCaptured(false);
            transaction.setCardNumber(posRequest.payment.acct);
            String exp = posRequest.payment.exp;
            if (exp != null) {
                transaction.setCardExpMonth(exp.substring(0, 2));
                transaction.setCardExpYear(exp.substring(2, 4));
            }
        }
        double tenderAmount = Double.parseDouble(posRequest.payment.pamt) / 100.0;
        transaction.setTenderAmount(tenderAmount);
        transaction.setTicket(ticket);
        if (tenderAmount >= ticket.getDueAmount()) {
            transaction.setAmount(ticket.getDueAmount());
        } else {
            transaction.setAmount(tenderAmount);
        }
        PosTransactionService transactionService = PosTransactionService.getInstance();
        try {
            double dueAmount = ticket.getDueAmount();
            transactionService.settleTicket(ticket, transaction);
            SettleTicketDialog.printTicket(ticket, transaction);
            SettleTicketDialog.showTransactionCompleteMsg(dueAmount, tenderAmount, ticket, transaction);
            if (SettleTicketDialog.waitDialog.isVisible()) {
                SettleTicketDialog.waitDialog.setCanceled(false);
                SettleTicketDialog.waitDialog.dispose();
                RootView.getInstance().showDefaultView();
            }
            POSDefaultInfo posDefaultInfo = new POSDefaultInfo();
            posDefaultInfo.setServer(posRequest.posDefaultInfo.server);
            posDefaultInfo.setTable(posRequest.posDefaultInfo.table);
            posDefaultInfo.setCheck(posRequest.posDefaultInfo.check);
            posDefaultInfo.setRes("1");
            posDefaultInfo.setrText("success");
            posResponse.setPosDefaultInfo(posDefaultInfo);
            return posResponse;
        }
        catch (Exception e) {
            Log.debug((Object)("Error:" + e));
            return posResponse;
        }
    }

    private POSResponse printCheck(POSRequest posRequest) {
        POSResponse posResponse = new POSResponse();
        POSDefaultInfo posDefaultInfo = new POSDefaultInfo();
        posDefaultInfo.setServer(posRequest.posDefaultInfo.server);
        posDefaultInfo.setTable(posRequest.posDefaultInfo.table);
        posDefaultInfo.setCheck(posRequest.posDefaultInfo.check);
        List<PrintText> printTexts = this.getPrintText(Integer.parseInt(posRequest.posDefaultInfo.check));
        posResponse.setPrintChecks(printTexts);
        posResponse.setPosDefaultInfo(posDefaultInfo);
        return posResponse;
    }

    private List<PrintText> getPrintText(Integer checkId) {
        Ticket ticket = TicketDAO.getInstance().loadFullTicket(checkId);
        ArrayList<PrintText> printTexts = new ArrayList<PrintText>();
        Restaurant restaurant = Application.getInstance().getRestaurant();
        printTexts.add(new PrintText(restaurant.getName(), "center"));
        printTexts.add(new PrintText(restaurant.getAddressLine1(), "center"));
        printTexts.add(new PrintText(restaurant.getTelephone(), "center"));
        printTexts.add(new PrintText("***Payment Receipt***", "center"));
        String line = "__________________________________";
        printTexts.add(new PrintText(line, "center"));
        printTexts.add(new PrintText("*" + ticket.getTicketType() + "*", "center"));
        printTexts.add(new PrintText("Terminal: " + ticket.getTerminal().getId()));
        printTexts.add(new PrintText("CHK#: " + ticket.getId()));
        printTexts.add(new PrintText("Table: " + ticket.getTableNumbers()));
        printTexts.add(new PrintText("Guests: " + ticket.getNumberOfGuests()));
        printTexts.add(new PrintText("Server: " + ticket.getOwner().getFirstName()));
        printTexts.add(new PrintText("Printed: " + new Date()));
        printTexts.add(new PrintText(line, "center"));
        printTexts.add(new PrintText("ITEM     QTY    SUB", "right"));
        printTexts.add(new PrintText(line, "center"));
        if (ticket.getTicketItems() != null) {
            List<TicketItem> ticketItems = ticket.getTicketItems();
            for (TicketItem ticketItem : ticketItems) {
                printTexts.add(new PrintText(ticketItem.getName() + "   " + ticketItem.getItemCount() + "    " + ticketItem.getUnitPriceDisplay(), "right"));
            }
        }
        printTexts.add(new PrintText(line, "center"));
        printTexts.add(new PrintText("Total    " + ticket.getSubtotalAmount(), "right"));
        printTexts.add(new PrintText("Tax    " + ticket.getTaxAmount(), "right"));
        printTexts.add(new PrintText(line, "center"));
        printTexts.add(new PrintText("Net Amount    " + ticket.getTotalAmount(), "right"));
        printTexts.add(new PrintText("Paid Amount   " + ticket.getPaidAmount(), "right"));
        printTexts.add(new PrintText("Due Amount    " + ticket.getDueAmount(), "right"));
        return printTexts;
    }
}

