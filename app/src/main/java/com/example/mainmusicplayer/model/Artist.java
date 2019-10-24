package com.example.mainmusicplayer.model;

import android.graphics.Bitmap;

import java.util.UUID;

public class Artist {
    private String name;
    private Long id;
    private Bitmap bitmap;
    private int tracks;
    private int albums;
    private Long albumId;
    private UUID mId;
    public Artist(UUID uuid) {
        mId = uuid;
    }
    public Artist()
    {
        this(UUID.randomUUID());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getTracks() {
        return tracks;
    }

    public void setTracks(int tracks) {
        this.tracks = tracks;
    }

    public int getAlbums() {
        return albums;
    }

    public void setAlbums(int albums) {
        this.albums = albums;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }
}
