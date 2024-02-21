package com.moutimid.facebookads.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.moutimid.facebookads.Model.DatabaseHelper;
import com.moutimid.facebookads.Model.Song;
import com.moutimid.facebookads.MusicPlayerActivity;
import com.moutimid.facebookads.R;

import java.io.Serializable;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<Song> songList;
    InterstitialAd mInterstitialAd;

    public SongAdapter(Context context, Activity activity, List<Song> songList) {
        this.context = context;
        this.activity = activity;
        this.songList = songList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.songNameTextView.setText(song.getName());
        holder.song_details_text_view.setText(song.getDescription());

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        boolean songFavorite = databaseHelper.isSongFavorite(song.getName());
        Log.d("data", songFavorite + "  " + song.getName());
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, "ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d("TAG", loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });


        if (songFavorite) {
            holder.song_fav_view.setVisibility(View.VISIBLE);
            holder.song_unfav_view.setVisibility(View.GONE);
        } else {
            holder.song_fav_view.setVisibility(View.GONE);
            holder.song_unfav_view.setVisibility(View.VISIBLE);
        }
        holder.song_fav_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(activity);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                databaseHelper.deleteSongFromFavorites(song.getName());
                holder.song_fav_view.setVisibility(View.GONE);
                holder.song_unfav_view.setVisibility(View.VISIBLE);
            }
        });
        holder.song_unfav_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                databaseHelper.addSongToFavorites(song);

                holder.song_fav_view.setVisibility(View.VISIBLE);
                holder.song_unfav_view.setVisibility(View.GONE);
            }
        });
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, MusicPlayerActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("songList", (Serializable) songList);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songNameTextView, song_details_text_view;
        ImageView songImageView, song_fav_view, song_unfav_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songNameTextView = itemView.findViewById(R.id.song_name_text_view);
            song_details_text_view = itemView.findViewById(R.id.song_details_text_view);
            songImageView = itemView.findViewById(R.id.song_image_view);
            song_fav_view = itemView.findViewById(R.id.song_fav_view);
            song_unfav_view = itemView.findViewById(R.id.song_unfav_view);
        }
    }
}
