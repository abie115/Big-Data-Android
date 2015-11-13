package com.android.bigdata.stepshunter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

/**
 * Created by Natalia on 2015-11-12.
 */
public class HunterService {

    private Context context;
    private static HunterService ourInstance = new HunterService();

    public static HunterService getInstance() {
        return ourInstance;
    }
    public void init(Context context) {
        this.context = context;
    }
    private HunterService() {

    }

    public void showAlertSettings() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(context.getResources().getString(R.string.GPSdisbaled));
        alertDialog.setMessage(context.getResources().getString(R.string.GPSalert));
        alertDialog.setPositiveButton(context.getResources().getString(R.string.Settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton(context.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}
