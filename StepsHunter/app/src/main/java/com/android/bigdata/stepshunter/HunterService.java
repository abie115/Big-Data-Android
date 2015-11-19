package com.android.bigdata.stepshunter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class HunterService extends Service {
    private IBinder mBinder = new LocalBinder();

    //ustawienia
    private static final String PREFS_NAME = "HunterPrefsFile";
    private static final String FREQUENCY_PREFS = "frequency";
    private SharedPreferences settings;

    //czas w milisekundach
    private static final long DEFAULT_FREQUENCY = 30 * 1000;
    private static final long MIN_FREQUENCY = 30 * 1000; //30 sekund
    private static final long MAX_FREQUENCY = 2 * 60 * 1000; //2 minuty
    private static long CURRENT_FREQUENCY;


    public HunterService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public void onCreate(){
        //Context context = getApplicationContext();
        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            // context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            //PreferenceManager.getDefaultSharedPreferences(context);
            //getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        readFrequency();
    }

    //***********************************

    //klasa umozliwiajaca bindowanie klientom
    public class LocalBinder extends Binder {
        HunterService getService (){
            return HunterService.this;
        }
    }

    //************************************

    public void setCurrentFrequency(long newFrequency){
        CURRENT_FREQUENCY = newFrequency * 1000;
        saveFrequency();
    }

    public long getCurrentFrequenct(){
        return CURRENT_FREQUENCY / 1000;
    }

    //powrot do domyslnej czestotliwosci
    public void setDefaultFrequency(){
        CURRENT_FREQUENCY = DEFAULT_FREQUENCY;
        saveFrequency();
    }

    public long getMinFrequency(){
        return MIN_FREQUENCY / 1000;
    }

    public long getMaxFrequency(){
        return MAX_FREQUENCY / 1000;
    }

    //zapis i odczyt ustawien
    private void saveFrequency(){
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putString(FREQUENCY_PREFS, Long.toString(CURRENT_FREQUENCY));
        preferencesEditor.commit();
    }

    private void readFrequency(){
        String savedFrequency = settings.getString(FREQUENCY_PREFS,"");

        try {
            CURRENT_FREQUENCY = Long.parseLong(savedFrequency);
        } catch (NumberFormatException e){
            CURRENT_FREQUENCY = DEFAULT_FREQUENCY;
            Log.d("HunterService", "Problem z pobieraniem częstotliwości.\n Wiadomość błędu: " + e.getMessage());
        }
    }
}
