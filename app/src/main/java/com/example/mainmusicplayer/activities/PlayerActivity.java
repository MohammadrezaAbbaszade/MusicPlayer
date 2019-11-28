package com.example.mainmusicplayer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import java.util.UUID;

public class PlayerActivity extends AppCompatActivity {
    private static final String ID_EXTRA = "id_extra_song";
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<Music> mMusicList;
    public static boolean mShuffle;
    public static boolean mRepeateAll;


    public static Intent newIntent(Context context, Long songId) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(ID_EXTRA,songId);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Long id= getIntent().getLongExtra(ID_EXTRA,0);
        FragmentManager fm=getSupportFragmentManager();
        Fragment fragment=fm.findFragmentById(R.id.player_activity_container);
        if(fragment==null) {
            fm.beginTransaction().replace(R.id.player_activity_container, PlayerFragment.newInstance(id))
                    .commit();
        }
    }
}
