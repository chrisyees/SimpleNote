package com.cs180.simplenote.simplenoteapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewNote extends AppCompatActivity {
    private EditText noteTitle, noteBody;
    private Spinner labelSelect;
    private Button createButton;
    private FirebaseAuth mAuth;
    private DatabaseReference notesDatabase;
    private String noteID;
    private boolean noteExists;
    private String selectedLabel;
    private String encodedPhoto;
    private ImageView photoView;
    private ArrayAdapter<String> adapter;

    private String previousLabel; //for spinner when editing note

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        mAuth = FirebaseAuth.getInstance();
        notesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(mAuth.getCurrentUser().getUid());

        createButton = findViewById(R.id.createNoteButton);
        noteTitle = findViewById(R.id.noteTitle);
        noteBody = findViewById(R.id.noteBody);
        labelSelect = findViewById(R.id.label_select);
        photoView = findViewById(R.id.imgView);

        Toolbar newNoteToolbar = findViewById(R.id.newNoteToolbar);
        newNoteToolbar.setElevation(0);
        setSupportActionBar(newNoteToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        encodedPhoto = "empty";

        checkExisting();

        //Label Spinner Selection
        List<String> Labels = Arrays.asList(getResources().getStringArray(R.array.Labels));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        labelSelect.setAdapter(adapter);
        labelSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()  {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
                {
                selectedLabel =  parent.getItemAtPosition(pos).toString();
                }

                public void onNothingSelected(AdapterView<?> parent)
                {
                    //nothing
                }
        });


        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deletePhoto();
                return false;
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = noteTitle.getText().toString();
                String body = noteBody.getText().toString();
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                if(!title.isEmpty() && !body.isEmpty()) {
                    createNote(title, body, date, encodedPhoto);
                } else {
                    Snackbar.make(v, "Fill empty fields", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createNote(String title, String body, String date, String encodedPhoto) {

        Map updateMap = new HashMap<String, String>();
        updateMap.put("title", title);
        updateMap.put("text", body);
        updateMap.put("labelName", selectedLabel);
        updateMap.put("date", date);
        updateMap.put("photoUri", encodedPhoto);

        if (noteExists) {
            notesDatabase.child(noteID).updateChildren(updateMap);
        } else {
            DatabaseReference newNoteRef = notesDatabase.push();
            newNoteRef.setValue(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(NewNote.this, "Note Saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(NewNote.this, MainActivity.class));
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
                    String temp = dataSnapshot.child(noteID).child("photoUri").getValue().toString();
                    int spinnerPosition = adapter.getPosition(dataSnapshot.child(noteID).child("labelName").getValue().toString());
                    labelSelect.setSelection(spinnerPosition);
                    if (!temp.equals("empty")) {
                        encodedPhoto = temp;
                        photoView.setVisibility(View.VISIBLE);
                        byte[] decodedByteArray = Base64.decode(temp, Base64.DEFAULT);
                        Bitmap photoBitmap =  BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
                        photoView.setImageBitmap(photoBitmap);
                    }
                    else {
                        photoView.setVisibility(View.GONE);
                    }
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

    protected void selectPhoto(){
        Intent intent  = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void deletePhoto(){
        if(noteExists)
            notesDatabase.child(noteID).child("photoUri").setValue("empty");
        photoView.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            Uri photoData = data.getData();
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoData);
                photoView.setImageBitmap(imageBitmap);
                photoView.setVisibility(View.VISIBLE);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if(imageBitmap != null)
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                encodedPhoto = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.newnote_menu, menu);
        if(noteExists)
            menu.getItem(2).setVisible(true);
        else
            menu.getItem(2).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_note:
                notesDatabase.child(noteID).removeValue();
                Toast.makeText(NewNote.this, "Note Deleted", Toast.LENGTH_LONG).show();
                startActivity(new Intent(NewNote.this, MainActivity.class));
            case R.id.menu_color_note:

            case R.id.menu_insert_photo:
                selectPhoto();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
