package com.android.bigdata.stepshunter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements IServiceCallbacks {

    TextView tvLatitude;
    TextView tvLongitude;
    TextView tvLog;
    Switch swGPS;

    boolean inSettings=false;
    private HunterService hService;
    private boolean hBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        tvLog = (TextView) findViewById(R.id.tvLog);
        swGPS = (Switch) findViewById(R.id.swGPS);

        hService=new HunterService(MainActivity.this);

        hService.startLocationManager();

       swGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hService.providerEnabled();
                    //jesli wlaczona lokalizacja to nie przechodzimy do settingow(false), starujemy gps
                  if(hService.canGetProvider){
                        inSettings=false;
                        Toast.makeText(getApplicationContext(), getString(R.string.GPSenabled), Toast.LENGTH_LONG).show();
                        hService.startSearchLocation();
                    }else{
                       inSettings=true;
                       showAlertSettings();
                    }
                } else {
                    hService.stopSearchLocation();
                }
             }
         });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
       //startHunterService();
      //  startService(new Intent(this, HunterService.class));
       //jesli przeslismy do settingow, to starujemy w onstart
        if(inSettings){
            hService.providerEnabled();
            if(hService.canGetProvider){
                Toast.makeText(getApplicationContext(), getString(R.string.GPSenabled), Toast.LENGTH_LONG).show();
                hService.startSearchLocation();
                inSettings=false;
            } else {
                //swGPS.setChecked(false);
                Toast.makeText(getApplicationContext(), getString(R.string.GPSdisabled), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopHunterService();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showCoordinates(Location location){
        if (location != null) {
            tvLatitude.setText(getString(R.string.tvLatitude)+": "+location.getLongitude());
            tvLongitude.setText(getString(R.string.tvLongitude)+": "+location.getLatitude());
            tvLog.setText(tvLog.getText()+" " + location.getLongitude()+" " + location.getLatitude()+"\n");
       }
    }

    public void showAlertSettings() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(MainActivity.this.getResources().getString(R.string.GPSdisabled));
        alertDialog.setMessage(MainActivity.this.getResources().getString(R.string.GPSalert));
        alertDialog.setPositiveButton(MainActivity.this.getResources().getString(R.string.Settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MainActivity.this.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton(MainActivity.this.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    //******************************************************
    // Serwis

    //jesli ma startowac przy rozpoczeciu aktywnosci to wywolac w onStart()
    private void startHunterService(){
        // Bindowanie serwisu
        Intent intent = new Intent(this, HunterService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    //jeśli ma konczyc przy konczeniu akrywnosci to wywolac w onStop()
    private void stopHunterService(){
        // Odbindowanie serwisu
        if (hBound) {
          //  hService.setCallbacks(null);//////////////interfejs
            unbindService(mConnection);
            hBound = false;
        }
    }

    //**********************************************

    // definiuje wywolania serwisu, parametry podane przez bindService()
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            HunterService.LocalBinder binder = (HunterService.LocalBinder) service;
            hService = binder.getService();
            hBound = true;
          // hService.setCallbacks(MainActivity.this); ////interfejs
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            hBound = false;
        }
    };

}
