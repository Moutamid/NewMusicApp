package com.moutimid.facebookads;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.moutimid.facebookads.Adapter.FavoriteSongsAdapter;
import com.moutimid.facebookads.Model.DatabaseHelper;
import com.moutimid.facebookads.Model.Song;
import com.vimalcvs.facebookads.R;

import java.util.List;

public class FavouriteScreen extends AppCompatActivity {

    private RecyclerView favoriteSongsRecyclerView;
    private FavoriteSongsAdapter adapter;
    TextView no_songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_screen);
       MobileAds.initialize(this);

        AdView mAdView;
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        favoriteSongsRecyclerView = findViewById(R.id.recycler_view);
        no_songs = findViewById(R.id.no_songs);
        favoriteSongsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DatabaseHelper databaseHelper = new DatabaseHelper(FavouriteScreen.this);
        // Assuming you have a method in your database helper to get all favorite songs
        List<Song> favoriteSongs = databaseHelper.getAllFavoriteSongs();
        if(favoriteSongs.size()>0)
        {
            no_songs.setVisibility(View.GONE);
        }
        else
        {
            no_songs.setVisibility(View.VISIBLE);

        }
        adapter = new FavoriteSongsAdapter(favoriteSongs, FavouriteScreen.this);
        favoriteSongsRecyclerView.setAdapter(adapter);
    }

    public void onBack(View view) {
        onBackPressed();
    }
}
