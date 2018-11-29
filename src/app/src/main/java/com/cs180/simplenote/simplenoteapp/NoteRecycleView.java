package com.cs180.simplenote.simplenoteapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class NoteRecycleView extends RecyclerView.ViewHolder {
    View mView;
    TextView textTitle, textBody;
    ImageView noteImg;
    CardView noteCard;
    LinearLayout linLayout;

    public NoteRecycleView(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        textTitle = mView.findViewById(R.id.note_title);
        textBody = mView.findViewById(R.id.note_body);
        noteImg = mView.findViewById(R.id.imageViewCard);
        noteCard = mView.findViewById(R.id.note_cardview);
        linLayout = mView.findViewById(R.id.card_linear_layout);
    }

    public void setTextTitle(String textTitle) {
        this.textTitle.setText(textTitle);
    }

    public void setTextBody(String textBody) {
        this.textBody.setText(textBody);
    }

    public void setNoteImg(String imgURI) {
        if (!imgURI.equals("empty")) {
            this.noteImg.setVisibility(View.VISIBLE);
            byte[] decodedByteArray = Base64.decode(imgURI, Base64.DEFAULT);
            Bitmap photoBitmap =  BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            this.noteImg.setImageBitmap(photoBitmap);
        }
        else {
            this.noteImg.setVisibility(View.GONE);
        }
    }

    public void setColor(String color){
        noteCard.setCardBackgroundColor(Color.parseColor(color));
    }

    public void setVisibility(int visibility) {
        mView.setVisibility(visibility);
        textTitle.setVisibility(visibility);
        textBody.setVisibility(visibility);
        noteImg.setVisibility(visibility);
        noteImg.setVisibility(visibility);
        noteCard.setVisibility(visibility);
        linLayout.setVisibility(visibility);
    }
}


