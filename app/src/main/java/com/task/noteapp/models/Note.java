package com.task.noteapp.models;

import android.app.Activity;

import androidx.annotation.Nullable;

import com.task.noteapp.helpers.Utils;

public class Note {
    private String id;
    private String title;
    private String content;
    @Nullable
    private String imageUrl;
    private long createdAt;
    private long updatedAt = -1;

    public Note() {
    }

    public Note(String title, String content, @Nullable String imageUrl) {
        int generatedTimeStamp = generateTimeStamp();

        this.id = String.valueOf(generatedTimeStamp);
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = generatedTimeStamp;
        this.updatedAt = generatedTimeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean hasImageUrl() {
        return imageUrl != null;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@Nullable String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getCreatedAtStr(Activity activity) {
        return "Created: " + Utils.convertToDate(activity, Utils.convertSecToMilli(getCreatedAt()));
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean showUpdatedAt() {
        return getUpdatedAt() != getCreatedAt();
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedAtStr(Activity activity) {
        return "Updated: " + Utils.convertToDate(activity, Utils.convertSecToMilli(getUpdatedAt()));
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static int generateTimeStamp() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    @Override
    public String toString() {
        return "Note{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
