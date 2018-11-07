package com.cs180.simplenote.simplenoteapp;

public class Notes {

    private String title;
    private String text;
    private String date;
    private String labelName;


    public Notes(){

    }

    public Notes(String title, String text, String date, String labelName) {
        this.title = title;
        this.text = text;
        this.date = date;
        this.labelName = labelName;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
