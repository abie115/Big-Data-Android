package com.android.bigdata.stepshunter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;

import com.android.bigdata.helper.Preloader;
import com.android.bigdata.storagedata.SettingsStorage;

public class SplashScreen extends Activity {

    private Preloader preloader;

    //settings
    private static final String USERNAME_PREFS = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Resources resources = getResources();

        String username = SettingsStorage.getSettings(USERNAME_PREFS, this);

        if(username.matches(""))
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    startRegistrationActivity();
                    hidePreloader();
                    finish();
                }
            }, resources.getInteger(R.integer.splash_screen_pause));
        else
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    startMainActivity();
                    hidePreloader();
                    finish();
                }
            }, resources.getInteger(R.integer.splash_screen_pause));
    }

    private void startRegistrationActivity() {
        Intent intent = new Intent(SplashScreen.this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        if (hasFocus) showPreloader();
    }

    private void showPreloader(){
        preloader = new Preloader(this);
        preloader.showPreloader();
    }

    private void hidePreloader(){
        if(preloader!=null)preloader.hidePreloader();
    }
}

