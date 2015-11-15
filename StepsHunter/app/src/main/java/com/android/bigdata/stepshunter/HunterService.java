package com.android.bigdata.stepshunter;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class HunterService extends Service {
    private IBinder mBinder = new LocalBinder();

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

}
