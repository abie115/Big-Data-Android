package com.android.bigdata.databaseconnection;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.bigdata.helper.ShowMessage;
import com.android.bigdata.stepshunter.MainActivity;
import com.android.bigdata.stepshunter.RegistrationFailureActiviry;
import com.android.bigdata.stepshunter.RegistrationSuccessActivity;
import com.android.bigdata.storagedata.SettingsStorage;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class ParseConnection extends Application {

    public final static String ERROR_MESSAGE = "com.android.bigdata.databaseconnection.ERROR_MESSAGE";

    //settings
    private static final String USERNAME_PREFS = "username";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

    }

    public static void register(final String username, String email, String password, final Context context){
        ParseUser user = new ParseUser();

        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    //go to successful registration page
                    Intent intent = new Intent(context, RegistrationSuccessActivity.class);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, RegistrationFailureActiviry.class);
                    intent.putExtra(ERROR_MESSAGE, e.getMessage());
                    context.startActivity(intent);
                }
            }
        });
    }

    public static void login(final String username, final String password, final Context context){
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if(parseUser != null){
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                } else {
                    ShowMessage.showOkDialog("Logowanie się nie powiodło", context);
                }
            }
        });
    }

    public static void logout(){
        ParseUser.logOut();
    }
}
