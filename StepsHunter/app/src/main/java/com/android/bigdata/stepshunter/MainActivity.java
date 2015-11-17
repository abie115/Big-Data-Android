package com.android.bigdata.stepshunter;

import android.Manifest;
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

public class MainActivity extends AppCompatActivity {

    TextView tvLatitude;
    TextView tvLongitude;
    TextView tvLog;
    Switch swGPS;

    private boolean goToSettings =false;
    private LocationManager locationManager;
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

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        swGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    providerEnabled();

                } else {
                    stopSearchLocation();
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
        if(goToSettings){
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                startSearchLocation();
                goToSettings=false;
            } else {
                swGPS.setChecked(false);
            }
        }
    }

    public void providerEnabled(){
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            goToSettings=false;
            Toast.makeText(getApplicationContext(), getString(R.string.GPSenabled), Toast.LENGTH_LONG).show();
            startSearchLocation();
        } else {
            goToSettings=true;
            HunterServiceSingleton.getInstance().init(MainActivity.this);
            HunterServiceSingleton.getInstance().showAlertSettings();
        }
    }

    public void startSearchLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);

    }

    public void stopSearchLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        locationManager.removeUpdates(locationListener);
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


    public void showCoordinates(Location location){
        if (location != null) {
            tvLatitude.setText(getString(R.string.tvLatitude)+": "+location.getLatitude());
            tvLongitude.setText(getString(R.string.tvLongitude)+": "+location.getLongitude());
            tvLog.setText(tvLog.getText()+" " + location.getLatitude()+" " + location.getLongitude()+"\n");
        }
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showCoordinates(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
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
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            hBound = false;
        }
    };

}
