package com.cs180.simplenote.simplenoteapp;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.DatabaseMetaData;

public class NewNote extends AppCompatActivity {
    private EditText noteTitle, noteBody;
    private Button createButton;
    private FirebaseAuth mAuth;
    private DatabaseReference notesDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        mAuth = FirebaseAuth.getInstance();
        notesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(mAuth.getCurrentUser().getUid());

        createButton = findViewById(R.id.createNoteButton);
        noteTitle = findViewById(R.id.noteTitle);
        noteBody = findViewById(R.id.noteBody);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = noteTitle.getText().toString();
                String body = noteBody.getText().toString();
                if(!title.isEmpty() && !body.isEmpty()) {
                    createNote(title, body);
                } else {
                    Snackbar.make(v, "Fill empty fields", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private  void createNote(String title, String body) {
    }
}
