package com.cs180.simplenote.simplenoteapp;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.DatabaseMetaData;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewNote extends AppCompatActivity {
    private EditText noteTitle, noteBody;
    private Button createButton;
    private FirebaseAuth mAuth;
    private DatabaseReference notesDatabase;
    private String noteID;
    private boolean noteExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        mAuth = FirebaseAuth.getInstance();
        notesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(mAuth.getCurrentUser().getUid());

        createButton = findViewById(R.id.createNoteButton);
        noteTitle = findViewById(R.id.noteTitle);
        noteBody = findViewById(R.id.noteBody);

        checkExisting();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = noteTitle.getText().toString();
                String body = noteBody.getText().toString();
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                if(!title.isEmpty() && !body.isEmpty()) {
                    createNote(title, body, date);
                    startActivity(new Intent(NewNote.this, MainActivity.class));
                } else {
                    Snackbar.make(v, "Fill empty fields", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createNote(String title, String body, String date) {

        Map updateMap = new HashMap<String, String>();
        updateMap.put("title", title);
        updateMap.put("text", body);
        updateMap.put("labelName", "");
        updateMap.put("date", date);

        if (noteExists) {
            notesDatabase.child(noteID).updateChildren(updateMap);
        } else {
            DatabaseReference newNoteRef = notesDatabase.push();
            newNoteRef.setValue(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    protected void checkExisting () {
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            //Toast.makeText(NewNote.this, "THIS IS AN EXISTING NOTE", Toast.LENGTH_SHORT).show();
            noteID = extras.getString("cNoteID");

            notesDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    noteTitle.setText(dataSnapshot.child(noteID).child("title").getValue().toString());
                    noteBody.setText(dataSnapshot.child(noteID).child("text").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(NewNote.this, "Note Read Failed", Toast.LENGTH_LONG).show();
                }
            });

            noteExists = true;
        } else {
            //Toast.makeText(NewNote.this, "THIS IS A NEW NOTE", Toast.LENGTH_SHORT).show();
            noteExists = false;
        }
    }
}
