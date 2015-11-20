package com.android.bigdata.stepshunter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;


/**
 * Created by Natalia on 2015-11-12.
 */
public class HunterServiceSingleton {

    private Context context;

    //czas w milisekundach
    private static final long DEFAULT_FREQUENCY = 30 * 1000;
    private static final long MIN_FREQUENCY = 30 * 1000; //30 sekund
    private static final long MAX_FREQUENCY = 2 * 60 * 1000; //2 minuty
    private static long CURRENT_FREQUENCY = DEFAULT_FREQUENCY;

    private static HunterServiceSingleton ourInstance = new HunterServiceSingleton();

    public static HunterServiceSingleton getInstance() {
        return ourInstance;
    }

    private HunterServiceSingleton() {
    }


    public void init(Context context) {
        this.context = context;
    }


    public static void setCurrentFrequency(long newFrequency){
        CURRENT_FREQUENCY = newFrequency * 1000;
    }

    public static long getCurrentFrequenct(){
        return CURRENT_FREQUENCY / 1000;
    }

    //powrot do domyslnej czestotliwosci
    public static void setDefaultFrequency(){
        CURRENT_FREQUENCY = DEFAULT_FREQUENCY;
    }

    public static long getMinFrequency(){
        return MIN_FREQUENCY / 1000;
    }

    public static long getMaxFrequency(){
        return MAX_FREQUENCY / 1000;
    }

}
