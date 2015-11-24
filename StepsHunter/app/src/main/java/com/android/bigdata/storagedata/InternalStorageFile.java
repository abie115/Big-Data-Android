package com.android.bigdata.storagedata;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.bigdata.stepshunter.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class InternalStorageFile {

    //e.g. to delete file_with_gps use:
    //import com.android.bigdata.storagedata.InternalStorageFile;
    //InternalStorageFile i = new InternalStorageFile();
    //i.DeleteInternalStorageFile(getApplicationContext(), getString(R.string.file_with_gps));

    private void showToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }

    public void writeToGpsFile(Context context, String text){
        if(!writeToFile(context, context.getString(R.string.file_with_gps), text))
            showToast(context, context.getString(R.string.toast_internal_storage_error));
    }

    public String readFromGpsFile(Context context){
        String fileContent = readFromFile(context, context.getString(R.string.file_with_gps));
        if(null==fileContent)
            showToast(context, context.getString(R.string.toast_internal_storage_error));

        return fileContent;
    }

    public void cleanGpsFile(Context context){
        boolean deleted = deleteFile(context, context.getString(R.string.file_with_gps));
        if (!deleted)
            showToast(context, context.getString(R.string.toast_internal_storage_error));
    }

    //true if open, write and close properly
    //creates the file if it does not exist
    //write line by line
    public boolean writeToFile(Context context, String filename, String text){
        boolean ret = true;
        String lineSeparator = "\n";

        try{
            FileOutputStream fileOutputStream = context.openFileOutput(filename, context.MODE_APPEND);
            fileOutputStream.write(text.concat(lineSeparator).getBytes());
            fileOutputStream.close();
        }catch(Exception e){
            ret = false;
            Log.w(context.getString(R.string.tag_writeToFile), e.toString());
        }
        return ret;
    }

    //returns the contents of a file (also empty) in one String or null if exception;
    public String readFromFile(Context context, String filename){
        StringBuilder stringBuilder = new StringBuilder();
        String oneLine = "";
        String ret = "";

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(filename)));

            while ((oneLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(oneLine);
            }

            ret = stringBuilder.toString();
            bufferedReader.close();

        } catch (FileNotFoundException e) {
            Log.w(context.getString(R.string.tag_readFromFile), e.toString());
            ret = null;
        } catch (IOException e) {
            Log.w(context.getString(R.string.tag_readFromFile), e.toString());
            ret = null;
        }

        return ret;
    }

    //true if the file was successfully deleted or never existed
    public boolean deleteFile(Context context, String filename){
        Boolean deleted = true;

        if( context.getFileStreamPath(filename).exists()) {
            deleted = context.deleteFile(filename);
        }

        return deleted;
    }

}
