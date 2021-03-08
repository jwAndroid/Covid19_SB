package com.example.covid_sb_kr.Model;

public class Hospital {

    private String hospTyTpCd; //선정유형
    private String sgguNm; //시군구명
    private String sidoNm; //시도명
    private String spclAdmTyCd; //구분코드
    private String telno; //전화번호
    private String yadmNm; //기관명


    public Hospital() {
    }

    public Hospital(String hospTyTpCd, String sgguNm, String sidoNm, String spclAdmTyCd, String telno, String yadmNm) {
        this.hospTyTpCd = hospTyTpCd;
        this.sgguNm = sgguNm;
        this.sidoNm = sidoNm;
        this.spclAdmTyCd = spclAdmTyCd;
        this.telno = telno;
        this.yadmNm = yadmNm;
    }

    public String getHospTyTpCd() {
        return hospTyTpCd;
    }

    public void setHospTyTpCd(String hospTyTpCd) {
        this.hospTyTpCd = hospTyTpCd;
    }

    public String getSgguNm() {
        return sgguNm;
    }

    public void setSgguNm(String sgguNm) {
        this.sgguNm = sgguNm;
    }

    public String getSidoNm() {
        return sidoNm;
    }

    public void setSidoNm(String sidoNm) {
        this.sidoNm = sidoNm;
    }

    public String getSpclAdmTyCd() {
        return spclAdmTyCd;
    }

    public void setSpclAdmTyCd(String spclAdmTyCd) {
        this.spclAdmTyCd = spclAdmTyCd;
    }

    public String getTelno() {
        return telno;
    }

    public void setTelno(String telno) {
        this.telno = telno;
    }

    public String getYadmNm() {
        return yadmNm;
    }

    public void setYadmNm(String yadmNm) {
        this.yadmNm = yadmNm;
    }
}
