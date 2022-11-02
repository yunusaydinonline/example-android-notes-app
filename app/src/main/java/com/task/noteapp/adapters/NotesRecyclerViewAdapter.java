package com.task.noteapp.adapters;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.task.noteapp.components.NoteListItem;
import com.task.noteapp.helpers.Utils;
import com.task.noteapp.models.Note;

import java.util.LinkedHashMap;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.NotesViewHolder> {
    Activity activity;
    LinkedHashMap<String, Note> entries;

    public NotesRecyclerViewAdapter(LinkedHashMap<String, Note> entries) {
        this.entries = entries;
    }

    public void addActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoteListItem listItem = new NoteListItem(parent.getContext());

        return new NotesViewHolder(listItem, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        String noteId = Utils.getIdFromPosition(entries, position);
        holder.bind(entries.get(noteId), position);
    }

    static class NotesViewHolder extends RecyclerView.ViewHolder {
        Activity activity;
        private final NoteListItem noteListItem;

        public NotesViewHolder(@NonNull NoteListItem itemView, Activity activity) {
            super(itemView);
            this.activity = activity;
            noteListItem = itemView;
        }

        public void bind(Note note, int position) {
            String updatedAt = note.showUpdatedAt() ? note.getUpdatedAtStr(activity) : null;
            noteListItem.setTitle(note.getTitle());
            noteListItem.setContent(note.getContent());
            noteListItem.setImage(note.getImageUrl());
            noteListItem.setDates(note.getCreatedAtStr(activity), updatedAt);
        }
    }
}
