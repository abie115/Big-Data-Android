package com.android.bigdata.storagedata;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.bigdata.stepshunter.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public void writeGPSJsonToFile(CoordinatesJavaBean coordinatesJavaBean, String fileName) {
        Gson gson = new Gson();

        if (!writeToFile(fileName, gson.toJson(coordinatesJavaBean)))
            showToast(context.getString(R.string.toast_internal_storage_error));
    }

    public JSONObject readGPSJsonFromFile(String fileName) {
        String oneLine;
        JSONArray jsonArray = new JSONArray();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(fileName)));

            while ((oneLine = bufferedReader.readLine()) != null) {
                jsonArray.put(new JSONObject(oneLine));
            }

            bufferedReader.close();
            return new JSONObject().put(context.getString(R.string.json_coordinates_title), jsonArray);

        } catch (FileNotFoundException e) {
            logException(e);
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            logException(e);
            return null;
        }
    }

    public void deleteGpsFile(String fileName) {
        try {
            if (ifFileExist(fileName))
                deleteFile(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showToast(context.getString(R.string.toast_internal_storage_error));
        }
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

    public void deleteFile(String filename) throws FileNotFoundException {
        if (ifFileExist(filename))
            context.deleteFile(filename);
        else
            throw new FileNotFoundException(context.getString(R.string.msg_fileDeletedException));
    }

    public boolean ifFileExist(String filename) {
        return context.getFileStreamPath(filename).exists();
    }

}
