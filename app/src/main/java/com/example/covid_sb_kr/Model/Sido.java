package com.example.covid_sb_kr.Model;

public class Sido {

    private String createDt;
    private String deathCnt;
    private String defCnt;
    private String gubun;
    private String incDec;
    private String isolClearCnt;
    private String isolIngCnt;
    private String localOccCnt;
    private String overFlowCnt;

    public Sido() {
    }

    public Sido(String createDt, String deathCnt, String defCnt, String gubun, String incDec, String isolClearCnt, String isolIngCnt , String localOccCnt , String overFlowCnt) {
        this.createDt = createDt;
        this.deathCnt = deathCnt;
        this.defCnt = defCnt;
        this.gubun = gubun;
        this.incDec = incDec;
        this.isolClearCnt = isolClearCnt;
        this.isolIngCnt = isolIngCnt;
        this.localOccCnt = localOccCnt;
        this.overFlowCnt = overFlowCnt;
    }

    public String getOverFlowCnt() {
        return overFlowCnt;
    }

    public void setOverFlowCnt(String overFlowCnt) {
        this.overFlowCnt = overFlowCnt;
    }

    public String getLocalOccCnt() {
        return localOccCnt;
    }

    public void setLocalOccCnt(String localOccCnt) {
        this.localOccCnt = localOccCnt;
    }

    public String getCreateDt() {
        return createDt;
    }

    public void setCreateDt(String createDt) {
        this.createDt = createDt;
    }

    public String getDeathCnt() {
        return deathCnt;
    }

    public void setDeathCnt(String deathCnt) {
        this.deathCnt = deathCnt;
    }

    public String getDefCnt() {
        return defCnt;
    }

    public void setDefCnt(String defCnt) {
        this.defCnt = defCnt;
    }

    public String getGubun() {
        return gubun;
    }

    public void setGubun(String gubun) {
        this.gubun = gubun;
    }

    public String getIncDec() {
        return incDec;
    }

    public void setIncDec(String incDec) {
        this.incDec = incDec;
    }

    public String getIsolClearCnt() {
        return isolClearCnt;
    }

    public void setIsolClearCnt(String isolClearCnt) {
        this.isolClearCnt = isolClearCnt;
    }

    public String getIsolIngCnt() {
        return isolIngCnt;
    }

    public void setIsolIngCnt(String isolIngCnt) {
        this.isolIngCnt = isolIngCnt;
    }
}
