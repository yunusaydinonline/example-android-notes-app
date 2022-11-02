package com.task.noteapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.task.noteapp.helpers.Utils;
import com.task.noteapp.models.Note;

import java.util.LinkedHashMap;

public class NotesRepository {
    private static final String TAG = "Repository";

    public enum DataChangedType {CREATED, UPDATED, DELETED, MOVED_TO_BEGINNING}

    public interface OnDataChangedListener {
        public void onDataChanged(DataChangedType changedType, int position);
    }

    private OnDataChangedListener listener;
    private SQLiteDatabase database;
    private static volatile NotesRepository instance = null;
    private final LinkedHashMap<String, Note> notes = new LinkedHashMap<>();

    private NotesRepository() {
        Log.d(TAG, "created successfully.");
    }

    public void init(Context context) {
        database = new Database(context).getWritableDatabase();
        fetchNotesFromDb();
        Log.d(TAG, "initiated successfully.");
    }

    public void addOnDataChangedListener(OnDataChangedListener listener) {
        this.listener = listener;
    }

    public static NotesRepository getInstance() {
        if (instance == null) {
            synchronized (NotesRepository.class) {
                if (instance == null) {
                    instance = new NotesRepository();
                }
            }
        }

        return instance;
    }

    public int getNotesCount() {
        return notes.size();
    }

    public LinkedHashMap<String, Note> getNotes() {
        return notes;
    }

    public Note getNote(String noteId) {
        return notes.get(noteId);
    }

    public void saveNote(Note note) {
        String noteId = note.getId();
        int position = Utils.getPositionFromId(notes, noteId);

        DataChangedType dataChangedType;
        int changedPosition = 0;

        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_TITLE, note.getTitle());
        values.put(Constants.COLUMN_CONTENT, note.getContent());
        values.put(Constants.COLUMN_IMAGE_URL, note.getImageUrl());
        notes.put(noteId, note);

        if (position == -1) {
            values.put(Constants.COLUMN_ID, note.getId());
            values.put(Constants.COLUMN_CREATED_AT, note.getCreatedAt());
            values.put(Constants.COLUMN_UPDATED_AT, note.getUpdatedAt());
            database.insert(Constants.TABLE_NAME, null, values);

            dataChangedType = DataChangedType.CREATED;
        } else {
            int updatedAt = Note.generateTimeStamp();
            note.setUpdatedAt(updatedAt);

            values.put(Constants.COLUMN_UPDATED_AT, updatedAt);
            database.update(Constants.TABLE_NAME, values, "_id = ?", new String[]{noteId});

            dataChangedType = DataChangedType.UPDATED;
            changedPosition = position;
        }

        moveNoteToBeginning(noteId);

        if (hasListener()) {
            listener.onDataChanged(dataChangedType, changedPosition);

            if (dataChangedType == DataChangedType.UPDATED) {
                listener.onDataChanged(DataChangedType.MOVED_TO_BEGINNING, changedPosition);
            }
        }
    }

    public void deleteNote(String noteId) {
        database.delete(Constants.TABLE_NAME, "_id = ?", new String[]{noteId});
        int position = Utils.getPositionFromId(notes, noteId);
        notes.remove(noteId);

        if (hasListener()) {
            listener.onDataChanged(DataChangedType.DELETED, position);
        }
    }

    private void fetchNotesFromDb() {
        String query = "SELECT * FROM " + Constants.TABLE_NAME + " ORDER BY " +
                Constants.COLUMN_UPDATED_AT + " DESC";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                int idCIndex = cursor.getColumnIndex(Constants.COLUMN_ID);
                int titleCIndex = cursor.getColumnIndex(Constants.COLUMN_TITLE);
                int contentCIndex = cursor.getColumnIndex(Constants.COLUMN_CONTENT);
                int imageUrlCIndex = cursor.getColumnIndex(Constants.COLUMN_IMAGE_URL);
                int createAtCIndex = cursor.getColumnIndex(Constants.COLUMN_CREATED_AT);
                int updatedAtCIndex = cursor.getColumnIndex(Constants.COLUMN_UPDATED_AT);

                while (!cursor.isAfterLast()) {
                    Note note = new Note();
                    note.setId(cursor.getString(idCIndex));
                    note.setTitle(cursor.getString(titleCIndex));
                    note.setContent(cursor.getString(contentCIndex));
                    note.setImageUrl(cursor.getString(imageUrlCIndex));
                    note.setCreatedAt(cursor.getLong(createAtCIndex));
                    note.setUpdatedAt(cursor.getLong(updatedAtCIndex));
                    notes.put(note.getId(), note);

                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }
    }

    private void moveNoteToBeginning(String noteId) {
        Note note = notes.remove(noteId);

        LinkedHashMap<String, Note> notesClone = (LinkedHashMap<String, Note>) notes.clone();
        notes.clear();
        notes.put(noteId, note);
        notes.putAll(notesClone);
    }

    private boolean hasListener() {
        return listener != null;
    }
}
