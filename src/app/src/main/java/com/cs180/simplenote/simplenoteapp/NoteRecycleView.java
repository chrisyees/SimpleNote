package com.cs180.simplenote.simplenoteapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

public class NoteRecycleView extends RecyclerView.ViewHolder {
    View mView;
    TextView textTitle, textBody;
    Spinner label_select;

    public NoteRecycleView(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        textTitle = mView.findViewById(R.id.note_title);
        textBody = mView.findViewById(R.id.note_body);
        label_select = mView.findViewById(R.id.label_select);
    }

    public void setTextTitle(String textTitle) {
        this.textTitle.setText(textTitle);
    }

    public void setTextBody(String textBody) {
        this.textBody.setText(textBody);
    }
;
   // public void setLabel(String label) { this.label_select.}
}


