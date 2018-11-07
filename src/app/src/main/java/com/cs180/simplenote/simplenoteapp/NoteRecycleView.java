package com.cs180.simplenote.simplenoteapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class NoteRecycleView extends RecyclerView.ViewHolder {
    View mView;
    TextView textTitle, textDate;

    public NoteRecycleView(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        textTitle = mView.findViewById(R.id.note_title);
        textDate = mView.findViewById(R.id.note_date);
    }

    public void setTextTitle(String textTitle) {
        this.textTitle.setText(textTitle);
    }

    public void setTextDate(String textDate) {
        this.textDate.setText(textDate);
    }
}
