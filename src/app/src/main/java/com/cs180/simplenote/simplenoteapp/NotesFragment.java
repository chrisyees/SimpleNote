package com.cs180.simplenote.simplenoteapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotesFragment extends Fragment {

    private FloatingActionButton addButton;
    private FloatingActionButton addNoteButton;
    private FloatingActionButton addListButton;
    private RecyclerView notesDisplay;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter adapter;
    private boolean isFabOpen = false;
    private DatabaseReference notesDatabase;
   // private LinearLayout listLayout;

    Bundle labelArgs;
    private String displayLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        notesDisplay = view.findViewById(R.id.note_display);
        notesDisplay.setHasFixedSize(true);
        notesDisplay.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        addButton = view.findViewById(R.id.add_list_item_button);
        addNoteButton = view.findViewById(R.id.create_note_button);
        addListButton = view.findViewById(R.id.create_list_button);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(mAuth.getCurrentUser().getUid());
        //notesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(mAuth.getCurrentUser().getUid());
       // listLayout = view.findViewById(R.id.listDisplay);


        labelArgs = getArguments();
        if(labelArgs == null)
        {
            displayLabel = "All"; //avoid null error, default
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
            protected void onBindViewHolder(@NonNull final NoteRecycleView holder, int position, @NonNull final Notes model) {
                Log.d("FirebaseLabel", "Note Label: " + model.getLabelName());
                Log.d("FirebaseLabel", "Display Label: " + displayLabel);
                //TODO: Print out correct notes based on displayLabel value
                if(model.getLabelName().equals(displayLabel) || displayLabel.equals("All")) {
                    Log.d("FirebaseLabel", "Note Label in IF: " + model.getLabelName());
                    Log.d("FirebaseLabel", "Display Label in IF: " + displayLabel);
                    holder.setVisibility(View.VISIBLE);
                }
                else {
                    holder.setVisibility(View.GONE);
                }
                if(model.getText().equals("List here!"))
                {
                    holder.setListVisibility(View.GONE);
                }
                    holder.setTextTitle(model.getTitle());
                    holder.setTextBody(model.getText());
                    holder.setNoteImg(model.getPhotoUri());
                    holder.setColor(model.getBackgroundColor());

                    Log.d("FirebaseNoteInfo", model.getTitle());
                    Log.d("FirebaseNoteInfo", model.getText());

                    final String cNoteID = getRef(position).getKey();

                mDatabase.child(cNoteID).child("Lists").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.clearList();
                        if (model.getText().equals("List here!")) {
                            Log.d("Display", "List Entered");
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String body = postSnapshot.child("body").getValue().toString();
                                String check = postSnapshot.child("isDone").getValue().toString();
                                Boolean boolCheck = false;
                                if (check.equals("true")) { //convert to boolean
                                    boolCheck = true;
                                }

                                LayoutInflater vi = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                                View listView = vi.inflate(R.layout.list_card, null);
                                EditText listBody = (EditText) listView.findViewById(R.id.listEntry);
                                CheckBox isDone = (CheckBox) listView.findViewById(R.id.checkBox);

                                listBody.setText(body);
                                isDone.setChecked(boolCheck);

                                listBody.setInputType(0); //prevent values from being edited
                                isDone.setClickable(false);

                                Log.d("Display", body + " " + check);
                                holder.addListItem(listView); //add them to the inflated view layout
                            }
                            mDatabase.child(cNoteID).child("Lists").removeEventListener(this); //stop listener
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CloseFabMenu();
                            Log.d("OnClick", "editing notes");
                            Intent intent = new Intent(getActivity(), NewNote.class);
                            intent.putExtra("cNoteID", cNoteID);
                            intent.putExtra("isList", "isList");
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
                Intent intent = new Intent(getActivity(), NewNote.class);
                intent.putExtra("ListCheck", "notList");
                startActivity(intent);
                //startActivity(new Intent(getActivity(), NewNote.class));
            }
        });

        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseFabMenu();
                Intent intent = new Intent(getActivity(), NewNote.class);
                intent.putExtra("ListCheck", "isList");
                startActivity(intent);
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
