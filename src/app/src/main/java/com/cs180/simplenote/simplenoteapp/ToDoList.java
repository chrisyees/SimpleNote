package com.cs180.simplenote.simplenoteapp;

public class ToDoList {

    private boolean checked;
    private String text;

    public ToDoList(){

    }

    public ToDoList(boolean checked, String text) {
        this.checked = checked;
        this.text = text;
    }

    public boolean getChecked() {
        return checked;
    }

    public String getText() {
        return text;
    }

    public void setChecked(boolean checked) { this.checked = checked; }

    public void setText(String text) {
        this.text = text;
    }

}
