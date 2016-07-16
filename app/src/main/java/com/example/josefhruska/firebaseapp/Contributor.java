package com.example.josefhruska.firebaseapp;

/**
 * Created by havry on 7/16/16.
 */
public class Contributor {

    private String name;
    private String photoUrl;

    public Contributor() {}

    public Contributor ( String name, String photoUrl ) {
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
