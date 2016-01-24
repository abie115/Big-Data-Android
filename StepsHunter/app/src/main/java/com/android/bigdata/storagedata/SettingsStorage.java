package com.android.bigdata.storagedata;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsStorage {

    //settings
    private static final String PREFS_NAME = "HunterPrefsFile";
    private static SharedPreferences settings;

    private static void readSettings(Context context){
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void saveSettings(String name, String value, Context context){
        readSettings(context);

        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putString(name, value);
        preferencesEditor.commit();
    }

    public static String getSettings(String name, Context context) {
        readSettings(context);

        try {
            return settings.getString(name, "");
        } catch (ClassCastException e){
            return "";
        }
    }

    public static void removeSettings(String name, Context context){
        readSettings(context);

        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.remove(name);
        preferencesEditor.apply();
    }
}
