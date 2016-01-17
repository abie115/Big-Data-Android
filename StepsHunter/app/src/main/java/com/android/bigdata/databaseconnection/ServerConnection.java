package com.android.bigdata.databaseconnection;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import com.android.bigdata.stepshunter.R;
import com.android.bigdata.storagedata.InternalStorageFile;
import com.android.bigdata.storagedata.SettingsStorage;

import java.io.IOException;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URL;

import static java.util.UUID.nameUUIDFromBytes;

public class ServerConnection extends Application {

    Context context;

    public ServerConnection(Context context) {
        this.context = context;
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isOnline()) {
                threadSentPost();
                context.unregisterReceiver(this);
            }
        }
    };

    public void turnOnSendingCoordinates() {
        new Thread() {
            @Override
            public void run() {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
                intentFilter.addAction("android.net.wifi.STATE_CHANGE");

                try {

                    while (true) {
                        context.registerReceiver(broadcastReceiver, intentFilter);
                        sleep(context.getResources().getInteger(R.integer.frequency_of_sending_coordinates));
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void threadSentPost() {
        new Thread() {
            @Override
            public void run() {
                sentPost();
            }
        }.start();
    }

    public void sentPost() {
        String data = prepareDataToSent();
        if (data == null) {
            return;
        }

        try {
            HttpURLConnection connection = prepareHttpUrlConnection(prepareServerUrl());
            sentDataToServer(data, connection);
            deleteGpsFileIfResponseOk(connection.getResponseCode());
            closeConnection(connection);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteGpsFileIfResponseOk(Integer responseCode) {
        if (responseCode == HttpURLConnection.HTTP_OK)
            new InternalStorageFile(context).deleteGpsFile();
    }

    private URL prepareServerUrl() throws MalformedURLException {
        String server_path = context.getString(R.string.server_path) + "/" +
                changeStringToUUID(new SettingsStorage().getSettings(context.getString(R.string.shared_preferences_user_id), context));

        return new URL(
                context.getString(R.string.server_protocol),
                context.getString(R.string.server_host),
                context.getResources().getInteger(R.integer.server_port),
                server_path
        );
    }

    private String changeStringToUUID(String id){
        return nameUUIDFromBytes(id.getBytes()).toString();
    }

    private String prepareDataToSent() {
        if (new InternalStorageFile(context).ifGpsFileExist())
            return new InternalStorageFile(context).readJsonFromGpsFile().toString();
        else
            return null;
    }

    private HttpURLConnection prepareHttpUrlConnection(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setConnectTimeout(context.getResources().getInteger(R.integer.server_connect_timeout));
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(0);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        String user_info = context.getString(R.string.server_user_login) + ":" + context.getString(R.string.server_user_password);
        String basic = Base64.encodeToString(user_info.getBytes(), Base64.NO_WRAP);
        connection.setRequestProperty("Authorization", "Basic " + basic);

        return connection;
    }

    private void sentDataToServer(String data, HttpURLConnection connection) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(data);
        out.flush();
        out.close();
    }

    private void closeConnection(HttpURLConnection connection) {
        connection.disconnect();
    }
}