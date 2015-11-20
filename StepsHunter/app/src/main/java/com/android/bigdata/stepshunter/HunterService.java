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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class HunterService extends Service {


    private static double wspolrzedna = 0;
    private IBinder mBinder = new LocalBinder();

    private /*final*/ Context mContext;
    // Rejestrujemy interfejs
    private IServiceCallbacks mIServiceCallbacks;
    private LocationManager locationManager;
    boolean canGetProvider = false;

    private Location lastLocation;
    double lastLocationTime;
    boolean GPSFix=false;

    //rivate static long MIN_FREQUENCY_UPDATE=1000;
    private static long MIN_DISTANCE_UPDATE=1;

    //ustawienia
    private static final String PREFS_NAME = "HunterPrefsFile";
    private static final String FREQUENCY_PREFS = "frequency";
    private SharedPreferences settings;

    //czas w milisekundach
    private static final long DEFAULT_FREQUENCY = 30 * 1000;
    private static final long MIN_FREQUENCY = 30 * 1000; //30 sekund
    private static final long MAX_FREQUENCY = 2 * 60 * 1000; //2 minuty
    private static long CURRENT_FREQUENCY;

    //dodaje do konstruktora interfej (must)!!!
    public HunterService(Context context,IServiceCallbacks iServiceCallbacks) { //usuniete
        this.mContext = context;
        this.mIServiceCallbacks = iServiceCallbacks;
    }

    public HunterService(){ }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public void onCreate(){
        //Context context = getApplicationContext();
        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            // context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            //PreferenceManager.getDefaultSharedPreferences(context);
            //getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        readFrequency();
    }

//-----------------------------gps reading/connection lost-------------------------------------------

    //klasa umozliwiajaca bindowanie klientom
    public class LocalBinder extends Binder {
        HunterService getService (){
            return HunterService.this;
        }

    }

    public void startLocationManager(){
        Log.d("startLocationManager","Włączone !!!!!!!!!!!!!!!!!!!!");
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
    }

    public void startSearchLocation(){
        Log.d("startSearchLocation","Zaczynam szukać !!!!!!!!!!!!!!!!!!!!");
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, CURRENT_FREQUENCY, MIN_DISTANCE_UPDATE, locationListener);  //updatuje co 1s, odleglosc 1 metra
        locationManager.addGpsStatusListener(gpsStatus); //wywoluje listenera statusu gps
    }

    public void stopSearchLocation(){
        Log.d("stopSearchLocation","Kończę szukać !!!!!!!!!!!!!!!!!!!!");
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
            if(location != null) {
                mIServiceCallbacks.showCoordinates(location);
                //  setCoordinates(location.getLongitude());
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

    //sprawdzanie zasiegu GPS
    private GpsStatus.Listener gpsStatus = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
                if (lastLocation != null)
                    GPSFix = (SystemClock.elapsedRealtime() - lastLocationTime) < 3000;
                if (GPSFix) { //jesli jest zasieg
                   // mIServiceCallbacks.messageFromService("Jest zasieg");
                } else { //jesli nie ma zasiegu
                    createLostGpsSignalNotification();
                    //mIServiceCallbacks.messageFromService("Nie ma zasiegu");
                }
            } else if (event == GpsStatus.GPS_EVENT_FIRST_FIX) {
                GPSFix = true;
            }
        }
    };

    /*
    public static void setCoordinates(double location){
        wspolrzedna=location;
    }

    public static double getCoordinates(){
        return wspolrzedna;
    }
    */
//-----------------------------frequency of measuring gps -------------------------------------------
    public void setContext(Context context){
        mContext = context;
    }

    //zapis i odczyt ustawien
    private void saveFrequency(){
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putString(FREQUENCY_PREFS, Long.toString(CURRENT_FREQUENCY));
        preferencesEditor.commit();
    }

    private void readFrequency(){
        String savedFrequency = settings.getString(FREQUENCY_PREFS, "");

        try {
            CURRENT_FREQUENCY = Long.parseLong(savedFrequency);
        } catch (NumberFormatException e){
            CURRENT_FREQUENCY = DEFAULT_FREQUENCY;
            Log.d("HunterService", "Problem z pobieraniem częstotliwości.\n Wiadomość błędu: " + e.getMessage());
        }
    }

    public void setCurrentFrequency(long newFrequency){
        CURRENT_FREQUENCY = newFrequency * 1000;
        saveFrequency();
    }

    public long getCurrentFrequenct(){
        return CURRENT_FREQUENCY / 1000;
    }

    //powrot do domyslnej czestotliwosci
    public void setDefaultFrequency(){
        CURRENT_FREQUENCY = DEFAULT_FREQUENCY;
        saveFrequency();
    }

    public long getMinFrequency(){
        return MIN_FREQUENCY / 1000;
    }

    public long getMaxFrequency(){
        return MAX_FREQUENCY / 1000;
    }

    public void setServiceCallbacks(IServiceCallbacks callbacks){
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

        //Action when click on notification
        //in the class 'HunterService' will be called functions The 'onBind()' and 'onCreate()'
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());
    }

//------------------------------------operiation on file-------------------------------------------------

    //true if the file was successfully deleted or never existed
    //e.g. to delete file_with_gps use: DeleteInternalStorageFile(getString(R.string.file_with_gps));
    public Boolean DeleteInternalStorageFile(String filename){
        Context context = getApplicationContext();
        Boolean deleted = true;

        if( context.getFileStreamPath(filename).exists()) {
            deleted = context.deleteFile(filename);
        }
        return deleted;
    }

    //true if file never existed or if properly opened, override and closed
    public Boolean EraseInternalStorageFile(String filename){
        Context context = getApplicationContext();
        FileOutputStream fileOutputStream;
        String eraser = "";
        Boolean erased = true;

        if(context.getFileStreamPath(filename).exists())
            try {
                fileOutputStream = context.openFileOutput(filename, MODE_PRIVATE);
                fileOutputStream.write(eraser.getBytes());
                fileOutputStream.close();
            } catch (Exception e) {
                erased = false;
            }

        return erased;
    }

    //true if open, write and close properly
    //creates the file if it does not exist
    //write line by line
    public Boolean WriteToInternalStorageFile(String filename, String text){
        FileOutputStream fileOutputStream;
        Boolean written = true;

        try{
            fileOutputStream = openFileOutput(filename, MODE_APPEND);
            fileOutputStream.write(text.concat("\n").getBytes());
            fileOutputStream.close();
        }catch(Exception e){
            written = false;
        }

        return written;
    }

    //returns the contents of a file (also empty) in one String or null if exception;
    public String ReadFromInternalStorageFile(String filename) {
        BufferedReader bufferedReader;
        StringBuilder stringBuilder = new StringBuilder();
        String oneLine;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(openFileInput(filename)));

            while ((oneLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(oneLine);
                //Log.d("Testline", oneLine);
            }

            bufferedReader.close();

        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

        //Log.d("Testall", stringBuilder.toString());
        return stringBuilder.toString();
    }
//--------------------------------------------------------------------------------------------------

}
