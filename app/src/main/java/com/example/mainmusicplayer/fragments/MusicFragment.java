package com.example.mainmusicplayer.fragments;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.mainmusicplayer.MusicRepository;
import com.example.mainmusicplayer.R;
import com.example.mainmusicplayer.activities.PlayerActivity;
import com.example.mainmusicplayer.model.Music;
import com.example.mainmusicplayer.utils.PictureUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends Fragment {
    private SearchView mSearchView;
    private SearchView.OnQueryTextListener queryTextListener;
    private Music mMusic;
    List<Music> musicList;
    private RecyclerView mRecyclerView;
    private MusicAdapter mMusicAdapter;

    public static MusicFragment newInstance() {

        Bundle args = new Bundle();

        MusicFragment fragment = new MusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MusicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            mSearchView = (SearchView) searchItem.getActionView();
        }
        if (mSearchView != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    mMusicAdapter.getFilter().filter(newText);

                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    mMusicAdapter.getFilter().filter(query);

                    return true;
                }
            };
            mSearchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_menu:
                return true;
        }
        mSearchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        init(view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateUI();

        return view;
    }

    private void init(View view) {
        mRecyclerView = view.findViewById(R.id.music_list_recycler);

    }

    private class MusicHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewTitle;
        private TextView mTextViewArtistName;
        private ImageView cover_image;
        private Music mMusic;

        public MusicHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewTitle = itemView.findViewById(R.id.songs_name_tv);
            mTextViewArtistName = itemView.findViewById(R.id.artist_name_tv);
            cover_image = itemView.findViewById(R.id.cover_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = PlayerActivity.newIntent(getActivity(), mMusic.getID());
                    startActivity(intent);

                }
            });
        }

        public void bindCrime(Music music) {
            mTextViewTitle.setText(music.getTitle());
            mTextViewArtistName.setText(music.getArtistName());
            mMusic = music;
        }
    }

    private class MusicAdapter extends RecyclerView.Adapter<MusicHolder> implements Filterable {

        private List<Music> mMusics;
        private List<Music> mMusicsListFiltered;

        public MusicAdapter(List<Music> musics) {
            mMusics = musics;
            mMusicsListFiltered = musics;
        }

        public void setCrimes(List<Music> musics) {
            mMusics = musics;
            mMusicsListFiltered = musics;
        }

        @NonNull
        @Override
        public MusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.music_list_item, parent, false);
            MusicHolder musicHolder = new MusicHolder(view);
            return musicHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MusicHolder holder, int position) {


            holder.bindCrime(mMusicsListFiltered.get(position));
            MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
            mediaMetadata.setDataSource(musicList.get(position).getPath());
            byte[] imageByte = mediaMetadata.getEmbeddedPicture();
            if (imageByte != null) {
                Bitmap bitmap = PictureUtils
                        .getScaledBitmap(imageByte, getActivity());
                holder.cover_image.setImageBitmap(bitmap);
            }


        }

        @Override
        public int getItemCount() {
            return mMusicsListFiltered.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        mMusicsListFiltered = mMusics;
                    } else {
                        List<Music> filteredList = new ArrayList<>();
                        for (Music row : mMusics) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) || row.getArtistName().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                        mMusicsListFiltered = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mMusicsListFiltered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mMusicsListFiltered = (ArrayList<Music>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }

    public void updateUI() {
        musicList = MusicRepository.getInstance().getMusicList();
        if (mMusicAdapter == null) {
            mMusicAdapter = new MusicAdapter(musicList);
            mRecyclerView.setAdapter(mMusicAdapter);
        } else {
            mMusicAdapter.setCrimes(musicList);
            mMusicAdapter.notifyDataSetChanged();
        }

    }

}
