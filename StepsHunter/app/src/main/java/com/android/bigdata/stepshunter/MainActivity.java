package com.android.bigdata.stepshunter;

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

public class MainActivity extends AppCompatActivity {

    private HunterService hService;
    private boolean hBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
