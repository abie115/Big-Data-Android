package com.android.bigdata.stepshunter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.File;
import java.io.FileOutputStream;

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

    //true if the file was successfully deleted or never existed
    //e.g. to delete file_with_gps use: DeleteInternalStorageFile(getString(R.string.file_with_gps));
    public Boolean DeleteInternalStorageFile(String filename){
        Context context = getApplicationContext();
        Boolean deleted = true;

        if( context.getFileStreamPath(filename).exists()) {
            deleted = context.deleteFile(filename);
        }
        return deleted;
    }

    //true if file never existed or if properly opened, override and closed
    public Boolean EraseInternalStorageFile(String filename){
        Context context = getApplicationContext();
        FileOutputStream fileOutputStream;
        String eraser = "";
        Boolean erased = true;

        if(context.getFileStreamPath(filename).exists())
            try {
                fileOutputStream = context.openFileOutput(filename, MODE_PRIVATE);
                fileOutputStream.write(eraser.getBytes());
                fileOutputStream.close();
            } catch (Exception e) {
                erased = false;
            }

        return erased;
    }

}
