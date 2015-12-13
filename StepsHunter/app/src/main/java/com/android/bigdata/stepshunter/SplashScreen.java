package com.android.bigdata.stepshunter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Resources resources = getResources();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startMainActivity();
                stopPreloaderAnimation();
                hidePreloaderAnimation();
                finish();
            }
        }, resources.getInteger(R.integer.splash_screen_pause));
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) startPreloaderAnimation();
    }

    public void startPreloaderAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation_rotation);
        findViewById(R.id.preloaderCircular).startAnimation(animation);
    }

    public void stopPreloaderAnimation() {
        findViewById(R.id.preloaderCircular).clearAnimation();
    }

    public void hidePreloaderAnimation() {
        ImageView imageView = (ImageView) findViewById(R.id.preloaderCircular);
        imageView.setVisibility(View.INVISIBLE);
    }
}
