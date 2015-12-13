package com.android.bigdata.storagedata;

import android.content.Context;
import android.widget.Toast;

import com.android.bigdata.stepshunter.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public class InternalStorageFile {

    private static Context context;

    public InternalStorageFile(Context context) {
        this.context = context;
    }

    private void showToast(String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }

    private void logException(Exception e) {
        Logger logger = Logger.getLogger(context.getString(R.string.tag_internalStorageFile));
        StringWriter trace = new StringWriter();
        e.printStackTrace(new PrintWriter(trace));
        logger.severe(trace.toString());
    }

    public void writeToGpsFile(String text) {
        if (!writeToFile(context.getString(R.string.file_with_gps), text))
            showToast(context.getString(R.string.toast_internal_storage_error));
    }

    public String readFromGpsFile() {
        String fileContent = readFromFile(context.getString(R.string.file_with_gps));
        if (null == fileContent) {
            showToast(context.getString(R.string.toast_internal_storage_error));
            return "";
        }

        return fileContent;
    }

    public void cleanGpsFile() {
        boolean deleted = deleteFile(context.getString(R.string.file_with_gps));
        if (!deleted)
            showToast(context.getString(R.string.toast_internal_storage_error));
    }

    //creates the file if it does not exist
    public boolean writeToFile(String filename, String text) {
        String lineSeparator = "\n";

        try {
            FileOutputStream fileOutputStream = context.openFileOutput(filename, context.MODE_APPEND);
            fileOutputStream.write(text.concat(lineSeparator).getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public String readFromFile(String filename) {
        StringBuilder stringBuilder = new StringBuilder();
        String oneLine;

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(filename)));

            while ((oneLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(oneLine);
            }

            bufferedReader.close();

        } catch (FileNotFoundException e) {
            logException(e);
            return null;
        } catch (IOException e) {
            logException(e);
            return null;
        }

        return stringBuilder.toString();
    }

    //true if the file was successfully deleted or never existed
    public boolean deleteFile(String filename) {
        Boolean deleted = true;

        try {

            if (context.getFileStreamPath(filename).exists())
                deleted = context.deleteFile(filename);
            else
                throw new FileNotFoundException(context.getString(R.string.msg_fileDeletedException));

        } catch (FileNotFoundException e) {
            logException(e);
        }

        return deleted;
    }

}