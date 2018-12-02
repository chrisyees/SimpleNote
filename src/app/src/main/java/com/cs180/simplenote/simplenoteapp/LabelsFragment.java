package com.cs180.simplenote.simplenoteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LabelsFragment extends Fragment {

    private EditText addLabel;
    private Spinner label_bar;
    private String selectedLabel;
    private FirebaseAuth mAuth;
    private DatabaseReference labelDatabase;
    final List<String> labelList = new ArrayList<String>();

    private Button add_label_button;
    private Button sort_button;
    private Button delete_label_button;

    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_labels, container, false);

        //initialize elements
        addLabel = view.findViewById(R.id.add_label);
        add_label_button = view.findViewById(R.id.add_label_button);
        sort_button = view.findViewById(R.id.sort_button);
        delete_label_button = view.findViewById(R.id.delete_label_button);
        label_bar = view.findViewById(R.id.label_bar);

        //get user label list from firebase
        mAuth = FirebaseAuth.getInstance();
        labelDatabase = FirebaseDatabase.getInstance().getReference().child("Labels").child(mAuth.getCurrentUser().getUid());

        labelList.clear();

        labelList.add("All");

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

        //labelList = Arrays.asList(getResources().getStringArray(R.array.Labels)); //retrieve pre-made list

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Label Spinner Selection
        //List<String> Labels = Arrays.asList(getResources().getStringArray(R.array.Labels));
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, labelList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        label_bar.setAdapter(adapter);
        label_bar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()  {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                selectedLabel =  parent.getItemAtPosition(pos).toString(); //store the value
            }

            public void onNothingSelected(AdapterView<?> parent)
            {
                //nothing
            }
        });

        //ADD BUTTON
        add_label_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteText = addLabel.getText().toString(); //get user input

                if(labelList.contains(noteText)) //if label already added
                {
                    Toast.makeText(getActivity(), "Label Already Exists", Toast.LENGTH_LONG).show();
                }
                else {
                    labelList.add(noteText);

                    adapter.notifyDataSetChanged();

                    //begin to write to firebase

                    Log.d("FirebaseAdd", "Beginning to write label.");

                    labelDatabase.push().setValue(noteText);

                    Log.d("Firebase", "Ending write to label.");

                    Toast.makeText(getActivity(), "Label Added", Toast.LENGTH_LONG).show(); //display confirmation
                }
            }
        });

        //DELETE BUTTON
        delete_label_button.setOnClickListener(new View.OnClickListener() { //go to notes fragment and display
            @Override
            public void onClick(View v) {

                if(!labelList.contains(selectedLabel)) //if label doesnt exist
                {
                    Toast.makeText(getActivity(), "Label Doesn't Exist", Toast.LENGTH_LONG).show();
                }
                else if(selectedLabel.equals("All")) //don't delete all
                {
                    Toast.makeText(getActivity(), "Cannot Delete All Label", Toast.LENGTH_LONG).show();
                }
                else {

                    for(int i = 0; i < labelList.size(); i++)
                    {
                        Log.d("ListOutput", labelList.get(i));
                    }

                    //begin to delete to firebase

                    Log.d("FirebaseAdd", "Beginning to write label.");

                    ValueEventListener listener = labelDatabase.addValueEventListener(new ValueEventListener() { //find and delete the label
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d("Deleting", selectedLabel);
                            Log.d("Deleting", "Entering");
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String temp = selectedLabel;
                                if (temp.equals(ds.getValue(String.class))) {
                                    Log.d("Deleting", ds.getValue(String.class));
                                    ds.getRef().removeValue();
                                    labelDatabase.removeEventListener(this); //stop listener
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("Errors", "Error deleting firebase label data");
                        }
                    });

                    Log.d("Firebase", "Ending write to label.");


                    for(int i = 0; i < labelList.size(); i++)
                    {
                        if(labelList.get(i).equals(selectedLabel))
                        {
                            labelList.remove(i);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    Toast.makeText(getActivity(), "Label Deleted", Toast.LENGTH_LONG).show(); //display confirmation

                    parseThroughDeletedLabelNotes();

                }
            }
        });

        //DISPLAY BUTTON
        sort_button.setOnClickListener(new View.OnClickListener() { //go to notes fragment and display
            @Override
            public void onClick(View v) {
                Log.d("Labels", selectedLabel);
                NotesFragment nFrag = new NotesFragment();

                Bundle bundle = new Bundle();
                bundle.putString("label", selectedLabel); //store the item in the spinner to send over
                nFrag.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, nFrag).commit();
            }
        });
    }

    private void parseThroughDeletedLabelNotes() //find and reset all labels in notes with deleted label
    {
        DatabaseReference noteDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(mAuth.getCurrentUser().getUid());
        noteDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Log.d("Parsing",ds.child("labelName").getValue(String.class) + " " + selectedLabel);
                    if(selectedLabel.equals(ds.child("labelName").getValue(String.class))) {
                        ds.getRef().child("labelName").setValue("All");
                    }
                }
                labelDatabase.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Errors", "Error deleting firebase label data");
            }
        });
    }
}