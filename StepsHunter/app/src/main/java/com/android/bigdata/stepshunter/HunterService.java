package com.android.bigdata.stepshunter;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.File;

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
ss
    public void DeleteFileWithGps(){
        try{
            File file = new File(getFilesDir(),getString(R.string.file_with_gps));
            Boolean deleted = file.delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
