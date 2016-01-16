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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ServerConnection extends Application {

    Context context;

    public ServerConnection(Context context) {
        this.context = context;
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isOnline()) {
                Log.d("Test", "isOnline");
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

    //try {context.unregisterReceiver(broadcastReceiver);}
    // catch(IllegalArgumentException e) {Log.d("Test", "nie wlaczony" + e.getMessage());}

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
        try {
            Log.d("Test", "start");

            URL url = prepareServerUrl();
            HttpURLConnection connection = prepareHttpUrlConnection(url);
            String data = prepareDataToSent();
            sentDataToServer(data, connection);

            //<string name="server_url">http://test:test123@dziablo.ddns.net:3000/api/coordinates</string>
            Log.d("Test", "server response " + connection.getResponseCode());
            Log.d("Test", "server header " + connection.getHeaderFields().toString());
            Log.d("Test", "server url " + url);
            Log.d("Test", "server data " + data);

            closeConnection(connection);

        } catch (MalformedURLException eURL) {
            eURL.printStackTrace();
            log(eURL);
        } catch (IOException eIO) {
            eIO.printStackTrace();
            log(eIO);
        }
    }

    private void log(IOException e){
        StringWriter trace = new StringWriter();
        e.printStackTrace(new PrintWriter(trace));
        Log.d("Test", trace.toString());
    }

    private URL prepareServerUrl() throws MalformedURLException {
        return new URL(
                context.getString(R.string.server_protocol),
                context.getString(R.string.server_host),
                context.getResources().getInteger(R.integer.server_port),
                context.getString(R.string.server_path));
    }

    private String prepareDataToSent() {
        return new InternalStorageFile(context).readJsonFromGpsFile().toString();
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

    private void closeConnection(HttpURLConnection connection){
        connection.disconnect();
    }
}