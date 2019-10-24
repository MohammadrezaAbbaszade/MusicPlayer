package com.example.mainmusicplayer.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.mainmusicplayer.R;
import com.example.mainmusicplayer.model.Album;
import com.example.mainmusicplayer.model.Music;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends Fragment {
    private SearchView mSearchView;
    private SearchView.OnQueryTextListener queryTextListener;
    private Album mAlbum;
    List<Album> mAlbumList;
    private RecyclerView mRecyclerView;
    private MusicFragment.MusicAdapter mMusicAdapter;

    public static AlbumFragment newInstance() {

        Bundle args = new Bundle();

        AlbumFragment fragment = new AlbumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);


        return view;
    }

}
