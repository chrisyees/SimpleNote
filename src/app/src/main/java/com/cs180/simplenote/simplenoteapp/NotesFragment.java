package com.cs180.simplenote.simplenoteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotesFragment extends Fragment {

    private FloatingActionButton addButton;
    private FloatingActionButton addNoteButton;
    private FloatingActionButton addListButton;
    private RecyclerView notesDisplay;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter adapter;
    private boolean isFabOpen = false;

    Bundle labelArgs;
    private String displayLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        notesDisplay = view.findViewById(R.id.note_display);
        notesDisplay.setHasFixedSize(true);
        notesDisplay.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        addButton = view.findViewById(R.id.create_button);
        addNoteButton = view.findViewById(R.id.create_note_button);
        addListButton = view.findViewById(R.id.create_list_button);
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
                if(model.getLabelName().equals(displayLabel) || displayLabel.equals("All")) {
                    Log.d("FirebaseLabel", "Note Label in IF: " + model.getLabelName());
                    Log.d("FirebaseLabel", "Display Label in IF: " + displayLabel);
                    holder.mView.setVisibility(View.VISIBLE);
                    holder.textTitle.setVisibility(View.VISIBLE);
                    holder.textBody.setVisibility(View.VISIBLE);
                    holder.noteImg.setVisibility(View.VISIBLE);
                }
                else {
                    holder.mView.setVisibility(View.GONE);
                    holder.textTitle.setVisibility(View.GONE);
                    holder.textBody.setVisibility(View.GONE);
                    holder.noteImg.setVisibility(View.GONE);
                }
                    holder.setTextTitle(model.getTitle());
                    holder.setTextBody(model.getText());
                    holder.setNoteImg(model.getPhotoUri());

                    Log.d("FirebaseNoteInfo", model.getTitle());
                    Log.d("FirebaseNoteInfo", model.getText());

                    final String cNoteID = getRef(position).getKey();

                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CloseFabMenu();
                            Log.d("OnClick", "editing notes");
                            Intent intent = new Intent(getActivity(), NewNote.class);
                            intent.putExtra("cNoteID", cNoteID);
                            intent.putExtra("previousLabel", displayLabel);
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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFabOpen)
                    ShowFabMenu();
                else
                    CloseFabMenu();
            }
        });

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseFabMenu();
                startActivity(new Intent(getActivity(), NewNote.class));
            }
        });

        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        //Toast.makeText(getContext(), "WEEEEEEEE", Toast.LENGTH_SHORT).show();

    }

    private void ShowFabMenu(){
        isFabOpen = true;
        addNoteButton.show();
        addListButton.show();
        //addNoteButton.startAnimation(new RotateAnimation(0,90));
    }

    private  void CloseFabMenu(){
        isFabOpen = false;
        addNoteButton.hide();
        addListButton.hide();
    }
}
