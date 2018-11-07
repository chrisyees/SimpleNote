package com.cs180.simplenote.simplenoteapp;

import android.app.Activity;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotesFragment extends Fragment {

    private Button addNoteButton;
    private RecyclerView notesDisplay;
    private GridLayoutManager gridLayoutManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter adapter;
    private ViewGroup cont;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        cont = container;
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        notesDisplay = view.findViewById(R.id.note_display);
        notesDisplay.setHasFixedSize(true);
        notesDisplay.setLayoutManager(gridLayoutManager);
        addNoteButton = view.findViewById(R.id.create_note_button);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(mAuth.getCurrentUser().getUid());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

//        FirebaseRecyclerOptions<Notes> options =
//                new FirebaseRecyclerOptions.Builder<Notes>()
//                        .setQuery(mDatabase, new SnapshotParser<Notes>() {
//                            @NonNull
//                            @Override
//                            public Notes parseSnapshot(@NonNull DataSnapshot snapshot) {
//                                return new Notes(snapshot.child("title").getValue().toString(),
//                                        snapshot.child("title").getValue().toString(),
//                                        snapshot.child("date").getValue().toString(),
//                                        snapshot.child("label").getValue().toString());
//                            }
//                        })
//                        .build();
//
//        adapter = new FirebaseRecyclerAdapter<Notes, NoteRecycleView>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull NoteRecycleView holder, int position, @NonNull Notes model) {
//                holder.setTextTitle(model.getTitle());
//                holder.setTextDate(model.getDate());
//            }
//
//            @NonNull
//            @Override
//            public NoteRecycleView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                View view = LayoutInflater.from(getActivity())
//                        .inflate(R.layout.note_card, cont, false);
//                return new NoteRecycleView(view);
//            }
//        };
//        notesDisplay.setAdapter(adapter);
//        adapter.startListening();
    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }

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
