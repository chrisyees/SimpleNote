package com.cs180.simplenote.simplenoteapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotesFragment extends Fragment {

    Activity mActivity;
    String notesList = ""; //TEMP VARIABLE
    private EditText addNote;
    private TextView noteList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        addNote = view.findViewById(R.id.add_note);
        noteList = view.findViewById(R.id.note_list);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button add_button = (Button) view.findViewById(R.id.add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteText = addNote.getText().toString(); //get user input
                // TODO: ADD TEXT TO NOTES -> ADD NOTES ARRAY?

                notesList = notesList + noteText + " ";

                noteList.setText(notesList);

                Log.d("Firebase", "Beginning to write note.");

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Notes");
                myRef.push().setValue(noteText); //push value to database

                Log.d("Firebase", "Ending write to note.");

                /*
                TODO : ADD ABILITY TO DYNAMICALLY ADD BUTTONS(NOTES) TO SCROLL VIEW
                ScrollView scrollView = (ScrollView) v.findViewById(R.id.note_list);

                LinearLayout linearLayout = new LinearLayout(mActivity);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                Button button = new Button(mActivity);
                button.setText("Some text");
                scrollView.addView(button);

                scrollView.addView(linearLayout);
                */
            }
        });
    }
}
