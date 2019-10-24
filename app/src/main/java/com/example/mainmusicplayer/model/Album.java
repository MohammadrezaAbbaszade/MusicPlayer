package com.example.mainmusicplayer.model;

import android.graphics.Bitmap;

import java.util.UUID;

public class Album {
    private UUID mUUID;
    private Long id;
    private String title;
    private String artist;
    private Bitmap bitmap;
    public Album(UUID uuid) {
        mUUID = uuid;
    }
    public Album()
    {
        this(UUID.randomUUID());
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public void setUUID(UUID UUID) {
        mUUID = UUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
