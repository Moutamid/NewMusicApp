package com.moutimid.facebookads;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.moutimid.facebookads.Model.DatabaseHelper;
import com.moutimid.facebookads.Model.RepeatMode;
import com.moutimid.facebookads.Model.Song;
import com.vimalcvs.facebookads.R;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MusicPlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    private TextView song_name, artist_name, duration_played, duration_total;
    private SeekBar seekBar;
    ImageView song_image_view;
    private ImageView playButton, nextButton, prevButton, shuffleButton, repeatButton, favButton;
    private MediaPlayer mediaPlayer;
    private List<Song> songList;
    private List<Song> originalSongList;
    private int currentPosition = 0;
    private boolean isPlaying = false;
    private boolean isShuffleOn = false;
    private RepeatMode repeatMode = RepeatMode.NO_REPEAT;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    DatabaseHelper databaseHelper;
    ImageView song_unfav_view, song_fav_view;
    private AdView adView;
    public InterstitialAd interstitialAdFB;
    public static boolean fb1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initViews();
        initMediaPlayer();
        loadFbInterstitialAd();
        databaseHelper = new DatabaseHelper(MusicPlayerActivity.this);
        final RelativeLayout adContainer = findViewById(R.id.ad_banner_50);
        AdView adView = new AdView(this, "2722927698006061_2722935724671925", AdSize.BANNER_HEIGHT_50);
        adContainer.addView(adView);
        AdSettings.turnOnSDKDebugger(getApplicationContext());
        AdSettings.setTestMode(true);// for get test ad in your device
        adView.loadAd();


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    pauseMusic();
                } else {
                    playMusic();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSong();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevSong();
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShuffleOn) {
                    originalSongList = new ArrayList<>(songList);
                    Collections.shuffle(songList);
                    toggleShuffle(true);
                } else {
                    songList.clear();
                    songList.addAll(originalSongList);
                    toggleShuffle(false);
                }
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRepeatMode();
                updateRepeatButtonIcon();
            }
        });

        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formatted(mCurrentPosition));
                    duration_total.setText(formatted(mediaPlayer.getDuration() / 1000));
                }
                handler.postDelayed(this, 1000);
            }
        };

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress * 1000);
                    duration_played.setText(formatted(progress));
                    duration_total.setText(formatted(mediaPlayer.getDuration() / 1000));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        if (!isPlaying) {
            playMusic();
        }
        song_fav_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeToFavorites();
                song_fav_view.setVisibility(View.GONE);
                song_unfav_view.setVisibility(View.VISIBLE);

            }
        });
      song_unfav_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToFavorites();
                song_fav_view.setVisibility(View.VISIBLE);
                song_unfav_view.setVisibility(View.GONE);
                showFBInterstitial();

            }
        });
//      banner_ad();
    }

    private void removeToFavorites() {
        Song currentSong = songList.get(currentPosition);
        databaseHelper.deleteSongFromFavorites(currentSong.getName());
    }

    private void initViews() {
        shuffleButton = findViewById(R.id.shuffle);
        repeatButton = findViewById(R.id.repeatButton);
        song_name = findViewById(R.id.song_name_text_view);
        artist_name = findViewById(R.id.song_details_text_view);
        duration_played = findViewById(R.id.duration_played);
        duration_total = findViewById(R.id.duration_total);
        seekBar = findViewById(R.id.seek_bar);
        playButton = findViewById(R.id.play_pause);
        nextButton = findViewById(R.id.next);
        prevButton = findViewById(R.id.prev);
        song_image_view = findViewById(R.id.song_image_view);
        song_fav_view = findViewById(R.id.song_fav_view);
        song_unfav_view = findViewById(R.id.song_unfav_view);
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        songList = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("songList")) {
            songList = (ArrayList<Song>) intent.getSerializableExtra("songList");
        }  currentPosition = getIntent().getIntExtra("position", 0);
        try {
            mediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + songList.get(currentPosition).getMusicResourceId()));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        duration_total.setText(formatted(mediaPlayer.getDuration() / 1000));

        song_name.setText(songList.get(currentPosition).getName());
        artist_name.setText(songList.get(currentPosition).getDescription());
