package com.cs180.simplenote.simplenoteapp;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.DatabaseMetaData;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                if(!title.isEmpty() && !body.isEmpty()) {
                    createNote(title, body, date);
                } else {
                    Snackbar.make(v, "Fill empty fields", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private  void createNote(String title, String body, String date) {
        DatabaseReference newNoteRef = notesDatabase.push();
        Notes note = new Notes(title, body, date, " ");

        newNoteRef.setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(NewNote.this, "Note Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewNote.this, "Note Failed To Save", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
