package com.android.bigdata.stepshunter;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class HunterService extends Service {


    private static double wspolrzedna = 0;
    private IBinder mBinder = new LocalBinder();

    private /*final*/ Context mContext;
    // Rejestrujemy interfejs
    private IServiceCallbacks mIServiceCallbacks;
    private LocationManager locationManager;
    boolean canGetProvider = false;

    //dodaje do konstruktora interfej (must)!!!
    public HunterService(Context context,IServiceCallbacks iServiceCallbacks) { //usuniete
        this.mContext = context;
        this.mIServiceCallbacks=iServiceCallbacks;
    }

    public HunterService(){ }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    //***********************************

    //klasa umozliwiajaca bindowanie klientom
    public class LocalBinder extends Binder {
        HunterService getService (){
            return HunterService.this;
        }

    }

    public void startLocationManager(){
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
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
        //updatuje co 1s, odleglosc 1 metra
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

    public void providerEnabled(){
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            canGetProvider=true;
        } else {
            canGetProvider=false;
       }
    }

    public boolean canGetProvider() {
        return canGetProvider;
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //wywoluje funkcje showCoordinates jesli nastapi zmiana wspolrzednych
            mIServiceCallbacks.showCoordinates(location);
          //  setCoordinates(location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            mIServiceCallbacks.messageFromService(mContext.getResources().getString(R.string.GPSenabled));
        }

        @Override
        public void onProviderDisabled(String provider) {
            mIServiceCallbacks.messageFromService(mContext.getResources().getString(R.string.GPSdisabled));
        }
    };


  /*  public static void setCoordinates(double location){
        wspolrzedna=location;
    }

    public static double getCoordinates(){
        return wspolrzedna;
    }*/
}
