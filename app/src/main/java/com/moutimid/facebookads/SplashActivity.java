package com.moutimid.facebookads;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.vimalcvs.facebookads.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        RelativeLayout imageView = findViewById(R.id.imageView);

        // Get the current Y position of the imageView
        float startY = imageView.getY();

        // Calculate the end Y position which is 100dp down from the top
        float endY = startY + getResources().getDimensionPixelSize(R.dimen.move_distance);

        // Create an ObjectAnimator to animate the translationY property
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "translationY", startY, endY);
        animator.setDuration(1000); // Set the duration of the animation in milliseconds (e.g., 1000ms = 1 second)
        animator.start(); // Start the animation
    }

    public void start(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
   public void fav(View view) {
        startActivity(new Intent(this, FavouriteScreen.class));
//        startActivity(new Intent(this, Ad_Banner.class));
    }

    public void our_application(View view) {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void rate_us(View view) {
        RateDialogClass rateDialogClass = new RateDialogClass(SplashActivity.this);
        rateDialogClass.show();
    }
}