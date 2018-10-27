package com.cs180.simplenote.simplenoteapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView title = (TextView) findViewById(R.id.textView);
        title.setVisibility(View.GONE);

        TextView edittext1 = (TextView) findViewById(R.id.editText1);
        edittext1.setVisibility(View.GONE);

        TextView edittext2 = (TextView) findViewById(R.id.editText2);
        edittext2.setVisibility(View.GONE);

        TextView edittext3 = (TextView) findViewById(R.id.editText7);
        edittext3.setVisibility(View.GONE);

        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        imageView.setVisibility(View.GONE);

        Button notesbutton = findViewById(R.id.notes_button);
        notesbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView title = (TextView) findViewById(R.id.textView);
                title.setVisibility(View.GONE);

                TextView edittext1 = (TextView) findViewById(R.id.editText1);
                edittext1.setVisibility(View.GONE);

                TextView edittext2 = (TextView) findViewById(R.id.editText2);
                edittext2.setVisibility(View.GONE);

                TextView edittext3 = (TextView) findViewById(R.id.editText7);
                edittext3.setVisibility(View.GONE);

                ImageView imageView = (ImageView) findViewById(R.id.imageView2);
                imageView.setVisibility(View.GONE);
            }
        });

        Button tagsbutton = findViewById(R.id.tags_button);
        tagsbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView title = (TextView) findViewById(R.id.textView);
                title.setVisibility(View.GONE);

                TextView edittext1 = (TextView) findViewById(R.id.editText1);
                edittext1.setVisibility(View.GONE);

                TextView edittext2 = (TextView) findViewById(R.id.editText2);
                edittext2.setVisibility(View.GONE);

                TextView edittext3 = (TextView) findViewById(R.id.editText7);
                edittext3.setVisibility(View.GONE);

                ImageView imageView = (ImageView) findViewById(R.id.imageView2);
                imageView.setVisibility(View.GONE);
            }
        });

        Button accountprofilebutton = findViewById(R.id.account_profile_button);
        accountprofilebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView title = (TextView) findViewById(R.id.textView);
                title.setVisibility(View.VISIBLE);

                TextView edittext1 = (TextView) findViewById(R.id.editText1);
                edittext1.setVisibility(View.VISIBLE);

                TextView edittext2 = (TextView) findViewById(R.id.editText2);
                edittext2.setVisibility(View.VISIBLE);

                TextView edittext3 = (TextView) findViewById(R.id.editText7);
                edittext3.setVisibility(View.VISIBLE);

                ImageView imageView = (ImageView) findViewById(R.id.imageView2);
                imageView.setVisibility(View.VISIBLE);
            }
        });

    }
}
