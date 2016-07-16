package com.example.josefhruska.firebaseapp;

import java.util.ArrayList;

public class Idea {

    private String title;
    private String description;
    private String authorName;
    private String photoUrl;
    private ArrayList<String> contributors;

    public Idea () {}

    public Idea (String title, String description, String authorName, String photoUrl,
                 ArrayList<String> contributorsUID ) {
        this.title = title;
        this.description = description;
        this.authorName = authorName;
        this.photoUrl = photoUrl;
        this.contributors = contributorsUID;
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

    public ArrayList<String> getContributors() {
        return contributors;
    }

    public void setContributors(ArrayList<String> contributors) {
        this.contributors = contributors;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
