package com.task.noteapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.task.noteapp.R;
import com.task.noteapp.adapters.NotesRecyclerViewAdapter;
import com.task.noteapp.adapters.RecyclerItemClickListener;
import com.task.noteapp.data.NotesRepository;
import com.task.noteapp.helpers.Constants;
import com.task.noteapp.helpers.Utils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private NotesRepository repository;
    private TextView noDataTextView;
    private RecyclerView notesRecyclerView;
    private FloatingActionButton addButton;
    private NotesRecyclerViewAdapter notesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = NotesRepository.getInstance();

        setupViews();
        setupActions();
        setupNotesRecyclerView();
    }

    private void setupViews() {
        addButton = findViewById(R.id.button_add);
        noDataTextView = findViewById(R.id.text_view_no_data);
        notesRecyclerView = findViewById(R.id.recycler_view_notes_list);

        configureViewStates();
    }

    private void setupActions() {
        addButton.setOnClickListener(onAddButtonClicked());
        repository.addOnDataChangedListener(onDataChangedListener());
    }

    private void configureViewStates() {
        if (repository.getNotesCount() > 0) {
            noDataTextView.setVisibility(View.GONE);
            notesRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noDataTextView.setVisibility(View.VISIBLE);
            notesRecyclerView.setVisibility(View.GONE);
        }
    }

    private void setupNotesRecyclerView() {
        notesListAdapter = new NotesRecyclerViewAdapter(repository.getNotes());
        notesListAdapter.addActivity(this);
        notesRecyclerView.setAdapter(notesListAdapter);
        notesRecyclerView.addOnItemTouchListener(onItemTouchListener());
    }

    private RecyclerItemClickListener onItemTouchListener() {
        return new RecyclerItemClickListener(
                getApplicationContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        startNoteActivity(position);
                    }
                }
        );
    }

    private View.OnClickListener onAddButtonClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNoteActivity(-1);
            }
        };
    }

    private NotesRepository.OnDataChangedListener onDataChangedListener() {
        return new NotesRepository.OnDataChangedListener() {
            @Override
            public void onDataChanged(NotesRepository.DataChangedType changedType, int position) {
                switch (changedType) {
                    case CREATED:
                        notesListAdapter.notifyItemInserted(position);
                        break;
                    case UPDATED:
                        notesListAdapter.notifyItemChanged(position);
                        break;
                    case DELETED:
                        notesListAdapter.notifyItemRemoved(position);
                        break;
                    case MOVED_TO_BEGINNING:
                        notesListAdapter.notifyItemMoved(position, 0);
                        break;
                }


                configureViewStates();
            }
        };
    }

    private void startNoteActivity(int position) {
        Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (position >= 0) {
            String noteId = Utils.getIdFromPosition(repository.getNotes(), position);
            intent.putExtra(Constants.NOTE_ID_KEY, noteId);
        }

        getApplicationContext().startActivity(intent);
    }
}