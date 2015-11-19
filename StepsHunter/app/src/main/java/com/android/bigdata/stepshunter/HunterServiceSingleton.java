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

    private static HunterServiceSingleton ourInstance = new HunterServiceSingleton();

    public static HunterServiceSingleton getInstance() {
        return ourInstance;
    }

    private HunterServiceSingleton() {
    }

    public void init(Context context) {
        this.context = context;
    }

}
