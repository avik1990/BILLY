package com.tpcodl.billingreading.models;

public class PaperModal {
    String paperId;
    String paperName;
    String paperVal;
    String paperStatus;
    String paperSetFlg;

    public PaperModal() {

    }

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getPaperVal() {
        return paperVal;
    }

    public void setPaperVal(String paperVal) {
        this.paperVal = paperVal;
    }

    public String getPaperStatus() {
        return paperStatus;
    }

    public void setPaperStatus(String paperStatus) {
        this.paperStatus = paperStatus;
    }

    public String getPaperSetFlg() {
        return paperSetFlg;
    }

    public void setPaperSetFlg(String paperSetFlg) {
        this.paperSetFlg = paperSetFlg;
    }
}
