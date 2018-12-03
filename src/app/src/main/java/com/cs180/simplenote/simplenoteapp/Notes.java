package com.cs180.simplenote.simplenoteapp;

import java.io.File;

public class Notes {

    private String title;
    private String text;
    private String date;
    private String labelName;
    private String photoUri;
    private String backgroundColor;
    private String reminder;
    private File audio;


    public Notes(){

    }

    public Notes(String title, String text, String date, String labelName, String photoUri, String backgroundColor, File audio, String reminder) {
        this.title = title;
        this.text = text;
        this.date = date;
        this.labelName = labelName;
        this.photoUri = photoUri;
        this.reminder = reminder;
        this.backgroundColor = backgroundColor;
        this.audio = audio;
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

    public String getPhotoUri() { return photoUri; }

    public String getBackgroundColor() { return backgroundColor; }

    public String getReminder() { return reminder;}

    public File getAudio() {
        return audio;
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

    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }

    public void setAudio(File audio) {
        this.audio = audio;
    }
}
