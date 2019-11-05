package com.example.mainmusicplayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.mainmusicplayer.activities.PlayerActivity;
import com.example.mainmusicplayer.activities.ViewPagerActivity;
import com.example.mainmusicplayer.model.Album;
import com.example.mainmusicplayer.model.Artist;
import com.example.mainmusicplayer.model.Music;
import com.example.mainmusicplayer.utils.PictureUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MusicRepository {
    private static MusicRepository mInstance;
    private List<Music> mMusicList;
    private List<Album> mAlbumList;
    private List<Artist> mArtistList;
    private Activity mActivity;
    final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    final String where = MediaStore.Audio.Media.IS_MUSIC + "!=0";

    public List<Artist> getArtistList() {
        return mArtistList;
    }

    public Music getMusic(Long id) {
        for (Music music : mMusicList) {
            if (music.getID().equals(id))
                return music;
        }
        return null;
    }

    public static MusicRepository getInstance() {
        if (mInstance == null) {
            mInstance = new MusicRepository();
        }
        return mInstance;
    }

    private MusicRepository() {
        mMusicList = new ArrayList<>();
        mAlbumList = new ArrayList<>();
        mArtistList = new ArrayList<>();
    }

    public List<Music> getMusicList() {
        return mMusicList;
    }

    public List<Album> getAlbumList() {
        return mAlbumList;
    }

    public void getFiles(Activity activity) {
        mActivity = activity;
        final Cursor cursor = activity.getContentResolver().query(uri,
                null, where, null, null);

        try {
            if (cursor.getCount() <= 0) return;

            while (cursor.moveToNext()) {
                Long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String artist = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String track = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String data = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                Long albumId = cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                int artistTracks = 0;
                int artistAlbums = 0;
                Long artistId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));

                int duration = cursor.getInt(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

//                Bitmap bitmap = setBitmap(activity, albumId);

                setArtistList(artist, artistTracks, artistAlbums, artistId, albumId);

                setAlbumList(artist, album, albumId);


                setSongList(id, artist, artistId, albumId, track, data, duration);

            }
        } finally {
            cursor.close();
        }
    }

    public String getAlbumPath(Activity activity, Long albumId) {
        String result = null;
/*        String where = MediaStore.Audio.Media.IS_MUSIC + "!= 0 " + " AND " + "cast(" +
                MediaStore.Audio.Media.ALBUM_ID + "as text) == " + String.valueOf(albumId);*/
        String where = MediaStore.Audio.Media.IS_MUSIC + "!=0" + " AND " + MediaStore.Audio.Media.ALBUM_ID + "=" + String.valueOf(albumId);

        final Cursor cursor = activity.getContentResolver().query(uri, null, where, null, null);
        try {
            if (cursor.getCount() <= 0)
                return null;

            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                result = data;
            }
        } finally {
            cursor.close();
        }


        return result;
    }

    public String getArtistPath(Activity activity, Long artistId) {
        String result = null;
/*        String where = MediaStore.Audio.Media.IS_MUSIC + "!= 0 " + " AND " + "cast(" +
                MediaStore.Audio.Media.ALBUM_ID + "as text) == " + String.valueOf(albumId);*/
        String where = MediaStore.Audio.Media.IS_MUSIC + "!=0" + " AND " + MediaStore.Audio.Media.ARTIST_ID + "=" + String.valueOf(artistId);
        final Cursor cursor = activity.getContentResolver().query(uri, null, where, null, null);
        try {
            if (cursor.getCount() <= 0)
                return null;

            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                result = data;
            }
        } finally {
            cursor.close();
        }


        return result;
    }

    private void setArtistList(String artist, int artistTracks, int artistAlbums, Long artistId, Long albumId) {
        Artist artistModel = new Artist();
        artistModel.setId(artistId);
        artistModel.setAlbums(artistAlbums);
        artistModel.setTracks(artistTracks);
//        artistModel.setBitmap(bitmap);
        artistModel.setName(artist);
        artistModel.setAlbumId(albumId);
        mArtistList.add(artistModel);
    }

    private void setAlbumList(String artist, String album, Long albumId) {
        Album albumModel = new Album();
        albumModel.setArtist(artist);
//        albumModel.setBitmap(bitmap);
        albumModel.setTitle(album);
        albumModel.setId(albumId);

        mAlbumList.add(albumModel);
    }

    private void setSongList(Long id, String artist, Long artistId, Long albumId, String track, String data, int duration) {
        Music music = new Music();
        music.setArtistName(artist);
        music.setID(id);
        music.setTitle(track);
        music.setPath(data);

        music.setTime(duration);
        music.setArtistId(artistId);
        music.setAlbumId(albumId);
//        music.setBitmap(bitmap);
        mMusicList.add(music);
    }

    public List<Music> getSongListByArtist(Long artistiD) {

        List<Music> result = new ArrayList<>();
        for (Music music : mMusicList) {
            if (music.getArtistId().equals(artistiD))
                result.add(music);
        }
        return result;
    }

    public List<Music> getSongListByAlbum(Long albumId) {
        List<Music> result = new ArrayList<>();
        for (Music music : mMusicList) {
            if (music.getAlbumId().equals(albumId))
                result.add(music);
        }
        return result;
    }

    public int getPosition(UUID uuid) {
        List<Music> musics = getMusicList();
        for (int i = 0; i < musics.size(); i++) {
            if (musics.get(i).getId().equals(uuid))
                return i;
        }

        return 0;
    }
}
