package com.android.bigdata.stepshunter;

/**
 * Created by Natalia on 2015-11-12.
 */
public class HunterService {
    private static HunterService ourInstance = new HunterService();

    public static HunterService getInstance() {
        return ourInstance;
    }

    private HunterService() {
    }
}
