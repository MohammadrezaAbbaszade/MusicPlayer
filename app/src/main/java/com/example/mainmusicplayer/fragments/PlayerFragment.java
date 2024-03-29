package com.example.mainmusicplayer.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mainmusicplayer.MusicRepository;
import com.example.mainmusicplayer.R;
import com.example.mainmusicplayer.activities.PlayerActivity;
import com.example.mainmusicplayer.model.Music;
import com.example.mainmusicplayer.utils.PictureUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment implements MediaPlayer.OnCompletionListener {
    private Music mMusic;
    private TextView mTvSongName, mTvSongArtist, mSeekBarStatusTv;
    private CircleImageView mSongCoverIv;
    private SeekBar mSeekBar;
    Timer timer;
    private Handler mHandler;
    private int lengh = 0;
    private boolean mWasPlaying = false;
    private static int oTime = 0, sTime = 0, eTime = 0;
    private TextView mDurationMusicTextView;
    private boolean mRepeateSong = false;
    public static MediaPlayer mMediaPlayer;
    private FloatingActionButton mActionButton;
    private static boolean mShuffle = false;
    private AppCompatImageButton mNextSongIbtn, mPreviousSongIbtn, mShuffleSongIbtn, mRepeateSongIbtn;
    private AppCompatCheckBox mRepeateAllCheckBox;
    private static final String ID = "id";
    private Long id;
    private static int musicPlayerTimePosition = 0;
    final Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            sTime = mMediaPlayer.getCurrentPosition();
            mSeekBarStatusTv.setText(String.format("%d min: %d sec", TimeUnit.MILLISECONDS.toMinutes(sTime),
                    TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
            mSeekBar.setProgress(sTime);
            mHandler.postDelayed(this, 100);
        }
    };

    public static PlayerFragment newInstance(Long id) {

        Bundle args = new Bundle();
        args.putLong(ID, id);
        Log.d("tag", String.valueOf(id));
        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Long id = getArguments().getLong(ID);
        mMusic = MusicRepository.getInstance().getMusic(id);
        if(mMediaPlayer!=null&&mMediaPlayer.isPlaying())
        {
            clearMediaPlayer();
            mHandler = new Handler();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer = MediaPlayer.create(getContext(), Uri.parse(mMusic.getPath()));
        }else {
            mHandler = new Handler();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer = MediaPlayer.create(getContext(), Uri.parse(mMusic.getPath()));
        }

        Log.d("tag", "onCreate");


    }

    public PlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mWasPlaying = true;
            setPauseImage();
            startPlaying(mMusic, mRepeateSong, lengh);
            songsTimeHandler(UpdateSongTime);
        }

        Log.d("tag", "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(UpdateSongTime);
        Log.d("tag", "onDestroy");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        init(view);
        Log.d("tag", "onCreateView");

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //Update the progress depending on seek bar
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    mSeekBar.setProgress(progress);
                }
                if (mSeekBar.getProgress() == mMediaPlayer.getDuration()) {
                    Log.d("tag", "onCompletion");
                    if(!mRepeateSong) {
                        onCompeleteSong();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mRepeateAllCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRepeateSong = true;
                    mMediaPlayer.setLooping(true);
                } else {
                    mRepeateSong = false;
                    mMediaPlayer.setLooping(false);
                }

            }
        });
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mWasPlaying == true) {
                    setPlayImage();
                    mMediaPlayer.pause();
                    mWasPlaying = false;
                    lengh = mMediaPlayer.getCurrentPosition();
                } else {
                    mWasPlaying = true;
                    setPauseImage();
                    startPlaying(mMusic, mRepeateSong, lengh);
                    songsTimeHandler(UpdateSongTime);
                }

            }
        });
        mNextSongIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearMediaPlayer();
                mHandler.removeCallbacks(UpdateSongTime);
                mHandler = new Handler();
                int index = MusicRepository.getInstance().getPosition(mMusic.getId());

                Music music = MusicRepository.getInstance().getMusicList().get((++index) % MusicRepository.getInstance().getMusicList()
                        .size());
                startPlaying(music, mRepeateSong, 0);
                songsTimeHandler(UpdateSongTime);
                MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
                mediaMetadata.setDataSource(music.getPath());
                byte[] imageByte = mediaMetadata.getEmbeddedPicture();
                if (imageByte != null) {
                    Bitmap bitmap = PictureUtils
                            .getScaledBitmap(imageByte, getActivity());
                    mSongCoverIv.setImageBitmap(bitmap);
                }
                mTvSongName.setText(music.getTitle());
                mTvSongArtist.setText(music.getArtistName());
                setPauseImage();

            }
        });
        mPreviousSongIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearMediaPlayer();
                mHandler.removeCallbacks(UpdateSongTime);
                mHandler = new Handler();
                int index = MusicRepository.getInstance().getPosition(mMusic.getId());
                Music music = MusicRepository.getInstance().getMusicList().get((index - 1 + MusicRepository.getInstance().getMusicList()
                        .size()) % MusicRepository.getInstance().getMusicList()
                        .size());
                startPlaying(music, mRepeateSong, 0);
                songsTimeHandler(UpdateSongTime);
                MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
                mediaMetadata.setDataSource(music.getPath());
                byte[] imageByte = mediaMetadata.getEmbeddedPicture();
                if (imageByte != null) {
                    Bitmap bitmap = PictureUtils
                            .getScaledBitmap(imageByte, getActivity());
                    mSongCoverIv.setImageBitmap(bitmap);
                }
                mTvSongName.setText(music.getTitle());
                mTvSongArtist.setText(music.getArtistName());
                setPauseImage();

            }
        });
        mShuffleSongIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShuffle = !mShuffle;
                setShuffleDrawble(mShuffle);

            }
        });
        return view;
    }

    private void init(View view) {
        mTvSongArtist = view.findViewById(R.id.player_song_artist);
        mTvSongName = view.findViewById(R.id.player_song_name);
        mSongCoverIv = view.findViewById(R.id.player_song_cover);
        mSeekBar = view.findViewById(R.id.player_seek_bar);
        mActionButton = view.findViewById(R.id.floatingActionButton);
        mSeekBarStatusTv = view.findViewById(R.id.seek_bar_status_tv);
        mNextSongIbtn = view.findViewById(R.id.play_next_iBtn);
        mPreviousSongIbtn = view.findViewById(R.id.previous_song_iBtn);
        mShuffleSongIbtn = view.findViewById(R.id.shuffle_play);
        mRepeateSongIbtn = view.findViewById(R.id.song_repeate_iBtn);
        mRepeateAllCheckBox = view.findViewById(R.id.repeate_all_check_box);
        mDurationMusicTextView = view.findViewById(R.id.music_duration);
        mDurationMusicTextView.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(mMusic.getTime()),
                TimeUnit.MILLISECONDS.toSeconds(mMusic.getTime()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mMusic.getTime()))));
        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(mMusic.getPath());
        byte[] imageByte = mediaMetadata.getEmbeddedPicture();
        if (imageByte != null) {
            Bitmap bitmap = PictureUtils
                    .getScaledBitmap(imageByte, getActivity());
            mSongCoverIv.setImageBitmap(bitmap);
        }
        mTvSongName.setText(mMusic.getTitle());
        mTvSongArtist.setText(mMusic.getArtistName());

    }

    private void setPauseImage() {
        mActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.ic_media_pause));
    }

    private void setShuffleDrawble(boolean shuffle) {
        if (shuffle)
            mShuffleSongIbtn.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_shuffle));
        else
            mShuffleSongIbtn.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_shuffle));
    }

    private void setPlayImage() {
        mActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.ic_media_play));
    }

    private void clearMediaPlayer() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        clearMediaPlayer();
        Music music = MusicRepository.getInstance().getMusicList().get(randomGenerator());
        startPlaying(music, mRepeateSong, 0);
        songsTimeHandler(UpdateSongTime);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        clearMediaPlayer();
    }

    public void startPlaying(Music music, boolean loop, int lengh) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        mMusic = music;
        mMediaPlayer = MediaPlayer.create(getContext(), Uri.parse(music.getPath()));

        if (lengh == 0) {
            mMediaPlayer.seekTo(0);
            mSeekBar.setProgress(0);
        } else {
            mMediaPlayer.seekTo(mSeekBar.getProgress());
        }
        mMediaPlayer.setLooping(loop);
        mSeekBar.setMax(mMediaPlayer.getDuration());

        mMediaPlayer.start();
    }

    private void songsTimeHandler(Runnable updateSongTime) {
        eTime = mMediaPlayer.getDuration();
        sTime = mMediaPlayer.getCurrentPosition();
        if (oTime == 0) {
            mSeekBar.setMax(eTime);
            oTime = 1;
        }

        mDurationMusicTextView.setText(String.format("%d min: %d sec", TimeUnit.MILLISECONDS.toMinutes(eTime),
                TimeUnit.MILLISECONDS.toSeconds(eTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(eTime))));
        mSeekBarStatusTv.setText(String.format("%d min: %d sec", TimeUnit.MILLISECONDS.toMinutes(sTime),
                TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
        mSeekBar.setProgress(sTime);
        mHandler.postDelayed(updateSongTime, 100);
    }

    public void stopPlaying() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("tag", "onPause");
    }

    private void onCompeleteSong() {
        clearMediaPlayer();
        mHandler.removeCallbacks(UpdateSongTime);
        mHandler = new Handler();

        int index = MusicRepository.getInstance().getPosition(mMusic.getId());
        if (mShuffle&&!mRepeateSong) {
            Music music = MusicRepository.getInstance().getMusicList().get(randomGenerator());
            startPlaying(music, mRepeateSong, 0);
            songsTimeHandler(UpdateSongTime);
            MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
            mediaMetadata.setDataSource(music.getPath());
            byte[] imageByte = mediaMetadata.getEmbeddedPicture();
            if (imageByte != null) {
                Bitmap bitmap = PictureUtils
                        .getScaledBitmap(imageByte, getActivity());
                mSongCoverIv.setImageBitmap(bitmap);
            }
            mTvSongName.setText(music.getTitle());
            mTvSongArtist.setText(music.getArtistName());
            setPauseImage();
        } else if(!mShuffle&&!mRepeateSong) {
            Music music = MusicRepository.getInstance().getMusicList().get((++index) % MusicRepository.getInstance().getMusicList()
                    .size());
            startPlaying(music, mRepeateSong, 0);
            songsTimeHandler(UpdateSongTime);
            MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
            mediaMetadata.setDataSource(music.getPath());
            byte[] imageByte = mediaMetadata.getEmbeddedPicture();
            if (imageByte != null) {
                Bitmap bitmap = PictureUtils
                        .getScaledBitmap(imageByte, getActivity());
                mSongCoverIv.setImageBitmap(bitmap);
            }
            mTvSongName.setText(music.getTitle());
            mTvSongArtist.setText(music.getArtistName());
            setPauseImage();
        }


    }

    private int randomGenerator() {
        Random random = new Random();
        int low = 0;
        int high = MusicRepository.getInstance().getMusicList().size();
        int result = random.nextInt(high - low) + low;
        return result;

    }
}
