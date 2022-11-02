package com.task.noteapp.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.task.noteapp.R;
import com.task.noteapp.components.TextInput;
import com.task.noteapp.data.NotesRepository;
import com.task.noteapp.helpers.Constants;
import com.task.noteapp.helpers.Utils;
import com.task.noteapp.models.Note;

public class NoteActivity extends AppCompatActivity {
    private static final String TAG = "NoteActivity";

    private NotesRepository repository;
    private ScrollView scrollView;
    private LinearLayout contentInfoLinearLayout;
    private ImageView contentImageView;
    private TextView createdAtTextView;
    private TextView updatedAtTextView;
    private TextInput titleTextInput;
    private TextInput contentTextInput;
    private TextInput imageURLTextInput;
    private View separatorView;
    private MenuItem saveMenuItem;
    private MenuItem editMenuItem;
    private MenuItem deleteMenuItem;

    private boolean isEditMode;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        repository = NotesRepository.getInstance();

        Bundle extras = getIntent().getExtras();

        if (extras == null) {
            extras = savedInstanceState;
        }

        String noteId = extras != null ? extras.getString(Constants.NOTE_ID_KEY, null) : null;

        if (noteId != null) {
            note = repository.getNote(noteId);
        }

        setupViews();
        setupActions();
        configureTitle();

        if (!hasNote()) {
            requestFocusToTitle();
        }

        createBackPressHandler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        saveMenuItem = menu.findItem(R.id.action_save);
        editMenuItem = menu.findItem(R.id.action_edit);
        deleteMenuItem = menu.findItem(R.id.action_delete);
        configureMenuItems();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Utils.hideSoftKeyboard(NoteActivity.this);
                finish();
                return true;
            case R.id.action_save:
                saveNoteHandler();
                return true;
            case R.id.action_edit:
                editNoteHandler();
                return true;
            case R.id.action_delete:
                deleteNoteHandler();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createBackPressHandler() {
        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isEditMode) {
                    cancelEditing();
                    return;
                }

                finish();
            }
        });
    }

    private void setupViews() {
        scrollView = findViewById(R.id.scroll_view);
        contentInfoLinearLayout = findViewById(R.id.linear_layout_content_info);
        contentImageView = findViewById(R.id.image_view_content);
        createdAtTextView = findViewById(R.id.text_view_created_at);
        updatedAtTextView = findViewById(R.id.text_view_updated_at);
        titleTextInput = findViewById(R.id.text_input_title);
        contentTextInput = findViewById(R.id.text_input_content);
        imageURLTextInput = findViewById(R.id.text_input_image_url);
        separatorView = findViewById(R.id.view_separator);

        configureFieldValues();
        configureViewStates();
    }

    private void setupActions() {
        scrollView.setOnTouchListener(onScrollChangeListener());
        titleTextInput.addTextChangedListener(textChangedListener());
        contentTextInput.addTextChangedListener(textChangedListener());
    }

    private View.OnTouchListener onScrollChangeListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utils.hideSoftKeyboard(NoteActivity.this);
                return false;
            }
        };
    }

    private TextWatcher textChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (saveMenuItem != null) {
                    saveMenuItem.setEnabled(isSaveActive());
                }
            }
        };
    }

    private void configureTitle() {
        if (!hasNote()) {
            setTitle(R.string.create_note);
        } else if (isEditMode) {
            setTitle(R.string.edit_note);
        } else {
            setTitle("");
        }
    }

    private void configureMenuItems() {
        if (!hasNote() || isEditMode) {
            saveMenuItem.setVisible(true);
            saveMenuItem.setEnabled(isSaveActive());
            editMenuItem.setVisible(false);
            deleteMenuItem.setVisible(false);
        } else {
            saveMenuItem.setVisible(false);
            editMenuItem.setVisible(true);
            deleteMenuItem.setVisible(true);
        }
    }

    private boolean isSaveActive() {
        String title = titleTextInput.getText().toString();
        String content = contentTextInput.getText().toString();
        return Utils.hasLength(title) && Utils.hasLength(content);
    }

    private void configureFieldValues() {
        if (hasNote()) {
            titleTextInput.setText(note.getTitle());
            contentTextInput.setText(note.getContent());

            if (note.hasImageUrl()) {
                imageURLTextInput.setText(note.getImageUrl());
                Utils.fetchAndSetImageToImageView(TAG, contentImageView, note.getImageUrl());
            }

            createdAtTextView.setText(note.getCreatedAtStr(this));
            updatedAtTextView.setText(note.getUpdatedAtStr(this));
        }
    }

    private void configureViewStates() {
        if (hasNote()) {
            titleTextInput.setEnabled(isEditMode);
            contentTextInput.setEnabled(isEditMode);
            imageURLTextInput.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
            separatorView.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
            contentInfoLinearLayout.setVisibility(!isEditMode ? View.VISIBLE : View.GONE);

            if (note.hasImageUrl()) {
                contentImageView.setVisibility(View.VISIBLE);
            } else {
                contentImageView.setVisibility(View.GONE);
            }

            if (note.showUpdatedAt()) {
                updatedAtTextView.setVisibility(View.VISIBLE);
            } else {
                updatedAtTextView.setVisibility(View.GONE);
            }
        } else {
            titleTextInput.setEnabled(true);
            contentTextInput.setEnabled(true);
            imageURLTextInput.setVisibility(View.VISIBLE);
            separatorView.setVisibility(View.VISIBLE);
            contentInfoLinearLayout.setVisibility(View.GONE);
        }
    }

    private void requestFocusToTitle() {
        titleTextInput.requestFocus();
        titleTextInput.setSelection(titleTextInput.getText().length());
        Utils.showSoftKeyboard(this);
    }

    private boolean hasNote() {
        return note != null;
    }

    private boolean hasImageUrl() {
        return Utils.hasLength(imageURLTextInput.getText().toString());
    }

    private void saveNoteHandler() {
        isEditMode = false;

        String title = titleTextInput.getText().toString().trim();
        String content = contentTextInput.getText().toString().trim();
        String imageUrl = hasImageUrl() ? imageURLTextInput.getText().toString().trim() : null;

        if (hasNote()) {
            note.setTitle(title);
            note.setContent(content);
            note.setImageUrl(imageUrl);
        } else {
            note = new Note(title, content, imageUrl);
        }

        repository.saveNote(note);

        Utils.hideSoftKeyboard(this);
        configureFieldValues();
        configureViewStates();
        configureMenuItems();
        configureTitle();
    }

    private void editNoteHandler() {
        isEditMode = true;
        configureViewStates();
        configureMenuItems();
        configureTitle();
        requestFocusToTitle();
    }

    private void deleteNoteHandler() {
        new AlertDialog
                .Builder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.ok, onAlertDialogButtonClicked())
                .setNeutralButton(R.string.cancel, onAlertDialogButtonClicked())
                .show();
    }

    private void cancelEditing() {
        isEditMode = false;
        configureFieldValues();
        configureViewStates();
        configureMenuItems();
        configureTitle();
    }

    private DialogInterface.OnClickListener onAlertDialogButtonClicked() {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (id == DialogInterface.BUTTON_POSITIVE) {
                    repository.deleteNote(note.getId());
                    finish();
                }
            }
        };
    }
}