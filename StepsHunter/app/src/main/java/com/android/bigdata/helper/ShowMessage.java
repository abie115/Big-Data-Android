package com.android.bigdata.helper;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.android.bigdata.stepshunter.R;

public class ShowMessage {

    public static void showOkDialog(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }
}
