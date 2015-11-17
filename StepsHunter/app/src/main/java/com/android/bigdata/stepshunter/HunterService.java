package com.android.bigdata.stepshunter;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class HunterService extends Service {
    private IBinder mBinder = new LocalBinder();

    //czas w milisekundach
    private static final long DEFAULT_FREQUENCY = 30 * 1000;
    private static final long MIN_FREQUENCY = 30 * 1000; //30 sekund
    private static final long MAX_FREQUENCY = 2 * 60 * 1000; //2 minuty
    private static long CURRENT_FREQUENCY = DEFAULT_FREQUENCY;

    public HunterService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    //***********************************

    //klasa umozliwiajaca bindowanie klientom
    public class LocalBinder extends Binder {
        HunterService getService (){
            return HunterService.this;
        }
    }

    //************************************

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
