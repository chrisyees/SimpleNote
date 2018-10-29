package com.cs180.simplenote.simplenoteapp;

public class Notes {

    public String title;
    public String labelName;
    public TextBody text;


    public void rename(String input) {
        title = input;
    }

    public void addLabel(String input) {
        labelName = input;
    }


}
