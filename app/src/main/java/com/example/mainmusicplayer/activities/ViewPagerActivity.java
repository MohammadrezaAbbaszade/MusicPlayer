package com.example.mainmusicplayer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.mainmusicplayer.MusicRepository;
import com.example.mainmusicplayer.R;
import com.example.mainmusicplayer.fragments.AlbumFragment;
import com.example.mainmusicplayer.fragments.ArtistFragment;
import com.example.mainmusicplayer.fragments.MusicFragment;
import com.google.android.material.tabs.TabLayout;

public class ViewPagerActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = findViewById(R.id.main_view_pager);
        mToolbar = findViewById(R.id.main_toolbar);
//        setSupportActionBar(mToolbar);
        mViewPager.setOffscreenPageLimit(2);
       checkRequestRresponse();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (position == 0)
                    return MusicFragment.newInstance();
                else if (position == 1)
                    return AlbumFragment.newInstance();
                else
                    return ArtistFragment.newInstance();
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0)
                    return "SONGS";
                else if (position == 1)
                    return "ALBUMS";
                else
                    return "ARTISTS";
            }
        });
        mTabLayout = findViewById(R.id.main_tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   MusicRepository.getInstance().getFiles(this);

                } else {
                    checkRequestRresponse();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void checkRequestRresponse() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }else {
            MusicRepository.getInstance().getFiles(this);
        }
    }

}
