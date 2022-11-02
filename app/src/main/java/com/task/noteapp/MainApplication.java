package com.task.noteapp;

import android.app.Application;

import com.task.noteapp.data.NotesRepository;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NotesRepository.getInstance().init(getApplicationContext());
    }
}
