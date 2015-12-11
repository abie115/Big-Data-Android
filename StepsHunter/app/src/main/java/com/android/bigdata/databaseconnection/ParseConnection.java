package com.android.bigdata.databaseconnection;


import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseConnection extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        // Initialization
        Parse.initialize(this);

        //ParseObject testObject = new ParseObject("TestObject");
        //testObject.put("testCol", "testData");

            //testObject.saveInBackground();
        //If the user is offline, the object will be stored on the device until a new connection has been established.
        //If the app is closed before the connection is back, Parse will try to save it again the next time the app is opened.
            //testObject.saveEventually();
    }
}
