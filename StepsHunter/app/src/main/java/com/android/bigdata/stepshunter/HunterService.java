package com.android.bigdata.stepshunter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.os.SystemClock;

import com.android.bigdata.helper.ShowMessage;
import com.android.bigdata.storagedata.SettingsStorage;


public class HunterService extends Service {

    private IBinder mBinder = new LocalBinder();

    private Context mContext;
    // interface rejestration
    private IServiceCallbacks mIServiceCallbacks;
    private LocationManager locationManager;
    boolean canGetProvider = false;

    private Location lastLocation;
    double lastLocationTime;
    boolean GPSFix = false;
    boolean isRange = false;

    private static long MIN_DISTANCE_UPDATE = 1;

    //settings
    private static final String FREQUENCY_PREFS = "frequency";

    //time in milliseconds
    private static final long DEFAULT_FREQUENCY = 30 * 1000;
    private static final long MIN_FREQUENCY = 30 * 1000; //30 seconds
    private static final long MAX_FREQUENCY = 2 * 60 * 1000; //2 minutes
    private static long CURRENT_FREQUENCY;

    public HunterService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        readFrequency();
    }

    //class for binging service to clients
    public class LocalBinder extends Binder {
        HunterService getService() {
            return HunterService.this;
        }

    }

    public void setContext(Context context) {
        mContext = context;
    }
//-----------------------------gps reading/connection lost-------------------------------------------

    public void startLocationManager() {
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
    }

    public void startSearchLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, CURRENT_FREQUENCY, MIN_DISTANCE_UPDATE, locationListener);  //update every CURRENT_FREQUENCY, 1 m
        locationManager.addGpsStatusListener(gpsStatus); //gps status listener
    }

    public void stopSearchLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        locationManager.removeUpdates(locationListener);
    }

    public void providerEnabled() {
        canGetProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean canGetProvider() {
        return canGetProvider;
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //calls function showCoordinates if coordinates was changed
            if (location != null) {
                mIServiceCallbacks.showCoordinates(location);
                lastLocationTime = SystemClock.elapsedRealtime();
                lastLocation = location;
            }
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

    //check GPS range
    private GpsStatus.Listener gpsStatus = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
                if (lastLocation != null)
                    GPSFix = (SystemClock.elapsedRealtime() - lastLocationTime) < 3000;
                if (GPSFix) {
                    if (isRange == false) {
                        hideLostGpsSignalNotification();
                    }
                    isRange = true;
                } else {
                    if (isRange == true) {
                        createLostGpsSignalNotification();
                    }
                    isRange = false;
                }
            } else if (event == GpsStatus.GPS_EVENT_FIRST_FIX) {
                GPSFix = true;
                if (isRange == false) {
                    hideLostGpsSignalNotification();
                }
                isRange = true;
            }
        }
    };


//-----------------------------frequency of measuring gps -------------------------------------------

    //write and read settings
    private void saveFrequency() {
        try {
            SettingsStorage.saveSettings(FREQUENCY_PREFS, Long.toString(CURRENT_FREQUENCY), this);
        } catch (NumberFormatException e) {
            ShowMessage.showOkDialog(getString(R.string.ERROR_readFrequency), mContext);
            Log.w("HunterService | saveFr", getString(R.string.LOG_readFrequency) + e.getMessage());
        }
    }

    private void readFrequency() {
        String savedFrequency = SettingsStorage.getSettings(FREQUENCY_PREFS, this);

        try {
            CURRENT_FREQUENCY = Long.parseLong(savedFrequency);
        } catch (NumberFormatException e) {
            setDefaultFrequency();
            Log.w("HunterService | readFr", getString(R.string.LOG_readFrequency) + e.getMessage());
        }
    }

    public void setCurrentFrequency(long newFrequency) {
        CURRENT_FREQUENCY = newFrequency * 1000;
        saveFrequency();
    }

    public long getCurrentFrequenct() {
        return CURRENT_FREQUENCY / 1000;
    }

    //comeback to default frequency
    public void setDefaultFrequency() {
        CURRENT_FREQUENCY = DEFAULT_FREQUENCY;
        saveFrequency();
    }

    public long getMinFrequency() {
        return MIN_FREQUENCY / 1000;
    }

    public long getMaxFrequency() {
        return MAX_FREQUENCY / 1000;
    }

    public void setServiceCallbacks(IServiceCallbacks callbacks) {
        mIServiceCallbacks = callbacks;
    }

//-------------------------------notification------------------------------------------------------

    private void createLostGpsSignalNotification() {
        NotificationCompat.Builder mBuilder;
        mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.lost_gps_signal)
                .setContentTitle(getString(R.string.notification_title_lost_gps_signal))
                .setContentText(getString(R.string.notification_text_lost_gps_signal))
                .setAutoCancel(true);

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, mBuilder.build());
    }

    private void hideLostGpsSignalNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }
}
