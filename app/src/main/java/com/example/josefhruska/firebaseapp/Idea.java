package com.example.josefhruska.firebaseapp;

import java.util.ArrayList;

public class Idea {

    private String title;
    private String description;
    private String authorName;
    private String photoUrl;

    public Idea () {}

    public Idea (String title, String description, String authorName, String photoUrl ) {
        this.title = title;
        this.description = description;
        this.authorName = authorName;
        this.photoUrl = photoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
