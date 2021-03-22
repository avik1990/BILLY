package com.tpcodl.billingreading.models;

public class PrinterModal {
    String printerId;
    String printerName;
    String printerVal;
    String printerStatus;
    String printerSetFlg;

    public PrinterModal() {

    }

    public PrinterModal(String printerId, String printerName, String printerVal, String printerStatus, String printerSetFlg) {
        this.printerId = printerId;
        this.printerName = printerName;
        this.printerVal = printerVal;
        this.printerStatus = printerStatus;
        this.printerSetFlg = printerSetFlg;
    }

    public String getPrinterId() {
        return printerId;
    }

    public void setPrinterId(String printerId) {
        this.printerId = printerId;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public String getPrinterVal() {
        return printerVal;
    }

    public void setPrinterVal(String printerVal) {
        this.printerVal = printerVal;
    }

    public String getPrinterStatus() {
        return printerStatus;
    }

    public void setPrinterStatus(String printerStatus) {
        this.printerStatus = printerStatus;
    }

    public String getPrinterSetFlg() {
        return printerSetFlg;
    }

    public void setPrinterSetFlg(String printerSetFlg) {
        this.printerSetFlg = printerSetFlg;
    }
}
