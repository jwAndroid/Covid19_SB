package com.example.covid_sb_kr.Model;

public class SidoCardModel {

    private String sidoName;
    private String sidoCnt;

    public SidoCardModel() { }

    public SidoCardModel(String sidoName , String sidoCnt) {
        this.sidoName = sidoName;
    }

    public String getSidoCnt() {
        return sidoCnt;
    }

    public void setSidoCnt(String sidoCnt) {
        this.sidoCnt = sidoCnt;
    }

    public String getSidoName() {
        return sidoName;
    }

    public void setSidoName(String sidoName) {
        this.sidoName = sidoName;
    }
}
