package com.example.mainmusicplayer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.mainmusicplayer.MusicRepository;
import com.example.mainmusicplayer.R;
import com.example.mainmusicplayer.fragments.AlbumFragment;
import com.example.mainmusicplayer.fragments.ArtistFragment;
import com.example.mainmusicplayer.fragments.MusicFragment;
import com.example.mainmusicplayer.fragments.PlayerFragment;
import com.example.mainmusicplayer.model.Music;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements PlayerFragment.CallBacks {
    private static final String ID_EXTRA = "id_extra_song";
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<Music> mMusicList;
    public static boolean mShuffle;
    public static boolean mRepeateAll;


    public static Intent newIntent(Context context, Long songId) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(ID_EXTRA, songId);
        return intent;
    }

    public int getSongIndex(Long id) {
        int index = -1;

        for (int i = 0; i < mMusicList.size(); i++) {

            if (mMusicList.get(i).getId().equals(id)) {
                index = i;
                break;

            }
        }
        return index;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mViewPager = findViewById(R.id.player_activity_view_pager);
        mTabLayout = findViewById(R.id.player_activity_tab);
        mMusicList = MusicRepository.getInstance().getMusicList();

        Long id = getIntent().getLongExtra(ID_EXTRA, 0);

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(),0) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return PlayerFragment.newInstance(mMusicList.get(position).getID());
            }

            @Override
            public int getCount() {
                return mMusicList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mMusicList.get(position).getTitle();
            }
        });
        mViewPager.setCurrentItem(getSongIndex(id));
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public void nextSong() {
        if (!mShuffle) {
            int current = mViewPager.getCurrentItem();
            mViewPager.setCurrentItem(current + 1);
        } else
            mViewPager.setCurrentItem(randomGenerator());
    }

    @Override
    public void previousSong() {
        if (!mShuffle) {
            int current = mViewPager.getCurrentItem();
            mViewPager.setCurrentItem(current - 1);
        } else
            mViewPager.setCurrentItem(randomGenerator());
    }

    @Override
    public void repeateList() {
        mViewPager.setCurrentItem(0);
    }

    private int randomGenerator() {
        Random random = new Random();
        int low = 0;
        int high = mMusicList.size();
        int result = random.nextInt(high - low) + low;
        return result;

    }

}
