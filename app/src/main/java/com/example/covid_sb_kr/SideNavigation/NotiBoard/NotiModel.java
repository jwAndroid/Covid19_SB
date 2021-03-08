package com.example.covid_sb_kr.SideNavigation.NotiBoard;

public class NotiModel {

    private String title;
    private String description;
    private String createDate;

    public NotiModel() {
    }

    public NotiModel(String title, String description  , String createDate) {
        this.title = title;
        this.description = description;
        this.createDate = createDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
