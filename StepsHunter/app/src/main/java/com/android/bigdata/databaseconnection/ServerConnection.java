package com.android.bigdata.databaseconnection;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    Context context;

    public ServerConnection(Context context){
        this.context = context;
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isOnline()) {
                Log.d("Test", "isOnline");
                //threadSentPost();
                context.unregisterReceiver(this);
            }
        }
    };

    public void turnOnSendingCoordinates(){
        new Thread(){
            @Override
            public void run() {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
                intentFilter.addAction("android.net.wifi.STATE_CHANGE");

                try {

                    while(true) {
                        context.registerReceiver(broadcastReceiver, intentFilter);
                        sleep(context.getResources().getInteger(R.integer.frequency_of_sending_coordinates));
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //try {context.unregisterReceiver(broadcastReceiver);}
    // catch(IllegalArgumentException e) {Log.d("Test", "nie wlaczony" + e.getMessage());}

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void threadSentPost(){
            new Thread() {
                @Override
                public void run() {
                   sentPost();
                }
            }.start();
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

