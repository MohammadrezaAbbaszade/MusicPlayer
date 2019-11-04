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
import com.example.mainmusicplayer.activities.MusicActivity;
import com.example.mainmusicplayer.model.Artist;
import com.example.mainmusicplayer.utils.PictureUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment {
    private Artist mArtist;
    List<Artist> mArtistList;
    private RecyclerView mRecyclerView;
    private ArtistAdaptor mArtistAdaptor;
    private SearchView mSearchView;
    private SearchView.OnQueryTextListener queryTextListener;

    public static ArtistFragment newInstance() {

        Bundle args = new Bundle();

        ArtistFragment fragment = new ArtistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ArtistFragment() {
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
                    mArtistAdaptor.getFilter().filter(newText);

                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    mArtistAdaptor.getFilter().filter(query);

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
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        mRecyclerView = view.findViewById(R.id.artist_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateUI();

        return view;
    }

    private class ArtistHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private TextView mArtistTv;
        private Artist mArtist;

        public ArtistHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.artist_item_cover);
            mArtistTv = itemView.findViewById(R.id.artist_item_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = MusicActivity.newIntent(getContext(), "artist", mArtist.getId());
                    startActivity(intent);
                }
            });
        }

        public void bindCrime(Artist artist) {
            mArtist = artist;
            mArtistTv.setText(artist.getName());
            mArtist = artist;
        }
    }

    private class ArtistAdaptor extends RecyclerView.Adapter<ArtistHolder> implements Filterable {

        private List<Artist> mArtists;
        private List<Artist> mArtistsListFiltered;

        public ArtistAdaptor(List<Artist> artists) {
            mArtists = artists;
            mArtistsListFiltered = artists;
        }

        public void setCrimes(List<Artist> artists) {
            mArtists = artists;
            mArtistsListFiltered = artists;
        }

        @NonNull
        @Override
        public ArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.artist_item, parent, false);
            ArtistHolder artistHolder = new ArtistHolder(view);
            return artistHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ArtistHolder holder, int position) {


            holder.bindCrime(mArtistsListFiltered.get(position));
            MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
            mediaMetadata.setDataSource(MusicRepository.getInstance().getArtistPath(getActivity(), mArtistList.get(position).getId()));
            byte[] imageByte = mediaMetadata.getEmbeddedPicture();
            if (imageByte != null) {
                Bitmap bitmap = PictureUtils
                        .getScaledBitmap(imageByte, getActivity());
                holder.mImageView.setImageBitmap(bitmap);
            }

        }

        @Override
        public int getItemCount() {
            return mArtistsListFiltered.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        mArtistsListFiltered = mArtists;
                    } else {
                        List<Artist> filteredList = new ArrayList<>();
                        for (Artist row : mArtists) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                        mArtistsListFiltered = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mArtistsListFiltered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mArtistsListFiltered = (ArrayList<Artist>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }

    public void updateUI() {
        mArtistList = MusicRepository.getInstance().getArtistList();
        if (mArtistAdaptor == null) {
            mArtistAdaptor = new ArtistAdaptor(mArtistList);
            mRecyclerView.setAdapter(mArtistAdaptor);
        } else {
            mArtistAdaptor.setCrimes(mArtistList);
            mArtistAdaptor.notifyDataSetChanged();
        }

    }
}
