package com.example.mainmusicplayer.fragments;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.example.mainmusicplayer.activities.MusicActivity;
import com.example.mainmusicplayer.model.Album;
import com.example.mainmusicplayer.model.Music;
import com.example.mainmusicplayer.utils.PictureUtils;

import java.util.ArrayList;
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
    private AlbumAdaptor mAlbumAdapter;

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
                    mAlbumAdapter.getFilter().filter(newText);

                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    mAlbumAdapter.getFilter().filter(query);

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
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        mRecyclerView = view.findViewById(R.id.album_recyler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        updateUI();
        return view;
    }

    private class AlbumHolder extends RecyclerView.ViewHolder {

        private ImageView mCoverIv;
        private TextView mArtistTv;
        private TextView mAlbumTv;
        private Album mAlbum;

        public AlbumHolder(@NonNull View itemView) {
            super(itemView);
            mAlbumTv = itemView.findViewById(R.id.album_item_album_tv);
            mArtistTv = itemView.findViewById(R.id.album_item_artist_tv);
            mCoverIv = itemView.findViewById(R.id.album_item_cover_iv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = MusicActivity.newIntent(getContext(), "album", mAlbum.getId());
                    startActivity(intent);
                }
            });
        }

        public void bindCrime(Album album) {
//            mCoverIv.setImageBitmap(album.getBitmap());
            mArtistTv.setText(album.getArtist());
            mAlbumTv.setText(album.getTitle());
            mAlbum = album;
        }
    }

    private class AlbumAdaptor extends RecyclerView.Adapter<AlbumHolder> implements Filterable {

        private List<Album> mAlbums;
        private List<Album> mAlbumsListFiltered;

        public AlbumAdaptor(List<Album> musics) {
            mAlbums = musics;
            mAlbumsListFiltered = musics;
        }

        public void setCrimes(List<Album> musics) {
            mAlbums = musics;
            mAlbumsListFiltered = musics;
        }

        @NonNull
        @Override
        public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.album_item, parent, false);
            AlbumHolder albumHolder = new AlbumHolder(view);
            return albumHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull AlbumHolder holder, int position) {


            holder.bindCrime(mAlbumsListFiltered.get(position));
//            MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
//            mediaMetadata.setDataSource(MusicRepository.getInstance().getAlbumPath(getActivity(),mAlbumList.get(position).getId()));
//            byte [] imageByte = mediaMetadata.getEmbeddedPicture();
//            if (imageByte !=null){
//                Bitmap bitmap = PictureUtils
//                        .getScaledBitmap(imageByte, getActivity());
//                holder.mCoverIv.setImageBitmap(bitmap);
//
//            }

        }

        @Override
        public int getItemCount() {
            return mAlbumsListFiltered.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        mAlbumsListFiltered = mAlbums;
                    } else {
                        List<Album> filteredList = new ArrayList<>();
                        for (Album row : mAlbums) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) || row.getArtist().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                        mAlbumsListFiltered = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mAlbumsListFiltered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mAlbumsListFiltered = (ArrayList<Album>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }

    public void updateUI() {
        mAlbumList = MusicRepository.getInstance().getAlbumList();
        if (mAlbumAdapter == null) {
            mAlbumAdapter = new AlbumAdaptor(mAlbumList);
            mRecyclerView.setAdapter(mAlbumAdapter);
        } else {
            mAlbumAdapter.setCrimes(mAlbumList);
            mAlbumAdapter.notifyDataSetChanged();
        }
    }
}