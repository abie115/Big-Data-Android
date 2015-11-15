package com.android.bigdata.stepshunter;

/**
 * Created by Natalia on 2015-11-12.
 */
public class HunterServiceSingleton {
    private static HunterServiceSingleton ourInstance = new HunterServiceSingleton();

    public static HunterServiceSingleton getInstance() {
        return ourInstance;
    }

    private HunterServiceSingleton() {
    }
}
