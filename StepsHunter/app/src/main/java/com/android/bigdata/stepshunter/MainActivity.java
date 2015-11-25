package com.android.bigdata.stepshunter;

import com.android.bigdata.storagedata.InternalStorageFile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;

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
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;



public class MainActivity extends AppCompatActivity implements IServiceCallbacks {

    private EditText frequency;

    private HunterService hService;
    private boolean hBound = false;
    boolean inSettings=false;
    private IServiceCallbacks hThis = this;
    private boolean isHunting = false;

    TextView tvLatitude;
    TextView tvLongitude;
    TextView tvLog;
    Switch swGPS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        tvLog = (TextView) findViewById(R.id.tvLog);
        swGPS = (Switch) findViewById(R.id.swGPS);
        tvLog.setText(tvLog.getText()+"\n");

        frequency = (EditText) findViewById(R.id.frequency_value);

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

        Log.d("onStart", "hBound: " + hBound + " hService: " + hService);

        if(hService == null)
            bindHunterService();

       if(inSettings){ //if comeback from settings, start from onstart
            hService.providerEnabled();
            if(hService.canGetProvider){
                Toast.makeText(getApplicationContext(), getString(R.string.GPSenabled), Toast.LENGTH_LONG).show();
                hService.startSearchLocation();
                inSettings=false;
                isHunting = true;
                frequency.setEnabled(false);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.GPSdisabled), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindHunterService();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showCoordinates(Location location){
        tvLatitude.setText(getString(R.string.tvLatitude) + " " + location.getLongitude());
        tvLongitude.setText(getString(R.string.tvLongitude) + " " + location.getLatitude());
        tvLog.setText(tvLog.getText() + " " + location.getLongitude() + " " + location.getLatitude() + "\n");

        //-------demonstration how class internalStorageFile works------
        //--------------------to change after testing-------------------
        InternalStorageFile internalStorageFile = new InternalStorageFile(getApplicationContext());
        internalStorageFile.writeToGpsFile(
                getString(R.string.tvLatitude) + ": " + location.getLongitude()
                        + " " +
                        getString(R.string.tvLongitude) + ": " + location.getLatitude()
        );

        String s = internalStorageFile.readFromGpsFile();
        Log.d("TestZapis/Odczyt", s);
        //--------------------------------------------------------------------

    }

    @Override
    public void messageFromService(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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

    // frequency
    public void changeFrequency(View view){
        String message;

        if(isHunting){
            message = getString(R.string.frequency_change_warning);
        } else {

            if(frequency.getText().toString().matches(""))
                message = "Musisz podać wartość.";
            else {
                long newFrequency = Long.parseLong(frequency.getText().toString());

                if (newFrequency < hService.getMinFrequency())
                    message = "Podana wartość jest zbyt niska.";
                else if (newFrequency > hService.getMaxFrequency())
                    message = "Podana wartość jest zbyt wysoka.";
                else {
                    hService.setCurrentFrequency(newFrequency);
                    message = "Zmieniono na " + hService.getCurrentFrequenct() + " sek.";
                }
            }

        }

        showDialog(message);
    }

    public void setDefaultFrequency(View view){
        String message;

        if(isHunting){
            message = getString(R.string.frequency_change_warning);
        } else {
            hService.setDefaultFrequency();
            frequency.setText(Long.toString(hService.getCurrentFrequenct()));
            message = "Przywrócono " + hService.getCurrentFrequenct() + " sek.";
        }
        showDialog(message);
    }

    private void showDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }


    //******************************************************
    // Service

    private void bindHunterService(){
        // service binding
        Intent intent = new Intent(this, HunterService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindHunterService(){
        // service unbinding
        if (hBound) {
            unbindService(mConnection);
            hBound = false;
        }
    }

    //**********************************************

    // define service call, parameters gived by bindService()
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            HunterService.LocalBinder binder = (HunterService.LocalBinder) service;
            hService = binder.getService();
            hBound = true;

            hService.setContext(MainActivity.this);
            hService.setServiceCallbacks(hThis);

            //set current frequency to edittext
            frequency.setText(Long.toString(hService.getCurrentFrequenct()));

            hService.startLocationManager();
            swGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Log.d("SwitchON","Jestem !!!!!!!!!!!!!!!!!!!!");
                        hService.providerEnabled();
                        if(hService.canGetProvider()){  //if localisation is on don't go to settings(false), start gps
                            Log.d("SwitchOFF","Mogę wziąć !!!!!!!!!!!!!!!!!!!!");
                            inSettings=false;
                            Toast.makeText(getApplicationContext(), getString(R.string.GPSenabled), Toast.LENGTH_LONG).show();
                            hService.startSearchLocation();
                            isHunting = true;
                            frequency.setEnabled(false);
                        }else{
                            Log.d("SwitchOFF","Nie mogę wziąć  !!!!!!!!!!!!!!!!!!!!");
                            inSettings=true;

                            showAlertSettings();
                        }
                    } else {
                        Log.d("SwitchOFF","Jestem !!!!!!!!!!!!!!!!!!!!");
                        hService.stopSearchLocation();
                        isHunting = false;
                        frequency.setEnabled(true);
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            hBound = false;
        }
    };

}
