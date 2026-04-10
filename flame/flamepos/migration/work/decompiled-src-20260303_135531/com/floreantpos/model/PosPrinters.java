/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 */
package com.floreantpos.model;

import com.floreantpos.PosLog;
import com.floreantpos.model.Printer;
import com.floreantpos.model.TerminalPrinters;
import com.floreantpos.model.VirtualPrinter;
import com.floreantpos.model.dao.TerminalPrintersDAO;
import com.floreantpos.model.dao.VirtualPrinterDAO;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.io.FileUtils;

@XmlRootElement(name="printers")
public class PosPrinters {
    private static String reportPrinter;
    private static String receiptPrinter;
    private Printer defaultKitchenPrinter;
    private static List<Printer> kitchenPrinters;
    private Map<VirtualPrinter, Printer> kitchePrinterMap = new HashMap<VirtualPrinter, Printer>();

    public String getReportPrinter() {
        return reportPrinter;
    }

    public void setReportPrinter(String reportPrinter) {
        PosPrinters.reportPrinter = reportPrinter;
    }

    public String getReceiptPrinter() {
        return receiptPrinter;
    }

    public void setReceiptPrinter(String receiptPrinter) {
        PosPrinters.receiptPrinter = receiptPrinter;
    }

    public List<Printer> getKitchenPrinters() {
        if (kitchenPrinters == null) {
            kitchenPrinters = new ArrayList<Printer>(4);
        }
        return kitchenPrinters;
    }

    public void setKitchenPrinters(List<Printer> kitchenPrinters) {
        PosPrinters.kitchenPrinters = kitchenPrinters;
    }

    public void addKitchenPrinter(Printer printer) {
        this.getKitchenPrinters().add(printer);
    }

    public void setDefaultKitchenPrinter(Printer defaultKitchenPrinter) {
        this.defaultKitchenPrinter = defaultKitchenPrinter;
    }

    public Printer getDefaultKitchenPrinter() {
        if (this.getKitchenPrinters().size() > 0) {
            this.defaultKitchenPrinter = kitchenPrinters.get(0);
            for (Printer printer : kitchenPrinters) {
                if (!printer.isDefaultPrinter()) continue;
                this.defaultKitchenPrinter = printer;
                break;
            }
        }
        return this.defaultKitchenPrinter;
    }

    public Printer getDefaultPrinter() {
        if (this.getKitchenPrinters().size() > 0) {
            this.defaultKitchenPrinter = kitchenPrinters.get(0);
            for (Printer printer : kitchenPrinters) {
                if (!printer.isDefaultPrinter()) continue;
                this.defaultKitchenPrinter = printer;
                break;
            }
        }
        return this.defaultKitchenPrinter;
    }

    public Printer getKitchenPrinterFor(VirtualPrinter vp) {
        return this.kitchePrinterMap.get(vp);
    }

    private void populatePrinterMaps() {
        this.kitchePrinterMap.clear();
        for (Printer printer : this.getKitchenPrinters()) {
            this.kitchePrinterMap.put(printer.getVirtualPrinter(), printer);
        }
    }

    public void save() {
        try {
            this.getDefaultKitchenPrinter();
            this.populatePrinterMaps();
            File file = new File("config", "printers.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(PosPrinters.class);
            Marshaller m = jaxbContext.createMarshaller();
            m.setProperty("jaxb.formatted.output", Boolean.TRUE);
            StringWriter writer = new StringWriter();
            m.marshal((Object)this, writer);
            FileUtils.write((File)file, (CharSequence)writer.toString());
        }
        catch (Exception e) {
            PosLog.error(this.getClass(), e);
        }
    }

    public static PosPrinters load() {
        try {
            List<TerminalPrinters> terminalPrinters = TerminalPrintersDAO.getInstance().findTerminalPrinters();
            PosPrinters printers = new PosPrinters();
            ArrayList<Printer> terminalActivePrinters = new ArrayList<Printer>();
            for (TerminalPrinters terminalPrinter : terminalPrinters) {
                int printerType = terminalPrinter.getVirtualPrinter().getType();
                if (printerType == 0) {
                    if (terminalPrinter.getPrinterName() == null) continue;
                    reportPrinter = terminalPrinter.getPrinterName();
                    continue;
                }
                if (printerType == 1) {
                    receiptPrinter = terminalPrinter.getPrinterName();
                    continue;
                }
                if (printerType == 4) continue;
                Printer printer = new Printer(terminalPrinter.getVirtualPrinter(), terminalPrinter.getPrinterName());
                terminalActivePrinters.add(printer);
            }
            kitchenPrinters = terminalActivePrinters;
            if (receiptPrinter == null) {
                receiptPrinter = PosPrinters.getDefaultPrinterName();
            }
            if (reportPrinter == null) {
                reportPrinter = PosPrinters.getDefaultPrinterName();
            }
            if (kitchenPrinters == null || kitchenPrinters.isEmpty()) {
                Printer printer = new Printer(new VirtualPrinter(1, "kitchen"), PosPrinters.getDefaultPrinterName());
                kitchenPrinters.add(printer);
            }
            printers.populatePrinterMaps();
            return printers;
        }
        catch (Exception e) {
            PosLog.error(PosPrinters.class, e.getMessage());
            return null;
        }
    }

    public static String getDefaultPrinterName() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        if (services.length > 0) {
            return services[0].getName();
        }
        return null;
    }

    private static void initVirtualPrinter(Printer printer) {
        if (printer == null) {
            return;
        }
        VirtualPrinter virtualPrinter = printer.getVirtualPrinter();
        VirtualPrinterDAO dao = VirtualPrinterDAO.getInstance();
        VirtualPrinter printerByName = dao.findPrinterByName(virtualPrinter.getName());
        if (printerByName != null) {
            printer.setVirtualPrinter(printerByName);
        } else {
            Integer id = virtualPrinter.getId();
            if (dao.get(id) != null) {
                dao.saveOrUpdate(virtualPrinter);
            } else {
                dao.save(virtualPrinter);
            }
        }
    }
}

