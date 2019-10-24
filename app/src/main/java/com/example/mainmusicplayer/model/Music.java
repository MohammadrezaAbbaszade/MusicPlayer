package com.example.mainmusicplayer.model;

import android.graphics.Bitmap;

import java.util.UUID;

public class Music {
    private Long mID;
    private UUID mId;
    private String mTitle;
    private String mAlbumName;
    private int mTime;
    private String mArtistName;
    private String mPath;
    private Bitmap mBitmap;
    public Music(UUID uuid) {
        mId = uuid;
    }
    public Music()
    {
        this(UUID.randomUUID());
    }

    public Long getID() {
        return mID;
    }

    public void setID(Long ID) {
        mID = ID;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
    public String getAlbumName() {
        return mAlbumName;
    }

    public void setAlbumName(String albumName) {
        mAlbumName = albumName;
    }

    public int getTime() {
        return mTime;
    }

    public void setTime(int time) {
        mTime = time;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public void setArtistName(String artistName) {
        mArtistName = artistName;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }
}
