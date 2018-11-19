package com.cs180.simplenote.simplenoteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotesFragment extends Fragment {

    private Button addNoteButton;
    private RecyclerView notesDisplay;
    private GridLayoutManager gridLayoutManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter adapter;

    Bundle labelArgs;
    private String displayLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        notesDisplay = view.findViewById(R.id.note_display);
        notesDisplay.setHasFixedSize(true);
        notesDisplay.setLayoutManager(gridLayoutManager);
        addNoteButton = view.findViewById(R.id.create_note_button);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(mAuth.getCurrentUser().getUid());

        labelArgs = getArguments();
        if(labelArgs == null)
        {
            displayLabel = "All"; //avoid null error
        }
        else
        {
            displayLabel = getArguments().getString("label"); //get item passed in by label fragment
        }

        Log.d("OnCreate", displayLabel);

        FirebaseRecyclerOptions<Notes> options =
        new FirebaseRecyclerOptions.Builder<Notes>()
        .setQuery(mDatabase, Notes.class)
        .setLifecycleOwner(this)
        .build();

        adapter = new FirebaseRecyclerAdapter<Notes, NoteRecycleView>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NoteRecycleView holder, int position, @NonNull Notes model) {
                Log.d("FirebaseLabel", "Note Label: " + model.getLabelName());
                Log.d("FirebaseLabel", "Display Label: " + displayLabel);
                //TODO: Print out correct notes based on displayLabel value
                //if(model.getLabelName() == displayLabel || displayLabel == "All") {
                    holder.setTextTitle(model.getTitle());
                    holder.setTextBody(model.getText());

                    Log.d("FirebaseNoteInfo", model.getTitle());
                    Log.d("FirebaseNoteInfo", model.getText());

                    final String cNoteID = getRef(position).getKey();

                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), NewNote.class);
                            intent.putExtra("cNoteID", cNoteID);
                            startActivity(intent);
                        }
                    });
                //}
            }

            @NonNull
            @Override
            public NoteRecycleView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new NoteRecycleView(LayoutInflater.from(getActivity())
                        .inflate(R.layout.note_card, container,false));
            }
        };
        notesDisplay.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewNote.class));
            }
        });
    }
}
