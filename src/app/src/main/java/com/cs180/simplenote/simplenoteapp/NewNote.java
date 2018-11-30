package com.cs180.simplenote.simplenoteapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private ConstraintLayout newNoteBackground;
    private FirebaseAuth mAuth;
    private DatabaseReference notesDatabase;
    private StorageReference recordingsRef;
    private String noteID;
    private boolean noteExists;
    private String selectedLabel;
    private String encodedPhoto;
    private String backgroundColor;
    private ImageView photoView;
    private static String mFileName = "empty";
    //private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;
    public File audio = null;
    //private PlayButton   mPlayButton = null;
    private MediaPlayer mPlayer = null;
    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;
    private boolean isRecording = false;
    private ArrayAdapter<String> adapter;

    //Spinner Variables
    private DatabaseReference labelDatabase;
    final List<String> labelList = new ArrayList<String>();


    private String previousLabel; //for spinner when editing note

    private final int PICK_IMAGE_REQUEST = 71;


    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        mAuth = FirebaseAuth.getInstance();
        notesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(mAuth.getCurrentUser().getUid());
        recordingsRef = FirebaseStorage.getInstance().getReference().child("recordings");

        createButton = findViewById(R.id.createNoteButton);
        noteTitle = findViewById(R.id.noteTitle);
        noteBody = findViewById(R.id.noteBody);
        labelSelect = findViewById(R.id.label_select);
        photoView = findViewById(R.id.imgView);
        newNoteBackground = findViewById(R.id.new_note_layout);

        labelDatabase = FirebaseDatabase.getInstance().getReference().child("Labels").child(mAuth.getCurrentUser().getUid());

        labelList.clear();

        labelList.add("All");

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        ValueEventListener listener = labelDatabase.addValueEventListener(new ValueEventListener() { //get user labels from firebase
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    if(!labelList.contains(ds.getValue(String.class))) { //prevent duplicate spinner value errors
                        Log.d("firebaseAddLabel", "adding" + ds.getValue(String.class));
                        labelList.add(ds.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Errors", "Error retrieving firebase label data");
            }
        });

        Toolbar newNoteToolbar = findViewById(R.id.newNoteToolbar);
        newNoteToolbar.setElevation(0);
        setSupportActionBar(newNoteToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        encodedPhoto = "empty";
        backgroundColor = "#ffffff";

        checkExisting();

        //Label Spinner Selection
        //List<String> Labels = Arrays.asList(getResources().getStringArray(R.array.Labels)); //get premade label list
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelList);
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


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStartPlaying) {
                    startPlaying();
                } else {
                    stopPlaying();
                }
                mStartPlaying = !mStartPlaying;
            }
        });

        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deletePhoto();
                return false;
            }
        });
    }

    private void createNote(String title, String body, String date, String encodedPhoto, String backgroundColor, final Uri voiceUri) {
        Toast.makeText(NewNote.this, "Saving Note...", Toast.LENGTH_SHORT).show();
        Map updateMap = new HashMap<String, String>();
        updateMap.put("title", title);
        updateMap.put("text", body);
        updateMap.put("labelName", selectedLabel);
        updateMap.put("date", date);
        updateMap.put("photoUri", encodedPhoto);
        updateMap.put("backgroundColor", backgroundColor);

        if (noteExists) {
            notesDatabase.child(noteID).updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()) {
                        if(voiceUri != null)
                            recordingsRef.child(noteID).putFile(voiceUri);
                        startActivity(new Intent(NewNote.this, MainActivity.class));
                        Toast.makeText(NewNote.this, "Note Saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(NewNote.this, MainActivity.class));
                    } else {
                        Toast.makeText(NewNote.this, "Note Failed To Save", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            final DatabaseReference newNoteRef = notesDatabase.push();
            newNoteRef.setValue(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        if(voiceUri != null)
                            recordingsRef.child(newNoteRef.getKey()).putFile(voiceUri);
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
                    backgroundColor = dataSnapshot.child(noteID).child("backgroundColor").getValue().toString();
                    newNoteBackground.setBackgroundColor(Color.parseColor(backgroundColor));

                    StorageReference audioRef = recordingsRef.child(noteID);
                    //File localFile = null;
                    try {
                        audio = File.createTempFile("Audio", "3gp");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    audioRef.getFile(audio).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            isRecording = true;
                            createButton.setVisibility(View.VISIBLE);
                            // Local temp file has been created
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            isRecording = false;
                            createButton.setVisibility(View.GONE);
                        }
                    });

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

    private void startPlaying() {
        if(isRecording){
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(audio.getPath());
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Toast.makeText(NewNote.this, "Voice Play Failed", Toast.LENGTH_LONG).show();        }
        }
        else {
            Toast.makeText(NewNote.this, "No Recording", Toast.LENGTH_LONG).show();
        }

    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;

    }

    private void startRecording() {
        Toast.makeText(NewNote.this, "Recording...", Toast.LENGTH_LONG).show();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/" + date + ".3gp";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Toast.makeText(NewNote.this, "Voice Record Failed", Toast.LENGTH_SHORT).show();
        }
        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        isRecording = true;
        createButton.setVisibility(View.VISIBLE);
        audio = new File(mFileName).getAbsoluteFile();
        Toast.makeText(NewNote.this, "Recording Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.newnote_menu, menu);
        if(noteExists)
            menu.getItem(0).setVisible(true);
        else
            menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_insert_photo:
                selectPhoto();
                break;
            case R.id.menu_delete_note:
                notesDatabase.child(noteID).removeValue();
                Toast.makeText(NewNote.this, "Note Deleted", Toast.LENGTH_LONG).show();
                startActivity(new Intent(NewNote.this, MainActivity.class));
                break;
            case R.id.menu_save_note:
                String title = noteTitle.getText().toString();
                String body = noteBody.getText().toString();
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                Uri uriAudio = null;
                if(isRecording){
                    uriAudio = Uri.fromFile(audio);
                }

                if(!title.isEmpty() && !body.isEmpty()) {
                    createNote(title, body, date, encodedPhoto, backgroundColor, uriAudio);
                } else {
                    Toast.makeText(NewNote.this, "Fill In Empty Fields", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_insert_voice:
                if (mStartRecording) {
                    startRecording();
                } else {
                    stopRecording();
                }
                mStartRecording = !mStartRecording;
                break;
            case R.id.menu_color_note_yellow:
                backgroundColor = "#fdfd96";
                break;
            case R.id.menu_color_note_red:
                backgroundColor = "#ff9aa2";
                break;
            case R.id.menu_color_note_orange:
                backgroundColor = "#ffdac1";
                break;
            case R.id.menu_color_note_blue:
                backgroundColor = "#aec6cf";
                break;
            case R.id.menu_color_note_purple:
                backgroundColor = "#c7ceea";
                break;
            case R.id.menu_color_note_green:
                backgroundColor = "#b5ead7";
                break;
            case R.id.menu_color_note_white:
                backgroundColor = "#ffffff";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        newNoteBackground.setBackgroundColor(Color.parseColor(backgroundColor));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
