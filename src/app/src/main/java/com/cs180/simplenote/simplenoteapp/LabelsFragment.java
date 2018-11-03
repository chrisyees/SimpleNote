package com.cs180.simplenote.simplenoteapp;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LabelsFragment extends Fragment {

    private EditText addLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_labels, container, false);

        addLabel = view.findViewById(R.id.add_label);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button add_label_button = (Button) view.findViewById(R.id.add_label_button);

        add_label_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteText = addLabel.getText().toString(); //get user input

                Log.d("Firebase", "Beginning to write label.");

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("labels");
                myRef.push().setValue(noteText); //push value to database

                Log.d("Firebase", "Ending write to label.");
            }
        });
    }
}