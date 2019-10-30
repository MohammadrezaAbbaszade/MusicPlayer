package com.example.mainmusicplayer.fragments;


import android.content.Context;
import android.graphics.Bitmap;
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
import java.util.Timer;
import java.util.TimerTask;
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
    private Handler mHandler;
    private boolean mWasPlaying;
    private static int oTime = 0, sTime = 0, eTime = 0;
    private TextView mDurationMusicTextView;
    private boolean mRepeateSong = false;
    private MediaPlayer mMediaPlayer;
    private FloatingActionButton mActionButton;
    private static boolean mShuffle = false;
    private AppCompatImageButton mNextSongIbtn, mPreviousSongIbtn, mShuffleSongIbtn, mRepeateSongIbtn;
    private CallBacks mCallBacks;
    private AppCompatCheckBox mRepeateAllCheckBox;
    private static final String ID = "id";
    private Long id;

    public static PlayerFragment newInstance(Long id) {

        Bundle args = new Bundle();
        args.putLong(ID, id);
        Log.d("tag", String.valueOf(id));
        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (PlayerActivity.mRepeateAll) {
            Log.d("tag", "onCompletion");
            clearMediaPlayer();
            mCallBacks.repeateList();
        }
    }

    public interface CallBacks {
        public void nextSong();

        public void previousSong();

        public void repeateList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("tag", "onattach");
        mCallBacks = (PlayerActivity) context;
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mCallBacks = null;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getLong(ID, 0);
        mMusic = MusicRepository.getInstance().getMusic(id);
        if (mMusic == null)
            Log.d("tag", "mMusic=null");
        else
            Log.d("tag", "mMmusicNotNull");
        mHandler = new Handler();
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mMusic.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        init(view);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mMediaPlayer!=null) {
                    eTime = mMediaPlayer.getDuration();
                    sTime = mMediaPlayer.getCurrentPosition();
                }
                if (oTime == 0) {
                    mSeekBar.setMax(eTime);
                    oTime = 1;
                }
                mDurationMusicTextView.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(eTime),
                        TimeUnit.MILLISECONDS.toSeconds(eTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(eTime))));
                mSeekBarStatusTv.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(sTime),
                        TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
                if (progress > 0 && mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                    clearMediaPlayer();
                    mActionButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_media_play));
                    mSeekBar.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mSeekBarStatusTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });
        mTvSongName.setText(mMusic.getTitle());
        mTvSongArtist.setText(mMusic.getArtistName());
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearMediaPlayer();
                startPlaying(mRepeateSong);
                songsTimeHandler(UpdateSongTime);
            }
        });
        mNextSongIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHandler != null)
                    mHandler.removeCallbacks(UpdateSongTime);
                clearMediaPlayer();
                mActionButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_media_play));
                mCallBacks.nextSong();
            }
        });
        mPreviousSongIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHandler != null)
                    mHandler.removeCallbacks(UpdateSongTime);
                clearMediaPlayer();
                mActionButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_media_play));
                mCallBacks.previousSong();
            }
        });
        mShuffleSongIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShuffle = !mShuffle;
                PlayerActivity.mShuffle = mShuffle;
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
    }

    private void setShuffleDrawble(boolean shuffle) {
        if (shuffle)
            mShuffleSongIbtn.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_shuffle));
        else
            mShuffleSongIbtn.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_shuffle));
    }

    private void setPauseImage() {
        mActionButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_media_pause));
    }

    private void clearMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            clearMediaPlayer();
        }
    }

    final Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            sTime = mMediaPlayer.getCurrentPosition();
            mSeekBarStatusTv.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(sTime),
                    TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
            mSeekBar.setProgress(sTime);
            mHandler.postDelayed(this, 100);
        }
    };

    private void songsTimeHandler(Runnable updateSongTime) {
        if (mWasPlaying)
            return;
        eTime = mMediaPlayer.getDuration();
        sTime = mMediaPlayer.getCurrentPosition();
        if (oTime == 0) {
            mSeekBar.setMax(eTime);
            oTime = 1;
        }
        mDurationMusicTextView.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(eTime),
                TimeUnit.MILLISECONDS.toSeconds(eTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(eTime))));
        mSeekBarStatusTv.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(sTime),
                TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
        mSeekBar.setProgress(sTime);
        mHandler.postDelayed(updateSongTime, 100);
    }

    public void startPlaying(boolean loop) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            clearMediaPlayer();
            mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
            mWasPlaying = true;
            mActionButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_media_play));

        }
        if (!mWasPlaying) {

            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                try {
                    mMediaPlayer.setDataSource(mMusic.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            setPauseImage();
            try {
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setVolume(0.5f, 0.5f);
            mMediaPlayer.setLooping(loop);
            mSeekBar.setMax(mMediaPlayer.getDuration());

            mMediaPlayer.start();
        }
    }
}
