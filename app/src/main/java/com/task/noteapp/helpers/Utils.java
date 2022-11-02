package com.task.noteapp.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.task.noteapp.models.Note;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utils {
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (inputMethodManager.isAcceptingText() && activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    public static void showSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.toggleSoftInput(
                InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT
        );
    }

    public static String clearLineBreaks(String str) {
        str = str.replace("\n", "");
        return str.replace("\r", "");
    }

    public static String convertToDate(Activity activity, long milliseconds) {
        Locale current = activity.getResources().getConfiguration().getLocales().get(0);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.''yy", current);
        Date date = new Date(milliseconds);
        return dateFormat.format(date);
    }

    public static boolean hasLength(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static void fetchAndSetImageToImageView(String tag, ImageView imageView, String imageUrl) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap image = null;

                try {
                    InputStream in = new java.net.URL(imageUrl).openStream();
                    image = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e(tag, e.getMessage());
                    e.printStackTrace();
                }

                Bitmap finalImage = image;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(finalImage);
                    }
                });
            }
        });
    }

    public static String getIdFromPosition(LinkedHashMap<String, Note> entries, int position) {
        List<String> listKeys = new ArrayList<String>(entries.keySet());
        return listKeys.get(position);
    }

    public static int getPositionFromId(LinkedHashMap<String, Note> entries, String id) {
        List<String> listKeys = new ArrayList<String>(entries.keySet());
        return listKeys.indexOf(id);
    }

    public static int convertDpToPixel(float dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static long convertSecToMilli(long timestamp) {
        return timestamp * 1000;
    }
}
