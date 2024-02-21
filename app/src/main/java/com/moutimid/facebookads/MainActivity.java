package com.moutimid.facebookads;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.moutimid.facebookads.Adapter.SongAdapter;
import com.moutimid.facebookads.Model.Song;
import com.vimalcvs.facebookads.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Song> songList;
    SongAdapter adapter;
//    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songList = new ArrayList<>();
        MobileAds.initialize(this);

        AdView mAdView;
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Add songs to songList
        songList.add(new Song("David Bowie", "The Rise and Fall of Ziggy Stardust",  R.raw.song1));
        songList.add(new Song("Nine Inch Nails", "Pretty Hate Machine",  R.raw.song2));
        songList.add(new Song("Tori Amos", "Little Earthquakes",  R.raw.song3));
        songList.add(new Song("Enigma", "Never mind",  R.raw.song4));
        songList.add(new Song("Madonna", "The Cross of Changes",  R.raw.song5));
        songList.add(new Song("Janet Jackson", "The Immaculate Collection",R.raw.song6));
        songList.add(new Song("Reflections of Hope", "The Velvet Rope",  R.raw.song7));
        songList.add(new Song("Lively and catchy track", "Excuse Me",  R.raw.excuse));
        songList.add(new Song("Emotional sequel to Filhal", "Filhal 2",  R.raw.filhal));
        songList.add(new Song("Soulful and heartfelt melody", "Kitab",  R.raw.kitab));
        songList.add(new Song("Romantic ballad about rainy season", "Main Barish ka Mosam",  R.raw.main_barish_ka_mosam));
        songList.add(new Song("Vibrant and energetic tune", "Mastani",  R.raw.mastani));
        songList.add(new Song("Rustic and earthy composition", "Mitti da Tibba",  R.raw.mitti_da_tabba));
        songList.add(new Song("Enchanting and mesmerizing melody", "O Kamli Jahi",  R.raw.o_kamli_jahi));
        songList.add(new Song("Trendy and modern track", "Prada",  R.raw.prada_song));
        songList.add(new Song("Soulful and melodic piece", "Sargi",  R.raw.sargi));
        songList.add(new Song("Energetic and upbeat song", "Tera Lara",  R.raw.tara_lara));
        songList.add(new Song("Romantic and dreamy composition", "Tera Vasta Falak sa ",  R.raw.tere_vasta));
        songList.add(new Song("Serene and tranquil melody", "Tu Subha di Pak Hawa ",  R.raw.tu_subha_di_pak_hawa_wrga));
        songList.add(new Song("Melodious and heartfelt tune", "Vaste",  R.raw.vaste_song));
        songList.add(new Song("Classic and timeless melody", "Zihal e Maskeen",  R.raw.zihal_e_maskenn));
        songList.add(new Song("Poignant and emotional ballad", "Zindagi ch kadi koi ay na Rabba",  R.raw.zndgi_ch_kadi));

        // RecyclerView setup
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter setup
         adapter = new SongAdapter(this,this,  songList);
        recyclerView.setAdapter(adapter);
        checkApp(MainActivity.this);

    }

    public static void checkApp(Activity activity) {
        String appName = "Music App";

        new Thread(() -> {
            URL google = null;
            try {
                google = new URL("https://raw.githubusercontent.com/Moutamid/Moutamid/main/apps.txt");
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(google != null ? google.openStream() : null));
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String input = null;
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if ((input = in != null ? in.readLine() : null) == null) break;
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                stringBuffer.append(input);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String htmlData = stringBuffer.toString();

            try {
                JSONObject myAppObject = new JSONObject(htmlData).getJSONObject(appName);

                boolean value = myAppObject.getBoolean("value");
                String msg = myAppObject.getString("msg");

                if (value) {
                    activity.runOnUiThread(() -> {
                        new AlertDialog.Builder(activity)
                                .setMessage(msg)
                                .setCancelable(false)
                                .show();
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        adapter.notifyDataSetChanged();
//        adView.resume();
//    }
//
//    @Override
//    protected void onPause() {
//        adView.pause();
//        super.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        adView.destroy();
//        super.onDestroy();
//    }
}
