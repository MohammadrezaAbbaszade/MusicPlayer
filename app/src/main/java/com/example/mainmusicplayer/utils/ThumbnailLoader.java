package com.example.mainmusicplayer.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ThumbnailLoader<T> extends HandlerThread {
    public static final int MESSAGE_DOWNLOAD_WHAT = 0;
    public static final String TAG = "ThumbnailLoader";
    private Bitmap bitmap;
    private Activity mActivity;
    //in background thread
    private Handler mRequestHandler;
    //in main thread
    private Handler mResponseHandler;
    private ConcurrentHashMap<T, String> mRequestMap;
    private ThumbnailDownloaderListener<T> mThumbnailDownloaderListener;

    public void setThumbnailDownloaderListener(ThumbnailDownloaderListener<T> thumbnailDownloaderListener) {
        mThumbnailDownloaderListener = thumbnailDownloaderListener;
    }

    public ThumbnailLoader(Handler responseHandler, Activity activity) {
        super(TAG);
        mActivity = activity;
        mResponseHandler = responseHandler;
        mRequestMap = new ConcurrentHashMap<T, String>();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == MESSAGE_DOWNLOAD_WHAT) {

                    //download image and show image in photo holder
                    final T target = (T) msg.obj;
                    final String path = mRequestMap.get(target);
                    if (path == null)
                        return;

                    try {
                        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
                        mediaMetadata.setDataSource(path);
                        byte[] imageByte = mediaMetadata.getEmbeddedPicture();
                        if (imageByte != null) {
                            bitmap = PictureUtils
                                    .getScaledBitmap(imageByte, mActivity);
                        }

                        mResponseHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mRequestMap.get(target) != path)
                                    return;
                                mRequestMap.remove(target);

                                mThumbnailDownloaderListener
                                        .onThumbnailDownloaded(target, bitmap);
                            }
                        });

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }
        };
    }

    public void queueThumbnail(String path, T target) {
        if (path == null) {
            mRequestMap.remove(target);
        } else {
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD_WHAT, target).sendToTarget();
            mRequestMap.put(target, path);
        }
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD_WHAT);
    }

    public interface ThumbnailDownloaderListener<T> {
        void onThumbnailDownloaded(T target, Bitmap bitmap);
    }
}
