package com.android.bigdata.databaseconnection;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.android.bigdata.stepshunter.R;
import com.android.bigdata.storagedata.InternalStorageFile;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ServerConnection extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Handler handler = new Handler();

        new Thread(new Runnable() {
            public void run() {
                sentPost();
            }
        }).start();

    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void sentPost() {

        try {
            String data = new InternalStorageFile(this).readJsonFromGpsFile().toString();
            URL url = new URL(this.getString(R.string.server_url));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            int HttpResult = connection.getResponseCode();
            Log.d("Test", "server response " + HttpResult);

            if(HttpResult == HttpURLConnection.HTTP_OK) {
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(data);
                out.flush();
                out.close();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
