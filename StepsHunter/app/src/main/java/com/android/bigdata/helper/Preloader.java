package com.android.bigdata.helper;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.bigdata.stepshunter.R;

public class Preloader {

    private Context contextActivity;
    private AlertDialog alertDialog;

    public Preloader(Context contextActivity) {
        this.contextActivity = contextActivity;
    }

    public void showPreloader() {
        alertDialog = createDialog();
        setCustomSettings();
        alertDialog.show();
        startPreloaderAnimation();
    }

    public void hidePreloader() {
        stopPreloaderAnimation();
        alertDialog.dismiss();
    }

    private AlertDialog createDialog() {

        LayoutInflater inflater = LayoutInflater.from(contextActivity);

        AlertDialog.Builder builder = new AlertDialog.Builder(contextActivity);
        builder.setView(inflater.inflate(R.layout.dialog_preloader, null));

        return builder.create();
    }

    private void setCustomSettings() {
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void startPreloaderAnimation() {
        Animation animation = AnimationUtils.loadAnimation(contextActivity, R.anim.animation_rotation);
        alertDialog.findViewById(R.id.spinPreloader).startAnimation(animation);
    }

    private void stopPreloaderAnimation() {
        alertDialog.findViewById(R.id.spinPreloader).clearAnimation();
    }
}