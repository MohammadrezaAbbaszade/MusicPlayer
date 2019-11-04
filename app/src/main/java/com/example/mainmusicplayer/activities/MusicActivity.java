package com.example.mainmusicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.mainmusicplayer.R;
import com.example.mainmusicplayer.fragments.MusicFragment;

public class MusicActivity extends AppCompatActivity {
    private static final String STATUS_EXTRA = "status_extra";
    private static final String ID_EXTRA = "id_extra";
public static Intent newIntent(Context context, String state, Long id)
{
    Intent intent=new Intent(context,MusicActivity.class);
    intent.putExtra(STATUS_EXTRA, state);
    intent.putExtra(ID_EXTRA, id);
    return intent;
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        String state = getIntent().getStringExtra(STATUS_EXTRA);
        Long id = getIntent().getLongExtra(ID_EXTRA, 0);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment=fragmentManager.findFragmentById(R.id.music_activity_fragment_container);
        fragmentManager.beginTransaction()
                .replace(R.id.music_activity_fragment_container, MusicFragment.newInstance(state, id))
                .commit();
    }
}
