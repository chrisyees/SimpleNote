package com.cs180.simplenote.simplenoteapp;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class LabelsFragment extends Fragment {

    private EditText addLabel;
    private Spinner label_bar;
    private String selectedLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_labels, container, false);

        addLabel = view.findViewById(R.id.add_label);
        label_bar = view.findViewById(R.id.label_bar);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button add_label_button = (Button) view.findViewById(R.id.add_label_button);

        //Label Spinner Selection
        List<String> Labels = Arrays.asList(getResources().getStringArray(R.array.Labels));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        label_bar.setAdapter(adapter);
        label_bar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()  {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                selectedLabel =  parent.getItemAtPosition(pos).toString();
                Log.d("Labels",selectedLabel);

                NotesFragment nFrag = new NotesFragment();

                Bundle bundle = new Bundle();
                bundle.putString("label", selectedLabel);
                nFrag.setArguments(bundle);

                getFragmentManager().beginTransaction().add(R.id.container, nFrag).commit();
            }

            public void onNothingSelected(AdapterView<?> parent)
            {
                //nothing
            }
        });

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