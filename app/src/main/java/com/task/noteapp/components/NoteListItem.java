package com.task.noteapp.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.task.noteapp.R;
import com.task.noteapp.helpers.Utils;

public class NoteListItem extends ConstraintLayout {
    private static final String TAG = "NoteListItem";
    Context context;

    LinearLayout contentLinearLayout;
    ImageView imageView;
    TextView titleTextView;
    TextView contentTextView;
    TextView createdAtTextView;
    TextView updatedAtTextView;
    Button overlayButton;

    public NoteListItem(Context context) {
        super(context);
        init(context, null, 0);
    }

    public NoteListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public NoteListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        this.context = context;

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.NoteListItem, defStyle, 0);
        a.recycle();

        setupViews();
    }

    private void setupViews() {
        this.setId(View.generateViewId());

        this.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.convertDpToPixel(100, context)
        ));

        setupImage();
        setupTitle();
        setupContent();
        setupDates();
        setupButton();
        mergeViews();
    }

    private void setupImage() {
        imageView = new ImageView(context);
        imageView.setId(View.generateViewId());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private void setupTitle() {
        titleTextView = createTextView(18, R.color.text_color);
        titleTextView.setSingleLine(true);
        titleTextView.setLines(1);
        titleTextView.setEllipsize(TextUtils.TruncateAt.END);
    }

    private void setupContent() {
        contentTextView = createTextView(14, R.color.subtext_color);
        contentTextView.setSingleLine(false);
        contentTextView.setMaxLines(2);
        contentTextView.setEllipsize(TextUtils.TruncateAt.END);
    }

    private void setupDates() {
        createdAtTextView = createTextView(11, R.color.subtext_color);
        createdAtTextView.setPadding(0, 0, Utils.convertDpToPixel(10, context), 0);
        createdAtTextView.setTypeface(createdAtTextView.getTypeface(), Typeface.BOLD);

        updatedAtTextView = createTextView(11, R.color.subtext_color);
        updatedAtTextView.setTypeface(updatedAtTextView.getTypeface(), Typeface.BOLD);
    }

    private void setupButton() {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(
                android.R.attr.selectableItemBackground,
                typedValue,
                true
        );

        overlayButton = new Button(context);
        overlayButton.setId(View.generateViewId());
        overlayButton.setBackgroundResource(typedValue.resourceId);
    }

    private void mergeViews() {
        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, Utils.convertDpToPixel(3, context));

        LayoutParams imageViewLayoutParams = new LayoutParams(
                Utils.convertDpToPixel(72, context),
                Utils.convertDpToPixel(72, context)
        );

        LayoutParams contentLayoutParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        contentLayoutParams.setMarginStart(Utils.convertDpToPixel(16, context));
        contentLayoutParams.setMarginEnd(Utils.convertDpToPixel(16, context));

        imageViewLayoutParams.setMarginStart(Utils.convertDpToPixel(16, context));
        addView(imageView, imageViewLayoutParams);

        LinearLayout datesLinearLayout = new LinearLayout(context);
        datesLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        datesLinearLayout.addView(createdAtTextView);
        datesLinearLayout.addView(updatedAtTextView);

        contentLinearLayout = new LinearLayout(context);
        contentLinearLayout.setId(View.generateViewId());
        contentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        contentLinearLayout.addView(titleTextView, layoutParams);
        contentLinearLayout.addView(contentTextView, layoutParams);
        contentLinearLayout.addView(datesLinearLayout);
        addView(contentLinearLayout, contentLayoutParams);

        addView(overlayButton, new LayoutParams(0, 0));

        createConstraints();
    }

    private TextView createTextView(int textSize, int colorId) {
        TextView textView = new TextView(context);
        textView.setId(View.generateViewId());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.setTextColor(getResources().getColor(colorId));
        return textView;
    }

    private void createConstraints() {
        ConstraintSet cSet = new ConstraintSet();
        cSet.clone(this);
        cSet.connect(imageView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                ConstraintSet.TOP);
        cSet.connect(imageView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM);
        cSet.connect(imageView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                ConstraintSet.START);
        cSet.connect(contentLinearLayout.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                ConstraintSet.TOP);
        cSet.connect(contentLinearLayout.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM);
        cSet.connect(contentLinearLayout.getId(), ConstraintSet.START, imageView.getId(),
                ConstraintSet.END);
        cSet.connect(contentLinearLayout.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,
                ConstraintSet.END);
        cSet.connect(overlayButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                ConstraintSet.TOP);
        cSet.connect(overlayButton.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM);
        cSet.connect(overlayButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                ConstraintSet.START);
        cSet.connect(overlayButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,
                ConstraintSet.END);
        cSet.applyTo(this);
    }

    public void setImage(@Nullable String imageUrl) {
        if (imageUrl != null) {
            imageView.setVisibility(View.VISIBLE);
            Utils.fetchAndSetImageToImageView(TAG, imageView, imageUrl);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    public void setTitle(String title) {
        titleTextView.setText(Utils.clearLineBreaks(title));
    }

    public void setContent(String content) {
        contentTextView.setText(Utils.clearLineBreaks(content));
    }

    public void setDates(String createdAt, @Nullable String updatedAt) {
        createdAtTextView.setText(createdAt);

        if (updatedAt != null) {
            updatedAtTextView.setVisibility(View.VISIBLE);
            updatedAtTextView.setText(updatedAt);
        } else {
            updatedAtTextView.setVisibility(View.GONE);
        }
    }
}