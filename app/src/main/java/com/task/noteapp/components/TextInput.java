package com.task.noteapp.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatEditText;

import com.task.noteapp.R;
import com.task.noteapp.helpers.Utils;

public class TextInput extends AppCompatEditText {
    private static final String TAG = "TextInput";

    boolean allowLineBreaks;

    public TextInput(Context context) {
        super(context);
        init(null, 0);
    }

    public TextInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TextInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TextInput, defStyle, 0);

        allowLineBreaks = a.getBoolean(R.styleable.TextInput_allowLineBreaks, true);

        a.recycle();
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            id = android.R.id.pasteAsPlainText;
        }

        boolean superCalled = super.onTextContextMenuItem(id);

        if (id == android.R.id.pasteAsPlainText && !allowLineBreaks) {
            try {
                String text = Utils.clearLineBreaks(this.getText().toString());
                this.setText(text);
                this.setSelection(this.getText().length());
            } catch (NullPointerException ex) {
                Log.d(TAG, ex.getMessage());
            }
        }

        return superCalled;
    }
}