//        song_image_view.setImageResource(songList.get(currentPosition).getImageResourceId());
        playButton.setImageResource(R.drawable.ic_baseline_pause);

        duration_total.setText(formatted(mediaPlayer.getDuration() / 1000));
        DatabaseHelper databaseHelper = new DatabaseHelper(MusicPlayerActivity.this);
        boolean songFavorite = databaseHelper.isSongFavorite(songList.get(currentPosition).getName());
        Log.d("data", songFavorite + "  " + songList.get(currentPosition).getName());
        if (songFavorite) {
            song_fav_view.setVisibility(View.VISIBLE);
            song_unfav_view.setVisibility(View.GONE);
        } else {
            song_fav_view.setVisibility(View.GONE);
            song_unfav_view.setVisibility(View.VISIBLE);
        }
    }

    private void playMusic() {
        mediaPlayer.start();
        isPlaying = true;
        playButton.setImageResource(R.drawable.ic_baseline_pause);
        handler.postDelayed(updateSeekBar, 0);
    }

    private void pauseMusic() {
        mediaPlayer.pause();
        isPlaying = false;
        playButton.setImageResource(R.drawable.ic_baseline_play_arrow);
        handler.removeCallbacks(updateSeekBar);
    }

    private void nextSong() {
        if (repeatMode == RepeatMode.REPEAT_ONE) {
            // If repeat one mode is enabled, just play the current song again
            playNextOrPreviousSong();
            return;
        }

        if (isShuffleOn) {
            currentPosition = new Random().nextInt(songList.size());
        } else {
            currentPosition = (currentPosition + 1) % songList.size();
        }

        song_name.setText(songList.get(currentPosition).getName());
        artist_name.setText(songList.get(currentPosition).getDescription());
//        song_image_view.setImageResource(songList.get(currentPosition).getImageResourceId());
        playButton.setImageResource(R.drawable.ic_baseline_pause);

        duration_total.setText(formatted(mediaPlayer.getDuration() / 1000));
        playNextOrPreviousSong();
    }

    private void prevSong() {
        if (repeatMode == RepeatMode.REPEAT_ONE) {
            // If repeat one mode is enabled, just play the current song again
            playNextOrPreviousSong();
            return;
        }

        if (isShuffleOn) {
            currentPosition = new Random().nextInt(songList.size());
        } else {
            currentPosition = (currentPosition - 1 + songList.size()) % songList.size();
        }

        song_name.setText(songList.get(currentPosition).getName());
        artist_name.setText(songList.get(currentPosition).getDescription());
//        song_image_view.setImageResource(songList.get(currentPosition).getImageResourceId());
        playButton.setImageResource(R.drawable.ic_baseline_pause);
        duration_total.setText(formatted(mediaPlayer.getDuration() / 1000));
        playNextOrPreviousSong();
    }

    private void playNextOrPreviousSong() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + songList.get(currentPosition).getMusicResourceId()));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

        Song currentSong = songList.get(currentPosition);
        song_name.setText(currentSong.getName());
        artist_name.setText(currentSong.getDescription());
//        song_image_view.setImageResource(currentSong.getImageResourceId());

        duration_total.setText(formatted(mediaPlayer.getDuration() / 1000));

        isPlaying = true;
    }

    private void toggleShuffle(boolean shuffleEnabled) {
        isShuffleOn = shuffleEnabled;
        if (isShuffleOn) {
            shuffleButton.setImageResource(R.drawable.ic_baseline_shuffle_24);
        } else {
            shuffleButton.setImageResource(R.drawable.ic_baseline_shuffle_off);
        }
    }

    private void toggleRepeatMode() {
        switch (repeatMode) {
            case NO_REPEAT:
                repeatMode = RepeatMode.REPEAT_ONE;
                break;
            case REPEAT_ONE:
                repeatMode = RepeatMode.REPEAT_ALL;
                break;
            case REPEAT_ALL:
                repeatMode = RepeatMode.NO_REPEAT;
                break;
        }
    }

    private void addToFavorites() {
        Song currentSong = songList.get(currentPosition);
        databaseHelper.addSongToFavorites(currentSong);
    }

    private void updateRepeatButtonIcon() {
        switch (repeatMode) {
            case NO_REPEAT:
                repeatButton.setImageResource(R.drawable.ic_baseline_repeat_off);
                break;
            case REPEAT_ONE:
                repeatButton.setImageResource(R.drawable.repeat);
                break;
            case REPEAT_ALL:
                repeatButton.setImageResource(R.drawable.ic_baseline_repeat);
                break;
        }
    }

    private String formatted(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (repeatMode == RepeatMode.NO_REPEAT) {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } else {
            nextSong();
        }
    }

    @Override

    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void onBack(View view) {
        pauseMusic();
        onBackPressed();
    }

    private void loadFbInterstitialAd() {
        this.interstitialAdFB = new InterstitialAd(this, "IMG_16_9_APP_INSTALL#2722927698006061_2722934531338711");
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            private Ad ad;
            private AdError adError;

            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                fb1 = false;
                interstitialAdFB.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                StringBuilder sb = new StringBuilder();
                sb.append("Interstitial ad failed to load: ");
                sb.append(adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }

        };
        interstitialAdFB.loadAd(
                interstitialAdFB.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build()
        );

    }

    public void showFBInterstitial() {
        InterstitialAd interstitialAd = this.interstitialAdFB;
        if (interstitialAd != null && interstitialAd.isAdLoaded()) {
            this.interstitialAdFB.show();
        }

    }
}
